package enquetedelumiere.lvlManager;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

import enquetedelumiere.pathfinding.GraphGenerator;
import enquetedelumiere.pathfinding.Graphe;
import enquetedelumiere.pathfinding.Node;

public class LvlManager {
	
	
	
	public static int tileHeight; //variables qui peuvent être utiles dans le code
	public static int tileWidth; 
	public static int pixelHeight;
	public static int pixelWidth;
	public static int tilePixelWidth;
	public static int tilePixelHeight;
	
	public static Graphe graph = new Graphe (new Array<Node>());;
	
	public static void loadLvl(TiledMap map) {
		
		//initialisation des valeurs des différentes variables de taille de la map
		
		MapProperties properties = map.getProperties();
		
		tileHeight = properties.get("height", Integer.class); //hauteur en nombre de cases

		tileWidth = properties.get("width", Integer.class); // longueur en nombre de cases
		
		tilePixelHeight = properties.get("tileheight", Integer.class);

		tilePixelWidth = properties.get("tilewidth", Integer.class); // nombre pixels hauteur d'une case

		pixelWidth = tilePixelWidth * tileWidth;//longueur pixel
													
		pixelHeight = tilePixelHeight * tileHeight;

		// création du graphe pour le pathfinding
		graph = GraphGenerator.generateGraph(map);
		
		
	}
	
}
