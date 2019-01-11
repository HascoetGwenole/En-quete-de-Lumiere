package enquetedelumiere.tools;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import enquetedelumiere.sprite.Character;
public class SuspectBarButton extends ImageButton{
	//Cette classe définit une instance de bouton de la barre de suspects.
	private Character personnage;
	
	public String name;
	public SuspectBarButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked, String spriteFile, HashMap<String, String> skin1, HashMap<String, Character> characters) {
		super(imageUp, imageDown, imageChecked);
		// TODO Auto-generated constructor stub
		
		//ce bouton est associé à un skin et à un personnage (Rq: les skins sont tirés aléatoirement, pour un personnage donné,
		//ils changent donc d'une partie à l'autre, c'est pourquoi ils est nécessaire de donner ces deux champs)
		name = skin1.get(spriteFile);	
		personnage = characters.get(name);
	}
	public Character getPersonnage() {
		return personnage;
	}
	public void setPersonnage(Character personnage) {
		this.personnage = personnage;
	}

}
