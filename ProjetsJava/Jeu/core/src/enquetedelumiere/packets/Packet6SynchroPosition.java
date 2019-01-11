package enquetedelumiere.packets;

import com.badlogic.gdx.math.Vector2;

/**
 * Ce paquet sert à resynchroniser la position du personnage clientName
 */

public class Packet6SynchroPosition extends Packet{
	
	public Vector2 position;
	public String clientName;

}
