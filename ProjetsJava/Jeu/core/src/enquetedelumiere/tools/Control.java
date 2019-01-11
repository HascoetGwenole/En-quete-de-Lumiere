package enquetedelumiere.tools;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

public class Control extends InputAdapter implements InputProcessor{
	
	public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;

	@Override
	public boolean keyDown(int keycode) {
		 switch (keycode) {
         case Keys.DOWN:
             down = true;
             break;
         case Keys.UP:
             up = true;
             break;
         case Keys.LEFT:
             left = true;
             break;
         case Keys.RIGHT:
             right = true;
             break;
         case Keys.Z:
             up = true;
             break;
         case Keys.Q:
             left = true;
             break;
         case Keys.S:
             down = true;
             break;
         case Keys.D:
             right = true;
             break;
         default:
             break;
     }
		 return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
        case Keys.DOWN:
            down = false;
            break;
        case Keys.UP:
            up = false;
            break;
        case Keys.LEFT:
            left = false;
            break;
        case Keys.RIGHT:
            right = false;
            break;
        case Keys.Z:
            up = false;
            break;
        case Keys.Q:
            left = false;
            break;
        case Keys.S:
            down = false;
            break;
        case Keys.D:
            right = false;
            break;
        default:
            break;
		}
		
		return false;
	}


	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
