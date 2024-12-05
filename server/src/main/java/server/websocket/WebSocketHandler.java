package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.InputException;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    // Service dependencies for authentication, game management, and user management
    private final UserS userS;
    private final AuthS authS;
    private final GameS gameS;
    private final ConnectionManager connects = new ConnectionManager();

    // Constructor to initialize the handler with required services
    public WebSocketHandler(AuthS authS, GameS gameS, UserS userS) {
        this.authS = authS;
        this.gameS = gameS;
        this.userS = userS;
    }

    // Method invoked when a message is received from a WebSocket session
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, InputException {
        System.out.println("Received Message: " + message);
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            // Authenticate the user using the provided authentication token
            Auth userData = authS.authenticate(command.getAuthToken());
            handleCommand(userData, command, session);
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
        }
    }

    // Handles different types of commands from the client based on the command type
    private void handleCommand(Auth userData, UserGameCommand command, Session session)
            throws IOException,
            DataAccessException,
            InvalidMoveException {
        switch (command.getCommandType()) {
            case CONNECT -> connect(userData, command.getGameID(), session);
            case MAKE_MOVE -> makeMove(command.getGameID(), command.getChessMove(), userData, session);
            case LEAVE -> leave(command.getGameID(), userData);
            case RESIGN -> resign(command.getGameID(), userData, session);
        }
    }

    // Method invoked when a WebSocket error occurs
    @OnWebSocketError
    public void onError(Throwable error) {
        System.err.println("WebSocket Error: " + error.getMessage());
    }

    // Handles the connection of a user to a game session
    private void connect(Auth user, Integer gameID, Session session) throws IOException, DataAccessException {
        System.out.println("User Connected: " + session);
        user = checkForUser(user, session);
        if (user != null) {
            Connect connect = new Connect(user.username(), gameID, session);
            GameData game = checkForGame(gameID, session, user);

            if (game != null) {
                connects.add(connect);
                sendLoadGameMessage(game, session);
                broadcastNotification(gameID, user.username(), user.username() + " joined the game as " + determineJoinType(user, game));
            }
        }
    }

    // Validates the user based on the authentication token
    private Auth checkForUser(Auth user, Session session) throws IOException {
        try {
            return authS.authenticate(user.authToken());
        } catch (Exception e) {
            sendErrorMessage(session, "Authorization failed");
            return null;
        }
    }

    // Validates if a game session is valid using the game ID
    private GameData checkForGame(Integer gameID, Session session, Auth user) throws DataAccessException, IOException {
        GameData game = gameS.getGames(gameID);
        if (game != null && game.gameID() == gameID) {
            return game;
        }
        sendErrorMessage(session, "Invalid Game ID");
        return null;
    }

    // Handles the MAKE_MOVE command from the client
    private void makeMove(Integer gameID, ChessMove move, Auth user, Session session) throws IOException, DataAccessException, InvalidMoveException {
        user = checkForUser(user, session);
        if (user == null) {return;}

        GameData game = checkForGame(gameID, session, user);
        if (game == null) {return;}

        // Pre-move check: Is the game already over?
        if (game.game().isGameWon()) {
            sendErrorMessage(session, "The game is already over.");
            return;
        }

        // Validate if it's the right turn
        if (!isCorrectTurn(game, user)) {
            sendErrorMessage(session, "It is not your turn");
            return;
        }

        // Attempt to make the move
        if (attemptMove(game, move, user, session)) {
            // Post-move check: Has the game ended after the move?
            if (game.game().isGameWon()) {
                concludeGame(game, gameID);
            } else {
                // Continue the game if not won
                broadcastLoadGame(gameID, game);
            }
        }
    }

    // Checks if it's the correct player's turn
    private boolean isCorrectTurn(GameData game, Auth user) {
        boolean isWhitesTurn = game.game().getTeamTurn() == ChessGame.TeamColor.WHITE;
        return (isWhitesTurn && game.whiteUsername().equals(user.username()))
                || (!isWhitesTurn && game.blackUsername().equals(user.username()));
    }

    // Sends an error message to the client session
    private void sendErrorMessage(Session session, String errorMessage) throws IOException {
        var error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        error.setErrorMessage(errorMessage);
        session.getRemote().sendString(new Gson().toJson(error));
    }

    // Tries to make a move in the game
    private boolean attemptMove(GameData game, ChessMove move, Auth user, Session session)
            throws IOException,
            InvalidMoveException,
            DataAccessException {
        ChessGame.TeamColor teamColor = determineTeamColor(user, game);

        if (teamColor == null || !game.game().validMoves(move.getStartPosition()).contains(move)) {
            sendErrorMessage(session, "Invalid Move");
            return false;
        }
        game.game().makeMove(move);
        gameS.updateGame(game.gameID(), game);

        // Notify move
        String notificationMessage = teamColor == ChessGame.TeamColor.WHITE ? "White Moved" : "Black Moved";
        broadcastNotification(game.gameID(), user.username(), notificationMessage);
        return true;
    }

    // Concludes the game when a winning condition is met
    private void concludeGame(GameData game, Integer gameID) throws IOException, DataAccessException {
        ChessGame.TeamColor winner = game.game().getWinner();
        gameS.updateGame(gameID, game);

        String victoryMessage = winner == ChessGame.TeamColor.WHITE ? "White has won the game" : "Black has won the game";
        broadcastNotification(gameID, "", victoryMessage);
    }

    // Broadcasts the current game state to all connected users
    private void broadcastLoadGame(Integer gameID, GameData game) throws IOException {
        if (!game.game().isGameWon()) {
            var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game);
            connects.broadcast(gameID, "", loadGame);
        }
    }

    // Determines the team color of the user in the game
    private ChessGame.TeamColor determineTeamColor(Auth user, GameData game) {
        if (user.username().equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (user.username().equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    // Ends the game and broadcasts a notification to users about the game's conclusion
    private void endGame(GameData game, ChessGame.TeamColor winner, String message) throws DataAccessException, IOException {
        game.game().setGameWon(true);
        game.game().setWinner(winner);
        gameS.updateGame(game.gameID(), game);
        broadcastNotification(game.gameID(), "", message);
    }

    // Handles when a user leaves a game
    private void leave(Integer gameID, Auth user) throws IOException {
        try {
            connects.removeUser(user.username(), gameID);
            gameS.leaveGame(authS, user.authToken(), gameID);
            broadcastNotification(gameID, user.username(), user.username() + " has left the game");
        } catch (Exception e) {
            System.err.println("Error processing leave request: " + e.getMessage());
        }
    }

    // Handles when a user resigns from a game
    private void resign(Integer gameID, Auth user, Session session) throws IOException, DataAccessException {
        user = checkForUser(user, session);
        if (user == null) {return;}

        GameData game = checkForGame(gameID, session, user);
        if (game != null) {
            ChessGame.TeamColor winner = determineResignationWinner(user, game);
            if (winner == null || game.game().isGameWon()) {
                sendErrorMessage(session, "Cannot resign from a finished game");
            } else {
                endGame(game, winner, user.username() + " has resigned");
            }
        }
    }

    // Determines the winner when a user resigns
    private ChessGame.TeamColor determineResignationWinner(Auth user, GameData game) {
        if (user.username().equals(game.whiteUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else if (user.username().equals(game.blackUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        return null;
    }

    // Broadcasts a notification to users
    private void broadcastNotification(Integer gameID, String username, String message) throws IOException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        notification.setMessage(message);
        connects.broadcast(gameID, username, notification);
    }

    // Sends a message to load the game state to a specific client session
    private void sendLoadGameMessage(GameData game, Session session) throws IOException {
        var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGame.setGame(game);
        session.getRemote().sendString(new Gson().toJson(loadGame));
    }

    // Determines how a user joined the game (e.g., as White, Black, or Observer)
    private String determineJoinType(Auth user, GameData game) {
        if (user.username().equals(game.blackUsername())) {
            return "Black";
        } else if (user.username().equals(game.whiteUsername())) {
            return "White";
        }
        return "Observer";
    }
}