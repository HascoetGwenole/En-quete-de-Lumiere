package enquetedelumiere.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import enquetedelumiere.listeners.ServerListener;
import enquetedelumiere.packets.*;


public class GameServer extends Server{

	//HashMap liant le nom d'utilisateur et la connection
	private HashMap<String, Connection> clients = new HashMap<String, Connection>();
	//HashMap liant le nom d'utilisateur et le nom du personnage
	private HashMap<String, String> linksBetweenCharsAndUsers = new HashMap<String, String>();
	//HashMap liant le nom du personnage et le nom d'utilisateur ( dans l'autre sens pour faciliter la gestion des deconnections )
	private HashMap<String, String> linksBetweenUsersAndChars = new HashMap<String, String>();
	
public void init() throws IOException {
	
	 this.start();
	 this.bind(54555, 54777);
	 
	 //Enregistrement des diff�rents packets
	 getKryo().register(Packet.class);
	 getKryo().register(Packet1Connect.class);
	 getKryo().register(Packet2Connected.class);
	 getKryo().register(Packet3Deplacement.class);
	 getKryo().register(Packet4InitPosition.class);
	 getKryo().register(Packet5Disconnect.class);
	 getKryo().register(Vector2.class);
	 getKryo().register(Packet6SynchroPosition.class);
	 getKryo().register(Packet7DeplacementIA.class);
	 getKryo().register(Packet7DeplacementIAMultiplexage.class);
	 getKryo().register(HashMap.class);
	 getKryo().register(Packet8GameOn.class);
	 getKryo().register(Packet9Accusation.class);
	 getKryo().register(Packet10ResultatAccusation.class);
	 getKryo().register(Packet666SynchroTimer.class);
	 getKryo().register(Packet11FinPartie.class);
	 getKryo().register(Packet12DesactivationIndice.class);
	 
	 //Création du tableau des personnages disponibles
	 ArrayList<String> spritenames = new ArrayList<String>();
	 spritenames.add("null");
	 spritenames.add("player0");
	 spritenames.add("player1");
	 spritenames.add("player2");
	 spritenames.add("player3");
	 spritenames.add("player4");
	 spritenames.add("player5");
	 spritenames.add("player6");
	 spritenames.add("player7");
	 
	 //création du tableau des skins dispos
	 ArrayList<String> skinsdispos = new ArrayList<String>();
	 skinsdispos.add("raven");
	 skinsdispos.add("marie");
	 skinsdispos.add("jake");
	 skinsdispos.add("playershiney");
	 skinsdispos.add("blondie");
	 skinsdispos.add("detective");
	 skinsdispos.add("detectiveconan");
	 skinsdispos.add("serge");

	 //création du tableau aléatoire
	 HashMap<String, String> skins = new HashMap<String, String>();

	Collections.shuffle(skinsdispos);
	int nbSkinDispos = skinsdispos.size();
	for (int i=0; i < nbSkinDispos;i++) {
		skins.put("player"+i, skinsdispos.get(i));
	}

	System.out.println("INITIALISATION SERVEUR");
	for(String name : skins.keySet()){
		System.out.println(name + " a le skin " + skins.get(name));
	}
	System.out.println("FIN INITIALISATION SERVEUR");
	 
	 //Création de la liste des personnages connectés + compteur de joueurs
	 ArrayList<String> connected = new ArrayList<String>();
	 

	 //Ajout du Listener
	 ServerListener serverListener = new ServerListener(clients, linksBetweenCharsAndUsers, linksBetweenUsersAndChars, spritenames, connected, skins, this);
	 this.addListener(serverListener);
}

}

