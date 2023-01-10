package battleship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents a Battleship Board. It contains a two-dimensional
 * array that stores the status of the field for each coordinate. It also
 * stores all the ships on the board, the number of ships per length and
 * the size of the board. When initializing the own board, each field is
 * assigned the status "BattleshipFieldStatus.EMPTY_NO_SHOOT" and when
 * initializing the opponent's board, each field is assigned the status
 * "BattleshipFieldStatus.UNKNOWN".
 *
 * @author friedrichvoelkers
 */
public final class BattleshipBoard {

    private final BattleshipFieldStatus[][] battleshipBoard;
    private final List<Ship> allShips = new ArrayList<>();
    private final HashMap<Integer, Integer> numberOfShipsPerLength = new HashMap<>();
    private final int xLength = BattleshipEngine.STANDARD_DIMENSION;
    private final int yLength = BattleshipEngine.STANDARD_DIMENSION;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           constructor                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BattleshipBoard(boolean isYourBoard) {
        this.battleshipBoard = new BattleshipFieldStatus[this.xLength][this.yLength];
        if (isYourBoard) createYourBoard();
        else createEnemiesBoard();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             UI-method                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public char printField(Coordinate coordinate) {

        switch (this.getFieldStatus(coordinate)) {
            case EMPTY_NO_SHOOT -> {
                return 'o';
            }
            case EMPTY_SHOOT -> {
                return '-';
            }
            case SHIP_NO_SHOOT -> {
                return 's';
            }
            case SHIP_SHOOT -> {
                return 'x';
            }
            default -> {
                return '?';
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          logic methods                                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void createEnemiesBoard() {
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {
                this.battleshipBoard[i][j] = BattleshipFieldStatus.UNKNOWN;
            }
        }
    }

    void createYourBoard() {
        for (int i = 0; i < xLength; i++) {
            for (int j = 0; j < yLength; j++) {
                this.battleshipBoard[i][j] = BattleshipFieldStatus.EMPTY_NO_SHOOT;
            }
        }
    }

    public void addShip(Ship ship) throws GameException {
        if (!shipAlreadyExists(ship)) {
            allShips.add(ship);
            if (numberOfShipsPerLength.containsKey(ship.getLength()))
                numberOfShipsPerLength.put(ship.getLength(), numberOfShipsPerLength.get(ship.getLength()) + 1);
            else numberOfShipsPerLength.put(ship.getLength(), 1);
            for (Coordinate coordinate : ship.getCoordinates()) {
                battleshipBoard[coordinate.getxCoordinate()][coordinate.getyCoordinate()] = BattleshipFieldStatus.SHIP_NO_SHOOT;
            }
        } else throw new GameException("This ship already exists.");
    }

    public boolean shipAlreadyExists(Ship shipParameter) {
        for (Ship ship : allShips) {
            for (Coordinate coordinate : ship.getCoordinates()) {
                for (Coordinate shipParameterCoordinate : shipParameter.getCoordinates()) {
                    if (shipParameterCoordinate.compareTo(coordinate) == 0) return true;
                }
            }
        }
        return false;
    }

    public Ship getShipOnACoordinate(Coordinate coordinateParameter) throws GameException {
        for (Ship ship : allShips) {
            for (Coordinate coordinate : ship.getCoordinates()) {
                if (coordinateParameter.compareTo(coordinate) == 0) return ship;
            }
        }
        throw new GameException("No ship was found at these coordinates.");
    }

    public boolean checkIfAllShipsAreShoot() {
        for (Ship ship : allShips) {
            for (Coordinate coordinate : ship.getCoordinates()) {
                if (coordinate.getBattleshipFieldStatus() != BattleshipFieldStatus.SHIP_SHOOT) return false;
            }
        }
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         Getter and Setter                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BattleshipFieldStatus getFieldStatus(Coordinate coordinate) {
        return this.battleshipBoard[coordinate.getxCoordinate()][coordinate.getyCoordinate()];
    }

    public void setFieldStatus(BattleshipFieldStatus battleshipFieldStatus, Coordinate coordinate) {
        battleshipBoard[coordinate.getxCoordinate()][coordinate.getyCoordinate()] = battleshipFieldStatus;
    }

    public int getNumberOfShips() {
        return allShips.size();
    }

    public int getNumberOfShipsPerLength(int length) {
        return this.numberOfShipsPerLength.getOrDefault(length, 0);
    }
}
