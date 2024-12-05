package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;

public class Connect {

    public final Integer gameID;
    public final String userName;
    public final Session session;

    public Connect(String userName, Integer gameID, Session session) {
        this.gameID = gameID;
        this.userName = userName;
        this.session = session;
    }

    // Sends a message if the session is open, logs otherwise
    public void send(String msg) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(msg);
        } else {
            System.err.println("Session is not open, message cannot be sent.");
        }
    }
}