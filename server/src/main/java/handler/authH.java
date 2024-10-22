package handler;

import dataaccess.*;
import model.*;
import service.*;
import spark.*;
import com.google.gson.Gson;

public class authH extends baseH{
    public authH(services services) {
        super(services);
        this.root = "/session";
    }
    public Object login(Request req, Response response) throws problem, DataAccessException {
        login loginRequest = login.fromJson(req.body());
        if(loginRequest == null){
            throw new problem("Bad Request", 400);
        }

        authS authS = this.services.fetchClientService(authS.class);
        auth auth = authS.login(loginRequest);
        if(auth == null){
            throw new problem("Unauthorized", 401);
        }

        this.setSuccessHeaders(response);
        return new Gson().toJson(auth);
    }

    public Object logout(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
        authS authS = this.services.fetchClientService(authS.class);
        authS.logout(authToken);

        this.setSuccessHeaders(res);
        return new Gson().toJson(new Object());
    }
    @Override
    public void initHandler() {
        Spark.post(this.root, this::login);
        Spark.delete(this.root, verifyAuth(this::logout));
    }
}
