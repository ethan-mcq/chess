package dataaccess;

import model.*;
public interface gameDAO extends baseDAO {

    gameList getAllGames() throws DataAccessException;
    gameData getGames(int gameID) throws DataAccessException;
    gameData joinGame(join join) throws DataAccessException;
    gameData createGame(gameData gameData) throws DataAccessException;
    void deleteAllGames() throws DataAccessException;
}
