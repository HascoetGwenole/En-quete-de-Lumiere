package enquetedelumiere.packets;

/**
 *  Ce paquet permet de transmettre tous les forces qui controlent les IA en un seul paquet
 */

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class Packet7DeplacementIAMultiplexage extends Packet{
	
	public HashMap<String, Vector2> aiForces;
	public String clientName;
}
