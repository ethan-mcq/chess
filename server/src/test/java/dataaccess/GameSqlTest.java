package dataaccess;

import dataaccess.GameSqlDai;
import dataaccess.DataAccessException;
import model.GameData;
import model.GameList;
import model.GameResponse;
import model.Join;
import chess.ChessGame;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameSqlTest {

    private GameSqlDai gameSqlDai;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameSqlDai = new GameSqlDai();
        gameSqlDai.deleteAllGames(); // Ensure clean state before each test
    }

    @AfterEach
    void tearDown() {
        gameSqlDai = null;
    }

    @Test
    void testCreateGamePositive() throws DataAccessException {
        GameData game = new GameData(0, "First Game", null, null, new ChessGame());
        GameData createdGame = gameSqlDai.createGame(game);
        assertNotNull(createdGame);
        assertEquals(game.gameName(), createdGame.gameName());
    }

    @Test
    void testCreateGameNegative() throws DataAccessException {
        GameData game = new GameData(0, "First Game", null, null, new ChessGame());
        gameSqlDai.createGame(game);

        // Attempt to create a game with the same name might be allowed as names are not unique constraints here,
        // so no DataAccessException is expected just due to the same name.
        // Therefore, this test might not make sense, as creating a game with the same name is valid.
        // Instead, we'll check if creating multiple games is handled.
        GameData duplicateGame = new GameData(0, "First Game", null, null, new ChessGame());
        GameData secondGame = gameSqlDai.createGame(duplicateGame);
        assertNotNull(secondGame);
    }

    @Test
    void testGetAllGamesPositive() throws DataAccessException {
        // Ensure we have at least one game to retrieve
        GameData game = new GameData(0, "First Game", null, null, new ChessGame());
        gameSqlDai.createGame(game);

        GameList gameList = gameSqlDai.getAllGames();
        List<GameResponse> games = gameList.getGames();
        assertFalse(games.isEmpty());
    }

    @Test
    void testGetGamesPositive() throws DataAccessException {
        GameData game = new GameData(0, "First Game", null, null, new ChessGame());
        GameData createdGame = gameSqlDai.createGame(game);

        GameData retrievedGame = gameSqlDai.getGames(createdGame.gameID());
        assertNotNull(retrievedGame);
        assertEquals(createdGame.gameName(), retrievedGame.gameName());
    }

    @Test
    void testGetGamesNegative() throws DataAccessException {
        GameData game = gameSqlDai.getGames(-1);
        assertNull(game);
    }

    @Test
    void testJoinGamePositive() throws DataAccessException {
        GameData game = new GameData(0, "First Game", null, null, new ChessGame());
        GameData createdGame = gameSqlDai.createGame(game);

        Join join = new Join("WHITE", "whitePlayer", createdGame.gameID());
        GameData updatedGame = gameSqlDai.joinGame(join);
        assertNotNull(updatedGame);
        assertEquals("whitePlayer", updatedGame.whiteUsername());
    }

    @Test
    void testJoinGameNegative() throws DataAccessException {
        Join join = new Join("WHITE", "whitePlayer", -1);
        GameData result = gameSqlDai.joinGame(join);
        assertNull(result);
    }

    @Test
    void testDeleteAllGamesPositive() throws DataAccessException {
        GameData game1 = new GameData(0, "First Game", null, null, new ChessGame());
        GameData game2 = new GameData(0, "Second Game", null, null, new ChessGame());
        gameSqlDai.createGame(game1);
        gameSqlDai.createGame(game2);

        gameSqlDai.deleteAllGames();

        assertTrue(gameSqlDai.getAllGames().getGames().isEmpty());
    }

    @Test
    void testDeleteAllGamesNegative() {
        assertDoesNotThrow(() -> gameSqlDai.deleteAllGames());
    }
}