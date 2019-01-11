package enquetedelumiere.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

import enquetedelumiere.screens.GameUI;
import enquetedelumiere.sprite.Character;
import enquetedelumiere.sprite.CharacterWithTarget;

/*
 * Classe qui gère les actions à réaliser lorsqu'il y a des collisions
 * 
 */
public class WorldContactListener implements ContactListener {

	GameUI ui;

	public WorldContactListener(Character player, GameUI ui) {
		this.ui = ui;
	}

	@Override
	public void beginContact(Contact contact) {

		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();


		// Pour les investigateurs
		if (A.getUserData() instanceof Character && B.getUserData() instanceof DialogueInteractiveBox) {
			if (((Character) A.getUserData()).isInvestigator()) {
				if (((DialogueInteractiveBox) B.getUserData()).isActiveClue()) {
					//System.out.println("INTERACTION" + ((DialogueInteractiveBox) B.getUserData()).getContent());
					String newTxt = ((DialogueInteractiveBox) B.getUserData()).getContent();
					ui.getDescriptionText().setText(newTxt);
					String title = ((DialogueInteractiveBox) B.getUserData()).getTitle();
					ui.getFenetre().getTitleLabel().setText(title);
					((Character) A.getUserData()).setCanInteract(true);
					((Character) A.getUserData()).setInspectedClue((DialogueInteractiveBox) B.getUserData());
				}
			}

		} else if (B.getUserData() instanceof Character && A.getUserData() instanceof DialogueInteractiveBox) {
			if (((Character) B.getUserData()).isInvestigator()) {
				if (((DialogueInteractiveBox) A.getUserData()).isActiveClue()) {
					//System.out.println("INTERACTION" + ((DialogueInteractiveBox) A.getUserData()).getContent());
					String newTxt = ((DialogueInteractiveBox) A.getUserData()).getContent();
					ui.getDescriptionText().setText(newTxt);
					String title = ((DialogueInteractiveBox) A.getUserData()).getTitle();
					ui.getFenetre().getTitleLabel().setText(title);
					((Character) B.getUserData()).setCanInteract(true);
					((Character) B.getUserData()).setInspectedClue((DialogueInteractiveBox) A.getUserData());
				}
			}
		}
		// pour le meurtier
		if (A.getUserData() instanceof Character && B.getUserData() instanceof DialogueInteractiveBox) {
			if (((Character) A.getUserData()).isMurderer()) {
				if (((DialogueInteractiveBox) B.getUserData()).isActiveClue()) {
					//System.out.println("INTERACTION" + ((DialogueInteractiveBox) B.getUserData()).getContent());

					String newTxt = ((DialogueInteractiveBox) B.getUserData()).getContent();
					ui.getDescriptionText().setText(newTxt);
					String title = ((DialogueInteractiveBox) B.getUserData()).getTitle();
					ui.getFenetre().getTitleLabel().setText(title);
					((Character) A.getUserData()).setCanInteract(true);

					((Character) A.getUserData()).setInspectedClue((DialogueInteractiveBox) B.getUserData());// on désactive la possibilité de
																					// fouiller l'indice
				}
			}

		} else if (B.getUserData() instanceof Character && A.getUserData() instanceof DialogueInteractiveBox) {
			if (((Character) B.getUserData()).isMurderer()) {
				if (((DialogueInteractiveBox) A.getUserData()).isActiveClue()) {

					//System.out.println("INTERACTION" + ((DialogueInteractiveBox) A.getUserData()).getContent());
					String newTxt = ((DialogueInteractiveBox) A.getUserData()).getContent();
					ui.getDescriptionText().setText(newTxt);
					String title = ((DialogueInteractiveBox) A.getUserData()).getTitle();
					ui.getFenetre().getTitleLabel().setText(title);
					((Character) B.getUserData()).setCanInteract(true);
					((Character) B.getUserData()).setInspectedClue((DialogueInteractiveBox) A.getUserData());;
				}
			}
		}

		// pour gerer les collisions des IA.
		if (A.getUserData() instanceof CharacterWithTarget && B.getUserData() instanceof Character) {
				// si l'ia va se cogner à un autre personnage
				CharacterWithTarget ia = (CharacterWithTarget) A.getUserData();
				Character player = (Character) B.getUserData();
				ia.setCollision(true);
				CollisionAvoidance collisionAvoidance = new CollisionAvoidance(ia, player);
				collisionAvoidance.avoidCollision();
				
			}
		if (B.getUserData() instanceof CharacterWithTarget && A.getUserData() instanceof Character) {
			// si l'ia va se cogner à un autre personnage
			CharacterWithTarget ia = (CharacterWithTarget) B.getUserData();
			Character player = (Character) A.getUserData();
			ia.setCollision(true);
			CollisionAvoidance collisionAvoidance = new CollisionAvoidance(ia, player);
			collisionAvoidance.avoidCollision();
		}

		// Pour gérer la profondeur entre premier et second plan
		if (A.getUserData() instanceof Character){
			Character chara = (Character) A.getUserData();
			if (B.getUserData() instanceof InteractiveBox){
				InteractiveBox box = (InteractiveBox) B.getUserData();
                chara.setBehind(true);
			}
		}
		if (B.getUserData() instanceof Character){
			Character chara = (Character) B.getUserData();
			if (A.getUserData() instanceof InteractiveBox){
				InteractiveBox box = (InteractiveBox) A.getUserData();
                chara.setBehind(true);
			}
		}

		// Pour le mécanisme d'accusation
		if (A.getUserData() instanceof  Character){
		    Character chara = (Character) A.getUserData();
		    if (chara.isPlayer() && B.getUserData() instanceof Character){
		        Character charaB = (Character) B.getUserData();
		        if(! chara.equals(charaB)){
		            chara.setSuspect(charaB.name);
                    chara.setCanAccuse(true);
                }
            }
		}
        if (B.getUserData() instanceof  Character){
            Character chara = (Character) B.getUserData();
            if (chara.isPlayer() && A.getUserData() instanceof Character){
                Character charaA = (Character) A.getUserData();
                if(! chara.equals(charaA)){
                    chara.setSuspect(charaA.name);
                    chara.setCanAccuse(true);
                }
            }
        }

	}

	

