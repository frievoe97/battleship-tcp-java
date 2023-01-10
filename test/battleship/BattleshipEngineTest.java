package battleship;

import org.junit.Test;
import org.junit.Assert;

/**
 * This test class tests the logic of the game engine.
 *
 * This class is inspired by @author thsc. A link to his
 * repository is in the Battleship interface. The class
 * has been modified to fit this game.
 *
 * @author friedrichvoelkers
 */
public class BattleshipEngineTest {

    @Test
    public void checkIfShipIsOnTheBoard1() throws GameException {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        Ship correctShip1 = createCorrectShip1();
        Assert.assertTrue(battleshipEngine.checkIfShipIsOnTheBoard(correctShip1));
    }

    @Test
    public void checkIfShipIsOnTheBoard2() throws GameException {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        Ship correctShip2 = createCorrectShip2();
        Assert.assertTrue(battleshipEngine.checkIfShipIsOnTheBoard(correctShip2));
    }

    @Test(expected = GameException.class)
    public void checkIfShipIsOnTheBoard3() throws GameException {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        Ship incorrectShip1 = createIncorrectShip1();
        battleshipEngine.checkIfShipIsOnTheBoard(incorrectShip1);
    }

    @Test
    public void checkIfAmountOfShipsOfThisLengthIsAlreadyChoosen1() throws GameException {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        Assert.assertFalse(battleshipEngine.checkIfAmountOfShipsOfThisLengthIsAlreadyChoosen(5));
        battleshipEngine.getYourBattleshipBoard().addShip(createCorrectShip1());
    }

    @Test
    public void checkIfAmountOfShipsOfThisLengthIsAlreadyChoosen2() throws GameException {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.getYourBattleshipBoard().addShip(createCorrectShip1());
        Assert.assertTrue(battleshipEngine.checkIfAmountOfShipsOfThisLengthIsAlreadyChoosen(5));
    }

    @Test
    public void checkIfTheShipHasDirectNeighbours1() throws GameException {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.getYourBattleshipBoard().addShip(createCorrectShip1());
        Assert.assertTrue(battleshipEngine.checkIfTheShipHasDirectNeighbours(new Ship(new Coordinate(5,7), new Coordinate(8,7))));
    }

    @Test
    public void checkIfTheShipHasDirectNeighbours2() throws GameException {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.getYourBattleshipBoard().addShip(createCorrectShip1());
        Assert.assertFalse(battleshipEngine.checkIfTheShipHasDirectNeighbours(new Ship(new Coordinate(5,7), new Coordinate(7,7))));
    }

    @Test
    public void getYourBattleshipBoard() {
    }

    @Test
    public void getEnemysBattleshipBoard() {
    }

    @Test
    public void getYourGameStatus() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        Assert.assertEquals(battleshipEngine.getYourGameStatus(), GameStatus.ON_CREATE);
    }

    @Test
    public void setYourGameStatus1() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.setYourGameStatus(GameStatus.YOU_LOSE);
        Assert.assertEquals(battleshipEngine.getYourGameStatus(), GameStatus.YOU_LOSE);
    }

    @Test
    public void setYourGameStatus2() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.setYourGameStatus(GameStatus.All_SHIPS_ARE_SET);
        Assert.assertEquals(battleshipEngine.getYourGameStatus(), GameStatus.All_SHIPS_ARE_SET);
    }

    @Test
    public void setYourGameStatus3() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.setYourGameStatus(GameStatus.YOU_WON);
        Assert.assertEquals(battleshipEngine.getYourGameStatus(), GameStatus.YOU_WON);
    }

    @Test
    public void getEnemiesGameStatus1() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        Assert.assertEquals(battleshipEngine.getEnemiesGameStatus(), GameStatus.ON_CREATE);
    }

    @Test
    public void setEnemiesGameStatus1() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.setYourGameStatus(GameStatus.YOU_LOSE);
        Assert.assertEquals(battleshipEngine.getYourGameStatus(), GameStatus.YOU_LOSE);
    }

    @Test
    public void setEnemiesGameStatus2() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.setYourGameStatus(GameStatus.All_SHIPS_ARE_SET);
        Assert.assertEquals(battleshipEngine.getYourGameStatus(), GameStatus.All_SHIPS_ARE_SET);
    }

    @Test
    public void setEnemiesGameStatus3() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        battleshipEngine.setYourGameStatus(GameStatus.YOU_WON);
        Assert.assertEquals(battleshipEngine.getYourGameStatus(), GameStatus.YOU_WON);
    }

    @Test
    public void getRequiredNumberOfShipsPerLength() throws GameException {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        for (int i = 2; i <= 5; i++) {
            // The number of required ships should be "6 - i" because there are 4 ships with a length of 2
            // 3 ships with a length of 3, 2 ships with a length of 4, 1 ship with a length of 5 and the
            // for-loop goes from 2 to 5.
            Assert.assertEquals(battleshipEngine.getRequiredNumberOfShipsPerLength(i), (6 - i));
        }
    }

    @Test
    public void getSumOfRequiredNumberOfShips() {
        BattleshipEngine battleshipEngine = new BattleshipEngine();
        Assert.assertEquals(battleshipEngine.getSumOfRequiredNumberOfShips(), 10);
    }

    private Ship createCorrectShip1() throws GameException {
        return new Ship(new Coordinate(9 , 5), new Coordinate(9, 9));
    }

    private Ship createCorrectShip2() throws GameException {
        return new Ship(new Coordinate(1 , 1), new Coordinate(1, 2));
    }

    private Ship createIncorrectShip1() throws GameException {
        return new Ship(new Coordinate(0 , 7), new Coordinate(0, 10));
    }

}