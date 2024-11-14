package handler;

import dataaccess.*;
import model.*;
import service.*;
import spark.*;
import com.google.gson.Gson;

public class AuthH extends BaseH {

    private static final Gson GSON = new Gson();
    private static final String AUTHORIZATION_HEADER = "Authorization";

    public AuthH(Services services) {
        super(services);
        this.root = "/session";
    }

    /**
     * Handles user Login request
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the Auth object
     * @throws Problem If there is an issue with the Login request
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Object login(Request httpRequest, Response httpResponse) throws Problem, DataAccessException {
        Login loginRequest = Login.fromJson(httpRequest.body());
        if (loginRequest == null) {
            throw new Problem("Bad Request", 400);
        }

        AuthS authS = this.services.fetchClientService(AuthS.class);
        Auth auth = authS.login(loginRequest);
        if (auth == null) {
            throw new Problem("Unauthorized", 401);
        }

        this.setSuccessHeaders(httpResponse);
        return GSON.toJson(auth);
    }

    /**
     * Handles user logout request
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return Empty JSON object
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Object logout(Request httpRequest, Response httpResponse) throws DataAccessException {
        String authToken = httpRequest.headers(AUTHORIZATION_HEADER);
        AuthS authS = this.services.fetchClientService(AuthS.class);
        authS.logout(authToken);

        this.setSuccessHeaders(httpResponse);
        return GSON.toJson(new Object());
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