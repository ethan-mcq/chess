package model;
import java.util.ArrayList;

public record GameList(ArrayList<GameResponse> gameArray) {
    public ArrayList<GameResponse> getGames() {
        return gameArray;
    }
}