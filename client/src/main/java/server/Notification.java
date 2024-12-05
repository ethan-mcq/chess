package server;

import websocket.messages.ServerMessage;

public interface Notification {
    void ping(ServerMessage notification);
}
