package enquetedelumiere.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;

public class Connexion implements Connection<Node> {

	public Node fromNode;
	public Node toNode;
	public float cost;
	
	
	public Connexion(Node fromNode, Node toNode, float cost ) {
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.cost = cost;		
	}
	@Override
	public float getCost() {
		// TODO Auto-generated method stub
		return cost;
	}

	@Override
	public Node getFromNode() {
		// TODO Auto-generated method stub
		return fromNode;
	}

	@Override
	public Node getToNode() {
		// TODO Auto-generated method stub
		return toNode;
	}
	

}
