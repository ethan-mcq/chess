package model;

import com.google.gson.Gson;

public record createRequest(String gameName) {
    public static createRequest fromJson(String json) {
        createRequest created = new Gson().fromJson(json, createRequest.class);
        if (created == null || !created.isValid()) {
            return null;
        }
        return created;
    }

    private boolean isValid() {
        return gameName != null && !gameName.isEmpty();
    }
}
