package dataaccess;

import model.*;
public interface GameDao extends BaseDao {

    GameList getAllGames() throws DataAccessException;
    GameData getGames(int gameID) throws DataAccessException;
    GameData joinGame(Join join) throws DataAccessException;
    GameData createGame(GameData gameData) throws DataAccessException;
    void deleteAllGames() throws DataAccessException;
}
