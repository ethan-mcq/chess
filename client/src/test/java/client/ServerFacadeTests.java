package client;

import com.google.gson.JsonObject;
import exception.InputException;
import org.junit.jupiter.api.*;
import server.Server;
import server.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static final String BASE_URL = "http://localhost:8071";
    private static String uniqueUsername;
    private static String uniquePassword;
    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(8071);
        System.out.println("Started test HTTP server on port " + port);
    }

    @BeforeEach
    public void generateUniqueCredentials() {
        uniqueUsername = generateRandomString(4);
        uniquePassword = generateRandomString(4);
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

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPosTest() throws InputException {
        ServerFacade sf = new ServerFacade(BASE_URL);
        JsonObject json = new JsonObject();
        json.addProperty("username", uniqueUsername);
        json.addProperty("password", uniquePassword);
        json.addProperty("email", "email@example.com");

        Response reply = sf.registerNewUser(json);

        assertNotNull(reply.authToken, "Auth token should not be null for successful registration");
        assertFalse(reply.authToken.isEmpty(), "Auth token should not be empty for successful registration");
    }

    @Test
    public void registerNegTest() {
        assertThrows(InputException.class, () -> {
            ServerFacade sf = new ServerFacade(BASE_URL);
            JsonObject json = new JsonObject();
            json.addProperty("username", "BINGO");
            json.addProperty("password", "BONGO");
            json.addProperty("email", "email@example.com");

            sf.registerNewUser(json);

            json.addProperty("username", "BINGO");
            json.addProperty("password", "BONGO");
            json.addProperty("email", "email@example.com");

            sf.registerNewUser(json);
        }, "Expected InputException for taken username");
    }

    @Test
    public void loginPosTest() throws InputException {
        ServerFacade sf = new ServerFacade(BASE_URL);
        JsonObject json = new JsonObject();
        json.addProperty("username", uniqueUsername);
        json.addProperty("password", uniquePassword);
        json.addProperty("email", "email@example.com");

        Response reply = sf.registerNewUser(json);

        assertNotNull(reply.authToken, "Auth token should not be null for successful login");
        assertFalse(reply.authToken.isEmpty(), "Auth token should not be empty for successful login");
    }

    @Test
    public void loginNegTest() {
        assertThrows(InputException.class, () -> {
            ServerFacade sf = new ServerFacade(BASE_URL);
            JsonObject json = new JsonObject();
            json.addProperty("username", "Non");
            json.addProperty("password", "Non");

            sf.loginUser(json);
        }, "Expected InputException for incorrect login credentials");
    }

    @Test
    public void logoutPosTest() throws InputException {
        ServerFacade sf = new ServerFacade(BASE_URL);
        JsonObject json = new JsonObject();
        json.addProperty("username", uniqueUsername);
        json.addProperty("password", uniquePassword);
        json.addProperty("email", "fakeemail@example.com");

        Response reply = sf.registerNewUser(json);
        assertNotNull(reply.authToken, "User should be registered successfully");

        Response logoutReply = sf.logoutUser(reply.authToken);
        assertNull(logoutReply.response, "Logout response should be null");
    }

    @Test
    public void logoutNegTest() {
        assertThrows(InputException.class, () -> {
            ServerFacade sf = new ServerFacade(BASE_URL);
            sf.logoutUser("fakeAuthToken");
        }, "Expected InputException for invalid auth token during logout");
    }

    @Test
    public void createPosTest() throws InputException {
        ServerFacade sf = new ServerFacade(BASE_URL);
        JsonObject json = new JsonObject();
        json.addProperty("username", uniqueUsername);
        json.addProperty("password", uniquePassword);
        json.addProperty("email", "email@example.com");

        Response reply = sf.registerNewUser(json);
        assertNotNull(reply.authToken, "User should be registered successfully");

        JsonObject game = new JsonObject();
        game.addProperty("gameName", "tester2");

        Response gameReply = sf.createNewGame(game, reply.authToken);
        assertNotNull(gameReply.gameID, "Game ID should not be null for a successfully created game");
    }

    @Test
    public void createNegTest() {
        assertThrows(InputException.class, () -> {
            ServerFacade sf = new ServerFacade(BASE_URL);
            JsonObject game = new JsonObject();
            game.addProperty("gameName", "tester");

            sf.createNewGame(game, "badAuthToken");
        }, "Expected InputException for invalid auth token during game creation");
    }

    @Test
    public void listPosTest() throws InputException {
        ServerFacade sf = new ServerFacade(BASE_URL);
        JsonObject json = new JsonObject();
        json.addProperty("username", uniqueUsername);
        json.addProperty("password", uniquePassword);
        json.addProperty("email", "email@example.com");

        Response reply = sf.registerNewUser(json);
        assertNotNull(reply.authToken, "User should be registered successfully");

        Response listReply = sf.listAllGames(reply.authToken);
        assertNotNull(listReply.games, "Games list should not be null");
    }

    @Test
    public void listNegTest() {
        assertThrows(InputException.class, () -> {
            ServerFacade sf = new ServerFacade(BASE_URL);
            sf.listAllGames("invalidAuthToken");
        }, "Expected InputException for invalid auth token during game listing");
    }

    @Test
    public void joinPosTest() throws InputException {
        ServerFacade sf = new ServerFacade(BASE_URL);
        JsonObject json = new JsonObject();
        json.addProperty("username", uniqueUsername);
        json.addProperty("password", uniquePassword);
        json.addProperty("email", "email@example.com");

        Response userReply = sf.registerNewUser(json);
        JsonObject game = new JsonObject();
        game.addProperty("gameName", "test");
        Response gameReply = sf.createNewGame(game, userReply.authToken);
        JsonObject joinGame = new JsonObject();
        joinGame.addProperty("playerColor", "WHITE");
        joinGame.addProperty("gameID", gameReply.gameID);
        Response joinReply = sf.joinGame(joinGame,  userReply.authToken);

        assertNotNull(joinReply);
    }

    @Test
    public void joinNegTest() {
        assertThrows(InputException.class, () -> {
            ServerFacade sf = new ServerFacade(BASE_URL);
            JsonObject joinGame = new JsonObject();
            joinGame.addProperty("playerColor", "WHITE");
            joinGame.addProperty("gameID", 100); // assuming an invalid game ID

            sf.joinGame(joinGame, "badAuthToken");
        }, "Expected InputException for invalid auth token during game joining");
    }
}