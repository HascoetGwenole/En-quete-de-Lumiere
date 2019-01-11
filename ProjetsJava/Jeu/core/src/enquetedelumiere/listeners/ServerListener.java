package enquetedelumiere.listeners;

import java.util.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import enquetedelumiere.packets.*;

public class ServerListener extends Listener{
	
	//HashMap liant le nom d'utilisateur et la connection
	private HashMap<String, Connection> clients = new HashMap<String, Connection>();
	//HashMap liant le nom d'utilisateur et le nom du personnage
	private HashMap<String, String> linksBetweenCharsAndUsers = new HashMap<String, String>();
	//HashMap liant le nom du personnage et le nom d'utilisateur ( dans l'autre sens pour faciliter la gestion des deconnections )
	private HashMap<String, String> linksBetweenUsersAndChars = new HashMap<String, String>();
	
	private ArrayList<String> spritenames = new ArrayList<String>();
	private ArrayList<String> connected = new ArrayList<String>();
	private HashMap<String, String> skins = new HashMap<String, String>();
	
	private Server server;

	private String murderer;
	
	public ServerListener(HashMap<String, Connection> clients, HashMap<String, String> linksBetweenCharsAndUsers, HashMap<String, String> linksBetweenUsersAndChars, ArrayList<String> spritenames, ArrayList<String> connected, HashMap<String, String> skins, Server server ) {
		
		this.clients = clients;
		this.linksBetweenCharsAndUsers = linksBetweenCharsAndUsers;
		this.linksBetweenUsersAndChars = linksBetweenUsersAndChars;
		this.spritenames = spritenames;
		this.connected = connected;
		this.server = server;
		this.skins = skins;

		// On choisit le meurtrier aléatoirement
        chooseRandomMurderer();
        System.out.println("Le meurtrier est " + murderer);

	}

	private void chooseRandomMurderer(){
		List<String> playerNames = Arrays.asList("player0", "player1", "player2", "player3");
		Random generator = new Random();
		murderer = playerNames.get(generator.nextInt(playerNames.size()));
	}
	 
