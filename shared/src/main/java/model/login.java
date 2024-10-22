package model;

import com.google.gson.Gson;

public record login (String username, String password) {
    private static final Gson GSON = new Gson();

    public static login fromJson(String json) {
        login login = GSON.fromJson(json, login.class);
        if (login == null || !login.isValid()) {
            return null;
        }
        return login;
    }

    public boolean isValid() {
        return username != null && !username.isEmpty() &&
                password != null && !password.isEmpty();
    }
}