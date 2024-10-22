package dataaccess;

import model.*;

public interface gameDAO extends baseDAO {

    gameList getAllGames();
    gameData getGames(int gameID);
    gameData joinGame(join join);
    gameData createGame(gameData gameData);
    void deleteAllGames();
}