	public void received(Connection connection, Object object) {
		
		 if( object instanceof Packet) {
			 
			 if ( object instanceof Packet1Connect ) {
				 
				 
				 //Demande de connexion
				 Packet1Connect connect = (Packet1Connect) object;
				 clients.put(connect.username, connection);
				 //System.out.println(connect.username + " est connecté");

				 
				 //Envoi du personnage et confirmmation de connexion
				 Packet1Connect config = new Packet1Connect();
				 config.username = spritenames.get(spritenames.size()-1);
				 config.skin = skins;
                 config.murderer = murderer;
				 connected.add(config.username);
				 //System.out.println(config.username + " est désormais occupé ");
				 spritenames.remove(spritenames.size()-1);
				 server.sendToTCP(clients.get(connect.username).getID(),config);

				 if( spritenames.size() == 1){
				 	// Il y a tous les joueurs, la partie peut commencer
					 Packet8GameOn gameOn = new Packet8GameOn();
					 server.sendToAllTCP(gameOn);
				 }

				 
				 //Creation du lien entre personnage et joueur
				 linksBetweenCharsAndUsers.put(config.username, connect.username);
				 linksBetweenUsersAndChars.put(connect.username, config.username);
				 
				 //On prévient les autres de la connexion
				 Packet2Connected newPlayer = new Packet2Connected();
				 newPlayer.characterName = config.username;
				 server.sendToAllExceptTCP(clients.get(connect.username).getID(), newPlayer);
				 
				 //On s'informe sur les personnages déjà présents
				 for (String player : connected) {
					 Packet2Connected connectedPlayers = new Packet2Connected();
					 connectedPlayers.characterName = player;
					 server.sendToTCP(clients.get(connect.username).getID(), connectedPlayers);
					 
				 }
				 
				 
			 } else if ( object instanceof Packet3Deplacement) {
				 //On envoie la position du joueur aux autres
				 Packet3Deplacement position = (Packet3Deplacement) object;
				 server.sendToAllExceptUDP(clients.get(linksBetweenCharsAndUsers.get(position.clientName)).getID(), position);

				 
			 } else if ( object instanceof Packet4InitPosition) {
				 //Envoie la position des joueurs présents au nouvel arrivant
				 Packet4InitPosition init = (Packet4InitPosition) object;
				 server.sendToTCP(clients.get(linksBetweenCharsAndUsers.get(init.destinataire)).getID(), init);
			 }
			 else if ( object instanceof Packet6SynchroPosition) {
				 //On envoie la position du joueur aux autres
				 Packet6SynchroPosition position = (Packet6SynchroPosition) object;
				 server.sendToAllExceptTCP(clients.get(linksBetweenCharsAndUsers.get(position.clientName)).getID(), position);

			 }
			 else if ( object instanceof Packet7DeplacementIA) {
				 Packet7DeplacementIA positionIA = (Packet7DeplacementIA) object;
				 server.sendToAllExceptUDP(clients.get(linksBetweenCharsAndUsers.get(positionIA.clientName)).getID(), positionIA);
				//System.out.println("l'IA " + positionIA.clientName + " s'est co en tant que IA");
			 }
			 else if (object instanceof Packet7DeplacementIAMultiplexage){
                 Packet7DeplacementIAMultiplexage aiPositions = (Packet7DeplacementIAMultiplexage) object;
				 server.sendToAllExceptUDP(clients.get(linksBetweenCharsAndUsers.get(aiPositions.clientName)).getID(), aiPositions);
			 }
             else if (object instanceof Packet8GameOn){
                 // Si le serveur reçoit un paquet8, c'est nécessairement pour arrêter la partie
                Packet11FinPartie endGame = new Packet11FinPartie();
                endGame.skinMeurtrier = skins.get(murderer);
                endGame.pseudoMeurtrier = linksBetweenCharsAndUsers.get(murderer);
                server.sendToAllTCP(endGame);
             }
             else if (object instanceof Packet9Accusation){
                 Packet9Accusation accusation = (Packet9Accusation) object;
                 if (accusation.suspect.equals(murderer)){
                     Packet10ResultatAccusation resultat = new Packet10ResultatAccusation();
                     resultat.reussi = true;
                     resultat.enqueteur = accusation.enqueteur;
                     resultat.pseudoEnqueteur = linksBetweenCharsAndUsers.get(accusation.enqueteur);
                     resultat.suspect = accusation.suspect;
                     resultat.pseudoSuspect = linksBetweenCharsAndUsers.get(accusation.suspect);
                     server.sendToAllTCP(resultat);
                 }else {
                     Packet10ResultatAccusation resultat= new Packet10ResultatAccusation();
                     resultat.reussi = false;
                     resultat.enqueteur = accusation.enqueteur;
                     resultat.suspect = accusation.suspect;
                     String pseudo = linksBetweenCharsAndUsers.get(accusation.enqueteur);
                     server.sendToTCP(clients.get(pseudo).getID(), resultat);
                     System.out.print("NULLLL");
                 }
             }else if (object instanceof Packet12DesactivationIndice){
                 Packet12DesactivationIndice disable = (Packet12DesactivationIndice) object;
                 server.sendToAllTCP(disable);
			 }
		     else if (object instanceof Packet666SynchroTimer){
				 Packet666SynchroTimer syncTimer = (Packet666SynchroTimer) object;
				 server.sendToAllExceptUDP(clients.get(linksBetweenCharsAndUsers.get(syncTimer.clientName)).getID(),syncTimer);
			 }
			 
		 
         }
	 }
	 
	 //Gestion de la deconnexion
	 public void disconnected(Connection connection) {
		 Packet5Disconnect disconnection = new Packet5Disconnect();
		 Iterator it = clients.entrySet().iterator();
		 String charName="";
		 while (it.hasNext()) {
			 Map.Entry pairs = (Map.Entry) it.next();
			 if (pairs.getValue() == connection) {
				 charName = linksBetweenUsersAndChars.get((String) pairs.getKey());
				 System.out.println(linksBetweenCharsAndUsers.get(charName) + " s'est déconnecté");
				 break;
				 
			 }
		 }
		 if (!charName.equalsIgnoreCase("")) {
			 disconnection.charName = charName;
			 spritenames.add(charName);
			 connected.remove(charName);
			 System.out.println(charName  + " est de nouveau disponible !");
			 linksBetweenUsersAndChars.remove(linksBetweenCharsAndUsers.get(charName));
			 linksBetweenCharsAndUsers.remove(charName);
			 server.sendToAllExceptTCP(connection.getID(), disconnection);
		 }
		 
            
		 
	 }

}
