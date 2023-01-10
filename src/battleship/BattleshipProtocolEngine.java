package battleship;

import network.GameSessionEstablishedListener;
import network.ProtocolEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The method takes care of the protocol of TCP communication. It implements the methods of
 * the Battleship interface and ensures that after the method call the data is sent to the
 * opposing side via the OutputStream. Thereby the data is converted into bytes. For each
 * method there is also a method that receives the data on the other side via the InputStream
 * and converts the data again via the stored protocol. With the protocol at the first place
 * always an integer value is sent, which indicates, which method is to be called, in order
 * to deserialize the data.
 * <p>
 * This class is inspired by @author thsc. A link to his repository is in the Battleship interface.
 * The class has been modified to fit this game
 *
 * @author thsc
 * @author friedrichvoelkers
 */
public final class BattleshipProtocolEngine implements Battleship, Runnable, ProtocolEngine {

    public static final int METHOD_DO_FIRE = 2;
    public static final int METHOD_DO_CHANGE_STATUS = 3;
    public static final int METHOD_DO_SEND_RESULT = 4;
    public static final int METHOD_BOTH_PLAY_SET_ALL_SHIPS = 5;
    public static final int METHOD_DO_SEND_MESSAGE = 6;

    public static final int GAME_STATUS_ON_CREATE = 0;
    public static final int GAME_STATUS_All_SHIPS_ARE_SET = 1;
    public static final int GAME_STATUS_PLAYING_YOUR_TURN = 2;
    public static final int GAME_STATUS_PLAYING_ENEMY_TURN = 3;
    public static final int GAME_STATUS_FINISH = 4;
    public static final int GAME_STATUS_YOU_WON = 5;
    public static final int GAME_STATUS_YOU_LOSE = 6;

    public static final int BATTLESHIP_FIELD_STATUS_EMPTY_NO_SHOOT = 0;
    public static final int BATTLESHIP_FIELD_STATUS_EMPTY_SHOOT = 1;
    public static final int BATTLESHIP_FIELD_STATUS_SHIP_NO_SHOOT = 2;
    public static final int BATTLESHIP_FIELD_STATUS_SHIP_SHOOT = 3;
    public static final int BATTLESHIP_FIELD_STATUS_UNKNOWN = 4;

