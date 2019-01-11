package enquetedelumiere.client;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;

import enquetedelumiere.packets.Packet;
import enquetedelumiere.packets.Packet10ResultatAccusation;
import enquetedelumiere.packets.Packet11FinPartie;
import enquetedelumiere.packets.Packet12DesactivationIndice;
import enquetedelumiere.packets.Packet1Connect;
import enquetedelumiere.packets.Packet2Connected;
import enquetedelumiere.packets.Packet3Deplacement;
import enquetedelumiere.packets.Packet4InitPosition;
import enquetedelumiere.packets.Packet5Disconnect;
import enquetedelumiere.packets.Packet666SynchroTimer;
import enquetedelumiere.packets.Packet6SynchroPosition;
import enquetedelumiere.packets.Packet7DeplacementIA;
import enquetedelumiere.packets.Packet7DeplacementIAMultiplexage;
import enquetedelumiere.packets.Packet8GameOn;
import enquetedelumiere.packets.Packet9Accusation;

public class GameClient extends Client {

	public void init(String Ip) {
		this.start();
		try {
			this.connect(5000, Ip, 54555, 54777); // PC fixe : "157.159.49.116" "192.168.1.55"
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Le serveur n'est pas accessible");
			System.exit(0);
		}
		
		// On enregistre les différents packets sur le client
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
	}
}