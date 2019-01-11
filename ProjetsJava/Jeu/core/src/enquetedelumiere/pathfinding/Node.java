package enquetedelumiere.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import enquetedelumiere.lvlManager.LvlManager;

public class Node implements IndexedNode<Node>{
	private Array<Connection<Node>> connections = new Array<Connection<Node>>();

	private int index;
	

	public Node() {
		index = Node.Indexer.getIndex();

	}
	
	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return index;
	}
	@Override
	public Array<Connection<Node>> getConnection() {
		// TODO Auto-generated method stub	
		return connections;
	}
	
	public void createConnection(Node toNode, float cost) {
		connections.add(new Connexion(this, toNode, cost));
		
	}
//classe qui permet de donner un unique index à chaque noeud
	private static class Indexer{
		private static int index = 0;
		public static int getIndex() {
			return index++;
		}
	}
	

	public Vector2 getCoordinatesWithIndex(int index) {
		int X = index % LvlManager.tileWidth;
		int x = X * LvlManager.tilePixelWidth; // pour avoir la position en pixels
		if (X == 0) {
			int Y = index / LvlManager.tileWidth;
			int y = Y * LvlManager.tilePixelHeight;
			return new Vector2(x,y);
			
	 		}
		else {
			int Y = index / LvlManager.tileWidth + 1;
			int y = Y * LvlManager.tilePixelHeight;
			return new Vector2(x,y);
			
		}
	}	
	
}
