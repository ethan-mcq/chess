package handler;

import spark.*;
import dataaccess.*;
import model.*;
import service.*;
import com.google.gson.Gson;

public class userH extends baseH {

    private static final Gson gson = new Gson();

    public userH(services services) {
        super(services);
        this.root = "/user";
    }

    @Override
    public void initHandler() {
        Spark.post(this.root, this::register);
    }

    /**
     * Handles user registration request.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of the auth object
     * @throws DataAccessException If there is an issue accessing data
     * @throws problem If there is an issue with the registration request
     */
    private Object register(Request httpRequest, Response httpResponse) throws DataAccessException, problem {
        user userRequest = user.fromJson(httpRequest.body());
        if (userRequest == null) {
            throw new problem("Bad Request", 400);
        }
        userS userService = this.services.fetchClientService(userS.class);
        auth auth = userService.createUser(userRequest);
        if (auth == null) {
            throw new problem("Username Taken", 403);
        }

        this.setSuccessHeaders(httpResponse);
        return gson.toJson(auth);
    }
}