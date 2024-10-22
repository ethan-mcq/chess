package model;

import com.google.gson.Gson;

public record join(String playerColor, String username, int gameID) {
    public static join fromJson(String json) {
        join joinGameRequest = new Gson().fromJson(json, join.class);
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
