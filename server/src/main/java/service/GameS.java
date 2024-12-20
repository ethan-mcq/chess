package service;

import dataaccess.*;
import model.*;

/**
 * Service class that handles game-related operations.
 */
public class GameS extends BaseS {
    /**
     * Constructs a new game service with the given Data access object.
     *
     * @param dataAccess The Data access object
     */
    public GameS(Data dataAccess) {
        super(dataAccess);
    }

    /**
     * Creates a new game.
     *
     * @param game The game Data
     * @return The created game Data
     * @throws DataAccessException If there is an issue accessing Data
     */
    public GameData createGame(GameData game) throws DataAccessException {
        GameDao gameDataAccess = this.dataAccess.fetchClientData(GameDao.class);
        return gameDataAccess.createGame(game);
    }

    /**
     * Retrieves the list of all games.
     *
     * @return The list of all games
     * @throws DataAccessException If there is an issue accessing Data
     */
    public GameList getAllGames() throws DataAccessException {
        GameDao gameDataAccess = this.dataAccess.fetchClientData(GameDao.class);
        return gameDataAccess.getAllGames();
    }

    /**
     * Retrieves the list of all games.
     *
     * @return The gameID info
     * @throws DataAccessException If there is an issue accessing Data
     */
    public GameData getGames(int gameID) throws DataAccessException {
        GameDao gameDataAccess = this.dataAccess.fetchClientData(GameDao.class);
        return gameDataAccess.getGames(gameID);
    }

    /**
     * Allows a user to Join a game.
     *
     * @param join The Join game request
     * @return The game Data
     * @throws DataAccessException If there is an issue accessing Data
     */
    public GameData joinGame(Join join) throws DataAccessException {
        GameDao gameDataAccess = this.dataAccess.fetchClientData(GameDao.class);
        return gameDataAccess.joinGame(join);
    }

    public void updateGame (Integer gameID, GameData game) throws DataAccessException {
        GameDao gameDataAccess = this.dataAccess.fetchClientData(GameDao.class);
        gameDataAccess.updateGame(gameID, game);
    }

    public void leaveGame(AuthS authS, String token, Integer gameID) throws DataAccessException {
        GameDao gameDataAccess = this.dataAccess.fetchClientData(GameDao.class);
        Auth user = authS.authenticate(token);
        GameData currentGame = gameDataAccess.getGames(gameID);

        String newBlackUsername = currentGame.blackUsername();
        String newWhiteUsername = currentGame.whiteUsername();

        if (user.username().equals(currentGame.blackUsername())) {
            newBlackUsername = null;
        } else if (user.username().equals(currentGame.whiteUsername())) {
            newWhiteUsername = null;
        }

        GameData updatedGame = new GameData(
                currentGame.gameID(),
                currentGame.gameName(),
                newWhiteUsername,
                newBlackUsername,
                currentGame.game()
        );

        gameDataAccess.updateGame(gameID, updatedGame);
    }


    /**
     * Deletes all games.
     *
     * @throws DataAccessException If there is an issue accessing Data
     */
    public void deleteAll() throws DataAccessException {
        GameDao gameDataAccess = this.dataAccess.fetchClientData(GameDao.class);
        gameDataAccess.deleteAllGames();
    }
}