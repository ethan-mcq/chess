package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConnectionManager {

    private final ConcurrentHashMap<Integer, List<Connect>> connections = new ConcurrentHashMap<>();

    // Adds a new connection to the map, initializing the list if necessary
    public void add(Connect connection) {
        connections.computeIfAbsent(connection.gameID, k -> new CopyOnWriteArrayList<>()).add(connection);
    }

    // Removes a user from a specific game's connection list
    public void removeUser(String userName, Integer gameID) {
        List<Connect> userConnections = connections.get(gameID);
        if (userConnections != null) {
            userConnections.removeIf(connection -> Objects.equals(connection.userName, userName));
        }
    }

    // Broadcasts a message to all users of a specific game, excluding a specified user
    public void broadcast(Integer gameId, String excludeVisitorName, ServerMessage notification) throws IOException {
        List<Connect> userConnections = connections.get(gameId);
        if (userConnections != null) {
            List<Connect> removeList = new CopyOnWriteArrayList<>();
            for (Connect conn : userConnections) {
                if (conn.session.isOpen()) {
                    if (!conn.userName.equals(excludeVisitorName)) {
                        conn.send(new Gson().toJson(notification));
                    }
                } else {
                    removeList.add(conn);
                }
            }
            userConnections.removeAll(removeList);
        }
    }
}