package enquetedelumiere.packets;

/**
 * Ce paquet permet d'envoyer le resultat d'une accusation
 */

public class Packet10ResultatAccusation extends Packet{

	public boolean reussi;
	public String enqueteur;
	public String pseudoEnqueteur;
	public String suspect;
	public String pseudoSuspect;
}
