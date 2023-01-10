package network;

import battleship.Battleship;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class is from @author thsc. A link to his repository
 * is in the Battleship interface. The class slightly modified
 * and reduced to the most necessary.
 *
 * @author thsc
 */
public class TCPStream extends Thread {
    private final int port;
    private final boolean asServer;
    private final String name;
    private TCPStreamCreatedListener listener;
    private Socket socket = null;
    private boolean fatalError = false;

    public final int WAIT_LOOP_IN_MILLIS = 1000; // 30 sec
    private Thread createThread = null;
    private final long waitInMillis = WAIT_LOOP_IN_MILLIS;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                           constructors                                                 //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public TCPStream(int port, boolean asServer, String name, TCPStreamCreatedListener listener) {
        this.port = port;
        this.asServer = asServer;
        this.name = name;
        this.listener = listener;
    }

    public TCPStream(int port, boolean asServer, String name) {
        this(port, asServer, name, null);
    }

    public void setStreamCreationListener(TCPStreamCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        this.createThread = Thread.currentThread();

        try {
            if(this.asServer) {
                System.out.println(this.getClass().getSimpleName() + ": Information: The TCP connection accepts only one connection per server.");
                TCPServer tcpServer = new TCPServer();
                this.socket = tcpServer.getSocket();
            } else {
                TCPClient tcpClient = new TCPClient();
                this.socket = tcpClient.getSocket();
            }

            if(this.listener != null) {
                this.listener.streamCreated(this);
            }
        } catch (IOException ex) {
            System.out.println("No connection could be established.");
            this.fatalError = true;
        }
    }

    public void waitForConnection() throws IOException {
        if(this.createThread == null) {
            /* in unit tests there is a race condition between the test
            thread and those newly created tests to establish a connection.

            Thus, this call could be in the right order - give it a
            second chance
            */

            try {
                Thread.sleep(this.waitInMillis);
            } catch (InterruptedException ignored) {
            }

            if(this.createThread == null) {
                // that's probably wrong usage:
                throw new IOException("must start TCPStream thread first by calling start()");
            }
        }

        while(!this.fatalError && this.socket == null) {
            try {
                Thread.sleep(this.waitInMillis);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void checkConnected() throws IOException {
        if(this.socket == null) {
            //<<<<<<<<<<<<<<<<<<debug
            String message = "no socket yet - should call connect first";
            System.out.println("no socket yet - should call connect first");
            //>>>>>>>>>>>>>>>>>>>debug
            throw new IOException(message);
        }
    }

    public InputStream getInputStream() throws IOException {
        this.checkConnected();
        return this.socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        this.checkConnected();
        return this.socket.getOutputStream();
    }

    private class TCPServer {
        private ServerSocket srvSocket = null;

        Socket getSocket() throws IOException {
            if(this.srvSocket == null) {
                this.srvSocket = new ServerSocket(port);
            }

            //<<<<<<<<<<<<<<<<<<debug
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.getClass().getSimpleName());
            stringBuilder.append(" (");
            stringBuilder.append(name);
            stringBuilder.append("): ");
            stringBuilder.append("opened port ");
            stringBuilder.append(port);
            stringBuilder.append(" on localhost and wait");
            System.out.println(stringBuilder);
            //>>>>>>>>>>>>>>>>>>>debug

            Socket socket = this.srvSocket.accept();
            //<<<<<<<<<<<<<<<<<<debug
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.getClass().getSimpleName());
            stringBuilder.append(" (");
            stringBuilder.append(name);
            stringBuilder.append("): ");
            stringBuilder.append("connected");
            System.out.println(stringBuilder);
            //>>>>>>>>>>>>>>>>>>>debug

            return socket;
        }

    }

    private class TCPClient {

        Socket getSocket() throws IOException {
            boolean killed = false;
            int numberOfTrials = 0;
            int maxNumberOfTrials = 10;
            while(!killed) {
                try {
                    //<<<<<<<<<<<<<<<<<<debug
                    System.out.println(this.getClass().getSimpleName() + " (" + name + "): Try to connect to localhost on port " + port);
                    //>>>>>>>>>>>>>>>>>>>debug
                    return new Socket(Battleship.LOCALHOST, port);
                }
                catch(IOException ioe) {
                    //<<<<<<<<<<<<<<<<<<debug
                    numberOfTrials++;
                    System.out.println(this.getClass().getSimpleName() + " (" + name + "): Try (" + numberOfTrials + "/" + maxNumberOfTrials + ") failed. Try again.");
                    if (numberOfTrials == maxNumberOfTrials) killed = true;
                    try {
                        Thread.sleep(waitInMillis);
                    } catch (InterruptedException ignored) {}
                }
            }
            throw new IOException("thread was killed before establishing a connection");
        }
    }
}
