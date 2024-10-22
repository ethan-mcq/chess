package handler;

import dataaccess.*;
import model.*;
import service.*;
import spark.*;
import com.google.gson.Gson;

import javax.swing.*;

public class authH extends baseH {

    private static final Gson gson = new Gson();
    private static final String AUTHORIZATION_HEADER = "Authorization";

    public authH(services services) {
        super(services);
        this.root = "/session";
    }

    /**
     * Handles user login request
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the auth object
     * @throws problem If there is an issue with the login request
     * @throws DataAccessException If there is an issue accessing data
     */
    public Object login(Request httpRequest, Response httpResponse) throws problem, DataAccessException {
        login loginRequest = login.fromJson(httpRequest.body());
        if (loginRequest == null) {
            throw new problem("Bad Request", 400);
        }

        authS authS = this.services.fetchClientService(authS.class);
        auth auth = authS.login(loginRequest);
        if (auth == null) {
            throw new problem("Unauthorized", 401);
        }

        this.setSuccessHeaders(httpResponse);
        return gson.toJson(auth);
    }

    /**
     * Handles user logout request
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return Empty JSON object
     * @throws DataAccessException If there is an issue accessing data
     */
    public Object logout(Request httpRequest, Response httpResponse) throws DataAccessException {
        String authToken = httpRequest.headers(AUTHORIZATION_HEADER);
        authS authS = this.services.fetchClientService(authS.class);
        authS.logout(authToken);

        this.setSuccessHeaders(httpResponse);
        return gson.toJson(new Object());
    }

    /**
     * Initializes the handler routes.
     */
    @Override
    public void initHandler() {
        Spark.post(this.root, this::login);
        Spark.delete(this.root, verifyAuth(this::logout));
    }
}