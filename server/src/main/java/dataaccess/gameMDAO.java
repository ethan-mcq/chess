package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class gameMDAO implements gameDAO {

    private final Map<Integer, gameData> gameDataMap = new ConcurrentHashMap<>();

    @Override
    public gameList getAllGames() throws DataAccessException {
        List<gameResponse> gameDataResponses = gameDataMap.values().stream()
                .map(gameData -> new gameResponse(
                        gameData.gameID(),
                        gameData.gameName(),
                        gameData.whiteUsername(),
                        gameData.blackUsername()))
                .toList();

        return new gameList(new ArrayList<>(gameDataResponses));
    }

    @Override
    public gameData getGames(int gameID) throws DataAccessException {
        return gameDataMap.get(gameID);
    }

    @Override
    public gameData joinGame(join joinRequest) throws DataAccessException {
        int gameID = joinRequest.gameID();
        gameData gameData = gameDataMap.get(gameID);

        if (gameData == null) {
            return null;
        }

        switch (joinRequest.playerColor().toUpperCase()) {
            case "WHITE":
                return joinGameAsWhite(joinRequest, gameData);
            case "BLACK":
                return joinGameAsBlack(joinRequest, gameData);
            default:
                return null;
        }
    }

    private gameData joinGameAsWhite(join joinRequest, gameData gameData) {
        if (gameData.whiteUsername() == null) {
            gameData newGameData = createNewGameData(gameData, joinRequest.username(), gameData.blackUsername());
            gameDataMap.put(newGameData.gameID(), newGameData);
            return newGameData;
        }
        return null;
    }

    private gameData joinGameAsBlack(join joinRequest, gameData gameData) {
        if (gameData.blackUsername() == null) {
            gameData newGameData = createNewGameData(gameData, gameData.whiteUsername(), joinRequest.username());
            gameDataMap.put(newGameData.gameID(), newGameData);
            return newGameData;
        }
        return null;
    }

    private gameData createNewGameData(gameData gameData, String whiteUsername, String blackUsername) {
        return new gameData(gameData.gameID(), gameData.gameName(), whiteUsername, blackUsername, gameData.game());
    }

    @Override
    public gameData createGame(gameData gameData) throws DataAccessException {
        int newGameID = gameDataMap.size() + 1;
        gameData newGameData = new gameData(newGameID, gameData.gameName(), gameData.blackUsername(), gameData.whiteUsername(), gameData.game());
        gameDataMap.put(newGameID, newGameData);
        return newGameData;
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        gameDataMap.clear();
    }
}