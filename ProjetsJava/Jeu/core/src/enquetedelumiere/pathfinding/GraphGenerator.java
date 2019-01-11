package enquetedelumiere.pathfinding;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

import enquetedelumiere.lvlManager.LvlManager;

public class GraphGenerator {
	
	public static Graphe generateGraph(TiledMap map) {

		Array<Node> nodes = new Array<Node>();

		TiledMapTileLayer tiles = (TiledMapTileLayer) map.getLayers().get("ground"); // On récupère le ground du tiled
		
		int mapHeight = LvlManager.tileHeight;
		int mapWidth = LvlManager.tileWidth;
		// on parcourt toute les cases de la tiled map en partant du coin inférieur
		// droit
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				// on créé un noeud pour chaque case
				Node node = new Node();

				// on ajoute le noeud au graphe
				nodes.add(node);

			}

		}
		// Pour chacune des cases de la tiledMap, on récupère les cases au dessus, à gauche, à droite, et en bas,
		//On teste tout d'abord si la case en question fait partie du sol (c'est à dire des endroits où l'ia peut se déplacer)
		//Puis si on peut se décacer sur la case en question, on créer des connexions avec les cases adjacentes lorsque c'est possible
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				TiledMapTileLayer.Cell target = tiles.getCell(x, y);
				TiledMapTileLayer.Cell up = tiles.getCell(x, y + 1);
				TiledMapTileLayer.Cell down = tiles.getCell(x, y - 1);
				TiledMapTileLayer.Cell left = tiles.getCell(x - 1, y);
				TiledMapTileLayer.Cell right = tiles.getCell(x + 1, y);

				Node targetNode = nodes.get(mapWidth * y + x); //Rq: les tableaux en libGDX sont en 1D 
				if (target != null) {  
					float cost = 1;
					if (y != 0 && down != null) { // on vérifie que la case supérieure fait partie du sol (si elle existe)
						Node downNode = nodes.get(mapWidth * (y - 1) + x); 
						targetNode.createConnection(downNode, cost); // si la case supérieure fait partie du sol, on la connecte à la case actuelle dans le graphe
					}

					if (x != 0 && left != null) {
						Node leftNode = nodes.get(mapWidth * y + x - 1);
						targetNode.createConnection(leftNode, cost);
					}

					if (y != mapHeight - 1 && up != null) {
						Node upNode = nodes.get(mapWidth * (y + 1) + x);
						targetNode.createConnection(upNode, cost);
					}

					if (x != mapWidth - 1 && right != null) {
						Node rightNode = nodes.get(mapWidth * y + x + 1);
						targetNode.createConnection(rightNode, cost);
					}
					
				}

			}

		}
		Graphe graph = new Graphe(nodes);
		graph.setTiles(tiles);
		return graph;
	}
}
