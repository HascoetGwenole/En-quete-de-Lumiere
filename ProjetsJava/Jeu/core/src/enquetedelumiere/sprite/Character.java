package enquetedelumiere.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryonet.Client;

import enquetedelumiere.game.Game;
import enquetedelumiere.packets.Packet12DesactivationIndice;
import enquetedelumiere.packets.Packet3Deplacement;
import enquetedelumiere.packets.Packet6SynchroPosition;
import enquetedelumiere.packets.Packet9Accusation;
import enquetedelumiere.screens.GameUI;
import enquetedelumiere.tools.Box2dFactory;
import enquetedelumiere.tools.Control;
import enquetedelumiere.tools.DialogueInteractiveBox;

public class Character extends Sprite implements Steerable<Vector2>, Comparable<Character> {

	protected Body body;
	private Body interactiveBody; // Boite de collision pour les interactions
	private int speed;
	public String name;

	private boolean isVisible = false;
	private boolean isPlayer = false;
	private boolean isBehind = false; // Vaut true si le personnage est derriere un mur ou meuble
	protected boolean isInvestigator = false;
	protected boolean isMurderer = false;
	private int dirX = 0;
	private int dirY = 0;

	protected float maxLinearSpeed;
	protected float maxLinearAcceleration;
	protected float maxAngularSpeed, maxAngularAcceleration;
	protected float boundingRadius;
	protected boolean tagged;
	public SteeringBehavior<Vector2> behavior;
	public SteeringAcceleration<Vector2> steeringOutput;

	private DialogueInteractiveBox inspectedClue; // l'indice en train d'être regardé

	private Texture AButton = new Texture("UIGame/AButton.png");
	private Texture EButton = new Texture("UIGame/EButton.png");
	private boolean canInteract = false;
	private boolean isInteract = false;
	private boolean canAccuse = false;
	private int nbAccusation = 0;
	private String suspect;

	private GameUI ui;
	
	private Game game;


	public Character(World world, String texturePath, int x, int y, String name, GameUI ui, Game game) {
		super(new Texture(texturePath));
		this.body = new Box2dFactory(world).createSpriteCollision(x, y, getTexture().getWidth() / 3, 2);
		this.interactiveBody = new Box2dFactory(world).createInteractiveCollision(x, y, 25, 25);
		this.setPosition(body.getPosition().x - this.getWidth() / 2, body.getPosition().y - this.getHeight() / 2);
		this.speed = 3000;
		this.name = name;
		this.ui = ui;
		this.game = game;

		maxLinearSpeed = 50;
		maxLinearAcceleration = 50;
		maxAngularSpeed = 50;
		maxAngularAcceleration = 50;
		boundingRadius = 1f;

		steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());

		tagged = false;

