package network;

/**
 * This class is from @author thsc. A link to his repository
 * is in the Battleship interface. The class slightly modified
 * and reduced to the most necessary.
 *
 * @author thsc
 */
public interface GameSessionEstablishedListener {
    void gameSessionEstablished(boolean coinToss, String partnerName);
}
