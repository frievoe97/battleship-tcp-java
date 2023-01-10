package battleship;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * This test class tests the logic of the game.
 *
 * This class is inspired by @author thsc. A link to his
 * repository is in the Battleship interface. The class
 * has been modified to fit this game.
 *
 * @author friedrichvoelkers
 */
public class BattleshipImplTests {

    public static final String FRIEDRICH = "Friedrich";

    private BattleshipImpl getBattleshipImpl() {
        return new BattleshipImpl(FRIEDRICH);
    }

    @Test
    public void pickTenCorrectShips() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        addTenCorrectShips(battleship, createTenCorrectShips());
    }

    // Set two identical ships
    @Test(expected = GameException.class)
    public void setIncorrectShips1() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        battleship.doSetShips(new Coordinate(0, 0), new Coordinate(0, 1));
        battleship.doSetShips(new Coordinate(0, 0), new Coordinate(0, 1));
    }

    // Set two direct neighbour ships
    @Test(expected = GameException.class)
    public void setIncorrectShips2() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        battleship.doSetShips(new Coordinate(0, 0), new Coordinate(0, 5));
        battleship.doSetShips(new Coordinate(1, 0), new Coordinate(1, 5));
    }

    // Set ship out of the board (right side)
    @Test(expected = GameException.class)
    public void setIncorrectShips3() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        battleship.doSetShips(new Coordinate(0, 7), new Coordinate(0, 10));
    }

    // Set ship out of the board (left side)
    @Test(expected = GameException.class)
    public void setIncorrectShips4() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        battleship.doSetShips(new Coordinate(0, -1), new Coordinate(0, 3));
    }

    // Set ship out of the board (upper side)
    @Test(expected = GameException.class)
    public void setIncorrectShips5() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        battleship.doSetShips(new Coordinate(-1, 0), new Coordinate(3, 0));
    }

    // Set ship out of the board (down side)
    @Test(expected = GameException.class)
    public void setIncorrectShips6() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        battleship.doSetShips(new Coordinate(8, 0), new Coordinate(10, 0));
    }

    // Shoot tests
    @Test
    public void shootCorrect1() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        addTenCorrectShips(battleship, createTenCorrectShips());
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                battleship.doFire(new Coordinate(i, j), true);
            }
        }
    }

    @Test
    public void shootCorrect2() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        addTenCorrectShips(battleship, createTenCorrectShips());
        shootAllTenCorrectShips(battleship, createTenCorrectShips());
    }

    @Test(expected = GameException.class)
    public void shootIncorrect1() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        addTenCorrectShips(battleship, createTenCorrectShips());
        battleship.doFire(new Coordinate(5, 10), true);
    }

    @Test(expected = GameException.class)
    public void shootIncorrect2() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        addTenCorrectShips(battleship, createTenCorrectShips());
        battleship.doFire(new Coordinate(5, -1), true);
    }

    @Test(expected = GameException.class)
    public void shootIncorrect3() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        addTenCorrectShips(battleship, createTenCorrectShips());
        battleship.doFire(new Coordinate(-1, 5), true);
    }

    @Test(expected = GameException.class)
    public void shootIncorrect4() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        addTenCorrectShips(battleship, createTenCorrectShips());
        battleship.doFire(new Coordinate(10, 5), true);
    }

    @Test
    public void checkGameStatus() throws GameException, InterruptedException {
        BattleshipImpl battleship = getBattleshipImpl();
        Assert.assertEquals(battleship.battleshipEngine.getYourGameStatus(), GameStatus.ON_CREATE);
        addTenCorrectShips(battleship, createTenCorrectShips());
        shootAllTenCorrectShips(battleship, createTenCorrectShips());
        Assert.assertEquals(battleship.battleshipEngine.getYourGameStatus(), GameStatus.YOU_LOSE);
        Assert.assertEquals(battleship.battleshipEngine.getEnemiesGameStatus(), GameStatus.YOU_WON);
    }

    @Test
    public void checkCoordinate1() throws GameException {
        Coordinate coordinate = new Coordinate(5, 5);
        Assert.assertEquals(coordinate.getxCoordinate(), 5);
        Assert.assertEquals(coordinate.getyCoordinate(), 5);
    }

    @Test(expected = GameException.class)
    public void checkCoordinate2() throws GameException {
        Coordinate coordinate = new Coordinate(-1, 5);
    }

    @Test
    public void checkCoordinate3() throws GameException {
        Coordinate coordinate = new Coordinate(1, 5);
        Assert.assertSame(coordinate.getBattleshipFieldStatus(), BattleshipFieldStatus.UNKNOWN);
        coordinate.setBattleshipFieldStatus(BattleshipFieldStatus.SHIP_NO_SHOOT);
        Assert.assertSame(coordinate.getBattleshipFieldStatus(), BattleshipFieldStatus.SHIP_NO_SHOOT);
        coordinate.setBattleshipFieldStatus(BattleshipFieldStatus.SHIP_SHOOT);
        Assert.assertSame(coordinate.getBattleshipFieldStatus(), BattleshipFieldStatus.SHIP_SHOOT);
        coordinate.setBattleshipFieldStatus(BattleshipFieldStatus.EMPTY_NO_SHOOT);
        Assert.assertSame(coordinate.getBattleshipFieldStatus(), BattleshipFieldStatus.EMPTY_NO_SHOOT);
        coordinate.setBattleshipFieldStatus(BattleshipFieldStatus.EMPTY_SHOOT);
        Assert.assertSame(coordinate.getBattleshipFieldStatus(), BattleshipFieldStatus.EMPTY_SHOOT);
    }

    @Test
    public void checkShip1() throws GameException{
        Ship ship = new Ship(new Coordinate(1,1), new Coordinate(1, 5));
        Assert.assertEquals(ship.getLength(), 5);
    }

    @Test
    public void checkShip2() throws GameException{
        Ship ship = new Ship(new Coordinate(1,1), new Coordinate(1, 5));

        for (Coordinate coordinate: ship.getCoordinates()) {
            Assert.assertSame(coordinate.getBattleshipFieldStatus(), BattleshipFieldStatus.SHIP_NO_SHOOT);
        }

        for (Coordinate coordinate: ship.getCoordinates()) {
            ship.gotShoot(coordinate);
        }

        for (Coordinate coordinate: ship.getCoordinates()) {
            Assert.assertSame(coordinate.getBattleshipFieldStatus(), BattleshipFieldStatus.SHIP_SHOOT);
        }
    }

    @Test
    public void checkYourBattleshipBoard1() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        battleshipBoard.createYourBoard();
        for (int i = 0; i < BattleshipEngine.STANDARD_DIMENSION; i++) {
            for (int j = 0; j < BattleshipEngine.STANDARD_DIMENSION; j++) {
                Assert.assertSame(battleshipBoard.getFieldStatus(new Coordinate(i, j)), BattleshipFieldStatus.EMPTY_NO_SHOOT);
            }
        }
    }

    @Test
    public void checkYourBattleshipBoard2() throws GameException {
        BattleshipBoard battleshipBoard = new BattleshipBoard(true);
        Ship ship = new Ship(new Coordinate(1,1), new Coordinate(1, 5));
        battleshipBoard.addShip(ship);

        for (Coordinate coordinate:ship.getCoordinates()) {
            Assert.assertSame(battleshipBoard.getFieldStatus(coordinate), BattleshipFieldStatus.SHIP_NO_SHOOT);
        }
    }

    @Test
    public void checkYourBattleshipBoard3() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        for (Ship ship:createTenCorrectShips()) {
            Assert.assertFalse(battleship.battleshipEngine.allShipsAreSet());
            battleship.battleshipEngine.getYourBattleshipBoard().addShip(ship);
        }
        Assert.assertTrue(battleship.battleshipEngine.allShipsAreSet());
    }

    @Test
    public void checkYourBattleshipBoard4() throws GameException {
        BattleshipImpl battleship = getBattleshipImpl();
        Ship ship = new Ship(new Coordinate(1,1), new Coordinate(1, 5));
        battleship.battleshipEngine.getYourBattleshipBoard().addShip(ship);
        Assert.assertTrue(battleship.battleshipEngine.checkIfShipIsOnTheBoard(ship));
    }


    @SuppressWarnings("DuplicatedCode")
    private ArrayList<Ship> createTenCorrectShips() throws GameException {
        return new ArrayList<>() {{
            add(new Ship(new Coordinate(0, 0), new Coordinate(0, 1)));
            add(new Ship(new Coordinate(0, 3), new Coordinate(3, 3)));
            add(new Ship(new Coordinate(0, 6), new Coordinate(0, 9)));
            add(new Ship(new Coordinate(3, 0), new Coordinate(5, 0)));
            add(new Ship(new Coordinate(2, 5), new Coordinate(2, 7)));
            add(new Ship(new Coordinate(2, 9), new Coordinate(3, 9)));
            add(new Ship(new Coordinate(5, 5), new Coordinate(6, 5)));
            add(new Ship(new Coordinate(5, 9), new Coordinate(7, 9)));
            add(new Ship(new Coordinate(7, 0), new Coordinate(7, 1)));
            add(new Ship(new Coordinate(9, 5), new Coordinate(9, 9)));
        }};
    }

    private void addTenCorrectShips(BattleshipImpl battleship, ArrayList<Ship> tenCorrectShips) throws GameException {
        for (Ship ship : tenCorrectShips) {
            battleship.doSetShips(ship.getStartCoordinate(), ship.getEndCoordinate());
        }
    }

    private void shootAllTenCorrectShips(BattleshipImpl battleship, ArrayList<Ship> tenCorrectShips) throws GameException {
        for (Ship ship : tenCorrectShips) {
            for (Coordinate coordinate : ship.getCoordinates()) {
                battleship.doFire(coordinate, true);
            }
        }
    }

}
