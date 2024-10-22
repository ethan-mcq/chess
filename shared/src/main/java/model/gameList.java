package model;
import java.util.ArrayList;

public record gameList(ArrayList<gameResponse> gameArray) {
    public ArrayList<gameResponse> getGames() {
        return gameArray;
    }
}