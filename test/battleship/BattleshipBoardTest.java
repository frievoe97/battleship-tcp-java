package battleship;

import org.junit.Assert;
import org.junit.Test;

/**
 * This test class tests the logic of the board.
 *
 * This class is inspired by @author thsc. A link to his
 * repository is in the Battleship interface. The class
 * has been modified to fit this game.
 *
 * @author friedrichvoelkers
 */
public class BattleshipBoardTest {

    private Ship createShip1() throws GameException {
        return new Ship(new Coordinate(9 , 5), new Coordinate(9, 9));
    }

    private Ship createShip2() throws GameException {
        return new Ship(new Coordinate(1 , 1), new Coordinate(1, 2));
    }

    private Coordinate createCoordinateOnBoard() throws GameException {
        return new Coordinate(5,5);
    }

    @Test
    public void createYourBoard1() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        for (int i = 0; i < BattleshipEngine.STANDARD_DIMENSION; i++) {
            for (int j = 0; j < BattleshipEngine.STANDARD_DIMENSION; j++) {;
                Assert.assertSame(battleshipBoard.getFieldStatus(new Coordinate(i, j)), BattleshipFieldStatus.EMPTY_NO_SHOOT);
            }
        }
    }

    @Test
    public void createYourBoard2() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(false);
        for (int i = 0; i < BattleshipEngine.STANDARD_DIMENSION; i++) {
            for (int j = 0; j < BattleshipEngine.STANDARD_DIMENSION; j++) {;
                Assert.assertSame(battleshipBoard.getFieldStatus(new Coordinate(i, j)), BattleshipFieldStatus.UNKNOWN);
            }
        }
    }

    @Test
    public void shipAlreadyExists1() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        Ship ship1 = createShip1();
        battleshipBoard.addShip(ship1);
        Assert.assertTrue(battleshipBoard.shipAlreadyExists(ship1));
    }

    @Test
    public void shipAlreadyExists2() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        Ship ship1 = createShip1();
        battleshipBoard.addShip(createShip2());
        Assert.assertFalse(battleshipBoard.shipAlreadyExists(ship1));
    }

    @Test
    public void getShipOnACoordinate() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        Ship ship1 = createShip1();
        battleshipBoard.addShip(ship1);
        Assert.assertEquals(battleshipBoard.getShipOnACoordinate(new Coordinate(9, 5)), ship1);
    }

    @Test
    public void checkIfAllShipsAreShoot() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        Ship ship1 = createShip1();
        battleshipBoard.addShip(ship1);
        Assert.assertNotEquals(battleshipBoard.getShipOnACoordinate(new Coordinate(9, 5)), createShip2());
    }

    @Test
    public void getFieldStatus1() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        Assert.assertEquals(battleshipBoard.getFieldStatus(createCoordinateOnBoard()), BattleshipFieldStatus.EMPTY_NO_SHOOT);
    }

    @Test
    public void getFieldStatus2() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(false);
        Assert.assertEquals(battleshipBoard.getFieldStatus(createCoordinateOnBoard()), BattleshipFieldStatus.UNKNOWN);
    }

    @Test
    public void setFieldStatus() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(false);
        battleshipBoard.setFieldStatus(BattleshipFieldStatus.SHIP_SHOOT, createCoordinateOnBoard());
        Assert.assertEquals(battleshipBoard.getFieldStatus(createCoordinateOnBoard()), BattleshipFieldStatus.SHIP_SHOOT);
    }

    @Test
    public void getNumberOfShips() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        battleshipBoard.addShip(createShip1());
        Assert.assertEquals(battleshipBoard.getNumberOfShips(), 1);
        battleshipBoard.addShip(createShip2());
        Assert.assertEquals(battleshipBoard.getNumberOfShips(), 2);
    }

    @Test
    public void getNumberOfShipsPerLength1() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        battleshipBoard.addShip(createShip1());
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(2), 0);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(3), 0);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(4), 0);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(5), 1);
    }

    @Test
    public void getNumberOfShipsPerLength2() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        battleshipBoard.addShip(createShip1());
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(2), 0);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(3), 0);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(4), 0);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(5), 1);
        battleshipBoard.addShip(createShip2());
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(2), 1);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(3), 0);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(4), 0);
        Assert.assertEquals(battleshipBoard.getNumberOfShipsPerLength(5), 1);
    }
}