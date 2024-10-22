package handler;

import spark.*;
import dataaccess.*;
import model.*;
import service.*;
import com.google.gson.Gson;

public class userH extends baseH {

    public userH(services services) {
        super(services);
        this.root = "/user";
    }

    @Override
    public void initHandler() {
        Spark.post(this.root, this::register);
    }

    private Object register(Request req, Response res) throws DataAccessException, problem {
        user userRequest = user.fromJson(req.body());
        if(userRequest == null) {
            throw new problem("Bad Request", 400);
        }
        userS userS = this.services.fetchClientService(userS.class);
        auth auth = userS.createUser(userRequest);
        if(auth == null){
            throw new problem("Username Taken", 403);
        }

        this.setSuccessHeaders(res);
        return new Gson().toJson(auth);
    }
}
