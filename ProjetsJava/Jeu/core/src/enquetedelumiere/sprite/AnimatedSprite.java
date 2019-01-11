package enquetedelumiere.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;

import enquetedelumiere.tools.Box2dFactory;

public class AnimatedSprite{

    private Animation<TextureRegion> animation;

    private float x;
    private float y;

    private float stateTime = .0f;

    public AnimatedSprite(World world, String texturePath, float x, float y, int col, int row, float timeInterval, boolean hitBox){
        this.x = x;
        this.y = y;

        // On charge la texture
        Texture textureAnimation = new Texture("map/animatedSprite/" + texturePath);

        int width = textureAnimation.getWidth()/col;
        int height = textureAnimation.getHeight()/row;

        // On la découpe en le bon nombre de frame
        TextureRegion[][] tmp = TextureRegion.split(textureAnimation, width, height);
        TextureRegion[] animationFrame = new TextureRegion[col * row];
        int index = 0;
        for(int i = 0; i < row; i ++){
            for(int j = 0; j < col; j ++){
                animationFrame[index++] = tmp[i][j];
            }
        }

        animation = new Animation<TextureRegion>(timeInterval, animationFrame);

        if (hitBox) (new Box2dFactory(world)).createStaticBox(width, height, x, y);
    }

    public void render(float delta, SpriteBatch batch){
        stateTime += delta;
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y);
    }

}