	@Override
	public void endContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();

		if (A.getUserData() instanceof Character && B.getUserData() instanceof DialogueInteractiveBox) {
			//System.out.println("FIN INTERACTION");
			((Character) A.getUserData()).setCanInteract(false);
			((Character) A.getUserData()).setInspectedClue(null);
		} else if (B.getUserData() instanceof Character && A.getUserData() instanceof DialogueInteractiveBox) {
			//System.out.println("FIN INTERACTION");
			((Character) B.getUserData()).setCanInteract(false);
			((Character) B.getUserData()).setInspectedClue(null);
		}
		if (A.getUserData() instanceof CharacterWithTarget) {
			CharacterWithTarget ia = (CharacterWithTarget) A.getUserData();
			ia.setCollision(false);
			
		}
		if (B.getUserData() instanceof CharacterWithTarget) {
			CharacterWithTarget ia = (CharacterWithTarget) B.getUserData();
			ia.setCollision(false);
					
		}

        // Pour gérer la profondeur entre premier et second plan
		if(A.getUserData() instanceof Character && B.getUserData() instanceof  InteractiveBox){
			//Gdx.app.log("Collision", "Je suis devant");
			((Character) A.getUserData()).setBehind(false);
		}
		if(B.getUserData() instanceof Character && A.getUserData() instanceof  InteractiveBox){
			//Gdx.app.log("Collision", "Je suis devant");
			((Character) B.getUserData()).setBehind(false);
		}

        // Pour le mécanisme d'accusation
        if (A.getUserData() instanceof  Character){
            Character chara = (Character) A.getUserData();
            if (chara.isPlayer() && B.getUserData() instanceof Character){
                chara.setCanAccuse(false);
            }
        }
        if (B.getUserData() instanceof  Character){
            Character chara = (Character) B.getUserData();
            if (chara.isPlayer() && A.getUserData() instanceof Character){
                chara.setCanAccuse(false);
            }
        }

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
