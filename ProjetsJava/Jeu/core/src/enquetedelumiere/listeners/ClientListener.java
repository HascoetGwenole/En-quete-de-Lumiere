package enquetedelumiere.listeners;

import java.util.HashMap;

import javax.swing.JOptionPane;

import enquetedelumiere.packets.*;
import enquetedelumiere.screens.NetworkScreen;
import enquetedelumiere.sprite.Character;
import enquetedelumiere.sprite.CharacterWithTarget;
import enquetedelumiere.tools.ClueGenerator;

import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {

	private Character activePlayer;
	private HashMap<String, Character> characters = new HashMap<String, Character>();
	private Client client;
	private World world;

	private NetworkScreen screen;

	private ClueGenerator clueGenerator;

	public ClientListener(Character activePlayer, HashMap<String, Character> characters, Client client, NetworkScreen screen, World world, ClueGenerator clueGenerator) {
		this.activePlayer = activePlayer;
		this.characters = characters;
		this.client = client;
		this.screen = screen;
		this.world = world;
		this.clueGenerator = clueGenerator;
	}

	public void received(Connection connection, Object object) {

		if (object instanceof Packet) {

			if (object instanceof Packet1Connect) {
				// On initialise le personnage ayant le nom reçu dans le packet

				HashMap<String, String> skin = ((Packet1Connect) object).skin;
				for (Character chara : characters.values()) {
					
					chara.setTexture(skin.get(chara.name));
					
					// Si le client reçoit null, cela veut dire que le serveur est plein
					if (((Packet1Connect) object).username.equals("null")) {
						JOptionPane.showMessageDialog(null, "Le serveur est plein");
						//System.exit(0);
					}
					// Sinon, il initialise le joueur
					else if (chara.name.equals(((Packet1Connect) object).username)) {
						chara.setIsVisible(true);
						//System.out.println("Personnage initialisé");
						if (chara instanceof CharacterWithTarget) {
							((CharacterWithTarget) chara).initIA(client);
						} else {
							chara.setIsPlayer(true, client);
							activePlayer = chara;
							screen.setActivePlayer(activePlayer);
							System.out.println("ACTIVE");
							screen.getGameUI().initialisationBarraSuspect(characters, skin,((Packet1Connect)object).murderer);
						}
					}

				}
				
			} else if (object instanceof Packet2Connected) {
				// On initialise les personnages présents (visuellement : On affiche le
				// personnage )
				Character character = null;
				for (Character chara : characters.values()) {
					if (chara.name.equals(((Packet2Connected) object).characterName)) {
						chara.setIsVisible(true);
						//System.out.println("Personnage " + ((Packet2Connected) object).characterName + " initialisé");
						character = chara;

					}
				}

				/* On envoie un packet d'initialisation au nouveau joueur contenant les
					coordonnées. On ne fait pas cette initialisation pour les IA car elles
					ne sont pas activePlayer, l'initialisation se fera avec la resynchronisation */

				if(! (character instanceof CharacterWithTarget)){
					Packet4InitPosition initialisation = new Packet4InitPosition();
					if (activePlayer != null) {
					initialisation.posX = activePlayer.getBody().getPosition().x;
					initialisation.posY = activePlayer.getBody().getPosition().y;
					initialisation.envoyeur = activePlayer.name;
					initialisation.destinataire = ((Packet2Connected) object).characterName;
					client.sendTCP(initialisation);
					}
				}
			} else if (object instanceof Packet3Deplacement) {
				// On met à jour les positions des différents joueurs
				Packet3Deplacement deplacement = (Packet3Deplacement) object;
				for (Character chara : characters.values()) {
					if (chara.name.equals(deplacement.clientName)) {
						chara.setDirX(deplacement.dirX);
						chara.setDirY(deplacement.dirY);
					}
				}
			} else if (object instanceof Packet4InitPosition) {
				// On initialise la position des joueurs déjà présents ( On positionne le
				// personnage aux coordonnées reçues )
				Packet4InitPosition init = (Packet4InitPosition) object;
				for (Character chara : characters.values()) {
					if (chara.name.equals(init.envoyeur)) {
					    if(!world.isLocked()){
                            synchronized (chara){
                                chara.getBody().setTransform(init.posX, init.posY, 0);
                            }
                        }
					}

				}
			} else if (object instanceof Packet5Disconnect) {
				// On cache le sprite du joueur déconnecté
				for (Character chara : characters.values()) {
					if (chara.name.equals(((Packet5Disconnect) object).charName)) {
						chara.setIsVisible(false);
					}
				}

			} else if (object instanceof Packet6SynchroPosition) {
				// On resynchronise la position des personnages
				Packet6SynchroPosition syncPosition = (Packet6SynchroPosition) object;
				for (Character chara : characters.values()) {
					if (chara.name.equals(syncPosition.clientName)) {
						if(!world.isLocked()){
							synchronized (chara){
								chara.getBody().setTransform(syncPosition.position, 0);
							}
						}
					}
				}			
			}else if (object instanceof Packet7DeplacementIA) {
				// Gère la position des IA de manière individuelle (non utilisée)
				Packet7DeplacementIA positionIA = (Packet7DeplacementIA) object;
				for (Character chara : characters.values()) {
					if (chara.name.equals(positionIA.clientName)) {
						if(chara instanceof CharacterWithTarget){
							((CharacterWithTarget) chara).setForce(positionIA.force);
						}
					}
				}
			}else if (object instanceof  Packet7DeplacementIAMultiplexage){
				//Gère la posistions de toutes les IA d'un coup
				Packet7DeplacementIAMultiplexage aiPositions = (Packet7DeplacementIAMultiplexage) object;
				for (String name : aiPositions.aiForces.keySet()){
					for (Character chara : characters.values()) {
						if (chara.name.equals(name)) {
							if(chara instanceof CharacterWithTarget){ // Conditions normalement redondante
								((CharacterWithTarget) chara).setForce(aiPositions.aiForces.get(name));
							}
						}
					}
				}
			}else if (object instanceof Packet8GameOn){
				screen.gameOn = true;
				screen.getGameUI().getWaintingText().setVisible(false);
			}else if (object instanceof Packet10ResultatAccusation){
				Packet10ResultatAccusation resultat = (Packet10ResultatAccusation) object;
				if (resultat.reussi){
					if(resultat.enqueteur.equals(activePlayer.name)){
						screen.getGameUI().getAccusationText().setText("Bravo, vous avez demasque " + resultat.suspect + "(@"+ resultat.pseudoSuspect + ")!!");
						screen.getGameUI().getAccusationText().setVisible(true);
					}else{
						screen.getGameUI().getAccusationText().setText(resultat.enqueteur + "(@"+ resultat.pseudoEnqueteur + ") a reussi a demasquer " + resultat.suspect + "(@"+ resultat.pseudoSuspect + ") !!");
						screen.getGameUI().getAccusationText().setVisible(true);
					}
					screen.gameOn = false;
					screen.getGameUI().getEndButton().setVisible(true);
				}else{
					screen.getGameUI().getAccusationText().setText("Rate ! Vous etes un bien pietre enqueteur ...");
					screen.getGameUI().getAccusationText().setVisible(true);

					//On lance un Thread pour pouvoir supprimer le message à l'écran au bout de 6000ms
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(6000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							screen.getGameUI().getAccusationText().setVisible(false);
						}
					}).start();
				}
			}
			else if(object instanceof Packet666SynchroTimer) {
				Packet666SynchroTimer syncTimer = (Packet666SynchroTimer) object;
				screen.getGameUI().timer = syncTimer.timer;
			}
            else if(object instanceof Packet11FinPartie) {
                Packet11FinPartie endGame = (Packet11FinPartie) object;
                screen.getGameUI().getAccusationText().setText("Perdu !Le meutrier (" + endGame.skinMeurtrier + " joue par " + endGame.pseudoMeurtrier + ") a reussi a enlever toutes les preuves de son crime ...");
                screen.getGameUI().getAccusationText().setVisible(true);
                screen.gameOn = false;
                screen.getGameUI().getEndButton().setVisible(true);
            }
            else if (object instanceof Packet12DesactivationIndice){
				Packet12DesactivationIndice disable = (Packet12DesactivationIndice) object;
                clueGenerator.getPositionsIndices().get(disable.idClue).setActiveClue(false);
			}
		}
	}

}
