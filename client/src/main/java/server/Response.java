package server;
import model.GameData;
import java.util.ArrayList;

public class Response {
    public String username;
    public String authToken;
    public String response;
    public Integer gameID;
    public ArrayList<GameData> gamesList;
    public String getUsername() {
        return username;
    }
    public String getAuthToken() {
        return authToken;
    }

}
