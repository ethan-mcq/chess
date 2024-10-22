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

    /**
     * Sets success headers on the response.
     * @param httpResponse The HTTP response
     */
    protected void setSuccessHeaders(Response httpResponse) {
        httpResponse.status(200);
        httpResponse.type("application/json");
    }

    /**
     * Initializes the handler routes.
     */
    public abstract void initHandler();

    /**
     * Validates the authorization token in the request.
     * @param httpRequest The HTTP request
     * @return true if the auth token is valid, false otherwise
     * @throws DataAccessException If there is an issue accessing data
     */
    public boolean validAuthToken(Request httpRequest) throws DataAccessException {
        String authToken = httpRequest.headers("Authorization");
        if (authToken == null) {
            return false;
        }
        authS authService = this.services.fetchClientService(authS.class);
        auth authReturn = authService.getAuthData(authToken);
        return authReturn != null;
    }

    /**
     * Wraps the given route with authorization verification.
     * @param route The route to wrap
     * @return The wrapped route
     */
    public Route verifyAuth(Route route) {
        return (httpRequest, httpResponse) -> {
            if (!validAuthToken(httpRequest)) {
                throw new problem("unauthorized", 401);
            }
            return route.handle(httpRequest, httpResponse);
        };
    }

    /**
     * Gets the root path for this handler.
     * @return The root path
     */
    public String getRoot() {
        return root;
    }
}