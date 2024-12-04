package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import exception.InputException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

/**
 * The ServerFacade class facilitates communication with the server through HTTP requests.
 * It provides methods to register, log in/out users, and manage chess games.
 */
public class ServerFacade {
    private final String serverUrl;

    /**
     * Constructs a ServerFacade with a specified server URL.
     *
     * @param url The URL of the server to connect to.
     */
    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    /**
     * Makes an HTTP request to the server and processes the response.
     *
     * @param <T>         The type of the response object.
     * @param method      The HTTP method to use (e.g., GET, POST).
     * @param location    The endpoint location on the server.
     * @param request     The request body to send (if any).
     * @param response    The class type of the expected response.
     * @param authToken   The authorization token (optional).
     * @return The response received from the server.
     * @throws InputException If there is an error with the request.
     */
    private <T> T makeRequest(String method, String location, Object request, Class<T> response, String authToken) throws InputException {
        try {
            URL url = (new URI(serverUrl + location)).toURL();
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod(method);
            httpConnection.setDoOutput(true);

            if (authToken != null && !authToken.isEmpty()) {
                httpConnection.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, httpConnection);
            httpConnection.connect();
            throwIfNotSuccessful(httpConnection);
            return readBody(httpConnection, response);

        } catch (Exception e) {
            throw new InputException(500, e.getMessage());
        }
    }

    /**
     * Writes the request body to the connection.
     *
     * @param request       The request object.
     * @param httpConnection The HttpURLConnection object.
     * @throws IOException If an I/O error occurs.
     */
    private static void writeBody(Object request, HttpURLConnection httpConnection) throws IOException {
        if (request != null) {
            httpConnection.addRequestProperty("Content-Type", "application/json");
            String json = new Gson().toJson(request);
            try (OutputStream body = httpConnection.getOutputStream()) {
                body.write(json.getBytes());
            }
        }
    }

    /**
     * Reads the response body from the connection.
     *
     * @param <T>             The type of the response object.
     * @param httpConnection  The HttpURLConnection object.
     * @param response        The class of the expected response.
     * @return The response object parsed from the connection's input stream.
     * @throws IOException If an I/O error occurs.
     */
    private static <T> T readBody(HttpURLConnection httpConnection, Class<T> response) throws IOException {
        T responseVal = null;
        if (httpConnection.getContentLength() < 0) {
            try (InputStream responseBody = httpConnection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(responseBody);
                if (response != null) {
                    responseVal = new Gson().fromJson(reader, response);
                }
            }
        }
        return responseVal;
    }

    /**
     * Throws an exception if the HTTP status code indicates failure.
     *
     * @param httpConnection The HttpURLConnection object.
     * @throws InputException If the response indicates a failure.
     * @throws IOException    If an I/O error occurs.
     */
    private void throwIfNotSuccessful(HttpURLConnection httpConnection) throws IOException, InputException {
        int status = httpConnection.getResponseCode();
        if (status / 100 != 2) {
            String message;
            switch (status) {
                case 400 -> message = "Bad request! Verify syntax and retry.";
                case 401 -> message = "Bad credentials provided. Retry.";
                case 403 -> message = "Parameter already taken.";
                default -> message = "Unknown Error " + status;
            }
            throw new InputException(status, SET_TEXT_COLOR_RED + message);
        }
    }

    // Public Methods for Server Communication

    /**
     * Registers a new user on the server.
     *
     * @param jsonObject The JSON object containing user details.
     * @return The response from the server.
     * @throws InputException If there is an error with the request.
     */
    public Response registerNewUser(JsonObject jsonObject) throws InputException {
        return this.makeRequest("POST", "/user", jsonObject, Response.class, null);
    }

    /**
     * Logs a user into the server.
     *
     * @param jsonObject The JSON object containing login details.
     * @return The response from the server.
     * @throws InputException If there is an error with the request.
     */
    public Response loginUser(JsonObject jsonObject) throws InputException {
        return this.makeRequest("POST", "/session", jsonObject, Response.class, null);
    }

    /**
     * Logs a user out from the server.
     *
     * @param authToken The authorization token of the user.
     * @return The response from the server.
     * @throws InputException If there is an error with the request.
     */
    public Response logoutUser(String authToken) throws InputException {
        return this.makeRequest("DELETE", "/session", null, Response.class, authToken);
    }

    /**
     * Joins a game on the server.
     *
     * @param jsonObject The JSON object containing game details.
     * @param authToken  The authorization token of the user.
     * @return The response from the server.
     * @throws InputException If there is an error with the request.
     */
    public Response joinGame(JsonObject jsonObject, String authToken) throws InputException {
        return this.makeRequest("PUT", "/game", jsonObject, Response.class, authToken);
    }

    /**
     * Creates a new game on the server.
     *
     * @param jsonObject The JSON object containing game details.
     * @param authToken  The authorization token of the user.
     * @return The response from the server.
     * @throws InputException If there is an error with the request.
     */
    public Response createNewGame(JsonObject jsonObject, String authToken) throws InputException {
        return this.makeRequest("POST", "/game", jsonObject, Response.class, authToken);
    }

    /**
     * Lists all games on the server.
     *
     * @param authToken The authorization token of the user.
     * @return The response from the server.
     * @throws InputException If there is an error with the request.
     */
    public Response listAllGames(String authToken) throws InputException {
        return this.makeRequest("GET", "/game", null, Response.class, authToken);
    }
}