package handler;

import com.google.gson.Gson;
import dataaccess.*;
import service.*;
import spark.*;

public class DbH extends BaseH {

    private static final Gson GSON = new Gson();

    public DbH(Services services) {
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
     * Clears all Data from the database.
     * @param httpRequest The HTTP request
     * @param httpResponse The HTTP response
     * @return JSON representation of an empty object
     * @throws DataAccessException If there is an issue accessing Data
     */
    public Object clearData(Request httpRequest, Response httpResponse) throws DataAccessException {
        this.services.fetchClientService(AuthS.class).deleteAll();
        this.services.fetchClientService(GameS.class).deleteAll();
        this.services.fetchClientService(UserS.class).deleteAll();

        this.setSuccessHeaders(httpResponse);
        return GSON.toJson(new Object());
    }
}