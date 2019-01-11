package enquetedelumiere.pathfinding;

import java.util.Iterator;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

public class GraphePath implements GraphPath<Node> {
	private Array<Node> nodes = new Array<Node>();

	public GraphePath() {
	}

	@Override
	public Iterator<Node> iterator() {
		// TODO Auto-generated method stub
		return nodes.iterator();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return nodes.size;
	}

	@Override
	public Node get(int index) {
		// TODO Auto-generated method stub
		return nodes.get(index);
	}

	@Override
	public void add(Node node) {
		// TODO Auto-generated method stub
		nodes.add(node);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		nodes.clear();
	}

	@Override
	public void reverse() {
		// TODO Auto-generated method stub
		nodes.reverse();

	}

}
