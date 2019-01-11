package enquetedelumiere.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import enquetedelumiere.pathfinding.Localisation;
import enquetedelumiere.sprite.AnimatedSprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


public class ClueGenerator {
	//classe qui permet de générer 10 indices aléatoirement placés sur la carte
	
	private HashMap<String, DialogueInteractiveBox> indices =new HashMap<String, DialogueInteractiveBox>();
	private List<AnimatedSprite> cluesParticules = new ArrayList<>();
	
	public ClueGenerator(World world, Array<Localisation> pointsDInteret) {
		
		
		
		int nbObjets = 10;
		boolean isUsed = false;	
			
			Random generator = new Random();
			Object[] positionsIndices = pointsDInteret.toArray();
			Array<Localisation> localisationsUtilisees = new Array<Localisation>();

			while(localisationsUtilisees.size < 10) {
			Object indice = positionsIndices[generator.nextInt(positionsIndices.length)];
			Localisation positionIndice = (Localisation) indice;
			if(!localisationsUtilisees.contains(positionIndice, false)) {
			localisationsUtilisees.add(positionIndice);
			String name = "Indice" + localisationsUtilisees.size;
			DialogueInteractiveBox clue = new DialogueInteractiveBox(world, positionIndice.getPosition().x, positionIndice.getPosition().y, 20, 20, CollisionType.DialogueCollision, "Il y a quelque chose d'interressant ", name) ;
			AnimatedSprite anim = new AnimatedSprite(world, "clue_particle.png", positionIndice.getPosition().x, positionIndice.getPosition().y, 7, 1, 0.25f, false);
			cluesParticules.add(anim);
			clue.name = name;
			indices.put(clue.name, clue);
				}
			}
		}
	

	public void render(float delta, SpriteBatch batch){
		for (AnimatedSprite anim : cluesParticules){
			anim.render(delta, batch);
		}
	}
	public HashMap<String, DialogueInteractiveBox> getPositionsIndices() {
		return indices;
	}
	

	
}
