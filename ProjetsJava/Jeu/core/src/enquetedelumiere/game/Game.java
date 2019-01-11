package enquetedelumiere.game;


import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import enquetedelumiere.screens.*;

public class Game extends com.badlogic.gdx.Game {
	
	public SpriteBatch batch;
	public HashMap<String, Texture> assetManager = new HashMap<String, Texture>();
	public HashMap<String, SpriteDrawable> icones = new HashMap<String, SpriteDrawable>();
	public static int resolutionX=1920;
	public static int resolutionY=1020;

	@Override
	public void create () {
		batch = new SpriteBatch();

		/*On cherche les assets graphique au démarrage du jeu, pour ne pas avoir à les charger
        quand on lance une partie et avoir un accès partout dans l'aplication*/
		assetManager.put("raven",new Texture ("map/characters/Raven_.png"));
		assetManager.put("marie",new Texture ("map/characters/marie.png"));
		assetManager.put("jake",new Texture ("map/characters/jake.png"));
		assetManager.put("playershiney",new Texture ("map64x64/playershiney.png"));
		assetManager.put("serge",new Texture ("map/characters/serge_policier.png"));
		assetManager.put("blondie",new Texture ("map/characters/Blondie.png"));
		assetManager.put("detective",new Texture ("map/characters/detective.png"));
		assetManager.put("detectiveconan",new Texture ("map/characters/Montreman.png"));

		icones.put("ravenPP",new SpriteDrawable(new Sprite(new Texture ("map/characters/Raven_PP.png"))));
		icones.put("mariePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/mariePP.png"))));
		icones.put("jakePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/jakePP.png"))));
		icones.put("playershineyPP",new SpriteDrawable(new Sprite(new Texture ("map64x64/playershineyPP.png"))));
		icones.put("sergePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/serge_policierPP.png"))));
		icones.put("blondiePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/BlondiePP.png"))));
		icones.put("detectivePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/detectivePP.png"))));
		icones.put("detectiveconanPP",new SpriteDrawable(new Sprite(new Texture ("map/characters/MontremanPP.png"))));

		setScreen(new MenuScreen2(this));
	
	}

	@Override
	public void render () {
		super.render();		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
