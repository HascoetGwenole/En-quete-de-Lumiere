package enquetedelumiere.packets;

/**
 * Ce paquet permet de signaler à tous les joueurs que la partie peut commencer (car tous les participants sont bien
 * connectés.
 * Si c'est le serveur qui recoit ce paquet, c'est au contraire pour signifier qu'un joueur a gagné et que donc
 * la partie est terminée
 */

public class Packet8GameOn extends Packet{
	
	public boolean canStart = true;
}
