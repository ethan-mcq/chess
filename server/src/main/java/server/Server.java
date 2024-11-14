package server;

import service.Services;
import spark.*;
import dataaccess.*;
import handler.*;
import dataaccess.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;

public class Server {

    private final List<BaseH> handler;
    private final dataaccess.Data data;
    private final Services services;

    public Server() {
        this.handler = new ArrayList<>();
        try {
            this.data = new Data(DataType.DB);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        this.services = new Services(data);
        this.setupHandlers();
    }

    private void setupHandlers() {
        this.handler.add(new AuthH(services));
        this.handler.add(new GameH(services));
        this.handler.add(new UserH(services));
        this.handler.add(new DbH(services));
    }

    private void initializeHandlers() {
        for (BaseH handler : handler) {
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

        Spark.exception(Problem.class, (problem, request, response) -> {
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
