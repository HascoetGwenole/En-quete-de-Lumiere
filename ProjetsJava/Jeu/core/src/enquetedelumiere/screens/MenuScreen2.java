package enquetedelumiere.screens;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import enquetedelumiere.game.Game;

public class MenuScreen2 implements Screen{

	private Stage stage;
	private Table table;
	private Game game;
	private Texture imageMenu;
	public MenuScreen2(final Game game) {
		OrthographicCamera camera = new OrthographicCamera();
		camera.zoom = 0.5f;
		this.game = game;
		this.stage = new Stage(new FitViewport(1920, 1080,camera));
		Gdx.input.setInputProcessor(stage);
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		Skin skin = new Skin(Gdx.files.internal("UIMenu/pixthulhu/skin/pixthulhu-ui.json"));
		Label nameLabel = new Label("Pseudo:", skin);
	    final TextField nameText = new TextField("", skin);
	    Label addressLabel = new Label("Serveur IP:", skin);
	    final TextField addressText = new TextField("127.0.0.1", skin);
	    SelectBox<String> resolution = new SelectBox<String>(skin);
	    Label BasLabel = new Label("      ", skin);
	    imageMenu = new Texture(Gdx.files.internal("UIMenu/imageMenu.png"));
		
		

	    Texture logo = new Texture("logo.png");
	    Image menuImage = new Image(logo);
	    menuImage.scaleBy(0.9f);
	    table.add(menuImage);
	    table.row();
	    table.add(nameLabel);
	    table.add(nameText).width(300);
	    TextButton serverButton = new TextButton("Lancer le serveur", skin);
	    table.add(serverButton).bottom();
	    table.row();
	    table.add(addressLabel);
	    table.add(addressText).width(300);
		TextButton button1 = new TextButton("Jouer", skin);
	    table.add(button1).bottom();
	    table.row();
	    table.add(resolution).colspan(50);
	    Array<String> resolutionArray = new Array<String>(2);
	    resolutionArray.add("1920*1020");
	    resolutionArray.add("1366*768");
	    resolution.setItems(resolutionArray);
	    table.row();
	    table.add(BasLabel).height(100);
	    
	    
	    
	    
	    final Dialog erreurnomvide = new Dialog("Erreur", skin);
	    erreurnomvide.button("Le pseudo ne peut etre vide");
		
	    
	    //Bouton Jouer
	    button1.addListener(new ChangeListener() {
	    	public void changed (ChangeEvent event, Actor actor) {
	    	    if (resolution.getSelected().equals("1920*1020")){
	    	    	Game.resolutionX=1920;
	    	    	Game.resolutionY=1020;
	    	    }
	    	    else if (resolution.getSelected().equals("1366*768")) {
	    	    	Game.resolutionX=1366;
	    	    	Game.resolutionY=768;
	    	    }
	    	    
	    	    
	    		   if (nameText.getText().equals("")) {
	    		    	table.add(erreurnomvide);
	    		    	erreurnomvide.show(stage);
	    		    }
	    		   else {
	    			   try {
	    				   game.setScreen(new NetworkScreen(game, nameText.getText(), addressText.getText(),false));
	    			   } catch (IOException e) {
	    				   // TODO Auto-generated catch block
	    				   e.printStackTrace();
	    			   }
	    		   }
	    		   }
	    	});
	    
	    //Bouton Lancer le serveur
	    serverButton.addListener(new ChangeListener() {
	    	public void changed (ChangeEvent event, Actor actor) {
	    	    if (resolution.getSelected().equals("1920*1020")){
	    	    	Game.resolutionX=1920;
	    	    	Game.resolutionY=1020;
	    	    }
	    	    else if (resolution.getSelected().equals("1366*768")) {
	    	    	Game.resolutionX=1366;
	    	    	Game.resolutionY=768;
	    	    }
	    	    
	    	    
	    		 if (nameText.getText().equals("")) {	    		    	
	    		    	erreurnomvide.show(stage);
	    		    }
	    		   else {
	    		try {
					game.setScreen(new NetworkScreen(game, nameText.getText(), addressText.getText(),true));
				} catch (IOException e) {
					e.printStackTrace();
				}
		   	}
	    	}
	    });
	    
	    // Liste resolution
	    if (resolution.getSelected().equals("1920*1020")){
	    	Game.resolutionX=1920;
	    	Game.resolutionY=1020;
	    }
	    else if (resolution.getSelected().equals("1366*768")) {
	    	Game.resolutionX=1366;
	    	Game.resolutionY=768;
	    }

	}
	
	
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0.3f, 0.6f, 1);

		stage.act(delta);
		game.batch.setProjectionMatrix(table.getStage().getCamera().combined);
		game.batch.begin();
		game.batch.draw(imageMenu,500,200,1000,640);
		game.batch.end();
		stage.draw();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		stage.dispose();
		
	}

}
