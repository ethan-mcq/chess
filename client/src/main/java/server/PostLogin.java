package server;

import chess.*;
import exception.InputException;
import model.*;

import java.util.Arrays;

public class PostLogin implements Client {
    private final String serverUrl;
    private final Repl repl;
    private final Notification notificationHandler;
    private GameData[] games;
    private ServerFacade server;

    public PostLogin(String serverUrl, Repl repl, Notification notificationHandler) {
        this.serverUrl = serverUrl;
        this.repl = repl;
        this.notificationHandler = notificationHandler;
    }

    @Override
    public String checker(String command) {
        try {
            helper();
            String[] tokens = command.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "LOGOUT" -> logout();
                case "CREATE" -> createGame(params);
                case "LIST" -> listGames();
                case "PLAY" -> playGame(params);
                case "OBSERVE" -> observeGame(params);
                case "QUIT" -> "quit";
                default -> helper();
            };
        } catch (IllegalArgumentException e) {
            return handleIllegalArgumentException(e);
        } catch (IndexOutOfBoundsException e) {
            return "WRONG, AGAIN";
        } catch (Exception e) {
            return handleGeneralException(e);
        }
    }

    @Override
    public String helper() {
        return """
                LOGOUT                             | Logout
                CREATE  <gameName>                 | Create new game
                LIST                               | List all games
                PLAY    <gameID> [BLACK|WHITE]     | Join a game as BLACK or WHITE player
                OBSERVE <gameID>                   | Observe a game
                HELP                               | Get help!
                QUIT                               | QUIT
                """;
    }

    private String logout() throws InputException {
        initServer();
        server.logout(this.repl.getAuthData());
        this.repl.changeState(State.SIGNEDOUT);
        return "LOGGED OUT";
    }

    private String createGame(String[] params) throws InputException {
        if (params.length < 1) {
            throw new IllegalArgumentException("CREATE command requires a game name.");
        }

        String gameName = params[0];
        initServer();
        GameData newGame = new GameData(0, gameName, null, null, null);
        server.createGame(this.repl.getAuthData(), newGame);
        return "CREATED GAME " + gameName;
    }

    public void loadGames() throws Exception {
        initServer();
        this.games = server.listGames(this.repl.getAuthData());
    }

    private String listGames() throws Exception {
        loadGames();

        StringBuilder gamesList = new StringBuilder();
        for (int i = 0; i < this.games.length; i++) {
            GameData game = this.games[i];
            String whitePlayer = game.whiteUsername() != null ? game.whiteUsername() : "OPEN";
            String blackPlayer = game.blackUsername() != null ? game.blackUsername() : "OPEN";
            gamesList.append(String.format(" Game #: %d\t Game Name: %s\t White Player: %s\t Black Player: %s%n",
                    i + 1, game.gameName(), whitePlayer, blackPlayer));
        }
        return gamesList.toString();
    }

    private String playGame(String[] params) throws Exception {
        if (params.length < 2) {
            throw new IllegalArgumentException("PLAY command requires a game ID and a team color.");
        }

        int gameNumber = Integer.parseInt(params[0]);
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(params[1].toUpperCase());

        initServer();
        server.joinGame(this.repl.getAuthData(), teamColor, games[gameNumber - 1].gameID());
        this.repl.changeState(State.INGAME);
        this.repl.joiningGame(games[gameNumber - 1]);

        return "JOINING GAME #" + gameNumber;
    }

    private String observeGame(String[] params) throws Exception {
        if (params.length < 1) {
            throw new IllegalArgumentException("OBSERVE command requires a game ID.");
        }

        int gameNumber = Integer.parseInt(params[0]);

        if (games[gameNumber - 1] == null) {
            throw new Exception("WRONG NUMBER");
        }

        this.repl.changeState(State.OBSERVING);
        this.repl.joiningGame(games[gameNumber - 1]);
        return "OBSERVING GAME #" + gameNumber;
    }

    private void initServer() {
        if (server == null) {
            server = new ServerFacade(this.serverUrl);
        }
    }

    private String handleIllegalArgumentException(IllegalArgumentException e) {
        if (e.getMessage().contains("No checker constant")) {
            return "BLACK | WHITE";
        } else if (e.getMessage().contains("For input string")) {
            return "GAME NOT IN LIST";
        } else {
            return "WRONG, AGAIN";
        }
    }

    private String handleGeneralException(Exception e) {
        return switch (e.getMessage()) {
            case "REFUSED" -> "CONNECTION REFUSED";
            case "403" -> "USERNAME ALREADY TAKEN";
            case "401" -> "INCORRECT USERNAME OR PASSWORD";
            default -> "SOMETHING WENT WRONG, PLEASE TRY AGAIN";
        };
    }
}