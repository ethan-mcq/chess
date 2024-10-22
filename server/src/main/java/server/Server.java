package server;

import spark.*;
import dataaccess.*;
import handler.*;
import dataaccess.data;
import service.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;

public class Server {

    private final List<baseH> handler;
    private final data dataAccess;
    private final service.services services;

    public Server() {
        this.handler = new ArrayList<>();
        this.dataAccess = new data(dataTypes.MEM_DATA);
        this.services = new services(dataAccess);
        this.setupHandlers();
    }

    private void setupHandlers() {
        this.handler.add(new authH(services));
        this.handler.add(new gameH(services));
        this.handler.add(new userH(services));
        this.handler.add(new dbH(services));
    }

    private void initializeHandlers() {
        for (baseH handler : handler) {
            handler.initHandler();
        }
    }
    public void throwProblem(Exception exception, Response response) {
        response.type("application/json");
        String jsonResponse = createJsonErrorResponse(exception.getMessage());
        response.body(jsonResponse);
    }

    private String createJsonErrorResponse(String errorMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Error: " + errorMessage);
        return new Gson().toJson(errorResponse);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("/web");

        Spark.exception(DataAccessException.class, (problem, request, response) -> {
            response.status(500);
            throwProblem(problem, response);
        });

        Spark.exception(problem.class, (problem, request, response) -> {
            response.status(problem.getStatusCode());
            throwProblem(problem, response);
        });

        initializeHandlers();
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
