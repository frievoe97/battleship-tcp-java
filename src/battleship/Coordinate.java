package battleship;

/**
 * This class represents the coordinates of a field. When a coordinate object is created,
 * it checks if the coordinates are inside the field and throws an error message if these
 * coordinates are outside the field. In addition, the classes provides static methods
 * that check whether two coordinates are horizontal or vertical.
 *
 * @author friedrichvoelkers
 */
public final class Coordinate implements Comparable<Coordinate> {

    private final int xCoordinate;
    private final int yCoordinate;
    private BattleshipFieldStatus battleshipFieldStatus = BattleshipFieldStatus.UNKNOWN;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           constructors                                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Coordinate(int xCoordinate, int yCoordinate) throws GameException {

        if (xCoordinate < 0 || xCoordinate >= BattleshipEngine.STANDARD_DIMENSION || yCoordinate < 0 || yCoordinate >= BattleshipEngine.STANDARD_DIMENSION)
            throw new GameException("Coordinate is not on the board");

        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public Coordinate(int xCoordinate, int yCoordinate, BattleshipFieldStatus battleshipFieldStatus) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.battleshipFieldStatus = battleshipFieldStatus;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          logic methods                                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean checkIfTwoCoordinatesAreVertical(Coordinate startCoordinate, Coordinate endCoordinate) {
        return startCoordinate.yCoordinate == endCoordinate.yCoordinate;
    }

    public static boolean checkIfTwoCoordinatesAreHorizontal(Coordinate startCoordinate, Coordinate endCoordinate) {
        return startCoordinate.xCoordinate == endCoordinate.xCoordinate;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         Getter and Setter                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public BattleshipFieldStatus getBattleshipFieldStatus() {
        return battleshipFieldStatus;
    }

    public void setBattleshipFieldStatus(BattleshipFieldStatus battleshipFieldStatus) {
        this.battleshipFieldStatus = battleshipFieldStatus;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         comparable method                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int compareTo(Coordinate coordinate) {
        if (this.xCoordinate == coordinate.getxCoordinate() && this.yCoordinate == coordinate.getyCoordinate())
            return 0;
        else return 1;
    }
}
