package websocket.commands;

import chess.ChessMove;
import websocket.messages.ServerMessage;

import java.util.Objects;

/**
 * Represents a command a user can send to the server over a websocket.
 *
 * Note: You can add to this class, but you should not alter the existing methods.
 */
public class UserGameCommand {

    // The type of command, e.g., CONNECT, MAKE_MOVE
    private final CommandType commandType;

    // Authorization token for validating user requests
    private final String authToken;

    // ID of the game to which this command relates
    private final Integer gameID;

    // Move to be made in a chess game, initially null
    private ChessMove move = null;

    // The role requested by the client
    private ServerMessage.ClientRole requestedRole;

    /**
     * Constructor for creating a UserGameCommand without a role or a move.
     * Suitable for basic commands like CONNECT or LEAVE.
     */
    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.authToken = authToken;
        this.commandType = commandType;
        this.gameID = gameID;
    }

    /**
     * Constructor for creating a UserGameCommand with an initial role.
     * Used for commands where a role is relevant, such as when changing the user's role in the game.
     */
    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ServerMessage.ClientRole newRole) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.requestedRole = newRole;
    }

    /**
     * Constructor for creating a UserGameCommand with an initial chess move.
     * Relevant for commands that involve making a chess move.
     */
    public UserGameCommand(CommandType commandType, String authToken, Integer gameID, ChessMove newMove) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        this.move = newMove;
    }

    /**
     * Enum defining the types of commands a user can send.
     */
    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        RESIGN,
        LEAVE
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setChessMove(ChessMove move) {
        this.move = move;
    }

    public ChessMove getChessMove() {
        return move;
    }

    public void setRole(ServerMessage.ClientRole role) {
        this.requestedRole = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserGameCommand)) return false;

        UserGameCommand that = (UserGameCommand) o;

        // Check for equality based on essential fields
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getGameID(), that.getGameID()) &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getChessMove(), that.getChessMove());
    }

    @Override
    public int hashCode() {
        // Generate a hash code based on essential fields
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}