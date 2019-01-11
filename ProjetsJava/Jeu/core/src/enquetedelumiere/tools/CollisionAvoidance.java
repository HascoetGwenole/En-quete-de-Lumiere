package enquetedelumiere.tools;

import java.util.Random;

import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import enquetedelumiere.lvlManager.LvlManager;
import enquetedelumiere.sprite.Character;
import enquetedelumiere.sprite.CharacterWithTarget;

public class CollisionAvoidance {
	CharacterWithTarget ia;
	Character player;

	public CollisionAvoidance(CharacterWithTarget ia, Character player) {
		this.ia = ia;
		this.player = player;
	}

	public void avoidCollision() {

		if (ia.isCanMove() == false) {
			// si l'ia attend sur un point d'interet

			if (player instanceof CharacterWithTarget) {
				CharacterWithTarget ia2 = (CharacterWithTarget) player;
				((CharacterWithTarget) player).setCanMove(false);
				// on dit à l'autre ia de s'arreter avant qu'il y ait collision
				new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ia2.setCanMove(true);
					}
				}).start();
			}
		}
		// on récupère les coordonnées de l'IA:
		int x = (int) ia.getPosition().x;
		int y = (int) ia.getPosition().y;
		// on associe ces coordonnées à la case correspondante sur la tiledMap
		int X = (int) (x / LvlManager.tilePixelWidth);
		int Y = (int) (y / LvlManager.tilePixelHeight);

		TiledMapTileLayer.Cell up = LvlManager.graph.getTiles().getCell(X, Y + 1);
		TiledMapTileLayer.Cell down = LvlManager.graph.getTiles().getCell(X, Y - 1);
		TiledMapTileLayer.Cell left = LvlManager.graph.getTiles().getCell(X - 1, Y);
		TiledMapTileLayer.Cell right = LvlManager.graph.getTiles().getCell(X + 1, Y);

		// on récupère les points de passages de l'ia
		Array<Vector2> waypoints = ia.getWaypoints();

		if (Math.abs(ia.getSteeringOutput().linear.y) > Math.abs(ia.getSteeringOutput().linear.x)) {
			// si l'ia se déplace selon y, on va éviter la collision en déplaçant l'ia d'une
			// case sur x

			// On commence par vérifier que les personnages sont alignés sur Y et qu'il va y
			// avoir collision
			float hitboxSizeY = ia.getTexture().getWidth() / 3;
			if (Math.abs(ia.getPosition().x - player.getPosition().x) <= hitboxSizeY) {

				if (ia.getSteeringOutput().linear.y > 0) {
					// si elle se déplace vers le haut

					if (right != null) {
						// si on peut on déplace l'ia à droite
						waypoints.removeIndex(0);
						waypoints.insert(0, new Vector2(x + 24, y + 16));
						waypoints.insert(0, new Vector2(x + 24, y));

					} else if (left != null) {
						// sinon on la déplace à gauche
						waypoints.removeIndex(0);
						waypoints.insert(0, new Vector2(x - 24, y + 16));
						waypoints.insert(0, new Vector2(x - 24, y));

					}

					ia.setWaypoints(waypoints);
				}
			}
		}

		else if (Math.abs(ia.getSteeringOutput().linear.x) > Math.abs(ia.getSteeringOutput().linear.y)) {
			// si l'ia se déplace selon x, on va éviter la collision en déplacant l'ia d'une
			// case sur y

			// on commence par vérifier que les personnages sont alignés selon x et qu'ils
			// vont bien se cogner

			if (Math.abs(ia.getPosition().y - player.getPosition().y) <= 2) {

				if (ia.getSteeringOutput().linear.x > 0) {
					// si l'IA se déplace vers la droite, elle va se décaler pour laisser passer
					// l'autre

					if (up != null) {
						// on déplace l'ia vers le haut si c'est possible
						waypoints.removeIndex(0);
						waypoints.insert(0, new Vector2(x + 16, y + 16));
						waypoints.insert(0, new Vector2(x, y + 16));
					} else if (down != null) {
						waypoints.removeIndex(0);
						waypoints.insert(0, new Vector2(x + 16, y - 16));
						waypoints.insert(0, new Vector2(x, y - 16));

					}
				}
				// après s'être déplacé sur cette nouvelle case, on reprend le trajet
				// initialement prévu
				ia.setWaypoints(waypoints);
			}

		}
	}
}
