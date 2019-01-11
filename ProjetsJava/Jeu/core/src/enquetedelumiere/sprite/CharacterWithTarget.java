package enquetedelumiere.sprite;

import java.util.HashMap;


import java.util.Random;

import enquetedelumiere.lvlManager.LvlManager;
import enquetedelumiere.packets.Packet6SynchroPosition;
import enquetedelumiere.pathfinding.GraphePath;
import enquetedelumiere.pathfinding.Heuristique;
import enquetedelumiere.pathfinding.Localisation;
import enquetedelumiere.pathfinding.Node;
import enquetedelumiere.tools.Control;

import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import com.esotericsoftware.kryonet.Client;

public class CharacterWithTarget extends Character {
	//les CharacterWithTarget sont les IAs
	private Array<Localisation> pointsDInteret = new Array<Localisation>();
	public Localisation target;
	public Localisation temporaryTarget; // cible temporaire de l'ai
	private IndexedAStarPathFinder pathFinder;
	public GraphePath resultPath = new GraphePath();
	private boolean canMove;
	private Array<Vector2> waypoints;

	private boolean isAI = false;
	private Vector2 force = new Vector2(0, 0);
	private HashMap<String, Vector2> iaForces;
	private boolean debug;
	private boolean collision;
	
	private int timeToWaypoint;

	public CharacterWithTarget(World world, String texturePath, int startX, int startY, String name,
			Array<Localisation> pointsDInteret, HashMap<String, Vector2> iaForces, enquetedelumiere.game.Game game) {
		
		super(world, texturePath, startX, startY, name, null, game);
		// TODO Auto-generated constructor stub
		this.pointsDInteret = pointsDInteret;
		// la cible est le premier point d'interet
		debug = false;
		canMove = true;
		collision = false;
		timeToWaypoint = 0;
		

		this.iaForces = iaForces;
		
		pathFinder = new IndexedAStarPathFinder<Node>(LvlManager.graph, false); // le deuxième champ est à false car on n'utilise pas de métrique dans notre recherche de chemin
		
		this.findWaypoints();
		if (waypoints.size != 0) {
		target = new Localisation(waypoints.get(0));
		}
		else {
		target = new Localisation(body.getPosition());
		}
	}

	public GraphePath getResultPath() {
		return resultPath;
	}

	public void setResultPath(GraphePath resultPath) {
		this.resultPath = resultPath;
	}

	public boolean isCanMove() {
		return canMove;
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public Array<Vector2> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(Array<Vector2> waypoints) {
		this.waypoints = waypoints;
	}

	@Override
	public void update(float delta, Control control, Client client) {

		if (isAI) {
			// Si le client calcule les IA
			if (behavior != null && canMove == true) {
				behavior.calculateSteering(steeringOutput);
				applySteering(delta);
				iaForces.put(this.name, new Vector2(steeringOutput.linear.x, steeringOutput.linear.y));
				force = new Vector2(steeringOutput.linear.x, steeringOutput.linear.y);
				if (steeringOutput.linear.x > 0) setFlip(false, false);
				else setFlip(true, false);
			} else {
				// Utile juste pour le son des pas
				iaForces.put(this.name, new Vector2(0, 0));
			}

			debug();

			super.update(delta, control, client);
			Seek<Vector2> seekSB = new Seek<Vector2>(this, this.seekTarget());
			setBehavior(seekSB);

		} else {
			// Si le client ne fait que rendre les IA
			boolean anyAccelerations = false;
			if (!force.isZero()) {
				synchronized (body) {
					body.applyForceToCenter(force, true); // 60 est la vitesse de l'IA
					anyAccelerations = true;
				}
				if (force.x > 0) setFlip(false, false);
				else setFlip(true, false);
			}

			if (anyAccelerations) {
				Vector2 velocity = body.getLinearVelocity();
				float currentSpeedSquare = velocity.len2();
				if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
					synchronized (body) {
						body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
					}
				}
			}

			super.update(delta, control, client);
		}

	}

	public Localisation seekTarget() {

		if (waypoints.size >= 1 && timeToWaypoint < 500 && collision == false ||waypoints.size >= 1 && timeToWaypoint < 100 && collision) {
			// si il lui reste des cible à atteindre et qu'il n'est pas bloqué dans la
			// recherche de cible (on distingue le cas où elle se cogne à un perso pour qu'elle attende moins longtemps)
			Vector2 waypoint = waypoints.get(0);
			if (waypoint.dst(body.getPosition()) > 1) {
				timeToWaypoint += 1;
				// on garde en mémoire la durée de la recherche
				temporaryTarget = new Localisation(waypoint); // la cible temporaire à suivre est la localisation du
																// premier waypoint
				return temporaryTarget;

			} else { // on a atteint la cible temporaire précédente, mais on n'est pas arrivé au
						// point d'intérêt
				timeToWaypoint = 0;
				// on réactualise le temps de recherche
				if (waypoints.size > 1) {
					waypoints.removeRange(0, 0);
					temporaryTarget = new Localisation(waypoints.get(0));
					return temporaryTarget;

				} else {// on est arrivé au point d'interet
					canMove = false;
					new Thread(new Runnable() {
						@Override
						public void run() {
							Random generator = new Random();
							int k = generator.nextInt(5);// pour attendre entre 0 et 5 secondes
							// on attend k secondes
							try {
								Thread.sleep(k * 1000);
							}

							catch (InterruptedException e) {
								e.printStackTrace();
							}

							canMove = true;
						}
					}).start();

					this.findWaypoints();

					return new Localisation(waypoints.get(0));
				}
			}
		} else {
			// si waypoints est null ou si la cible met trop de temps à chercher son
			// prochain waypoint,
			// on fait croire à la cible qu'elle est arrivée pour qu'à la prochaine update
			// elle cherche un nouveau point d'interet
		
			timeToWaypoint = 0;
			setCanMove(true);
			Array<Vector2> waypoints = new Array<Vector2>();
			waypoints.add(body.getPosition());
			setWaypoints(waypoints);
			return new Localisation(waypoints.get(0));

		}
	}

