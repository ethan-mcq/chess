package model;

import com.google.gson.Gson;

public record UserM(String username, String password, String email) {
    public static UserM fromJson(String json) {
        UserM userData = new Gson().fromJson(json, UserM.class);
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