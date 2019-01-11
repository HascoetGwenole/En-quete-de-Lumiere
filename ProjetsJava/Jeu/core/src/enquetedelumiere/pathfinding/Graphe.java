package enquetedelumiere.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;


import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import enquetedelumiere.lvlManager.LvlManager;

public class Graphe implements IndexedGraph<Node> {
	
	private Array<Node> nodes = new Array<Node>();
	private TiledMapTileLayer tiles;
	
	public Graphe(Array<Node> nodes) {
		this.nodes=nodes;		
	}
	
	@Override
	public Array<Connection<Node>> getConnections(Node fromNode) {
		// TODO Auto-generated method stub
		return fromNode.getConnection();
	}

	@Override
	public int getIndex(Node node) {
		// TODO Auto-generated method stub
		return node.getIndex();
	}

	@Override
	public int getNodeCount() {
		// TODO Auto-generated method stub
		return nodes.size;
	}
	public Node getNodeByCoordonates(int x, int y) {
		int X = (int) (x / LvlManager.tilePixelWidth);
		int Y = (int) (y / LvlManager.tilePixelHeight);

		return nodes.get(LvlManager.tileWidth * Y + X);		
	
	}

	public TiledMapTileLayer getTiles() {
		return tiles;
	}

	public void setTiles(TiledMapTileLayer tiles) {
		this.tiles = tiles;
	}
	
}
