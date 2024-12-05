package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameMdao implements GameDao {

    private final Map<Integer, GameData> gameDataMap = new ConcurrentHashMap<>();

    @Override
    public GameList getAllGames() throws DataAccessException {
        List<GameResponse> gameDataResponses = new ArrayList<>();
        for (GameData gameData : gameDataMap.values()) {
            gameDataResponses.add(new GameResponse(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName()
            ));
        }
        return new GameList(new ArrayList<>(gameDataResponses));
    }

    @Override
    public GameData getGames(int gameID) throws DataAccessException {
        if(gameDataMap.containsKey(gameID)) {
            return gameDataMap.get(gameID);
        }
        return null;
    }

    @Override
    public GameData joinGame(Join joinRequest) throws DataAccessException {
        int gameID = joinRequest.gameID();
        if (!gameDataMap.containsKey(gameID)) {
            return null;
        }

        GameData gameData = gameDataMap.get(gameID);
        String playerColor = joinRequest.playerColor();

        if ("WHITE".equals(playerColor)) {
            return joinGameAsWhite(joinRequest, gameData);
        } else {
            return joinGameAsBlack(joinRequest, gameData);
        }
    }

    private GameData joinGameAsWhite(Join joinRequest, GameData gameData) {
        if (gameData.whiteUsername() == null) {
            GameData newGameData = createNewGameData(gameData, joinRequest.username(), gameData.blackUsername());
            gameDataMap.put(newGameData.gameID(), newGameData);
            return newGameData;
        }
        return null;
    }

    private GameData joinGameAsBlack(Join joinRequest, GameData gameData) {
        if (gameData.blackUsername() == null) {
            GameData newGameData = createNewGameData(gameData, gameData.whiteUsername(), joinRequest.username());
            gameDataMap.put(newGameData.gameID(), newGameData);
            return newGameData;
        }
        return null;
    }

    private GameData createNewGameData(GameData gameData, String whiteUsername, String blackUsername) {
        return new GameData(gameData.gameID(), gameData.gameName(), whiteUsername, blackUsername, gameData.game());
    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        int newGameID = gameDataMap.size() + 1;
        GameData newGameData = new GameData(newGameID, gameData.gameName(), gameData.blackUsername(), gameData.whiteUsername(), gameData.game());
        gameDataMap.put(newGameID, newGameData);
        return newGameData;
    }

    @Override
    public GameData updateGame(int gameID, GameData game) throws DataAccessException {
        gameDataMap.replace(gameID, game);
        return gameDataMap.get(gameID);
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        gameDataMap.clear();
    }
}