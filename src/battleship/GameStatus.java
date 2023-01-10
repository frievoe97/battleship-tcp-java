package battleship;

/**
 * The GameStatus enumeration is used to describe the
 * status of the game.
 *
 * @author friedrichvoelkers
 */
public enum GameStatus {
    ON_CREATE,          // The game was created and you can set ships
    All_SHIPS_ARE_SET,  // You set all of your ships
    PLAYING_YOUR_TURN,  // It's your turn
    PLAYING_ENEMY_TURN, // It's your enemies turn
    YOU_WON,            // You won the game
    YOU_LOSE,           // You lose the game
    FINISH              // The game is over
}
