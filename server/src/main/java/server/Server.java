package server;

import service.Services;
import service.UserS;
import spark.Spark;
import dataaccess.*;
import handler.*;
import service.*;
import server.websocket.WebSocketHandler;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    // List to store various HTTP request handlers
    private final List<BaseH> handlers;
    // Data access object
    private final Data data;
    // Service layer to access different system services
    private final Services services;
    // WebSocket handler for managing WebSocket connections
    private final WebSocketHandler webSocketHandler;

    // Constructor for the Server class
    public Server() {
        this.handlers = new ArrayList<>();
        try {
            // Initializing data access layer
            this.data = new Data(DataType.DB);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        // Initializing service layer
        this.services = new Services(data);
        // Setup all HTTP request handlers
        setupHandlers();
        // Initializes the WebSocket handler with necessary services
        webSocketHandler = new WebSocketHandler(
                services.fetchClientService(AuthS.class),
                services.fetchClientService(GameS.class),
                services.fetchClientService(UserS.class)
        );
    }

    // Adds specific request handlers to the handlers list
    private void setupHandlers() {
        handlers.add(new AuthH(services));
        handlers.add(new GameH(services));
        handlers.add(new UserH(services));
        handlers.add(new DbH(services));
    }

    // Initializes each handler by calling their init method
    private void initializeHandlers() {
        handlers.forEach(BaseH::initHandler);
    }

    // Formats exception information into a JSON response
    public void throwProblem(Exception exception, spark.Response response) {
        response.type("application/json");
        String jsonResponse = createJsonErrorResponse(exception.getMessage());
        response.body(jsonResponse);
    }

    // Creates a JSON structured error message
    private String createJsonErrorResponse(String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Error: " + errorMessage);
        return new Gson().toJson(errorResponse);
    }

    // Runs the server on a specified port and initializes all services and handlers
    public int run(int desiredPort) {
        // Setup server port and static file location
        Spark.port(desiredPort);
        Spark.staticFiles.location("/web");

        // Exception handling for specific exceptions
        Spark.exception(DataAccessException.class, (exception, request, response) -> {
            response.status(500);
            throwProblem(exception, response);
        });

        Spark.exception(Problem.class, (problem, request, response) -> {
            response.status(problem.getStatusCode());
            throwProblem(problem, response);
        });

        // Setup WebSocket endpoint
        Spark.webSocket("/ws", webSocketHandler);

        // Initialize all the handlers
        initializeHandlers();
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    // Stops the server
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}