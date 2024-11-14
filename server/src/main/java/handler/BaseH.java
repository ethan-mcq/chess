package handler;

import dataaccess.*;
import model.*;
import service.*;
import spark.*;

public abstract class BaseH {

    protected final Services services;
    protected String root;

    public BaseH(Services services) {
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
     * @return true if the Auth token is valid, false otherwise
     * @throws DataAccessException If there is an issue accessing Data
     */
    public boolean validAuthToken(Request httpRequest) throws DataAccessException {
        String authToken = httpRequest.headers("Authorization");
        if (authToken == null) {
            return false;
        }
        AuthS authService = this.services.fetchClientService(AuthS.class);
        Auth authReturn = authService.getAuthData(authToken);
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
                throw new Problem("unauthorized", 401);
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