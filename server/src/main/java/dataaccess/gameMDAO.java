package dataaccess;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class gameMDAO implements gameDAO {
    Map<Integer, gameData> gameDataMap = new ConcurrentHashMap<>();

    @Override
    public gameList getAllGames() {
        List<gameResponse> gameDataResponses = new ArrayList<>();
        for (gameData gameData : gameDataMap.values()) {
            gameDataResponses.add(new gameResponse(
                    gameData.gameID(),
                    gameData.whiteUser(),
                    gameData.blackUser(),
                    gameData.gameName()
            ));
        }
        return new gameList(new ArrayList<>(gameDataResponses));
    }

    @Override
    public gameData getGames(int gameID) {
        if(gameDataMap.containsKey(gameID)) {
            return gameDataMap.get(gameID);
        }
        return null;
    }

    @Override
    public gameData joinGame(join join) {
        int gameID = join.gameID();
        if (!gameDataMap.containsKey(gameID)) {
            return null;
        }

        gameData gameData = gameDataMap.get(gameID);
        String playerColor = join.playerColor();

        if ("WHITE".equals(playerColor)) {
            return joinGameAsWhite(join, gameData);
        } else {
            return joinGameAsBlack(join, gameData);
        }
    }

    private gameData joinGameAsWhite(join joinGameRequest, gameData gameData) {
        if (gameData.whiteUser() == null) {
            gameData newgameData = createNewgameData(gameData, joinGameRequest.username(), gameData.blackUser());
            gameDataMap.put(newgameData.gameID(), newgameData);
            return newgameData;
        }
        return null;
    }

    private gameData joinGameAsBlack(join join, gameData gameData) {
        if (gameData.blackUser() == null) {
            gameData newgameData = createNewgameData(gameData, gameData.whiteUser(), join.username());
            gameDataMap.put(newgameData.gameID(), newgameData);
            return newgameData;
        }
        return null;
    }

    private gameData createNewgameData(gameData gameData, String whiteUser, String blackUser) {
        return new gameData(gameData.gameID(), gameData.gameName(), gameData.game(), whiteUser, blackUser);
    }

    @Override
    public gameData createGame(gameData gameData) {
        gameData newgameData = new gameData(gameDataMap.size() + 1, gameData.gameName(), gameData.game(), gameData.blackUser(), gameData.whiteUser());
        gameDataMap.put(gameDataMap.size()+1, newgameData);
        return newgameData;
    }

    @Override
    public void deleteAllGames() {
        gameDataMap.clear();
    }
}
