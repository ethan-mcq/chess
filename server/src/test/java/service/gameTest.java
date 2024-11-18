package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import chess.ChessGame;

public class gameTest {

    private data dataSource;
    private gameS gameService;
    private gameDAO gameDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        dataSource = new data(dataTypes.MEM_DATA);
        gameService = new gameS(dataSource);
        gameDao = dataSource.fetchClientData(gameDAO.class);
    }

    @Test
    @DisplayName("Successfully create a new game")
    public void testCreateGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        gameData newGame = new gameData(0, "testGame", null, null, chessGame);
        gameData createdGame = gameService.createGame(newGame);

        assertNotNull(createdGame, "Created game should not be null");
        assertEquals(1, createdGame.gameID(), "Game IDs should match");
    }

    @Test
    @DisplayName("Successfully get all games")
    public void testGetAllGamesSuccess() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        gameData game1 = new gameData(0, "testGame1", null, null, chessGame1);
        gameData game2 = new gameData(0, "testGame2", null, null, chessGame1);

        gameService.createGame(game1);
        gameService.createGame(game2);

        gameList allGames = gameService.getAllGames();

        assertNotNull(allGames, "Game list should not be null");
        assertEquals(2, allGames.getGames().size(), "There should be two games in the list");
    }

    @Test
    @DisplayName("Successfully join a game as a specific player")
    public void testJoinGameSuccess() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        gameData newGame = new gameData(0, "testGame", null, null, chessGame);
        gameService.createGame(newGame);

        join joinRequest = new join("WHITE", "user1", 1);
        gameData joinedGame = gameService.joinGame(joinRequest);

        assertNotNull(joinedGame, "Joined game should not be null");
        assertEquals(1, joinedGame.gameID(), "Game IDs should match");
        assertEquals("user1", joinedGame.whiteUsername(), "White player should match");
    }

    @Test
    @DisplayName("Successfully delete all games")
    public void testDeleteAllGamesSuccess() throws DataAccessException {
        ChessGame chessGame1 = new ChessGame();
        ChessGame chessGame2 = new ChessGame();
        gameData game1 = new gameData(0, "testGame1", null, null, chessGame1);
        gameData game2 = new gameData(0, "testGame2", null, null, chessGame2);

        gameService.createGame(game1);
        gameService.createGame(game2);

        gameService.deleteAll();

        gameList allGames = gameService.getAllGames();

        assertNotNull(allGames, "Game list should not be null");
        assertTrue(allGames.getGames().isEmpty(), "Game list should be empty after deletion");
    }

    @Test
    @DisplayName("Deleting all games when no games exist")
    public void testDeleteAllGamesWhenNoGamesExist() throws DataAccessException {
        gameService.deleteAll();

        gameList allGames = gameService.getAllGames();

        assertNotNull(allGames, "Game list should not be null");
        assertTrue(allGames.getGames().isEmpty(), "Game list should be empty after deletion");
    }
}