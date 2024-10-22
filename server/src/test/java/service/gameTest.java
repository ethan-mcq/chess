package service;

import dataaccess.DataAccessException;
import dataaccess.data;
import dataaccess.gameDAO;
import model.gameData;
import model.gameList;
import model.gameResponse;
import model.join;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;


public class gameTest {

    private data mockData;
    private gameDAO mockGameDAO;
    private gameS gameService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        mockData = mock(data.class);
        mockGameDAO = mock(gameDAO.class);
        gameService = new gameS(mockData);

        when(mockData.fetchClientData(gameDAO.class)).thenReturn(mockGameDAO);
    }

    @Test
    @DisplayName("createGame creates a game successfully")
    public void testCreateGame_Success() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        gameData game = new gameData(1, "gameName", chessGame, "whitePlayer", "blackPlayer");
        when(mockGameDAO.createGame(game)).thenReturn(game);

        gameData result = gameService.createGame(game);

        assertNotNull(result);
        assertEquals(1, result.gameID());
        assertEquals("gameName", result.gameName());
        assertEquals(chessGame, result.game());
        assertEquals("whitePlayer", result.whiteUsername());
        assertEquals("blackPlayer", result.blackUsername());
        verify(mockGameDAO, times(1)).createGame(game);
    }

    @Test
    @DisplayName("createGame throws DataAccessException")
    public void testCreateGame_Failure() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        gameData game = new gameData(1, "gameName", chessGame, "whitePlayer", null);
        when(mockGameDAO.createGame(game)).thenThrow(new DataAccessException("Failed to create game"));

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(game);
        });

        assertEquals("Failed to create game", exception.getMessage());
        verify(mockGameDAO, times(1)).createGame(game);
    }

    @Test
    @DisplayName("getAllGames retrieves all games successfully")
    public void testGetAllGames_Success() throws DataAccessException {
        ArrayList<gameResponse> gameArray = new ArrayList<>();
        gameList games = new gameList(gameArray);
        when(mockGameDAO.getAllGames()).thenReturn(games);

        gameList result = gameService.getAllGames();

        assertNotNull(result);
        assertEquals(gameArray, result.getGames());
        verify(mockGameDAO, times(1)).getAllGames();
    }

    @Test
    @DisplayName("getAllGames throws DataAccessException")
    public void testGetAllGames_Failure() throws DataAccessException {
        when(mockGameDAO.getAllGames()).thenThrow(new DataAccessException("Failed to retrieve games"));

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.getAllGames();
        });

        assertEquals("Failed to retrieve games", exception.getMessage());
        verify(mockGameDAO, times(1)).getAllGames();
    }

    @Test
    @DisplayName("joinGame allows user to join a game successfully")
    public void testJoinGame_Success() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        join joinRequest = new join("WHITE", "user1", 1);
        gameData game = new gameData(1, "gameName", chessGame, "user1", "user2");
        when(mockGameDAO.joinGame(joinRequest)).thenReturn(game);

        gameData result = gameService.joinGame(joinRequest);

        assertNotNull(result);
        assertEquals(1, result.gameID());
        assertEquals("gameName", result.gameName());
        assertEquals(chessGame, result.game());
        assertEquals("user1", result.whiteUsername());
        assertEquals("user2", result.blackUsername());
        verify(mockGameDAO, times(1)).joinGame(joinRequest);
    }

    @Test
    @DisplayName("joinGame throws DataAccessException")
    public void testJoinGame_Failure() throws DataAccessException {
        join joinRequest = new join("WHITE", "user1", 1);
        when(mockGameDAO.joinGame(joinRequest)).thenThrow(new DataAccessException("Failed to join game"));

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(joinRequest);
        });

        assertEquals("Failed to join game", exception.getMessage());
        verify(mockGameDAO, times(1)).joinGame(joinRequest);
    }

    @Test
    @DisplayName("deleteAll deletes all games successfully")
    public void testDeleteAll_Success() throws DataAccessException {
        doNothing().when(mockGameDAO).deleteAllGames();

        gameService.deleteAll();

        verify(mockGameDAO, times(1)).deleteAllGames();
    }

    @Test
    @DisplayName("deleteAll throws DataAccessException")
    public void testDeleteAll_Failure() throws DataAccessException {
        doThrow(new DataAccessException("Failed to delete all games")).when(mockGameDAO).deleteAllGames();

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.deleteAll();
        });

        assertEquals("Failed to delete all games", exception.getMessage());
        verify(mockGameDAO, times(1)).deleteAllGames();
    }
}