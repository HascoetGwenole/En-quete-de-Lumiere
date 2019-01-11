package enquetedelumiere.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import enquetedelumiere.client.GameClient;
import enquetedelumiere.game.Game;
import enquetedelumiere.listeners.ClientListener;
import enquetedelumiere.lvlManager.LvlManager;
import enquetedelumiere.packets.*;
import enquetedelumiere.pathfinding.Localisation;
import enquetedelumiere.server.GameServer;
import enquetedelumiere.sprite.AnimatedSprite;
import enquetedelumiere.sprite.Character;
import enquetedelumiere.sprite.CharacterWithTarget;
import enquetedelumiere.tools.*;

public class NetworkScreen implements Screen {

	private Game game;
	private OrthographicCamera camera;
	private Viewport viewport;

	private GameUI ui;

	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private int[] foreground = { 3, 4 };
	private int[] background = { 1, 2 };

	private World world;
	private Box2DDebugRenderer b2dRenderer;

	private Control control;

	private HashMap<String, Character> characters = new HashMap<String, Character>();

	private HashMap<String, Vector2> iaForces = new HashMap<String, Vector2>();

	private Character activePlayer;

	private GameClient client;
	private boolean isHost;
	public boolean gameOn = false;
	private GameServer server;

	private SoundManager soundManager;
	private ArrayList<AnimatedSprite> animatedSprites = new ArrayList<AnimatedSprite>();

	private ClueGenerator clueGenerator;


	public NetworkScreen(Game game, String username, String Ip, boolean isHost) throws IOException {
		// Relatif à l'affichage du jeu
		this.game = game;
		this.camera = new OrthographicCamera();
		this.camera.position.set(100, 0, 0);

		if(Game.resolutionX == 1920){
		    this.camera.zoom = .357f;
        }
		else {
		    this.camera.zoom = .5f;
        }

		this.viewport = new FitViewport(Game.resolutionX, Game.resolutionY, this.camera);
		this.isHost = isHost;

		ui = new GameUI(game.batch, this, game);

		// Gestion des entrées
		initInputControl();

		// Relatif à la physique du jeu
		initMapAndPhysic();

		// création du graphe
		LvlManager.loadLvl(map);

		// Creation de points d'interet
		Array<Localisation> pointsDInteret = new Array<Localisation>();
		pointsDInteret.add(new Localisation(new Vector2(200, 200)));

		for (MapObject object : map.getLayers().get("points_interets").getObjects()
				.getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			pointsDInteret.add(new Localisation(new Vector2(rect.getX(), rect.getY())));
		}

		// Positionnement des indices où générer des interactions sur ces points
		// d'interets
		clueGenerator = new ClueGenerator(world, pointsDInteret);

		initPlayers(pointsDInteret);

		// Pour gérer les sons
		soundManager = new SoundManager(characters, this);

		initNetwork(Ip, username);

		createAnimatedSprite();

	}

    /**
     * Les fonctions suivantes servent à initier les différents composants essentiels au jeu
     */
	private void initPlayers(Array<Localisation> pointsDInteret){
        // Création personnage
        Character player1 = new Character(world, "map/characters/detective.png", 52*16, (120-23)*16, "player0", ui, game);
        Character player2 = new Character(world, "map/characters/jake.png", 16*95, 16*(120-23), "player1", ui, game);
        Character player3 = new Character(world, "map/characters/marie.png", 90*16, (120-39)*8, "player2", ui, game);
        Character player4 = new Character(world, "map64x64/player4.png", 13*16, (120-109)*16, "player3", ui, game);
        CharacterWithTarget player4shiney = new CharacterWithTarget(world, "map64x64/player4shiney.png", 101*16, (120-106)*16, "player4",
                pointsDInteret, iaForces, game);
        CharacterWithTarget player5shiney = new CharacterWithTarget(world, "map64x64/player3shiney.png", 84*16, (120-105)*16, "player5",
                pointsDInteret, iaForces, game);
        CharacterWithTarget player6shiney = new CharacterWithTarget(world, "map64x64/player2shiney.png", 62*16, (201-105)*16, "player6",
                pointsDInteret, iaForces, game);
        CharacterWithTarget player7shiney = new CharacterWithTarget(world, "map64x64/playershiney.png", 20*16, (120-107)*16, "player7",
                pointsDInteret, iaForces, game);

        this.characters.put(player1.name, player1);
        this.characters.put(player2.name, player2);
        this.characters.put(player3.name, player3);
        this.characters.put(player4.name, player4);
        this.characters.put(player4shiney.name, player4shiney);
        this.characters.put(player5shiney.name,player5shiney);
        this.characters.put(player6shiney.name, player6shiney);
        this.characters.put(player7shiney.name, player7shiney);
    }

    private void initNetwork(String Ip, String username) throws IOException {
        // L'host ouvre le server
        if (isHost) {
            server = new GameServer();
            server.init();
        }

        // On ouvre le client
        client = new GameClient();
        client.init(Ip);

        // On ajoute le Listener
        ClientListener clientListener = new ClientListener(activePlayer, characters, client, this, world, clueGenerator);
        client.addListener(clientListener);

        // On rentre un username qu'on envoie au serveur
        Packet1Connect newClient = new Packet1Connect();
        newClient.username = username;
        client.sendTCP(newClient);

        //On initiliase les IA
        if (isHost) {
            characters.values()
                    .stream()
                    .filter(c -> c instanceof  CharacterWithTarget)
                    .forEach(c -> {
                        Packet1Connect newIA = new Packet1Connect();
                        newIA.username = username + c.name;
                        client.sendTCP(newIA);
                    });
        }
    }