		this.body.getFixtureList().first().setUserData(this);
		this.interactiveBody.getFixtureList().first().setUserData(this);
		

	}

	public synchronized void update(float delta, Control control, Client client) {

		if (isPlayer && !isInteract) {
			move(control, client);
			interact(client);
		}

		updateBodies(delta);

		flipSprite();
	}

	private void move(Control control, Client client){
		if (control.down) {
			dirY = -1;
			dirX = 0;
		} else if (control.up) {
			dirY = 1;
			dirX = 0;
		} else if (control.right) {
			dirX = 1;
			dirY = 0;

		} else if (control.left) {
			dirX = -1;
			dirY = 0;
		} else {
			dirX = 0;
			dirY = 0;
		}

		// On envoie des packets que si le joueur bouge
		if (body.getLinearVelocity().x != 0 || body.getLinearVelocity().y != 0) {
			// Envoi du packet avec les déplacements du joueur
			Packet3Deplacement pos = new Packet3Deplacement();
			pos.dirX = dirX;
			pos.dirY = dirY;
			pos.clientName = this.name;
			client.sendUDP(pos);
		}
	}

	private void interact(Client client){
		// Si le joueur veut interagir avec le décor
		if (Gdx.input.isKeyJustPressed(Input.Keys.A) && canInteract && isInvestigator) {
			inspectedClue.setActiveClue(false);// on désactive l'indice
			// On désactive l'indice pour les autres joueurs
			Packet12DesactivationIndice disable = new Packet12DesactivationIndice();
			disable.idClue = inspectedClue.name;
			client.sendTCP(disable);
			ui.getFenetre().setVisible(true);
			canInteract = false;
			isInteract = true;
			ui.removeASuspect();

		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.A) && canInteract && isMurderer) {
			inspectedClue.setActiveClue(false);
			ui.getFenetre().setVisible(true);

		} else if (Gdx.input.isKeyJustPressed(Input.Keys.E) && canAccuse && nbAccusation < 1) {
			nbAccusation++;
			canAccuse = false;
			Packet9Accusation accusation = new Packet9Accusation();
			accusation.enqueteur = name;
			accusation.suspect = suspect;
			client.sendTCP(accusation);
		}
	}

	private void updateBodies(float delta){
		body.setLinearVelocity(dirX * speed * delta, dirY * speed * delta);
		interactiveBody.setTransform(body.getPosition().x, body.getPosition().y, (float) Math.PI / 4);
		setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 + 32);
	}

	/**
	 * Oriente le sprite en fonction de la direction de son mouvement
	 */
	private void flipSprite(){
		if (!(this instanceof CharacterWithTarget)) {
			if (dirX > 0) {
				this.setFlip(true, false);
			} else {
				this.setFlip(false, false);
			}
		}
	}


	public synchronized void render(SpriteBatch batch) {
		if (isVisible) {
			this.draw(batch);
		}

		if (isPlayer && canInteract) {
			batch.draw(AButton, getX() + getTexture().getWidth() / 2 - 10, getY() + getTexture().getHeight() + 20);
		} else if (isPlayer && canAccuse && nbAccusation < 1) {
			batch.draw(EButton, getX() + getTexture().getWidth() / 2 - 30, getY() + getTexture().getHeight() + 20);
		}

	}



	public synchronized void applySteering(float delta) {
		boolean anyAccelerations = false;

		if (!steeringOutput.linear.isZero()) {
			Vector2 force = steeringOutput.linear.scl(delta * 3600);
			body.applyForceToCenter(force, true);
			anyAccelerations = true;
		}

		if (steeringOutput.angular != 0) {
			body.applyTorque(steeringOutput.angular * delta, true);
			anyAccelerations = true;
		}

		if (anyAccelerations) {
			Vector2 velocity = body.getLinearVelocity();
			float currentSpeedSquare = velocity.len2();
			if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
				body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float) Math.sqrt(currentSpeedSquare)));
			}
		}
	}

	public Body getBody() {
		return body;
	}

	public synchronized void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void setIsPlayer(boolean isPlayer, final Client client) {
		/*Quand on devient joueur, on lance un Thread qui permet le synchronisation des
		 positions */
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					Packet6SynchroPosition syncPosition = new Packet6SynchroPosition();
					synchronized (this) {
						syncPosition.position = body.getPosition();
						syncPosition.clientName = name;
						client.sendTCP(syncPosition);
					}
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		this.isPlayer = isPlayer;
	}

	public synchronized void setDirX(int dirX) {
		this.dirX = dirX;
	}

	public synchronized void setDirY(int dirY) {
		this.dirY = dirY;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Character) {
			Character chara = (Character) obj;
			if (this.name.equals(chara.name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Vector2 getPosition() {
		return body.getPosition();
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}

	@Override
	public void setOrientation(float orientation) {
		body.getTransform().setRotation(orientation);
	}

	@Override
	public float vectorToAngle(Vector2 vector) {
		return (float) Math.atan2(-vector.x, vector.y);
	}

	@Override
	public Vector2 angleToVector(Vector2 outVector, float angle) {
		outVector.x = -(float) Math.sin(angle);
		outVector.y = (float) Math.cos(angle);
		return outVector;
	}

	@Override
	public Location<Vector2> newLocation() {

		return new SteerableAdapter<Vector2>();
	}

	@Override
	public float getZeroLinearSpeedThreshold() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setZeroLinearSpeedThreshold(float value) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getMaxLinearSpeed() {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;

	}
	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxLinearAcceleration = maxAngularAcceleration;

	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}

	public void setBehavior(SteeringBehavior<Vector2> behavior) {
		this.behavior = behavior;
	}

	public SteeringBehavior<Vector2> getBehavior() {
		return behavior;
	}

	public synchronized void setBehind(boolean isBehind) {
		this.isBehind = isBehind;
	}

	public SteeringAcceleration<Vector2> getSteeringOutput() {
		return steeringOutput;
	}

	public synchronized boolean isBehind(){return isBehind;}

	public synchronized void setCanInteract(boolean value){ canInteract = value;}

	public synchronized void setIsInteract(boolean value){ isInteract = value;}

	@Override
	public int compareTo(Character o) {
		return (int) -(body.getPosition().y - o.getBody().getPosition().y);
	}

	public synchronized boolean isInvestigator() {
		return isInvestigator;
	}

	public synchronized void setInvestigator(boolean isInvestigator) {
		this.isInvestigator = isInvestigator;
	}

	public synchronized boolean isMurderer() {
		return isMurderer;
	}

	public synchronized void setMurderer(boolean isMurderer) {
		this.isMurderer = isMurderer;
	}


	public synchronized DialogueInteractiveBox getInspectedClue() {
		return inspectedClue;
	}

	public synchronized void setInspectedClue(DialogueInteractiveBox inspectedClue) {
		this.inspectedClue = inspectedClue;
	}

	public synchronized void setCanAccuse(boolean value) {
		canAccuse = value;
	}

	public boolean isPlayer() {
		return isPlayer;
	}

	public synchronized void setSuspect(String suspect) {
		this.suspect = suspect;
	}
	
	public synchronized void setTexture(String filename) {
		setTexture(game.assetManager.get(filename));
		setSize(game.assetManager.get(filename).getWidth(),game.assetManager.get(filename).getHeight());
	}

}
