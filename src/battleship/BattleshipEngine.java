package battleship;

import java.util.HashMap;

/**
 * This class contains the own and the opponent Battleship board and
 * takes care of the logic of the game, which is not declared in the
 * Battleship interface, because these methods are not implemented by
 * the protocol engine.
 *
 * @author friedrichvoelkers
 */
public final class BattleshipEngine {

    static final int STANDARD_DIMENSION = 10;

    private final BattleshipBoard yourBattleshipBoard;
    private final BattleshipBoard enemysBattleshipBoard;
    private final int width;
    private final int height;
    final HashMap<Integer, Integer> requiredNumberOfShipsPerLength = new HashMap<>() {{
        put(2, 4);
        put(3, 3);
        put(4, 2);
        put(5, 1);
    }};
    private GameStatus yourGameStatus;
    private GameStatus enemiesGameStatus;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           constructor                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BattleshipEngine() {
        this.yourBattleshipBoard = new BattleshipBoard(true);
        this.enemysBattleshipBoard = new BattleshipBoard(false);
        this.width = STANDARD_DIMENSION;
        this.height = STANDARD_DIMENSION;
        this.yourGameStatus = GameStatus.ON_CREATE;
        this.enemiesGameStatus = GameStatus.ON_CREATE;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          logic methods                                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkIfShipIsOnTheBoard(Ship ship) {
        for (Coordinate coordinate : ship.getCoordinates()) {
            if (!this.checkIfCoordinateIsOnTheBoard(coordinate)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIfCoordinateIsOnTheBoard(Coordinate coordinate) {
        return !(coordinate.getxCoordinate() < 0 || coordinate.getxCoordinate() >= this.width || coordinate.getyCoordinate() < 0 || coordinate.getyCoordinate() >= this.height);
    }

    public boolean allShipsAreSet() {
        return this.yourBattleshipBoard.getNumberOfShipsPerLength(2) == this.requiredNumberOfShipsPerLength.get(2) &&
                this.yourBattleshipBoard.getNumberOfShipsPerLength(3) == this.requiredNumberOfShipsPerLength.get(3) &&
                this.yourBattleshipBoard.getNumberOfShipsPerLength(4) == this.requiredNumberOfShipsPerLength.get(4) &&
                this.yourBattleshipBoard.getNumberOfShipsPerLength(5) == this.requiredNumberOfShipsPerLength.get(5);
    }

    boolean checkIfAmountOfShipsOfThisLengthIsAlreadyChoosen(int length) throws GameException {
        return getRequiredNumberOfShipsPerLength(length) == yourBattleshipBoard.getNumberOfShipsPerLength(length);
    }

    boolean checkIfTheShipHasDirectNeighbours(Ship ship) {
        for (Coordinate coordinate : ship.getCoordinates()) {
            for (int i = coordinate.getxCoordinate() - 1; i <= coordinate.getxCoordinate() + 1; i++) {
                for (int j = coordinate.getyCoordinate() - 1; j <= coordinate.getyCoordinate() + 1; j++) {
                    try {
                        if (yourBattleshipBoard.getFieldStatus(new Coordinate(i, j)) == BattleshipFieldStatus.SHIP_NO_SHOOT)
                            return true;
                    } catch (GameException ignored) {
                    }
                }
            }
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         Getter and Setter                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BattleshipBoard getYourBattleshipBoard() {
        return yourBattleshipBoard;
    }

    BattleshipBoard getEnemysBattleshipBoard() {
        return enemysBattleshipBoard;
    }

    public GameStatus getYourGameStatus() {
        return yourGameStatus;
    }

    void setYourGameStatus(GameStatus yourGameStatus) {
        this.yourGameStatus = yourGameStatus;
    }

    GameStatus getEnemiesGameStatus() {
        return enemiesGameStatus;
    }

    void setEnemiesGameStatus(GameStatus enemiesGameStatus) {
        this.enemiesGameStatus = enemiesGameStatus;
    }

    int getRequiredNumberOfShipsPerLength(int length) throws GameException {
        int MIN_SHIP_LENGTH = 2;
        int MAX_SHIP_LENGTH = 5;
        if (length < MIN_SHIP_LENGTH || length > MAX_SHIP_LENGTH) throw new GameException("The ship length is not correct.");
        else return requiredNumberOfShipsPerLength.get(length);
    }

    int getSumOfRequiredNumberOfShips() {
        int result = 0;
        for (int number : requiredNumberOfShipsPerLength.values()) {
            result += number;
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             UI-method                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String printBoards() throws GameException {
        StringBuilder stringBuilder = new StringBuilder().append("Your board:\t\t\tEnemies board:\n");
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                stringBuilder.append(this.yourBattleshipBoard.printField(new Coordinate(i, j))).append(" ");
            }
            stringBuilder.append("\t\t");
            for (int j = 0; j < this.width; j++) {
                stringBuilder.append(this.enemysBattleshipBoard.printField(new Coordinate(i, j))).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }


}
