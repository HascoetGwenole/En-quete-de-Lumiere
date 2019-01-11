package enquetedelumiere.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import enquetedelumiere.game.Game;
import enquetedelumiere.packets.Packet666SynchroTimer;
import enquetedelumiere.packets.Packet8GameOn;
import enquetedelumiere.sprite.Character;
import enquetedelumiere.tools.SuspectBarButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Cette classe gère toutes l'interface graphique du jeu
 *
 */

public class GameUI {

	private Stage stage;
	private Skin skin;

	private Label descriptionText;
	private Table table0;
	private Table table1;
	private Window fenetre;

	private Label textTimer;
	public  float timer = 60 * 3;

	private Label textFPS;
	private Label waitingText;

	private Label accusationText;
	private TextButton endButton;

	private NetworkScreen screen;
	private HashMap<String, SuspectBarButton> buttonBar = new HashMap<String, SuspectBarButton>();

	private SpriteDrawable drawableUp;
	private SpriteDrawable drawableChecked;
	private HashMap<String, SpriteDrawable> icones = new HashMap<String, SpriteDrawable>();

	private Label roleLabel;
	private Label missionLabel;


	public GameUI(SpriteBatch batch, NetworkScreen screen, Game game) {
		this.screen = screen;
		stage = new Stage(new FitViewport(Game.resolutionX, Game.resolutionY), batch);

		skin = new Skin(Gdx.files.internal("UIGame/uiskin.json"));
		skin.getFont("default-font").getData().setScale(1.5f, 1.5f);

		Texture character = new Texture("UIGame/character.png");
		Texture suspected = new Texture("UIGame/suspected.png");
		drawableUp = new SpriteDrawable(new Sprite(character));
		drawableChecked = new SpriteDrawable(new Sprite(suspected));

		// La table générale
		Table table = new Table();

		// La table qui permet d'afficher le dialogue d'interaction
		table0 = new Table();
		fenetre = new Window("Interaction", skin);
		descriptionText = new Label("Je suis un artiste", skin);

		TextButton closeButtonInvesigator = new TextButton("Regarder", skin);
		closeButtonInvesigator.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				fenetre.setVisible(false);
				screen.getActivePlayer().setIsInteract(false);
			}

		});


		table0.add(descriptionText).center();
		table0.row();
		table0.add(closeButtonInvesigator).bottom().right();
		fenetre.setVisible(false);
		fenetre.add(table0);

		Table affichageHaut = new Table();
		textTimer = new Label("Compte a rebours", skin);
		affichageHaut.add(textTimer).spaceRight(300);
		affichageHaut.add(fenetre);

		// Le texte au centre de l'écran
		// La fenetre d'attente de jeu
		waitingText = new Label("En attente de connexion des autres personnages ...", skin);

		// La fenêtre des accusation
		accusationText = new Label("J'accuse", skin);
		accusationText.setVisible(false);

		// le bouton final pour revenir au menu
		endButton = new TextButton("Retourner au menu", skin);
		endButton.setVisible(false);
		endButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				screen.dispose();
				game.setScreen(new MenuScreen2(game));
			}

		});

		// La table qui permet d'afficher la bar des personnages
		table1 = new Table();

		Table tableBas = new Table();

		roleLabel = new Label("Role : ...", skin);
		missionLabel = new Label("Ta mission est ...", skin);
		textFPS = new Label("", skin);
		tableBas.add(roleLabel).left();
		tableBas.row();
		tableBas.add(missionLabel).left();
		tableBas.row();
		tableBas.add(table1);
		tableBas.add(textFPS).left();

		table.add(affichageHaut);
		table.row();
		table.add(waitingText).center().expand();
		table.row();
		table.add(accusationText);
		table.row();
		table.add(endButton);
		table.row();
		table.add(tableBas).expand().center().bottom();

		table.setFillParent(true);
		//table.debugAll();
		stage.addActor(table);

		// Chargement de tous les icones
		icones.put("ravenPP",new SpriteDrawable(new Sprite(new Texture ("map/characters/Raven_PP.png"))));
		icones.put("mariePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/mariePP.png"))));
		icones.put("jakePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/jakePP.png"))));
		icones.put("playershineyPP",new SpriteDrawable(new Sprite(new Texture ("map64x64/playershineyPP.png"))));
		icones.put("sergePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/serge_policierPP.png"))));
		icones.put("blondiePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/BlondiePP.png"))));
		icones.put("detectivePP",new SpriteDrawable(new Sprite(new Texture ("map/characters/detectivePP.png"))));
		icones.put("detectiveconanPP",new SpriteDrawable(new Sprite(new Texture ("map/characters/MontremanPP.png"))));
		System.out.println(icones.size());
	}

	public void update(float delta) {
		textFPS.setText(Integer.toString(Gdx.graphics.getFramesPerSecond()) + " FPS");
		if (screen.isGameOn()) {
			timer = timer - delta;
			textTimer.setText(" Temps avant que le meutrier ne s'echappe " + Float.toString(Math.round(timer)) + "s");

			if (screen.isHost()) {
				Packet666SynchroTimer syncTimer = new Packet666SynchroTimer();
				syncTimer.timer = timer;
				syncTimer.clientName = screen.getActivePlayer().name;
				screen.getClient().sendUDP(syncTimer);

				if (timer <= 0) {
					Packet8GameOn endGame = new Packet8GameOn();
					endGame.canStart = false;
					screen.getClient().sendTCP(endGame);
				}
			}
		}
	}

	public Stage getStage() {
		return stage;
	}

	public Label getDescriptionText() {
		return descriptionText;
	}

	public Window getFenetre() {
		return fenetre;
	}

	public Label getWaintingText() {
		return waitingText;
	}

	public Label getAccusationText() {
		return accusationText;
	}

	public TextButton getEndButton() {
		return endButton;
	}

	public void initialisationBarraSuspect(HashMap<String, Character> characters, HashMap<String, String> skin, String meutrier) {
		//On initiliase déja le statut (enqueteur ou meurtrier) de chacun des personnages
		for(Character chara : characters.values()){
			if(chara.name.equals(meutrier)){
				chara.setMurderer(true);
				chara.setInvestigator(false);
			}else{
				chara.setMurderer(false);
				chara.setInvestigator(true);
			}
		}
		
		HashMap<String, String> skin1 = new HashMap<String, String>();
		for(Entry<String, String> entry : skin.entrySet()) {
			skin1.put(entry.getValue(), entry.getKey());
		}

		//On crée un barre des suspects temporaires qui contient tous les personnages
		HashMap<String, SuspectBarButton> buttonBar = new HashMap<>();

		SuspectBarButton button1 = new SuspectBarButton(icones.get("ravenPP"), drawableUp, drawableChecked,
				"raven", skin1, characters);
		buttonBar.put(button1.name, button1);
		SuspectBarButton button2 = new SuspectBarButton(icones.get("mariePP"), drawableUp, drawableChecked,
				"marie", skin1, characters);
		buttonBar.put(button2.name, button2);
		SuspectBarButton button3 = new SuspectBarButton(icones.get("jakePP"), drawableUp, drawableChecked,
				"jake", skin1, characters);
		buttonBar.put(button3.name, button3);
		SuspectBarButton button4 = new SuspectBarButton(icones.get("playershineyPP"), drawableUp, drawableChecked,
				"playershiney", skin1, characters);
		buttonBar.put(button4.name, button4);
		SuspectBarButton button5 = new SuspectBarButton(icones.get("blondiePP"), drawableUp, drawableChecked,
				"blondie", skin1, characters);
		buttonBar.put(button5.name, button5);
		SuspectBarButton button6 = new SuspectBarButton(icones.get("detectivePP"), drawableUp, drawableChecked,
				"detective", skin1, characters);
		buttonBar.put(button6.name, button6);
		SuspectBarButton button7 = new SuspectBarButton(icones.get("detectiveconanPP"), drawableUp, drawableChecked,
				"detectiveconan", skin1, characters);
		buttonBar.put(button7.name, button7);
		SuspectBarButton button8 = new SuspectBarButton(icones.get("sergePP"), drawableUp, drawableChecked,
				"serge", skin1, characters);
		buttonBar.put(button8.name, button8);

		// On supprime de cette barre, le personnage que le joueur incarne
		for (String charaName : buttonBar.keySet()) {
			if (!charaName.equals(screen.getActivePlayer().name)) {
				this.buttonBar.put(charaName, buttonBar.get(charaName));
			}
		}

		// On crée enfin la barre de suspects qui sera utilisée en jeu
		for (SuspectBarButton button : this.buttonBar.values()) {
			table1.add(button);
		}

		// On initilise le texte de mission des personnages
		if(screen.getActivePlayer().isMurderer()){
			roleLabel.setText("Role : Meurtrier");
			missionLabel.setText("Mission : cache tous les indices pour qu'on ne te trouve pas !");
		}else {
			roleLabel.setText("Role : Enqueteur");
			missionLabel.setText("Mission : cherche des indices pour demasquer le meurtrier parmi les invites");
		}
	}

	public void removeASuspect() { // cette fonction permet de supprimer aléatoirement un suspect (non meurtier) de la barre de recherche d'un enquêteur
		if (buttonBar.size() > 1) {
			Random generator = new Random();
			Object[] buttons = buttonBar.values().toArray();
			Object randomButton = buttons[generator.nextInt(buttons.length)];
			SuspectBarButton button = (SuspectBarButton) randomButton;
			while (button.getPersonnage().isMurderer()) {
				randomButton = buttons[generator.nextInt(buttons.length)];
				button = (SuspectBarButton) randomButton;
			}
			buttonBar.remove(button.name);
			table1.removeActor(button);
			
		}
	}
}
