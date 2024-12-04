package server;
import model.*;

import java.util.ArrayList;

public class Response {
    public String username;
    public String authToken;
    public ArrayList<GameResponse> games;
    //public String response;
    public Integer gameID;

    public String username() {
        return username;
    }
    public String authToken() {
        return authToken;
    }
}
