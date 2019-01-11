package enquetedelumiere.pathfinding;

import com.badlogic.gdx.ai.steer.utils.Path;

public class ParaChemin implements Path.PathParam {
	float distance;
	@Override
	public float getDistance() {
		// TODO Auto-generated method stub
		return distance;
	}

	@Override
	public void setDistance(float distance) {
		this.distance=distance;
		// TODO Auto-generated method stub
		
	}

}
