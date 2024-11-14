package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import model.*;
import service.*;
import spark.*;
import java.util.HashMap;
import java.util.Map;

public class GameH extends BaseH {

    private static final Gson GSON = new Gson();

    public GameH(Services services) {
        super(services);
        this.root = "/game";
    }

    @Override
    public void initHandler() {
        Spark.get(this.root, verifyAuth(this::getListOfGames));
        Spark.post(this.root, verifyAuth(this::createGame));
        Spark.put(this.root, verifyAuth(this::joinGame));
    }

    /**
     * Handles the request to Join a game.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the response
     * @throws Problem If there is an issue with the Join request
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Object joinGame(Request httpRequest, Response httpResponse) throws Problem, DataAccessException {
        GameS gameService = this.services.fetchClientService(GameS.class);
        AuthS authService = this.services.fetchClientService(AuthS.class);
        String authToken = httpRequest.headers("Authorization");

        Auth auth = authService.getAuthData(authToken);
        String username = auth.username();

        JsonObject jsonObject = GSON.fromJson(httpRequest.body(), JsonObject.class);
        jsonObject.addProperty("username", username);
        String modifiedJson = GSON.toJson(jsonObject);

        Join joinGame = Join.fromJson(modifiedJson);

        if (joinGame == null) {
            throw new Problem("Bad Request", 400);
        }

        GameData game = gameService.joinGame(joinGame);
        if (game == null) {
            throw new Problem("Game Taken", 403);
        }

        this.setSuccessHeaders(httpResponse);
        Map<String, Object> jsonResponse = new HashMap<>();
        return GSON.toJson(jsonResponse);
    }

    /**
     * Handles the request to create a new game.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the response
     * @throws DataAccessException If there is an issue accessing Data
     * @throws Problem If there is an issue with the create request
     */
    public Object createGame(Request httpRequest, Response httpResponse) throws DataAccessException, Problem {
        GameS gameService = this.services.fetchClientService(GameS.class);
        CreateRequest created = CreateRequest.fromJson(httpRequest.body());

        if (created == null) {
            throw new Problem("Bad Request", 400);
        }

        GameData game = new GameData(0, created.gameName(), null, null, new ChessGame());
        GameData newGame = gameService.createGame(game);

        this.setSuccessHeaders(httpResponse);
        return GSON.toJson(new CreateResponse(newGame.gameID()));
    }

    /**
     * Handles the request to get the list of games.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the list of games
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Object getListOfGames(Request httpRequest, Response httpResponse) throws DataAccessException {
        GameS gameService = this.services.fetchClientService(GameS.class);
        GameList gameListResult = gameService.getAllGames();

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("games", gameListResult.getGames());

        this.setSuccessHeaders(httpResponse);
        return GSON.toJson(jsonResponse);
    }
}