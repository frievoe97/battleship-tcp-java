package battleship;

/**
 * The Battleship interface specifies all methods that are important for the logic of
 * the game. The interface is implemented by the BattleshipImpl class, which implements
 * the game logic, and by the BattleshipProtocolEngine class, which implements the TCP
 * communication protocol.
 * <p>
 * This class is inspired by @author thsc. The class has been modified to fit this game.
 * This is the <a href="https://github.com/thsc42/TicTacToe">link</a> to his repository.
 *
 * @author friedrichvoelkers
 */
public interface Battleship {

    int DEFAULT_PORT = 3000;
    String LOCALHOST = "localhost";

    /**
     * This method is used to shoot at the opponent's board.
     *
     * @param coordinate  Sets the coordinates
     * @param isYourBoard Specifies whether this is the own board or the generic board.
     *                    This is important because this method is also called when you
     *                    get the answer from the opponent about what is on this board.
     * @throws GameException Throws an error if the coordinates are outside the playing
     *                       board.
     */
    void doFire(Coordinate coordinate, boolean isYourBoard) throws GameException;

    /**
     * This method is used to place a ship on your own board.
     *
     * @param startCoordinate Sets the start coordinates.
     * @param endCoordinate   Sets the end coordinates.
     * @throws GameException Throws an error if the coordinates are off-board, there is
     *                       already a ship there, the maximum number of ships has been
     *                       reached, or if it is right next to another ship.
     */
    void doSetShips(Coordinate startCoordinate, Coordinate endCoordinate) throws GameException;

    /**
     * This method updates the own or opponent's GameStatus.
     *
     * @param gameStatus            New GameStatus
     * @param isYourStatus          Whether it is your own GameStatus.
     * @param bothPlayerSetAllShips Whether both GameStatus should be
     *                              changed. This is important for the
     *                              point when both players have set
     *                              all ships.
     * @throws GameException Throws an error when there is an error with the protocol engine.
     */
    void doChangeGameStatus(GameStatus gameStatus, boolean isYourStatus, boolean bothPlayerSetAllShips) throws GameException;

    /**
     * This method changes the status of a field after you attack the opponent and know what is on the field.
     *
     * @param battleshipFieldStatus The status of the field
     * @param coordinate            The coordinates of the field
     * @throws GameException Throws an error if the sent coordinates are not correct.
     */
    void doSendResult(BattleshipFieldStatus battleshipFieldStatus, Coordinate coordinate) throws GameException;
}