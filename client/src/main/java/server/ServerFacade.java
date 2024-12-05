package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import exception.InputException;

public class ServerFacade {

    private final String serverEndPoint;

    /**
     * Initializes ServerFacade with the server URL.
     *
     * @param url the server URL
     */
    public ServerFacade(String url) {
        this.serverEndPoint = url;
    }

    /**
     * Logs a user into the system.
     *
     * @param user the user credentials
     * @return Auth object containing authentication data
     * @throws InputException if a request error occurs
     */
    public Auth login(UserM user) throws InputException {
        return makeRequest("POST", "/session", user, Auth.class, null);
    }

    /**
     * Registers a new user.
     *
     * @param user the registration details for the user
     * @return Auth object containing authentication data
     * @throws InputException if a request error occurs
     */
    public Auth register(UserM user) throws InputException {
        return makeRequest("POST", "/user", user, Auth.class, null);
    }

    /**
     * Logs out the user.
     *
     * @param auth the current user's authentication details
     * @return Auth object indicating the logged-out state
     * @throws InputException if a request error occurs
     */
    public Auth logout(Auth auth) throws InputException {
        return makeRequest("DELETE", "/session", null, null, auth);
    }

    /**
     * Lists all available games.
     *
     * @param auth the current user's authentication details
     * @return Array of GameData containing information about games
     * @throws InputException if a request error occurs
     */
    public GameData[] listGames(Auth auth) throws InputException {
        record ListGameResponse(GameData[] games) {}
        var response = this.makeRequest("GET", "/game", null, ListGameResponse.class, auth);
        return response.games;
    }

    /**
     * Creates a new game.
     *
     * @param auth the current user's authentication details
     * @param gameData the game data to be created
     * @return GameData containing the created game's information
     * @throws InputException if a request error occurs
     */
    public GameData createGame(Auth auth, GameData gameData) throws InputException {
        return makeRequest("POST", "/game", gameData, GameData.class, auth);
    }

    /**
     * Joins a game as a specified player color.
     *
     * @param auth the current user's authentication details
     * @param playerColor the desired team color
     * @param gameID the ID of the game to join
     * @return GameData containing updated game information
     * @throws InputException if a request error occurs
     */
    public GameData joinGame(Auth auth, ChessGame.TeamColor playerColor, int gameID) throws InputException {
        Map<String, Object> game = new HashMap<>();
        game.put("playerColor", playerColor.toString());
        game.put("gameID", gameID);
        return makeRequest("PUT", "/game", game, GameData.class, auth);
    }

    /**
     * Clears application data from the server.
     *
     * @throws InputException if a request error occurs
     */

    /**
     * Makes an HTTP request to the server and processes the response.
     *
     * @param method        the HTTP method (GET, POST, PUT, DELETE)
     * @param fullPath          the target endpoint path
     * @param httpRequest       the request body
     * @param responseClass the expected class of the response
     * @param auth      the authentication information (optional)
     * @param <T>           the type of response expected
     * @return a response of type T
     * @throws InputException if an error occurs during the request
     */
    private <T> T makeRequest(String method, String fullPath, Object httpRequest, Class<T> responseClass, Auth auth) throws InputException {
        try {
            URL url = (new URI(serverEndPoint + fullPath)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (auth != null) {
                http.addRequestProperty("Authorization", auth.authToken());
            }
            writeBody(httpRequest, http);
            http.connect();
            unsuccessfulTry(http);
            return readBody(http, responseClass);
        } catch (Exception exception) {
            throw new InputException(500, exception.getMessage());
        }
    }

    /**
     * Writes the request as a JSON object to the output stream of the HTTP connection.
     *
     * @param httpRequest the request object to be sent
     * @param http    the HTTP connection
     * @throws IOException if an error occurs during writing
     */
    private static void writeBody(Object httpRequest, HttpURLConnection http) throws IOException {
        if (httpRequest != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(httpRequest);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    /**
     * Checks the HTTP status code and throws an exception if the request wasn't successful.
     *
     * @param http the HTTP connection
     * @throws IOException    if an error occurs during obtaining the response code
     * @throws InputException if the response status is not a success (i.e., not 2xx)
     */
    private void unsuccessfulTry(HttpURLConnection http) throws IOException, InputException {
        int status = http.getResponseCode();
        if (!isSuccessfulChecker(status)) {
            throw new InputException(status, "FAILURE: " + status);
        }
    }

    /**
     * Reads the HTTP response body and deserializes it to the specified class.
     *
     * @param http          the HTTP connection
     * @param responseClass the class to deserialize response to
     * @param <T>           the type of response expected
     * @return an object of type T
     * @throws IOException if an error occurs during reading the response
     */
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        if (http.getContentLength() < 0 && responseClass != null) {
            try (InputStream respBody = http.getInputStream(); InputStreamReader reader = new InputStreamReader(respBody)) {
                return new Gson().fromJson(reader, responseClass);
            }
        }
        return null;
    }

    /**
     * Determines if the HTTP status code indicates a successful request.
     *
     * @param status the HTTP status code
     * @return true if the status indicates success, false otherwise
     */
    private boolean isSuccessfulChecker(int status) {
        return status / 100 == 2;
    }
}