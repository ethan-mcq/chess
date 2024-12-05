package server;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.InputException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

// WebSocketFacade class extends Endpoint for WebSocket functionality
public class WebSocketFacade extends Endpoint {

    // WebSocket session object
    private Session session;

    // Notification handler for processing server messages
    private Notification notificationHandler;

    /**
     * Constructor to initialize the WebSocket connection
     * @param url URL of the WebSocket server
     * @param notificationHandler Notification handler
     * @throws InputException if connection fails
     */
    public WebSocketFacade(String url, Notification notificationHandler) throws InputException {
        try {
            // Replace HTTP scheme with WebSocket scheme
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            // Create WebSocket container and connect to server
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // Set message handler for incoming messages
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    notificationHandler.ping(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new InputException(500, ex.getMessage());
        }
    }

    // Required method by the Endpoint; not utilized in this context
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // No action needed here
    }

    /**
     * Connects to a game using game ID and authentication token
     * @param gameID ID of the game to connect
     * @param authToken Authentication token
     * @throws InputException if sending command fails
     */
    public void connectToGame(Integer gameID, String authToken) throws InputException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new InputException(500, ex.getMessage());
        }
    }

    /**
     * Sends a move in a game
     * @param gameID ID of the game
     * @param authToken Authentication token
     * @param move Chess move to be made
     * @throws InputException if sending command fails
     */
    public void makeMove(Integer gameID, String authToken, ChessMove move) throws InputException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
            command.setChessMove(move);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new InputException(500, ex.getMessage());
        }
    }

    /**
     * Leaves a game
     * @param gameID ID of the game
     * @param authToken Authentication token
     * @throws InputException if sending command fails
     */
    public void leaveGame(Integer gameID, String authToken) throws InputException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            session.getBasicRemote().sendText(new Gson().toJson(command));
            session.close();
        } catch (IOException ex) {
            throw new InputException(500, ex.getMessage());
        }
    }

    /**
     * Resigns from a game
     * @param gameID ID of the game
     * @param authToken Authentication token
     * @throws InputException if sending command fails
     */
    public void resign(Integer gameID, String authToken) throws InputException {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new InputException(500, ex.getMessage());
        }
    }
}