	public void findWaypoints() {
		// cette méthode permet d'obtenir un tableau de positions à suivre pour se déplacer vers un point
		//d'intêret aléatoire.
		Random generator = new Random();
		int i = generator.nextInt(pointsDInteret.size);
		target = pointsDInteret.get(i);
		// on recreer un chemin à suivre jusqu'à une nouvelle cible
		resultPath = new GraphePath();

		int xCible = (int) (target.getPosition().x);
		int yCible = (int) (target.getPosition().y);

		int startX = (int) (this.getPosition().x);
		int startY = (int) (this.getPosition().y);
		Node startNode = LvlManager.graph.getNodeByCoordonates(startX, startY);
		Node endNode = LvlManager.graph.getNodeByCoordonates(xCible, yCible);
		
		//On applique le pathfinder de libGDX basé sur A* pour trouver un chemin entre les noeud de dépard et le noeud d'arrivé
		pathFinder.searchNodePath(startNode, endNode, new Heuristique(), resultPath);

		waypoints = new Array<Vector2>(resultPath.getCount());// on recrée un chemin de waypoints à suivre

		//On transforme le chemin de noeuds en chemin de positions
		for (int j = 0; j < resultPath.getCount(); j++) {
			waypoints.add(
					resultPath.get(j).getCoordinatesWithIndex(resultPath.get(j).getIndex()));
		}
		if (waypoints.size==0) {
			waypoints.add(body.getPosition());
		}

	}

	public void initIA(final Client client) {
		isAI = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					synchronized (this){
						Packet6SynchroPosition syncPosition = new Packet6SynchroPosition();
						syncPosition.position = body.getPosition();
						syncPosition.clientName = name;
						client.sendUDP(syncPosition);
					}
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public synchronized void setForce(Vector2 force) {
		this.force = force;
	}

	public boolean isMoving() {
		if (force.isZero())
			return false;
		else
			return true;
	}

	public Array<Localisation> getPointsDInteret() {
		return pointsDInteret;
	}

	public void setPointsDInteret(Array<Localisation> pointsDInteret) {
		this.pointsDInteret = pointsDInteret;
	}

	public Localisation getTarget() {
		return target;
	}

	public void setTarget(Localisation target) {
		this.target = target;
	}

	public Localisation getTemporaryTarget() {
		return temporaryTarget;
	}

	public void setTemporaryTarget(Localisation temporaryTarget) {
		this.temporaryTarget = temporaryTarget;
	}

	public IndexedAStarPathFinder getPathFinder() {
		return pathFinder;
	}

	public void setPathFinder(IndexedAStarPathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}

	public void debug() {
		// La fonction debug permet à une IA de repartir si elle se trouve sur une case où elle n'est pas censée pouvoir se déplacer
		if (steeringOutput.linear.y == 0 && steeringOutput.linear.x == 0) {

			// si une IA s'arrête et que le probleme n'est pas encore traité

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {

						if (debug == false) {
							// si l'ia n'est pas encore en train d'être débugée, on attend 5 secondes ( car l'ia peut attendre jusqu'à 5 ,
							// au maximum dans son fonctionnement normal)
							Thread.sleep(5000);
							debug = true;
						} 
						
						
						if (debug = true){

							while (steeringOutput.linear.y == 0 && steeringOutput.linear.x == 0) {
								// si l'IA n'est pas repartie après 5 secondes
								
								setCanMove(true);
								// on vérifie qu'elle peut se déplacer

								int x = (int) getPosition().x;
								int y = (int) getPosition().y;
								
								// on associe la position du joueur à la case où trouve pour regarder où il peut se déplacer

								int X = (int) (x / LvlManager.tilePixelWidth);
								int Y = (int) (y / LvlManager.tilePixelHeight);
								
								
								TiledMapTileLayer.Cell up = LvlManager.graph.getTiles().getCell(X, Y + 1);
								TiledMapTileLayer.Cell down = LvlManager.graph.getTiles().getCell(X, Y - 1);
								TiledMapTileLayer.Cell left = LvlManager.graph.getTiles().getCell(X - 1, Y);
								TiledMapTileLayer.Cell right = LvlManager.graph.getTiles().getCell(X + 1, Y);

								if (up != null) {
									// si l'IA peut se déplacer vers le haut, on la déplace vers le haut
									Array<Vector2> waypoints = new Array<Vector2>();
									waypoints.add(new Vector2(x, y + 8));
									setWaypoints(waypoints);
								} else if (down != null) {
									Array<Vector2> waypoints = new Array<Vector2>();
									waypoints.add(new Vector2(x, y - 8));
									setWaypoints(waypoints);
								} else if (left != null){

									Array<Vector2> waypoints = new Array<Vector2>();
									waypoints.add(new Vector2(x - 8, y));
									setWaypoints(waypoints);
								} else if (right != null){

									Array<Vector2> waypoints = new Array<Vector2>();
									waypoints.add(new Vector2(x + 8, y));
									setWaypoints(waypoints);
								}

							}
						}

					}

					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}).start();
			if (steeringOutput.linear.y != 0 || steeringOutput.linear.x != 0) {
				debug = false;
			}
		}
		
		

	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}
	
}
