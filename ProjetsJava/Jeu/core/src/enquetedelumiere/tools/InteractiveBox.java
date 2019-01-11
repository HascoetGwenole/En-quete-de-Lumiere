package enquetedelumiere.tools;

import com.badlogic.gdx.physics.box2d.*;

public class InteractiveBox {

    private Body body;
    private CollisionType type;
 
    
    
    public InteractiveBox(World world, float x, float y, float width, float height, CollisionType type){
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.KinematicBody;
        bDef.position.set(x, y);
        body = world.createBody(bDef);
        FixtureDef fDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);
        fDef.shape = shape;
        fDef.isSensor = true;
        Fixture fixture = body.createFixture(fDef);

        this.type = type;
       
        fixture.setUserData(this);
    }

    public CollisionType getType(){
        return type;
    }


 	
    
    
}
