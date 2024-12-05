package websocket.messages;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket.
 *
 * Note: You can add to this class, but you should not alter the existing methods.
 */
public class ServerMessage {

    // Type of message to be sent, e.g., LOAD_GAME, ERROR
    private ServerMessageType serverMessageType;

    // Optional general message content
    private String message = null;

    // Optional specific error message content
    private String errorMessage = null;

    // Game data associated with the message, if applicable
    private GameData game = null;

    // New field for client's authentication token
    private String authToken = null;

    // The role of the client in the game
    private ClientRole role;

    // Chess board data associated with this message
    ChessBoard boardData;


    /**
     * Enum defining the types of server messages that can be sent.
     */
    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
        AUTH  // New message type for authentication
    }

    /**
     * Enum defining possible roles a client can have.
     */
    public enum ClientRole {
        White,
        Black,
        Observer,
        non,
        noChange
    }

    // Retrieves the role of the client
    public ClientRole getRole() {
        return this.role;
    }

    // Updates the client role
    public void setRole(ClientRole newRole) {
        this.role = newRole;
    }

    /**
     * Constructor for creating a ServerMessage with a specified type.
     */
    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Retrieves the type of server message
    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    // Retrieves the general message content
    public String getMessage() {
        return message;
    }

    // Updates the general message content
    public void setMessage(String message) {
        this.message = message;
    }

    // Retrieves any specific error message content
    public String getErrorMessage() {
        return errorMessage;
    }

    // Updates the specific error message content
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Retrieves the game data associated with this message
    public GameData getGame() {
        return game;
    }

    // Updates the game data
    public void setGame(GameData game) {
        this.game = game;
    }

    // Sets the chess board data
    public void setBoard(ChessBoard board) {
        this.boardData = board;
    }

    // Retrieves the chess board data
    public ChessBoard getBoard() {
        return boardData;
    }

    // Retrieves the client's authentication token
    public String getAuthToken() {
        return authToken;
    }

    // Sets the client's authentication token
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        // Simplified equality based on server message type
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        // Generating a hash code based only on essential field
        return Objects.hash(getServerMessageType());
    }
}