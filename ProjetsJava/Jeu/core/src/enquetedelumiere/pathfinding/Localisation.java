package enquetedelumiere.pathfinding;

import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;

public class Localisation implements Location<Vector2> {
	// cette classe à pour but de permettre aux IA d'appliquer des Steering Behaviors vers une position
	//car les Steering behaviors ne fonctionnent ue sur des objets steerable.

	private Vector2 position;
	private float orientation;
	
	public Localisation(Vector2 position) {
		orientation = 0;
		this.position = position; 
	}
	@Override
	public Vector2 getPosition() {
	
		return position;
	}

	@Override
	public float getOrientation() {
		// TODO Auto-generated method stub
		return orientation;
	}

	@Override
	public void setOrientation(float orientation) {
		this.orientation = orientation;
		
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return (float) Math.atan2(-vector.x, vector.y);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		outVector.x = -(float) Math.sin(angle);
		outVector.y = (float) Math.cos(angle);
		return outVector;
	}

	@Override
	public Location<Vector2> newLocation() {
		return new SteerableAdapter<Vector2>();
	}

}
