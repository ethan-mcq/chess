package model;

import com.google.gson.Gson;

public record user (String username, String password, String email) {
    public static user fromJson(String json) {
        user userData = new Gson().fromJson(json, user.class);
        if (userData == null || !userData.isValid()) {
            return null;
        }
        return userData;
    }

    private boolean isValid() {
        return username != null && !username.isEmpty() &&
                password != null && !password.isEmpty() &&
                email != null && !email.isEmpty();
    }
}