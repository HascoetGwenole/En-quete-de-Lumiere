package enquetedelumiere.pathfinding;

import java.util.Random;

import com.badlogic.gdx.utils.Array;

public class LandMarks {
	Array<Localisation> pointsDInteret = new Array<Localisation>();

	public LandMarks(Array<Localisation> pointsDInteret) {
		this.pointsDInteret = pointsDInteret;
	}

	public void addLanmarks(Array<Localisation> pointsDInteret) {
		this.pointsDInteret.addAll(pointsDInteret);
	}
	public Localisation randomizeALandMark(){ //on tire au hasard un point d'interet du tableau
		Random generator = new Random();
		int i = generator.nextInt(pointsDInteret.size);
		System.out.println(i);
		Localisation LandMark = pointsDInteret.get(i);
		return LandMark;
	}
	


}
