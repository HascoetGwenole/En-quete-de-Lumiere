package enquetedelumiere.packets;

/**
 * Ce paquet permet de dire à tous les clients que la partie est terminée car un des joueurs a gagné. On
 * transmet le nom du meurtrier pour pouvoir le communiquer sur l'écran de fin
 */

public class Packet11FinPartie extends Packet{

	public String skinMeurtrier;
	public String pseudoMeurtrier;
}
