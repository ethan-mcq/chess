package server;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import exception.InputException;

import java.net.*;
import java.io.*;
import java.util.zip.GZIPOutputStream;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade (String url) {
        serverUrl = url;
    }
    private <T> T requesterFunction(String method, String location, Object request, Class<T> response, String authToken) throws InputException {
        try {
            URL url = (new URI(serverUrl + location)).toURL();
            HttpURLConnection httpVal = (HttpURLConnection) url.openConnection();
            httpVal.setRequestMethod(method);
            httpVal.setDoOutput(true);

            if (authToken != null && !authToken.isEmpty()) {
                httpVal.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, httpVal);
            httpVal.connect();
            throwIfNotSuccessful(httpVal);
            var responseVal = readBody(httpVal, response);

            return responseVal;

        } catch (Exception exception) {
            throw new InputException(500, exception.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection httpVal) throws IOException {
        if (request != null) {
            httpVal.addRequestProperty("Content-Type", "application/json");
            String json = new Gson().toJson(request);
            try (OutputStream body = httpVal.getOutputStream()) {
                body.write(json.getBytes());
            }
        }
    }

    private static <T> T readBody(HttpURLConnection httpVal, Class<T> response) throws IOException {
        T responseVal = null;
        if(httpVal.getContentLength() < 0) {
            try(InputStream responseBody = httpVal.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(responseBody);
                if (response != null) {
                    responseVal = new Gson().fromJson(reader, response);
                }
            }
        }
        return responseVal;
    }

    private void throwIfNotSuccessful(HttpURLConnection httpVal) throws IOException, InputException {
        var status = httpVal.getResponseCode();
        if (status / 100 != 2) {
            if (status == 400) {
                throw new InputException(status, SET_TEXT_COLOR_RED + "Bad request! Verify syntax and retry.");
            }
            else if (status == 401) {
                throw new InputException(status, SET_TEXT_COLOR_RED + "Bad credentials provided. Retry.");
            }
            else if (status == 403) {
                throw new InputException(status, SET_TEXT_COLOR_RED + "Parameter already taken.");
            }
            else if (status == 500) {
                throw new InputException(status, SET_TEXT_COLOR_RED + "Error " + status);
            }
            else {
                throw new InputException(status, SET_TEXT_COLOR_RED + "Unknown Error" + status);
            }
        }
    }

    //Public Classes
    public Response registerNewUser(JsonObject jsonObject) throws InputException {
        var location = "/user";
        return this.requesterFunction("POST", location, jsonObject, Response.class, null);
    }
}