    private void createAnimatedSprite(){
        //Pour gérer les sprites avec animation
        AnimatedSprite piano = new AnimatedSprite(world, "pianoAntoine.png", 15*16-4, (120-102)*16-15, 16, 1,0.2f , true);
        AnimatedSprite gramophone = new AnimatedSprite(world, "gramophone.png", 113*16, (120-35)*16, 5, 1,0.09f , true);
        animatedSprites.add(piano);
        animatedSprites.add(gramophone);
    }

    private void initInputControl(){
        control = new Control();
        InputMultiplexer mutiplexer = new InputMultiplexer();
        mutiplexer.addProcessor(control);
        mutiplexer.addProcessor(ui.getStage());
        Gdx.input.setInputProcessor(mutiplexer);
    }

    private void initMapAndPhysic(){
        this.world = new World(new Vector2(0, 0), true);
        this.b2dRenderer = new Box2DDebugRenderer();

        // Création de la carte
        map = new TmxMapLoader().load("map/map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        new Box2dFactory(world).createWorldCollision(map);

        // gestion des collisions
        world.setContactListener(new WorldContactListener(getActivePlayer(), ui));

        /*Création des boites d'interactions qui permettent de savoir si le personnage
		 est derriere un mur */
        for (MapObject object : map.getLayers().get("behind").getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new InteractiveBox(world, rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2,
                    rect.getWidth(), rect.getHeight(), CollisionType.DialogueCollision);
        }
    }


	@Override
	public void show() {

	}

    /**
     * La fonction centralise la mise à jour de toutes les données du jeu à chaque frame
     * @param delta correspond au temps entre deux frames
     */

	public synchronized void update(float delta) {

        // Mise à jour de la physique du monde
	    world.step(1 / 60f, 6, 2);

		if(gameOn){
		    // la caméra suit le personnage
			camera.position.set(activePlayer.getPosition(), 0);
			camera.update();

            // Mise à jour de la position des joueurs
            for (Character chara : characters.values()) {
                chara.update(delta, control, client);
            }
        }
		renderer.setView(camera.combined, camera.position.x - viewport.getWorldWidth()/2, camera.position.y - viewport.getWorldHeight()/2, viewport.getWorldWidth(), viewport.getWorldHeight());

		ui.update(delta);
        soundManager.update();
	}

    /**
     * La fonction se charge d'afficher tous les éléments graphique à chaque frame
     * @param delta
     */
	@Override
	public void render(float delta) {
		update(delta);

		// Nettoie la fenêtre
		Gdx.gl.glClearColor(0, 0, 0, 1); // Nettoyage de la fenêtre
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.render(); // Rendu de la carte

		game.batch.setProjectionMatrix(camera.combined); // Ne rendre que ce que la caméra voit

		if (!gameOn) renderer.render(); // Rendu de la carte

        if(gameOn){
            renderPlayers(delta);

            if(isHost){
                // On communique la position des IA  à tous les clients
                Packet7DeplacementIAMultiplexage iaPositions = new Packet7DeplacementIAMultiplexage();
                iaPositions.clientName = activePlayer.name;
                iaPositions.aiForces = iaForces;
                client.sendUDP(iaPositions);
            }
        }
        game.batch.begin();
		clueGenerator.render(delta, game.batch);
		game.batch.end();

		//b2dRenderer.render(world, camera.combined); // rendu des boites de collision


		// On affiche l'UI
		game.batch.setProjectionMatrix(ui.getStage().getCamera().combined);
		ui.getStage().act(delta);
		ui.getStage().draw();
	}

	private void renderPlayers(float delta){
        /* On affiche tous les personnages dans un ordre correspondant à leur profondeur dans la carte.
        Ainsi un personnage devant un autre sera affiché au dessus de lui
         */
        renderer.render(background);
        game.batch.begin();
        characters.values().stream()
                .filter(c -> c.isBehind())
                .sorted()
                .forEach(c -> c.render(game.batch));
        game.batch.end();
        renderer.render(foreground);
        game.batch.begin();
        for(AnimatedSprite sprite : animatedSprites){
            sprite.render(delta, game.batch);
        }
        characters.values().stream()
                .filter(c -> !c.isBehind())
                .sorted()
                .forEach(c -> c.render(game.batch));
        game.batch.end();
    }

	@Override
	public void resize(int width, int height) {
		this.ui.getStage().getViewport().update(width, height);
		this.viewport.update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		client.stop();
		if(isHost) server.stop();
	}

    public void setActivePlayer(Character activePlayer) {
        this.activePlayer = activePlayer;
    }
    public Character getActivePlayer() {return activePlayer;}
    public GameUI getGameUI(){return ui;}

	public boolean isHost() {
		return isHost;
	}

	public boolean isGameOn() {
		return gameOn;
	}

	public Client getClient(){
    	return client;
	}
}
