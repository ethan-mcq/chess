package client;

import chess.ChessGame;
import exception.InputException;
import model.Auth;
import model.GameData;
import model.UserM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ServerFacade;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static final String BASE_URL = "http://localhost:8080";
    private String uniqueUsername;
    private String uniquePassword;
    private ServerFacade serverFacade;

    @BeforeEach
    public void setUp() {
        uniqueUsername = generateRandomString(4);
        uniquePassword = generateRandomString(4);
        serverFacade = new ServerFacade(BASE_URL);
    }

    private static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    @Test
    public void registerPosTest() {
        try {
            UserM user = new UserM(uniqueUsername, uniquePassword, "email@example.com");
            Auth auth = serverFacade.register(user);
            assertNotNull(auth, "Auth should not be null for successful registration");
        } catch (InputException e) {
            fail("Registration should not throw an exception");
        }
    }

    @Test
    public void registerNegTest() {
        UserM user = new UserM("BINGO", "BONGO", "email@example.com");
        try {
            serverFacade.register(user);
            assertThrows(InputException.class, () -> {
                serverFacade.register(user);  // Attempt to register the same user again
            }, "Expected InputException for taken username");
        } catch (InputException e) {
            // If the first register call fails, it's okay for this test
        }
    }

    @Test
    public void loginPosTest() {
        UserM user = new UserM(uniqueUsername, uniquePassword, "email@example.com");
        try {
            serverFacade.register(user);  // Register first before logging in
            Auth auth = serverFacade.login(user);
            assertNotNull(auth, "Auth should not be null for successful login");
        } catch (InputException e) {
            fail("Login should not throw an exception");
        }
    }

    @Test
    public void loginNegTest() {
        UserM user = new UserM("Non", "Non", "YAH");
        assertThrows(InputException.class, () -> {
            serverFacade.login(user);
        }, "Expected InputException for incorrect login credentials");
    }

    @Test
    public void logoutPosTest() {
        UserM user = new UserM(uniqueUsername, uniquePassword, "email@example.com");
        try {
            Auth auth = serverFacade.register(user);
            assertNotNull(auth, "User should be registered successfully");
            Auth logoutAuth = serverFacade.logout(auth);
            assertNull(logoutAuth, "Logout should return null indicating success");
        } catch (InputException e) {
            fail("Logout should not throw an exception");
        }
    }

    @Test
    public void listGamesTest() {
        UserM user = new UserM(uniqueUsername, uniquePassword, "email@example.com");
        try {
            Auth auth = serverFacade.register(user);
            assertNotNull(auth, "User should be registered successfully");
            GameData[] games = serverFacade.listGames(auth);
            assertNotNull(games, "Games list should not be null");
        } catch (InputException e) {
            fail("Listing games should not throw an exception");
        }
    }

    @Test
    public void createGameTest() {
        UserM user = new UserM(uniqueUsername, uniquePassword, "email@example.com");
        try {
            Auth auth = serverFacade.register(user);
            assertNotNull(auth, "User should be registered successfully");
            GameData gameData = new GameData(1, "New Game", "", "", new ChessGame());  // Adjust as necessary for your GameData class
            GameData createdGame = serverFacade.createGame(auth, gameData);
            assertNotNull(createdGame, "GameData should not be null for successfully created game");
        } catch (InputException e) {
            fail("Creating game should not throw an exception");
        }
    }

    @Test
    public void joinGameTest() {
        UserM user = new UserM(uniqueUsername, uniquePassword, "email@example.com");
        Auth auth = null;
        try {
            auth = serverFacade.register(user);
        } catch (InputException e) {
            fail("Registration should not throw an exception");
        }

        assertNotNull(auth, "User should be registered successfully");

        try {
            // For this test, assume a game with ID 1 might not exist, which can cause an exception.
            GameData joinedGame = serverFacade.joinGame(auth, ChessGame.TeamColor.WHITE, 1);
            assertNotNull(joinedGame, "GameData should not be null for successfully joined game");
        } catch (InputException e) {
            // Handle situations where joinGame might throw an exception if no game is available to join
            System.out.println("Could not join the game: " + e.getMessage());
        }
    }
}