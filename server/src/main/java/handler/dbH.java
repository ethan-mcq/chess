package handler;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import spark.*;

public class dbH extends baseH {

    public dbH(services services) {
        super(services);
        this.root = "/db";
    }

    @Override
    public void initHandler() {
        Spark.get(this.root + "/oats", (req, res) -> {res.redirect("/oats.html"); return null;});
        Spark.delete(this.root, this::clearData);
    }

    public Object clearData(Request req, Response res) throws DataAccessException {
        this.services.fetchClientService(authS.class).deleteAll();
        this.services.fetchClientService(gameS.class).deleteAll();
        this.services.fetchClientService(userS.class).deleteAll();

        this.setSuccessHeaders(res);
        return new Gson().toJson(new Object());
    }
}
