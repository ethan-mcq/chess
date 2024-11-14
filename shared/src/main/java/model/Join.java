package model;

import com.google.gson.Gson;

public record Join(String playerColor, String username, int gameID) {
    public static Join fromJson(String json) {
        Join joinGameRequest = new Gson().fromJson(json, Join.class);
        if (joinGameRequest == null || !joinGameRequest.isValid()) {
            return null;
        }
        return joinGameRequest;
    }

    private boolean isValid() {
        return playerColor != null && !playerColor.isEmpty() &&
                (playerColor.equals("WHITE") || playerColor.equals("BLACK")) &&
                username != null && !username.isEmpty() &&
                gameID >= 1;
    }
}
