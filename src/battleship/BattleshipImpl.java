package battleship;

import network.GameSessionEstablishedListener;

import java.util.Random;

/**
 * The BattleshipImpl class implements the logic of the game
 * and calls the BattleshipProtocolEngine to send the data
 * to the opponent.
 * <p>
 * This class is inspired by @author thsc. A link to his
 * repository is in the Battleship interface. The class
 * has been modified to fit this game.
 *
 * @author thsc
 * @author friedrichvoelkers
 */
public final class BattleshipImpl implements Battleship, GameSessionEstablishedListener {

    public BattleshipEngine battleshipEngine;
    public String yourName;
    public String enemiesName;
    private BattleshipProtocolEngine battleshipProtocolEngine;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           constructor                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BattleshipImpl(String yourName) {
        this.battleshipEngine = new BattleshipEngine();
        this.yourName = yourName;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                          logic methods                                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void doSendResult(BattleshipFieldStatus battleshipFieldStatus, Coordinate coordinate) {
        switch (battleshipFieldStatus) {
            case SHIP_NO_SHOOT -> battleshipEngine.getEnemysBattleshipBoard().setFieldStatus(BattleshipFieldStatus.SHIP_SHOOT, coordinate);
            case EMPTY_NO_SHOOT -> battleshipEngine.getEnemysBattleshipBoard().setFieldStatus(BattleshipFieldStatus.EMPTY_SHOOT, coordinate);
        }
    }

    @Override
    public void doFire(Coordinate coordinate, boolean isYourBoard) throws GameException {
        if (isYourBoard) {
            BattleshipFieldStatus battleshipFieldStatus;
            GameStatus yourGameStatus;
            GameStatus enemiesGameStatus;
            String message;

            // Hier steht, was passiert, wenn ich angegriffen werde
            switch (battleshipEngine.getYourBattleshipBoard().getFieldStatus(coordinate)) {
                case EMPTY_NO_SHOOT -> {
                    battleshipFieldStatus = BattleshipFieldStatus.EMPTY_NO_SHOOT; // Du bist dran
                    battleshipEngine.getYourBattleshipBoard().setFieldStatus(BattleshipFieldStatus.EMPTY_SHOOT, coordinate);
                    yourGameStatus = GameStatus.PLAYING_YOUR_TURN;
                    enemiesGameStatus = GameStatus.PLAYING_ENEMY_TURN;
                    message = "Unfortunately, you didn't hit a ship and it's your enemy's turn.";
                }
                case EMPTY_SHOOT -> {
                    battleshipFieldStatus = BattleshipFieldStatus.EMPTY_SHOOT;
                    yourGameStatus = GameStatus.PLAYING_ENEMY_TURN;
                    enemiesGameStatus = GameStatus.PLAYING_YOUR_TURN;
                    message = "You have already shot at this field. Please try again.";
                }
                case SHIP_NO_SHOOT -> {
                    battleshipFieldStatus = BattleshipFieldStatus.SHIP_NO_SHOOT;
                    battleshipEngine.getYourBattleshipBoard().setFieldStatus(BattleshipFieldStatus.SHIP_SHOOT, coordinate);
                    battleshipEngine.getYourBattleshipBoard().getShipOnACoordinate(coordinate).gotShoot(coordinate);

                    if (battleshipEngine.getYourBattleshipBoard().checkIfAllShipsAreShoot()) {
                        yourGameStatus = GameStatus.YOU_LOSE;
                        enemiesGameStatus = GameStatus.YOU_WON;
                        message = """
                                ########################################
                                #                                      #
                                #       Congratulation! You Won!       #
                                #                                      #
                                ########################################
                                """;
                        System.out.println("""
                                ########################################
                                #                                      #
                                #         You lost the game :(         #
                                #                                      #
                                ########################################
                                """);

                    } else {
                        yourGameStatus = GameStatus.PLAYING_ENEMY_TURN;
                        enemiesGameStatus = GameStatus.PLAYING_YOUR_TURN;
                        message = "You hit a ship! It's your turn again!";

                        if (Ship.checkIfShipIsCompleteyShoot(battleshipEngine.getYourBattleshipBoard().getShipOnACoordinate(coordinate))) {
                            message = message + " The ship is completely hit.";
                        } else {
                            message = message + " But the ship has not yet been completely hit.";
                        }

                    }
                }
                case SHIP_SHOOT -> {
                    battleshipFieldStatus = BattleshipFieldStatus.SHIP_SHOOT; // Gegner ist nochmal dran
                    yourGameStatus = GameStatus.PLAYING_ENEMY_TURN;
                    enemiesGameStatus = GameStatus.PLAYING_YOUR_TURN;
                    message = "You have already shot at this field. Please try again.";
                }
                default -> throw new GameException("The BattleshipFieldStatus is unknown.");
            }

            battleshipEngine.setYourGameStatus(yourGameStatus);
            battleshipEngine.setEnemiesGameStatus(enemiesGameStatus);

            if (this.battleshipProtocolEngine != null) {
                battleshipProtocolEngine.doChangeGameStatus(yourGameStatus, true, false);
                battleshipProtocolEngine.doChangeGameStatus(enemiesGameStatus, false, false);
                battleshipProtocolEngine.doSendResult(battleshipFieldStatus, coordinate);
                battleshipProtocolEngine.doSendMessage(message);
            }

        } else {
            // Check if gameStatus is correct
            if (!(battleshipEngine.getYourGameStatus() == GameStatus.PLAYING_YOUR_TURN &&
                    battleshipEngine.getEnemiesGameStatus() == GameStatus.PLAYING_ENEMY_TURN))
                throw new GameException("The GameStatus is not correct.");

            // Ich schieße und sende das an den Gegenspieler
            if (this.battleshipProtocolEngine != null) {
                this.battleshipProtocolEngine.doFire(coordinate, false);
            }
        }
    }

    @Override
    public void doSetShips(Coordinate startCoordinate, Coordinate endCoordinate) throws GameException {

        // If the ship is not vertical or horizontal, this implementation throws an exception
        Ship ship = new Ship(startCoordinate, endCoordinate);

        // Check if gameStatus is correct
        if (battleshipEngine.getYourGameStatus() != GameStatus.ON_CREATE)
            throw new GameException("The GameStatus is not correct.");

        // Check if the ship in on the board
        if (!battleshipEngine.checkIfShipIsOnTheBoard(ship)) throw new GameException("The ship is not on the board.");

        // Check if the amount of this length isn't already reached
        if (battleshipEngine.checkIfAmountOfShipsOfThisLengthIsAlreadyChoosen(ship.getLength()))
            throw new GameException("You have already set all ships of length " + ship.getLength() + ".");

        // Check if there is no direct neighbour
        if (battleshipEngine.checkIfTheShipHasDirectNeighbours(ship))
            throw new GameException("The ship has direct neighbors. One field must always remain free.");

        battleshipEngine.getYourBattleshipBoard().addShip(ship);

        if (this.battleshipProtocolEngine != null) {
            this.battleshipProtocolEngine.doSetShips(startCoordinate, endCoordinate);
            this.battleshipProtocolEngine.doSendMessage("Your enemy has placed a ship. (" +
                    battleshipEngine.getYourBattleshipBoard().getNumberOfShips() + "/" +
                    battleshipEngine.getSumOfRequiredNumberOfShips() + ")");
            if (battleshipEngine.allShipsAreSet()) {
                battleshipEngine.setYourGameStatus(GameStatus.All_SHIPS_ARE_SET);
                battleshipProtocolEngine.doChangeGameStatus(GameStatus.All_SHIPS_ARE_SET, true, false);
            }
        }
    }

    @Override
    public void doChangeGameStatus(GameStatus gameStatus, boolean isYourStatus, boolean bothPlayerSetAllShips) throws GameException {

        if (bothPlayerSetAllShips && isYourStatus) {
            battleshipEngine.setYourGameStatus(gameStatus);

            if (gameStatus == GameStatus.PLAYING_ENEMY_TURN)
                battleshipEngine.setEnemiesGameStatus(GameStatus.PLAYING_YOUR_TURN);
            else battleshipEngine.setEnemiesGameStatus(GameStatus.PLAYING_ENEMY_TURN);

            return;
        }

        if (isYourStatus) battleshipEngine.setYourGameStatus(gameStatus);
        else battleshipEngine.setEnemiesGameStatus(gameStatus);

        if (battleshipEngine.getYourGameStatus() == GameStatus.All_SHIPS_ARE_SET && gameStatus == GameStatus.All_SHIPS_ARE_SET && !isYourStatus) {
            boolean iStart = new Random().nextBoolean();

            if (iStart) {
                battleshipEngine.setYourGameStatus(GameStatus.PLAYING_YOUR_TURN);
                battleshipEngine.setEnemiesGameStatus(GameStatus.PLAYING_ENEMY_TURN);
            } else {
                battleshipEngine.setYourGameStatus(GameStatus.PLAYING_ENEMY_TURN);
                battleshipEngine.setEnemiesGameStatus(GameStatus.PLAYING_YOUR_TURN);
            }

            battleshipProtocolEngine.bothPlayerSetAllShips(iStart);
        }

        if (this.battleshipProtocolEngine != null && isYourStatus) {
            battleshipProtocolEngine.doChangeGameStatus(gameStatus, true, false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           tcp methods                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void gameSessionEstablished(boolean oracle, String partnerName) {
    }

    public void setProtocolEngine(BattleshipProtocolEngine protocolEngine) {
        this.battleshipProtocolEngine = protocolEngine;
        this.battleshipProtocolEngine.subscribeGameSessionEstablishedListener(this);
    }
}
