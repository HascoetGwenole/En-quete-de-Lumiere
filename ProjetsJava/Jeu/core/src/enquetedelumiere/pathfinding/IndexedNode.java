package enquetedelumiere.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public interface IndexedNode<Node> {
	int getIndex();
	
	Array<Connection<Node>> getConnection();

	
}
