package handler;

import spark.*;
import dataaccess.*;
import model.*;
import service.*;
import com.google.gson.Gson;

public class UserH extends BaseH {

    private static final Gson GsonString = new Gson();

    public UserH(Services services) {
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
     * @return JSON representation of the Auth object
     * @throws DataAccessException If there is an issue accessing Data
     * @throws Problem If there is an issue with the registration request
     */
    private Object register(Request httpRequest, Response httpResponse) throws DataAccessException, Problem {
        UserM userRequest = UserM.fromJson(httpRequest.body());
        if (userRequest == null) {
            throw new Problem("Bad Request", 400);
        }
        UserS userService = this.services.fetchClientService(UserS.class);
        Auth auth = userService.createUser(userRequest);
        if (auth == null) {
            throw new Problem("Username Taken", 403);
        }

        this.setSuccessHeaders(httpResponse);
        return GsonString.toJson(auth);
    }
}