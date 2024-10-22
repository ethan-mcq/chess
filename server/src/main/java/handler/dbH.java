package handler;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import spark.*;

public class dbH extends baseH {

    private static final Gson gson = new Gson();

    public dbH(services services) {
        super(services);
        this.root = "/db";
    }

    @Override
    public void initHandler() {
        Spark.get(this.root + "/oats", (httpRequest, httpResponse) -> {
            httpResponse.redirect("/oats.html");
            return null;
        });
        Spark.delete(this.root, this::clearData);
    }

    /**
     * Clears all data from the database.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of an empty object
     * @throws DataAccessException If there is an issue accessing data
     */
    public Object clearData(Request httpRequest, Response httpResponse) throws DataAccessException {
        this.services.fetchClientService(authS.class).deleteAll();
        this.services.fetchClientService(gameS.class).deleteAll();
        this.services.fetchClientService(userS.class).deleteAll();

        this.setSuccessHeaders(httpResponse);
        return gson.toJson(new Object());
    }
}