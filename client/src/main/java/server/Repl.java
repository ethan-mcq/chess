package server;

import static ui.EscapeSequences.*;
import chess.ChessGame;
import com.google.gson.JsonObject;
import exception.InputException;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * The Repl class implements a Read-Eval-Print Loop (REPL) for interacting with the chess server.
 */
public class Repl {

    private String serverURL;
    private ServerFacade serverFacade;
    private String username;
    private String authToken;
    private boolean signedIn;
    private Scanner scanner;
    private List<Game> games;
    private Integer nextGameID;

    /**
     * Constructor initializes the REPL with the specified server URL.
     * @param serverUrl The URL of the server to connect to.
     */
    public Repl(String serverUrl) {
        this.serverURL = serverUrl;
        this.serverFacade = new ServerFacade(serverURL);
        this.scanner = new Scanner(System.in);
        this.games = new ArrayList<>();
        this.nextGameID = 1;
    }

    /**
     * Handles pre-login commands in the REPL.
     */
    public void preLogin() {
        String result = "";
        System.out.println(SET_TEXT_COLOR_MAGENTA + verifyPreLogin("HELP"));
        while (!result.equals("See you next time!")) {
            System.out.print(RESET_TEXT_COLOR + " >> ");
            String line = scanner.nextLine();
            try {
                result = verifyPreLogin(line.toUpperCase());
                System.out.println(SET_TEXT_COLOR_MAGENTA + result);
                if (signedIn) {
                    postLogin();
                    System.out.println(SET_TEXT_COLOR_MAGENTA + verifyPreLogin("HELP"));
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Handles post-login commands in the REPL.
     */
    public void postLogin() {
        String result = "";
        System.out.println(SET_TEXT_COLOR_MAGENTA + verifyPostLogin("HELP"));
        while (signedIn) {
            System.out.print(RESET_TEXT_COLOR + username + " > ");
            String line = scanner.nextLine();

            try {
                result = verifyPostLogin(line.toUpperCase());
                System.out.println(SET_TEXT_COLOR_MAGENTA + result);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Evaluates input commands before the user logs in.
     * @param input The input command as a string.
     * @return The evaluation result as a string.
     */
    public String verifyPreLogin(String input) {
        input = input.trim();
        try {
            String[] tokens = input.split(" ");
            if (input.isEmpty()) {
                return SET_TEXT_COLOR_RED + "No command";
            }
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "REGISTER" -> registerNewUser(params);
                case "LOGIN" -> loginUser(params);
                case "QUIT" -> "See you next time!";
                case "HELP" -> RESET_TEXT_COLOR + """
                        
                        Try to navigate with a command below!
                        
                        REGISTER <username> <password> <email> | Register new user
                        LOGIN  <username> <password>         | Log into account
                        QUIT                                 | End Chess
                        HELP                                 | Get help!
                        """;
                default -> SET_TEXT_COLOR_RED + "Unknown Command, TRY AGAIN";
            };
        } catch (InputException e) {
            return e.getMessage();
        }
    }

    /**
     * Registers a new user with the provided parameters.
     * @param params Registration parameters.
     * @return The registration result.
     * @throws InputException If input is invalid.
     */
    public String registerNewUser(String... params) throws InputException {
        if (params.length == 3) {
            JsonObject json = new JsonObject();
            json.addProperty("username", params[0]);
            json.addProperty("password", params[1]);
            json.addProperty("email", params[2]);
            Response reply = serverFacade.registerNewUser(json);
            authToken = reply.authToken();
            username = reply.username();
            signedIn = true;
            return "You are now logged in, " + username;
        } else {
            return SET_TEXT_COLOR_RED + "Expected: REGISTER <username> <password> <email>";
        }
    }

    /**
     * Logs in a user with the provided parameters.
     * @param params Login parameters.
     * @return The login result.
     * @throws InputException If input is invalid.
     */
    public String loginUser(String... params) throws InputException {
        if (params.length == 2) {
            JsonObject json = new JsonObject();
            json.addProperty("username", params[0]);
            json.addProperty("password", params[1]);
            Response reply = serverFacade.loginUser(json);
            authToken = reply.authToken();
            username = reply.username();
            signedIn = true;
            return "You are now logged in, " + username;
        } else {
            return SET_TEXT_COLOR_RED + "Expected: LOGIN <username> <password>";
        }
    }

    /**
     * Evaluates input commands after the user logs in.
     * @param input The input command as a string.
     * @return The evaluation result as a string.
     */
    public String verifyPostLogin(String input) {
        input = input.trim();
        try {
            String[] tokens = input.split(" ");
            if (input.isEmpty()) {
                return SET_TEXT_COLOR_RED + "No command";
            }
            String cmd = tokens[0];
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "LOGOUT" -> logoutUser(params);
                case "CREATE" -> createGame(params);
                case "LIST" -> listGames(params);
                case "PLAY" -> joinGame(params);
                case "OBSERVE" -> observeGame(params);
                case "HELP" -> RESET_TEXT_COLOR + """
                        
                        LOGOUT                               | Logout
                        CREATE  <gameName>                   | Create new game
                        LIST                                 | List all games
                        PLAY    <gameID> [BLACK|WHITE]    | Join a game as BLACK or WHITE player
                        OBSERVE <gameID>                  | Observe a game
                        HELP                                 | Get help!
                        """;
                default -> SET_TEXT_COLOR_RED + "Unknown Command, TRY AGAIN";
            };
        } catch (InputException e) {
            return e.getMessage();
        }
    }

    /**
     * Logs out a user with the provided parameters.
     * @param params Logout parameters.
     * @return The logout result.
     * @throws InputException If input is invalid.
     */
    public String logoutUser(String... params) throws InputException {
        if (params.length == 0) {
            serverFacade.logoutUser(authToken);
            signedIn = false;
            return "Logged out, see you next time!";
        } else {
            return SET_TEXT_COLOR_RED + "Expected: LOGOUT (no parameters)";
        }
    }

    /**
     * Creates a new game with the provided parameters.
     * @param params Game creation parameters.
     * @return The game creation result.
     * @throws InputException If input is invalid.
     */
    public String createGame(String... params) throws InputException {
        if (params.length == 1) {
            JsonObject json = new JsonObject();
            json.addProperty("gameName", params[0]);
            Response reply = serverFacade.createNewGame(json, authToken);
            games.add(new Game(nextGameID, params[0], reply.gameID, null, null));
            String message = "Created game named: \"" + params[0] + ". Game ID is: " + nextGameID;
            nextGameID++;
            return message;
        } else {
            return SET_TEXT_COLOR_RED + "Expected: CREATE <gameName>";
        }
    }

    /**
     * Lists available games for the user.
     * @param params Parameters for listing games.
     * @return The list of games as a string.
     * @throws InputException If input is invalid.
     */
    public String listGames(String... params) throws InputException {
        if (params.length == 0) {
            Response reply = serverFacade.listAllGames(authToken);
            updateLocalGamesList(reply);
            StringBuilder s = new StringBuilder();
            for (Game game : games) {
                s.append(game.toString()).append("\n");
            }
            return s.toString();
        } else {
            return SET_TEXT_COLOR_RED + "Expected: LIST (no parameters)";
        }
    }

    // Helper method to update local games list with server response
    private void updateLocalGamesList(Response reply) {
        boolean needsAdded;
        for (GameResponse serverGame : reply.games) {
            needsAdded = true;
            for (Game localGame : games) {
                if (localGame.getGameID() == serverGame.gameID()) {
                    localGame.setBlack(serverGame.blackUsername());
                    localGame.setWhite(serverGame.whiteUsername());
                    needsAdded = false;
                }
            }
            if (needsAdded) {
                games.add(new Game(nextGameID, serverGame.gameName(), serverGame.gameID(),
                        serverGame.whiteUsername(), serverGame.blackUsername()));
                nextGameID++;
            }
        }
    }

    /**
     * Joins a game with the provided parameters.
     * @param params Join game parameters.
     * @return The result of joining the game.
     * @throws InputException If input is invalid.
     */
    public String joinGame(String... params) throws InputException {
        if (params.length == 2 && isValidGameAndColor(params)) {
            JsonObject json = new JsonObject();
            for (Game game : games) {
                if (game.getIndex() == Integer.parseInt(params[0])) {
                    json.addProperty("gameID", game.getGameID());
                }
            }
            json.addProperty("playerColor", params[1].toUpperCase());
            serverFacade.joinGame(json, authToken);
            renderGameBoard();
            return "Joined Game, good luck!";
        } else {
            return SET_TEXT_COLOR_RED + "Expected: PLAY <gameID> [BLACK|WHITE]";
        }
    }

    // Helper method to validate game index and color
    private boolean isValidGameAndColor(String[] params) {
        int gameID = Integer.parseInt(params[0]);
        String color = params[1];
        return gameID < nextGameID && gameID > 0 &&
                (color.equalsIgnoreCase("white") || color.equalsIgnoreCase("black"));
    }

    // Helper method to render game board
    private void renderGameBoard() {
        Render render = new Render();
        System.out.println(render.renderBoard(new ChessGame().getBoard(), ChessGame.TeamColor.WHITE));
        System.out.println(render.renderBoard(new ChessGame().getBoard(), ChessGame.TeamColor.BLACK));
    }

    /**
     * Observes a game with the provided parameters.
     * @param params Observe game parameters.
     * @return The result of observing the game.
     * @throws InputException If input is invalid.
     */
    public String observeGame(String... params) throws InputException {
        if (params.length == 1 && isValidgameID(params[0])) {
            renderGameBoard();
            return "Observing Game";
        } else {
            return SET_TEXT_COLOR_RED + "Expected: OBSERVE <gameID>";
        }
    }

    // Helper method to validate game index
    private boolean isValidgameID(String gameIDStr) {
        int gameID = Integer.parseInt(gameIDStr);
        return gameID < nextGameID && gameID >= 1;
    }
}