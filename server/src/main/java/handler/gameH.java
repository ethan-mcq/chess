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

public class gameH extends baseH {

    private static final Gson gson = new Gson();

    public gameH(services services) {
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
     * Handles the request to join a game.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the response
     * @throws problem If there is an issue with the join request
     * @throws DataAccessException If there is an issue accessing data
     */
    public Object joinGame(Request httpRequest, Response httpResponse) throws problem, DataAccessException {
        gameS gameService = this.services.fetchClientService(gameS.class);
        authS authService = this.services.fetchClientService(authS.class);
        String authToken = httpRequest.headers("Authorization");

        auth auth = authService.getAuthData(authToken);
        String username = auth.username();

        JsonObject jsonObject = gson.fromJson(httpRequest.body(), JsonObject.class);
        jsonObject.addProperty("username", username);
        String modifiedJson = gson.toJson(jsonObject);

        join joinGame = join.fromJson(modifiedJson);

        if (joinGame == null) {
            throw new problem("Bad Request", 400);
        }

        gameData game = gameService.joinGame(joinGame);
        if (game == null) {
            throw new problem("Game Taken", 403);
        }

        this.setSuccessHeaders(httpResponse);
        Map<String, Object> jsonResponse = new HashMap<>();
        return gson.toJson(jsonResponse);
    }

    /**
     * Handles the request to create a new game.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the response
     * @throws DataAccessException If there is an issue accessing data
     * @throws problem If there is an issue with the create request
     */
    public Object createGame(Request httpRequest, Response httpResponse) throws DataAccessException, problem {
        gameS gameService = this.services.fetchClientService(gameS.class);
        createRequest created = createRequest.fromJson(httpRequest.body());

        if (created == null) {
            throw new problem("Bad Request", 400);
        }

        gameData game = new gameData(0, created.gameName(), new ChessGame(), null, null);
        gameData newGame = gameService.createGame(game);

        this.setSuccessHeaders(httpResponse);
        return gson.toJson(new createResponse(newGame.gameID()));
    }

    /**
     * Handles the request to get the list of games.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the list of games
     * @throws DataAccessException If there is an issue accessing data
     */
    public Object getListOfGames(Request httpRequest, Response httpResponse) throws DataAccessException {
        gameS gameService = this.services.fetchClientService(gameS.class);
        gameList gameListResult = gameService.getAllGames();

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("games", gameListResult.getGames());

        this.setSuccessHeaders(httpResponse);
        return gson.toJson(jsonResponse);
    }
}