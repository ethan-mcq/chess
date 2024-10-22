package service;

import dataaccess.*;
import model.*;

public class gameS extends baseS {
    public gameS(data dataAccess) {
        super(dataAccess);
    }

    public gameData createGame(gameData game) throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        return gameDataAccess.createGame(game);
    }

    public gameList getAllGames() throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        return gameDataAccess.getAllGames();
    }

    public gameData joinGame(join join) throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        return gameDataAccess.joinGame(join);
    }

    public void deleteAll() throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        gameDataAccess.deleteAllGames();
    }
}
