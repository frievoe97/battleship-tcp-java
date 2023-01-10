package battleship;

/**
 * The class extends the Exception class and is used to throw errors.
 *
 * @author friedrichvoelkers
 */
public class GameException extends Exception {

    public GameException(String message) {
        super(message);
    }

}
