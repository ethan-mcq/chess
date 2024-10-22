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

public class gameH extends baseH{
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

    public Object joinGame(Request req, Response res) throws problem, DataAccessException {
        gameS gameS = this.services.fetchClientService(gameS.class);
        authS authS = this.services.fetchClientService(authS.class);
        String authToken = req.headers("Authorization");

        auth auth = authS.getAuthData(authToken);
        String username = auth.username();

        JsonObject jsonObject = new Gson().fromJson(req.body(), JsonObject.class);
        jsonObject.addProperty("username", username);
        String modifiedJson = new Gson().toJson(jsonObject);


        join joinGame = join.fromJson(modifiedJson);

        if(joinGame == null) {
            throw new problem("Bad Request", 400);
        }

        gameData game = gameS.joinGame(joinGame);
        if(game == null){
            throw new problem("Game Taken", 403);
        }

        this.setSuccessHeaders(res);
        Map<String, Object> jsonResponse = new HashMap<>();
        return new Gson().toJson(jsonResponse);
    }

    public Object createGame(Request req, Response res) throws DataAccessException, problem {
        gameS gameS = this.services.fetchClientService(gameS.class);
        createRequest created = createRequest.fromJson(req.body());

        if(created == null){
            throw new problem("Bad Request", 400);
        }

        gameData game = new gameData(0, created.gameName(), new ChessGame(), null, null);
        gameData newGame = gameS.createGame(game);

        this.setSuccessHeaders(res);
        return new Gson().toJson(new createResponse(newGame.gameID()));
    }

    public Object getListOfGames(Request req, Response res) throws DataAccessException {
        gameS gameS = this.services.fetchClientService(gameS.class);
        gameList gameListResult = gameS.getAllGames();

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("games", gameListResult.getGames());

        this.setSuccessHeaders(res);
        return new Gson().toJson(jsonResponse);
    }
}
