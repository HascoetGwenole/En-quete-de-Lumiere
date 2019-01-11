package enquetedelumiere.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import enquetedelumiere.game.Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "En'quête de Lumière";
		config.height = 1080;
		config.width = 1920;
		config.addIcon("logo.png", FileType.Internal);
		new LwjglApplication(new Game(), config);
	}
}
