package ui;

import battleship.*;
import network.GameSessionEstablishedListener;
import network.TCPStream;
import network.TCPStreamCreatedListener;

import java.io.*;
import java.util.ArrayList;

/**
 * This class contains the main method and reads the
 * user input and calls the methods for the game logic.
 * <p>
 * This class is inspired by @author thsc. A link to his
 * repository is in the Battleship interface. The class
 * has been modified to fit this game.
 *
 * @author thsc
 * @author friedrichvoelkers
 */
public class BattleshipUI implements TCPStreamCreatedListener, GameSessionEstablishedListener {

    private static final String CHEAT_SET = "cheatSet";     // DELETE LATER
    private static final String CHEAT_FIRE = "cheatFire";   // DELETE LATER
    private static final String CREATE = "create";
    private static final String JOIN = "join";
    private static final String FIRE = "fire";
    private static final String SET = "set";
    private static final String SHOW = "show";
    private static final String EXIT = "exit";
    private static final String RULES = "rules";

    private static final int WAIT_MILLI_SECONDS = 50;

    private final BufferedReader bufferedReader;
    private final PrintStream printStream;
    private final String yourName;
    private final BattleshipImpl battleship;
    private TCPStream tcpStream;
    private BattleshipProtocolEngine battleshipProtocolEngine;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           constructor                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public BattleshipUI(InputStream inputStream, PrintStream printStream, String yourName) {
        this.printStream = printStream;
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.yourName = yourName;
        this.battleship = new BattleshipImpl(yourName);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           main-method                                                  //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        String yourName = null;

        if (args.length < 1) {
            System.out.println("Please enter your name:");
            try {
                yourName = new BufferedReader(new InputStreamReader(System.in)).readLine();
            } catch (IOException e) {
                System.exit(1);
            }
        } else {
            yourName = args[0];
        }

        System.out.println("Welcome " + yourName + " to the game!");

        BattleshipUI battleshipUI = new BattleshipUI(System.in, System.out, yourName);
        battleshipUI.printWelcomeScreen();
        battleshipUI.printRules();
        battleshipUI.printTwoEmptyLines();
        battleshipUI.printCommands();
        battleshipUI.printTwoEmptyLines();
        battleshipUI.readInputLoop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                              UI Methods                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void printWelcomeScreen() {
        String stringBuilder = """
                ######################################\t    __|__ |___| |\\
                #                                    #\t    |o__| |___| | \\
                #                                    #\t    |___| |___| |o \\
                #       Welcome to Battleship!       #\t   _|___| |___| |__o\\
                #                                    #\t  /...\\_____|___|____\\_/
                #                                    #\t  \\   o * o * * o o  /
                ######################################\t~~~~~~~~~~~~~~~~~~~~~~~~~~""";
        this.printStream.println(stringBuilder);
    }

    private void printRules() {
        this.printTwoEmptyLines();
        String rules = """
                Rules:

                The window has a size of 10 x 10. Each player has 4 ships of a length
                of 2, 3 ships of a length of 3, 2 ships of a length of 4 and 1 ship
                of a length of 5. Ship should have an empty box between each other.
                """;
        this.printStream.println(rules);
    }

    private void printCommands() {

        String stringBuilder = "Choose one of the commands:" +
                "\n" +
                "\n" +
                "create \t Create a new game." +
                "\n" +
                "join \t Join an other game." +
                "\n" +
                "fire \t Select a point." + printIfConectionExists() +
                "\n" +
                "set \t Set your ships." + printIfConectionExists() +
                "\n" +
                "show \t Show both boards." + printIfConectionExists() +
                "\n" +
                "rules \t Show the rules." +
                "\n" +
                "exit \t Exit the game." +
                "\n" +
                "help \t Do you need help?";
        this.printStream.println(stringBuilder);
    }

    private String printIfConectionExists() {
        if (this.alreadyConnected()) return "";
        else return " (You have to establish a connection first)";
    }

    private void printTwoEmptyLines() {
        this.printStream.println("\n");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                         read user input                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void readInputLoop() {
        boolean again = true;

        while (again) {
            String inputString;
            try {
                inputString = bufferedReader.readLine();
                String[] inputStringArray = inputString.split(" ", 2);

                if (inputStringArray.length != 2) {
                    inputString = inputString + " .";
                    inputStringArray = inputString.split(" ", 2);
                }

                switch (inputStringArray[0]) {
                    case CHEAT_SET -> setNineCorrectShips();
                    case CHEAT_FIRE -> shootNineCorrectShips();
                    case CREATE -> this.doCreate();
                    case JOIN -> this.doJoin();
                    case FIRE -> this.doFire(inputStringArray[1]);
                    case SET -> this.doSetShips(inputStringArray[1]);
                    case SHOW -> this.doShow();
                    case RULES -> this.doRules();
                    case EXIT -> {
                        again = false;
                        this.doExit();
                    }
                    default -> {
                        this.printStream.println("Unknown command: " + inputString);
                        this.printStream.println("Please read the Commands again.");
                        this.printCommands();
                    }
                }
            } catch (IOException ex) {
                this.printStream.println("cannot read from input stream - fatal, give up");
            }
        }
        this.printStream.println();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                      Battleship API-Methods                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void doFire(String parameter) {

        try {
            // Check if a connection is there
            if (!this.alreadyConnected()) throw new GameException("No connection. Please use create or join");

            // Check if input is correct
            int[] intParameter = this.integerParameterIsCorrect(parameter, 2);

            // Implement logic
            battleship.doFire(new Coordinate(intParameter[0], intParameter[1]), false);

            // Print boards and wait a moment until you got the answer
            Thread.sleep(WAIT_MILLI_SECONDS);
            if (battleship.battleshipEngine.getYourGameStatus() != GameStatus.YOU_WON)
                this.printStream.println(battleship.battleshipEngine.printBoards());
        } catch (GameException | InterruptedException e) {
            this.printStream.println(e.getLocalizedMessage());
        }
    }

    private void doSetShips(String parameter) {

        try {
            // Check if a connection is there
            if (!this.alreadyConnected()) throw new GameException("No connection. Please use create or join");

            // Check if input is correct
            int[] intParameter = this.integerParameterIsCorrect(parameter, 4);

            // Implement logic
            battleship.doSetShips(new Coordinate(intParameter[0], intParameter[1]), new Coordinate(intParameter[2], intParameter[3]));

            // Print boards
            Thread.sleep(WAIT_MILLI_SECONDS);
            if (battleship.battleshipEngine.getYourGameStatus() == GameStatus.ON_CREATE) {
                this.printStream.println(battleship.battleshipEngine.printBoards());
            } else {
                this.printStream.println("All of your ships are set.");
            }
        } catch (GameException | InterruptedException e) {
            this.printStream.println(e.getLocalizedMessage());
        }

    }

    private void doCreate() {

        if (this.alreadyConnected()) return;

        this.tcpStream = new TCPStream(Battleship.DEFAULT_PORT, true, this.yourName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();
    }

    private void doJoin() {

        if (this.alreadyConnected()) return;

        this.tcpStream = new TCPStream(Battleship.DEFAULT_PORT, false, this.yourName);
        this.tcpStream.setStreamCreationListener(this);
        this.tcpStream.start();
    }

    private void doRules() {
        this.printRules();
    }

    private void doShow() {
        try {
            if (!this.alreadyConnected()) throw new GameException("No connection. Please use create or join");
            else this.printStream.println(battleship.battleshipEngine.printBoards());
        } catch (GameException gameException) {
            this.printStream.println(gameException.getLocalizedMessage());
        }
    }

    private void doExit() throws IOException {
        this.battleshipProtocolEngine.close();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                             Utility Methods                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    int[] integerParameterIsCorrect(String stringParameter, int numberOfIntegerValues) throws GameException {
        int[] returnIntArray = new int[numberOfIntegerValues];
        String[] stringArray = stringParameter.trim().split(" ");
        if (stringArray.length != numberOfIntegerValues) throw new GameException("Incorrect number of parameters");

        for (int i = 0; i < numberOfIntegerValues; i++) {
            try {
                returnIntArray[i] = Integer.parseInt(stringArray[i]) - 1;
            } catch (NumberFormatException e) {
                throw new GameException("Can't parse input (" + stringArray[i] + ") to Integer.");
            }

        }
        return returnIntArray;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                      TCP-Connection Methods                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean alreadyConnected() {
        return this.tcpStream != null;
    }

    @Override
    public void streamCreated(TCPStream stream) {
        this.battleshipProtocolEngine = new BattleshipProtocolEngine(this.battleship, this.yourName);
        this.battleship.setProtocolEngine(battleshipProtocolEngine);
        this.battleshipProtocolEngine.subscribeGameSessionEstablishedListener(this);

        try {
            battleshipProtocolEngine.handleConnection(stream.getInputStream(), stream.getOutputStream());
        } catch (IOException e) {
            System.err.println("cannot get streams from tcpStream - fatal, give up: " + e.getLocalizedMessage());
            System.exit(1);
        }

        try {
            Thread.sleep(WAIT_MILLI_SECONDS);
        } catch (InterruptedException ignored) {}

        this.printStream.println("The TCP connection is established. The game can start. Your enemy's name is: " + battleship.enemiesName);
        this.printStream.println("Please set your ships.");
    }

    @Override
    public void gameSessionEstablished(boolean oracle, String enemiesName) {
        this.battleship.enemiesName = enemiesName;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           cheat methods                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setNineCorrectShips() {
        try {
            for (Ship ship: createNineCorrectShips()) {
                battleship.doSetShips(ship.getStartCoordinate(), ship.getEndCoordinate());
            }
        } catch (GameException gameException) {
            System.out.println("Cheat Error");
        }
    }

    private void shootNineCorrectShips() {
        try {
            for (Ship ship: createNineCorrectShips()) {
                for (Coordinate coordinate: ship.getCoordinates()) {
                    battleship.doFire(coordinate, false);
                }
            }
        } catch (GameException gameException) {
            System.out.println("Cheat Error");
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private ArrayList<Ship> createNineCorrectShips() throws GameException {
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
        }};
    }
}
