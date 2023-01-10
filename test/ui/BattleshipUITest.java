package ui;

import battleship.GameException;
import org.junit.Test;

/**
 * This test class tests the ui of the game
 *
 * @author friedrichvoelkers
 */
public class BattleshipUITest {

    public static final String FRIEDRICH = "Friedrich";

    BattleshipUI getBattleshipUI() {
        return new BattleshipUI(System.in, System.out, FRIEDRICH);
    }

    @Test
    public void testIntegerParameterIsCorrect1() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("   1 ", 1);
    }

    @Test
    public void testIntegerParameterIsCorrect2() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("1 1", 2);
    }

    @Test
    public void testIntegerParameterIsCorrect3() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("1 1325 123", 3);
    }

    @Test
    public void testIntegerParameterIsCorrect4() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("431 523 124 421", 4);
    }

    @Test
    public void testIntegerParameterIsCorrect5() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("4314 214 21 421 214", 5);
    }

    @Test(expected = GameException.class)
    public void testIntegerParameterIsCorrect6() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect(" ", 1);
    }

    @Test(expected = GameException.class)
    public void testIntegerParameterIsCorrect7() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("2 j", 2);
    }

    @Test(expected = GameException.class)
    public void testIntegerParameterIsCorrect8() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("4 12 s", 3);
    }

    @Test(expected = GameException.class)
    public void testIntegerParameterIsCorrect9() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("a 4 12 s", 4);
    }

    @Test(expected = GameException.class)
    public void testIntegerParameterIsCorrect10() throws GameException {
        BattleshipUI battleshipUI = getBattleshipUI();
        battleshipUI.integerParameterIsCorrect("4 12 s 4 123", 5);
    }
}