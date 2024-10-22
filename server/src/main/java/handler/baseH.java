package handler;

import dataaccess.*;
import model.*;
import service.*;
import spark.*;

public abstract class baseH {

    protected final services services;
    protected String root;

    public baseH(services services) {
        this.services = services;
    }

    protected void setSuccessHeaders(Response response) {
        response.status(200);
        response.type("application/json");
    }

    public abstract void initHandler();

    public boolean validAuthToken(Request request) throws DataAccessException {
        String authToken = request.headers("Authorization");
        if (authToken == null) {
            return false;
        }
        authS authS = this.services.fetchClientService(authS.class);
        auth authReturn = authS.getAuthData(authToken);
        return authReturn != null;
    }

    public Route verifyAuth(Route route) {
        return (request, response) -> {
            if (!validAuthToken(request)) {
                throw new problem("unauthorized", 401);
            }
            return route.handle(request, response);
        };
    }

    public String getRoot() {
        return root;
    }
}
