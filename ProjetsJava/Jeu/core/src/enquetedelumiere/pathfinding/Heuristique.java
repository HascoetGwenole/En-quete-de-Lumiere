package enquetedelumiere.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

import enquetedelumiere.lvlManager.LvlManager;

public class Heuristique implements Heuristic<Node> {
	
	
	
	@Override
	public float estimate(Node startNode, Node endNode) {
		// TODO Auto-generated method stub
		int startIndex = startNode.getIndex();
		int endIndex = endNode.getIndex();
		
		int startY = startIndex / LvlManager.tileWidth;
		int startX = startIndex % LvlManager.tileWidth;
		
		int endY = endIndex / LvlManager.tileWidth;
		int endX = endIndex % LvlManager.tileWidth;
		//Manhattan method pour calcul de l'heuristique
		float distance = Math.abs(startX - endX) + Math.abs(startY - endY);
		return distance;
		
	}

	
}
