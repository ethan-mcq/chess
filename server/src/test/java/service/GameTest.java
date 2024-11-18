package src.test.java.dataaccess.service;

import dataaccess.*;
import model.*;
import dataaccess.Data;
import dataaccess.DataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import chess.ChessGame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    private Data DataSource;
    private GameS GameService;
    private GameDao GameDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DataSource = new Data(DataType.MEM_DATA);
        GameService = new GameS(DataSource);
        GameDao = DataSource.fetchClientData(GameDao.class);
    }

    @Test
    @DisplayName("Successfully create a new game")
    public void testCreateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(0, "testGame", null, null, chessGame);
        GameData createdGame = GameService.createGame(newGame);

        assertNotNull(createdGame, "Created game should not be null");
        assertEquals(1, createdGame.gameID(), "Game IDs should match");
    }

    @Test
    @DisplayName("Successfully get all GameS")
    public void testGetAllGameSSuccess() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        GameData game1 = new GameData(0, "testGame1", null, null, chessGame1);
        GameData game2 = new GameData(0, "testGame2", null, null, chessGame2);

        GameService.createGame(game1);
        GameService.createGame(game2);

        GameList allGameS = GameService.getAllGames();

        assertNotNull(allGameS, "Game list should not be null");
        assertEquals(2, allGameS.getGames().size(), "There should be two GameS in the list");
    }

    @Test
    @DisplayName("Successfully join a game as a specific player")
    public void testJoinGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(0, "testGame", null, null, chessGame);
        GameService.createGame(newGame);

        Join joinRequest = new Join("WHITE", "user1", 1);
        GameData joinedGame = GameService.joinGame(joinRequest);

        assertNotNull(joinedGame, "Joined game should not be null");
        assertEquals(1, joinedGame.gameID(), "Game IDs should match");
        assertEquals("user1", joinedGame.whiteUsername(), "White player should match");
    }

    @Test
    @DisplayName("Successfully delete all GameS")
    public void testDeleteAllGameSSuccess() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        GameData game1 = new GameData(0, "testGame1", null, null, chessGame1);
        GameData game2 = new GameData(0, "testGame2", null, null, chessGame2);

        GameService.createGame(game1);
        GameService.createGame(game2);

        GameService.deleteAll();

        GameList allGameS = GameService.getAllGames();

        assertNotNull(allGameS, "Game list should not be null");
        assertTrue(allGameS.getGames().isEmpty(), "Game list should be empty after deletion");
    }

    @Test
    @DisplayName("Deleting all GameS when no GameS exist")
    public void testDeleteAllGameSWhenNoGameSExist() throws DataAccessException {
        GameService.deleteAll();

        GameList allGameS = GameService.getAllGames();

        assertNotNull(allGameS, "Game list should not be null");
        assertTrue(allGameS.getGames().isEmpty(), "Game list should be empty after deletion");
    }
}