package battleship;

import java.util.ArrayList;

/**
 * The Ship class describes a ship. It stores the start and end coordinates
 * as a coordinates object and also all coordinates of the ship in an ArrayList.
 * In addition, the length is also stored as an integer value, so that it is
 * easier to check whether all ships of this length have already been set.
 * It also provides a static method to check if a ship has been completely sunk.
 *
 * @author friedrichvoelkers
 */
public final class Ship {

    private final ArrayList<Coordinate> coordinates = new ArrayList<>();
    private final Coordinate startCoordinate;
    private final Coordinate endCoordinate;
    private int length;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           constructor                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Ship(Coordinate startCoordinate, Coordinate endCoordinate) throws GameException {

        if (!(Coordinate.checkIfTwoCoordinatesAreHorizontal(startCoordinate, endCoordinate) || Coordinate.checkIfTwoCoordinatesAreVertical(startCoordinate, endCoordinate)))
            throw new GameException("The Ship is not vertical or horizontal");

        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        createCoordinatesAndLength(startCoordinate, endCoordinate);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          logic methods                                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void createCoordinatesAndLength(Coordinate startCoordinate, Coordinate endCoordinate) {
        if (Coordinate.checkIfTwoCoordinatesAreVertical(startCoordinate, endCoordinate)) {
            addCoordinatesToArray(startCoordinate.getyCoordinate(), true, startCoordinate.getxCoordinate(), endCoordinate.getxCoordinate());
            this.length = Math.abs(startCoordinate.getxCoordinate() - endCoordinate.getxCoordinate()) + 1;
        }

        if (Coordinate.checkIfTwoCoordinatesAreHorizontal(startCoordinate, endCoordinate)) {
            addCoordinatesToArray(startCoordinate.getxCoordinate(), false, startCoordinate.getyCoordinate(), endCoordinate.getyCoordinate());
            this.length = Math.abs(startCoordinate.getyCoordinate() - endCoordinate.getyCoordinate()) + 1;
        }
    }

    public static boolean checkIfShipIsCompleteyShoot(Ship ship) {
        for (Coordinate coordinate : ship.getCoordinates()) {
            if (coordinate.getBattleshipFieldStatus() != BattleshipFieldStatus.SHIP_SHOOT) return false;
        }
        return true;
    }

    public void gotShoot(Coordinate coordinateParameter) {
        for (Coordinate coordinate : coordinates) {
            if (coordinateParameter.compareTo(coordinate) == 0) {
                coordinate.setBattleshipFieldStatus(BattleshipFieldStatus.SHIP_SHOOT);
            }
        }
    }

    private void addCoordinatesToArray(int fixedRowOrColumn, boolean isVertical, int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            for (int i = startIndex; i <= endIndex; i++) {
                if (!isVertical) this.coordinates.add(new Coordinate(fixedRowOrColumn, i, BattleshipFieldStatus.SHIP_NO_SHOOT));
                else this.coordinates.add(new Coordinate(i, fixedRowOrColumn, BattleshipFieldStatus.SHIP_NO_SHOOT));
            }
        } else {
            for (int i = endIndex; i <= startIndex; i++) {
                if (!isVertical) this.coordinates.add(new Coordinate(fixedRowOrColumn, i, BattleshipFieldStatus.SHIP_NO_SHOOT));
                else this.coordinates.add(new Coordinate(i, fixedRowOrColumn, BattleshipFieldStatus.SHIP_NO_SHOOT));
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         Getter and Setter                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public Coordinate getStartCoordinate() {
        return this.startCoordinate;
    }

    public Coordinate getEndCoordinate() {
        return this.endCoordinate;
    }

    public int getLength() {
        return length;
    }
}