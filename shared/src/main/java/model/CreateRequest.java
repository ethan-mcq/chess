package model;

import com.google.gson.Gson;

public record CreateRequest(String gameName) {
    public static CreateRequest fromJson(String json) {
        CreateRequest created = new Gson().fromJson(json, CreateRequest.class);
        if (created == null || !created.isValid()) {
            return null;
        }
        return created;
    }

    private boolean isValid() {
        return gameName != null && !gameName.isEmpty();
    }
}
