package battleship;

import network.TCPStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

/**
 * This test class tests the tcp connection and logic
 * of the game.
 *
 * This class is inspired by @author thsc. A link to his
 * repository is in the Battleship interface. The class
 * has been modified to fit this game.
 *
 * @author friedrichvoelkers
 */
public class ProtocolEngineTests {

    public static final String FRIEDRICH = "Friedrich";
    public static final String SABINE = "Sabine";
    public static final int PORTNUMBER = 3000;
    public static final long THREAD_SLEEP_DURATION = 50;

    @Test
    public void protocolEngineTest1() throws IOException, InterruptedException, GameException {

        BattleshipImpl friedrichBattleship = new BattleshipImpl(FRIEDRICH);
        BattleshipProtocolEngine friedrichBattleshipProtocolEngine = new BattleshipProtocolEngine(friedrichBattleship, FRIEDRICH);
        friedrichBattleship.setProtocolEngine(friedrichBattleshipProtocolEngine);


        BattleshipImpl sabineBattleship = new BattleshipImpl(SABINE);
        BattleshipProtocolEngine sabineBattleshipProtocolEngine = new BattleshipProtocolEngine(sabineBattleship, SABINE);
        sabineBattleship.setProtocolEngine(sabineBattleshipProtocolEngine);

        Assert.assertSame(friedrichBattleship.battleshipEngine.getYourGameStatus(), GameStatus.ON_CREATE);
        Assert.assertSame(sabineBattleship.battleshipEngine.getYourGameStatus(), GameStatus.ON_CREATE);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                           setup tcp                                                    //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        TCPStream friedrichTCPStream = new TCPStream(PORTNUMBER, true, FRIEDRICH);
        TCPStream sabineTCPStream = new TCPStream(PORTNUMBER, false, SABINE);

        friedrichTCPStream.start();
        sabineTCPStream.start();

        friedrichTCPStream.waitForConnection();
        sabineTCPStream.waitForConnection();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //                                       launch protocol engine                                           //
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        friedrichBattleshipProtocolEngine.handleConnection(friedrichTCPStream.getInputStream(), friedrichTCPStream.getOutputStream());
        sabineBattleshipProtocolEngine.handleConnection(sabineTCPStream.getInputStream(), sabineTCPStream.getOutputStream());

        Thread.sleep(THREAD_SLEEP_DURATION);

        // Friedrich sets 10 correct ships
        addTenCorrectShips(friedrichBattleship, createTenCorrectShips());

        Assert.assertSame(friedrichBattleship.battleshipEngine.getYourGameStatus(), GameStatus.All_SHIPS_ARE_SET);
        Assert.assertSame(sabineBattleship.battleshipEngine.getYourGameStatus(), GameStatus.ON_CREATE);

        // Sabine sets 10 correct ships
        addTenCorrectShips(sabineBattleship, createTenCorrectShips());

        Thread.sleep(THREAD_SLEEP_DURATION);
        Assert.assertTrue((friedrichBattleship.battleshipEngine.getYourGameStatus() == GameStatus.PLAYING_YOUR_TURN &&
                sabineBattleship.battleshipEngine.getYourGameStatus() == GameStatus.PLAYING_ENEMY_TURN) ||
                (friedrichBattleship.battleshipEngine.getYourGameStatus() == GameStatus.PLAYING_ENEMY_TURN &&
                        sabineBattleship.battleshipEngine.getYourGameStatus() == GameStatus.PLAYING_YOUR_TURN));

        if (friedrichBattleship.battleshipEngine.getYourGameStatus() == GameStatus.PLAYING_YOUR_TURN) {
            shootAllTenCorrectShips(friedrichBattleship, createTenCorrectShips());
        } else {
            shootAllTenCorrectShips(sabineBattleship, createTenCorrectShips());
        }

        Thread.sleep(THREAD_SLEEP_DURATION);
        Assert.assertTrue((friedrichBattleship.battleshipEngine.getYourGameStatus() == GameStatus.YOU_LOSE &&
                sabineBattleship.battleshipEngine.getYourGameStatus() == GameStatus.YOU_WON) ||
                (friedrichBattleship.battleshipEngine.getYourGameStatus() == GameStatus.YOU_WON &&
                        sabineBattleship.battleshipEngine.getYourGameStatus() == GameStatus.YOU_LOSE));

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

    private void shootAllTenCorrectShips(BattleshipImpl battleship, ArrayList<Ship> tenCorrectShips) throws GameException, InterruptedException {
        for (Ship ship : tenCorrectShips) {
            for (Coordinate coordinate : ship.getCoordinates()) {
                Thread.sleep(10);
                battleship.doFire(coordinate, false);
            }
        }
    }

}
