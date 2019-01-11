package enquetedelumiere.tools;


import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;


/*
 * Classe relative à physique du jeu,
 * Elle sert à initialiser les collisions de la plupart des objets du monde
 */
public class Box2dFactory {
	
	private World world;

	public Box2dFactory(World world) {
		this.world = world;
	}
	
	public void createWorldCollision (TiledMap map) {
		Body body;
		BodyDef bodyDef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fixDef = new FixtureDef();
		
		
		for (MapObject object : map.getLayers().get("wallCollision").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			bodyDef.position.set(rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
			
			body = world.createBody(bodyDef);
			shape.setAsBox(rect.getWidth()/2, rect.getHeight()/2);
			fixDef.shape = shape;
			fixDef.density = 10000;
			body.createFixture(fixDef);
		
			
		}

		createInteractiveCollision();
	}
	
	public Body createSpriteCollision(float x, float y, int width, int height) {
		Body body;
		BodyDef bDef = new BodyDef();
		bDef.type = BodyType.DynamicBody;
		bDef.position.set(x, y);
		body = world.createBody(bDef);
		FixtureDef fDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);
		fDef.shape = shape;
		body.createFixture(fDef);
		
		return body;
	}

	public Body createInteractiveCollision(int x, int y, int radius) {
		Body body;
		BodyDef bDef = new BodyDef();
		bDef.type = BodyType.KinematicBody;
		bDef.position.set(x, y);
		body = world.createBody(bDef);
		FixtureDef fDef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		fDef.shape = shape;
		fDef.isSensor = true;
		body.createFixture(fDef);
		
		return body;
	}
	
	public Body createInteractiveCollision(int x, int y, int width, int height ) {
		Body body;
		BodyDef bDef = new BodyDef();
		bDef.type = BodyType.KinematicBody;
		bDef.position.set(x, y);
		body = world.createBody(bDef);
		FixtureDef fDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width, height);
				fDef.shape = shape;
		fDef.isSensor = true;
		body.createFixture(fDef);
		
		return body;
	}
	
	
	private void createInteractiveCollision(){
		/*new DialogueInteractiveBox(world, 4*64, 2*64, 10, 10,
                CollisionType.DialogueCollision, "Il y a quelque chose de bizarre au sol ...", "Parquet");
		 new DialogueInteractiveBox(world, 4*64, 5*64, 10, 10,
				CollisionType.DialogueCollision, "Il y a une fissure dans le mur on dirait ...", "Fissure");*/
	}

	public void createStaticBox(int width, int height, float x, float y){
		Body body;
		
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixDef = new FixtureDef();

		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(x + width/2, y + height/2);
		PolygonShape shape = new PolygonShape();

		body = world.createBody(bodyDef);
		shape.setAsBox(width/2, height/2);
		fixDef.shape = shape;
		body.createFixture(fixDef);
	}
}
