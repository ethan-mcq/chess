package service;

import dataaccess.*;
import model.*;

/**
 * Service class that handles game-related operations.
 */
public class gameS extends baseS {

    /**
     * Constructs a new game service with the given data access object.
     *
     * @param dataAccess The data access object
     */
    public gameS(data dataAccess) {
        super(dataAccess);
    }

    /**
     * Creates a new game.
     *
     * @param game The game data
     * @return The created game data
     * @throws DataAccessException If there is an issue accessing data
     */
    public gameData createGame(gameData game) throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        return gameDataAccess.createGame(game);
    }

    /**
     * Retrieves the list of all games.
     *
     * @return The list of all games
     * @throws DataAccessException If there is an issue accessing data
     */
    public gameList getAllGames() throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        return gameDataAccess.getAllGames();
    }

    /**
     * Retrieves the list of all games.
     *
     * @return The gameID info
     * @throws DataAccessException If there is an issue accessing data
     */
    public gameData getGames(int gameID) throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        return gameDataAccess.getGames(gameID);
    }

    /**
     * Allows a user to join a game.
     *
     * @param join The join game request
     * @return The game data
     * @throws DataAccessException If there is an issue accessing data
     */
    public gameData joinGame(join join) throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        return gameDataAccess.joinGame(join);
    }

    /**
     * Deletes all games.
     *
     * @throws DataAccessException If there is an issue accessing data
     */
    public void deleteAll() throws DataAccessException {
        gameDAO gameDataAccess = this.dataAccess.fetchClientData(gameDAO.class);
        gameDataAccess.deleteAllGames();
    }
}