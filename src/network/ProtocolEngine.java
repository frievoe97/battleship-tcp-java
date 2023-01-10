package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class is from @author thsc. A link to his repository
 * is in the Battleship interface. The class slightly modified
 * and reduced to the most necessary.
 *
 * @author thsc
 */
public interface ProtocolEngine {

    void handleConnection(InputStream inputStream, OutputStream outputStream) throws IOException;

    void close() throws IOException;

    void subscribeGameSessionEstablishedListener(GameSessionEstablishedListener gameSessionEstablishedListener);
}
