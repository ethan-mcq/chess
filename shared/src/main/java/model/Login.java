package model;

import com.google.gson.Gson;

public record Login(String username, String password) {
    private static final Gson GSON = new Gson();

    public static Login fromJson(String json) {
        Login login = GSON.fromJson(json, Login.class);
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