    private final Battleship battleship;
    private final List<GameSessionEstablishedListener> sessionCreatedListenerList = new ArrayList<>();
    private final String yourName;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean coinToss;
    private String enemiesName;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           constructor                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BattleshipProtocolEngine(Battleship battleship, String yourName) {
        this.battleship = battleship;
        this.yourName = yourName;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                        thread run method                                               //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void run() {

        try {
            DataOutputStream dataOutputStream = new DataOutputStream(this.outputStream);
            DataInputStream dataInputStream = new DataInputStream(this.inputStream);
            this.coinToss = new Random().nextBoolean();
            dataOutputStream.writeUTF(this.yourName);
            this.enemiesName = dataInputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!this.sessionCreatedListenerList.isEmpty()) {
            for (GameSessionEstablishedListener gameSessionEstablishedListener : this.sessionCreatedListenerList) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                    gameSessionEstablishedListener.gameSessionEstablished(BattleshipProtocolEngine.this.coinToss, BattleshipProtocolEngine.this.enemiesName);
                }).start();
            }
        }

        try {
            boolean again = true;
            while (again) again = this.read();
        } catch (GameException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                serialize/deserialize methods                                           //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void doFire(Coordinate coordinate, boolean isYourBoard) throws GameException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.outputStream);
        try {
            dataOutputStream.writeInt(METHOD_DO_FIRE);
            dataOutputStream.writeInt(coordinate.getxCoordinate());
            dataOutputStream.writeInt(coordinate.getyCoordinate());
        } catch (IOException e) {
            throw new GameException("The input could not be serialized.");
        }
    }

    private void deserializeDoFire() throws GameException {
        DataInputStream dataInputStream = new DataInputStream(this.inputStream);
        try {
            int xCoordinate = dataInputStream.readInt();
            int yCoordinate = dataInputStream.readInt();
            battleship.doFire(new Coordinate(xCoordinate, yCoordinate), true);
        } catch (IOException e) {
            throw new GameException("The input could not be deserialized.");
        }
    }

    public void doSendMessage(String message) throws GameException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.outputStream);
        try {
            dataOutputStream.writeInt(METHOD_DO_SEND_MESSAGE);
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            throw new GameException("The input could not be serialized.");
        }
    }

    private void deserializeDoSendMessage() throws GameException {
        DataInputStream dataInputStream = new DataInputStream(this.inputStream);
        try {
            System.out.println(dataInputStream.readUTF());
        } catch (IOException e) {
            throw new GameException("The input could not be deserialized.");
        }
    }

    @Override
    public void doSetShips(Coordinate startCoordinate, Coordinate endCoordinate) {
    }

    @Override
    public void doChangeGameStatus(GameStatus gameStatus, boolean isYourStatus, boolean bothPlayerSetAllShips) throws GameException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.outputStream);
        try {
            dataOutputStream.writeInt(METHOD_DO_CHANGE_STATUS);
            dataOutputStream.writeInt(this.convertGameStatusToInteger(gameStatus));
            dataOutputStream.writeBoolean(isYourStatus);
        } catch (IOException e) {
            throw new GameException("The input could not be serialized.");
        }
    }

    private void deserializeChangeStatus() throws GameException {
        DataInputStream dataInputStream = new DataInputStream(this.inputStream);
        try {
            int symbolInt = dataInputStream.readInt();
            GameStatus gameStatus = this.convertIntegerToGameStatus(symbolInt);
            boolean isYourTurn = dataInputStream.readBoolean();
            battleship.doChangeGameStatus(gameStatus, !isYourTurn, false);
        } catch (IOException e) {
            throw new GameException("The input could not be deserialized.");
        }
    }

    @Override
    public void doSendResult(BattleshipFieldStatus battleshipFieldStatus, Coordinate coordinate) throws GameException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.outputStream);
        try {
            dataOutputStream.writeInt(METHOD_DO_SEND_RESULT);
            dataOutputStream.writeInt(this.convertBattleshipFieldStatusToInteger(battleshipFieldStatus));
            dataOutputStream.writeInt(coordinate.getxCoordinate());
            dataOutputStream.writeInt(coordinate.getyCoordinate());
        } catch (IOException e) {
            throw new GameException("The input could not be serialized.");
        }
    }

    private void deserializeDoSendResult() throws GameException {
        DataInputStream dataInputStream = new DataInputStream(this.inputStream);
        try {
            int symbolInt = dataInputStream.readInt();
            int xCoordinate = dataInputStream.readInt();
            int yCoordinate = dataInputStream.readInt();
            BattleshipFieldStatus battleshipFieldStatus = this.convertIntegerToBattleshipFieldStatus(symbolInt);
            battleship.doSendResult(battleshipFieldStatus, new Coordinate(xCoordinate, yCoordinate));
        } catch (IOException e) {
            throw new GameException("The input could not be deserialized.");
        }
    }

    public void bothPlayerSetAllShips(boolean iStart) throws GameException {
        if (iStart) {
            System.out.println("Randomness has decided. You start!");
        } else {
            System.out.println("Randomness has decided. Your enemy starts!");
        }
        DataOutputStream dataOutputStream = new DataOutputStream(this.outputStream);
        try {
            dataOutputStream.writeInt(METHOD_BOTH_PLAY_SET_ALL_SHIPS);
            dataOutputStream.writeBoolean(iStart);
        } catch (IOException e) {
            throw new GameException("The input could not be serialized.");
        }
    }

    private void deserializeBothPlayerSetAllShips() throws GameException {
        DataInputStream dataInputStream = new DataInputStream(this.inputStream);
        try {
            boolean enemyStarts = dataInputStream.readBoolean();

            if (enemyStarts) {
                battleship.doChangeGameStatus(GameStatus.PLAYING_ENEMY_TURN, true, true);
                System.out.println("Randomness has decided. Your enemy starts!");
            } else {
                battleship.doChangeGameStatus(GameStatus.PLAYING_YOUR_TURN, true, true);
                System.out.println("Randomness has decided. You start!");
            }
        } catch (IOException e) {
            throw new GameException("The input could not be deserialized.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                             serialize/deserialize helper methods                                       //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private int convertGameStatusToInteger(GameStatus gameStatus) {
        return switch (gameStatus) {
            case PLAYING_YOUR_TURN -> GAME_STATUS_PLAYING_YOUR_TURN;
            case PLAYING_ENEMY_TURN -> GAME_STATUS_PLAYING_ENEMY_TURN;
            case FINISH -> GAME_STATUS_FINISH;
            case ON_CREATE -> GAME_STATUS_ON_CREATE;
            case All_SHIPS_ARE_SET -> GAME_STATUS_All_SHIPS_ARE_SET;
            case YOU_WON -> GAME_STATUS_YOU_WON;
            case YOU_LOSE -> GAME_STATUS_YOU_LOSE;
        };
    }

    private GameStatus convertIntegerToGameStatus(int gameStatus) throws GameException {
        return switch (gameStatus) {
            case GAME_STATUS_PLAYING_YOUR_TURN -> GameStatus.PLAYING_YOUR_TURN;
            case GAME_STATUS_PLAYING_ENEMY_TURN -> GameStatus.PLAYING_ENEMY_TURN;
            case GAME_STATUS_FINISH -> GameStatus.FINISH;
            case GAME_STATUS_ON_CREATE -> GameStatus.ON_CREATE;
            case GAME_STATUS_All_SHIPS_ARE_SET -> GameStatus.All_SHIPS_ARE_SET;
            case GAME_STATUS_YOU_WON -> GameStatus.YOU_WON;
            case GAME_STATUS_YOU_LOSE -> GameStatus.YOU_LOSE;
            default -> throw new GameException("The GameStatus is not unknown.");
        };
    }

    private int convertBattleshipFieldStatusToInteger(BattleshipFieldStatus battleshipFieldStatus) {
        return switch (battleshipFieldStatus) {
            case EMPTY_NO_SHOOT -> BATTLESHIP_FIELD_STATUS_EMPTY_NO_SHOOT;
            case EMPTY_SHOOT -> BATTLESHIP_FIELD_STATUS_EMPTY_SHOOT;
            case SHIP_NO_SHOOT -> BATTLESHIP_FIELD_STATUS_SHIP_NO_SHOOT;
            case SHIP_SHOOT -> BATTLESHIP_FIELD_STATUS_SHIP_SHOOT;
            case UNKNOWN -> BATTLESHIP_FIELD_STATUS_UNKNOWN;
        };
    }

    private BattleshipFieldStatus convertIntegerToBattleshipFieldStatus(int battleshipFieldStatus) throws GameException {
        return switch (battleshipFieldStatus) {
            case BATTLESHIP_FIELD_STATUS_EMPTY_NO_SHOOT -> BattleshipFieldStatus.EMPTY_NO_SHOOT;
            case BATTLESHIP_FIELD_STATUS_EMPTY_SHOOT -> BattleshipFieldStatus.EMPTY_SHOOT;
            case BATTLESHIP_FIELD_STATUS_SHIP_NO_SHOOT -> BattleshipFieldStatus.SHIP_NO_SHOOT;
            case BATTLESHIP_FIELD_STATUS_SHIP_SHOOT -> BattleshipFieldStatus.SHIP_SHOOT;
            default -> throw new GameException("The GameStatus is not unknown.");
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                   read the DataInputStream                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    boolean read() throws GameException {
        DataInputStream dataInputStream = new DataInputStream(this.inputStream);

        try {
            int readInt = dataInputStream.readInt();
            switch (readInt) {
                case METHOD_DO_CHANGE_STATUS:
                    this.deserializeChangeStatus();
                    return true;
                case METHOD_DO_FIRE:
                    this.deserializeDoFire();
                    return true;
                case METHOD_DO_SEND_RESULT:
                    this.deserializeDoSendResult();
                    return true;
                case METHOD_BOTH_PLAY_SET_ALL_SHIPS:
                    this.deserializeBothPlayerSetAllShips();
                    return true;
                case METHOD_DO_SEND_MESSAGE:
                    this.deserializeDoSendMessage();
                    return true;
                default:
                    return false;
            }
        } catch (IOException e) {
            try {
                this.close();
            } catch (IOException ignored) {}
            return false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           tcp methods                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handleConnection(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;

        Thread protocolThread = new Thread(this);
        protocolThread.start();
    }

    @Override
    public void close() throws IOException {
        if (this.outputStream != null) this.outputStream.close();
        if (this.inputStream != null) this.inputStream.close();
    }

    @Override
    public void subscribeGameSessionEstablishedListener(GameSessionEstablishedListener ocListener) {
        this.sessionCreatedListenerList.add(ocListener);
    }
}
