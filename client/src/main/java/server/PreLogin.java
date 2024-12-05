package server;

import exception.InputException;
import model.*;

import java.util.Arrays;

public class PreLogin implements Client {
    private final String serverUrl;
    private final Repl repl;
    private ServerFacade server;

    public PreLogin(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    @Override
    public String checker(String command) {
        try {
            String[] tokens = command.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "REGISTER" -> registerNewUser(params);
                case "LOGIN" -> loginUser(params);
                case "QUIT" -> "See you next time!";
                default -> helper();
            };
        } catch (Exception e) {
            return getErrorMessage(e.getMessage());
        }
    }

    @Override
    public String helper() {
        return """
                Try to navigate with a command below!
                
                REGISTER <username> <password> <email> | Register new user
                LOGIN  <username> <password>           | Log into account
                QUIT                                   | End Chess
                HELP                                   | Get help!
                """;
    }

    private String loginUser(String[] params) throws InputException {
        if (params.length != 2) {
            throw new IllegalArgumentException("LOGIN command requires a username and password.");
        }
        UserM data = new UserM(params[0], params[1], null);
        server = new ServerFacade(this.serverUrl);
        Auth auth = server.login(data);
        this.repl.setAuthData(auth);
        this.repl.changeState(State.SIGNEDIN);
        return params[0] + " IS LOGGED IN";
    }

    private String registerNewUser(String[] params) throws InputException {
        if (params.length != 3) {
            throw new IllegalArgumentException("REGISTER command requires a username, email, and password.");
        }
        UserM data = new UserM(params[0], params[1], params[2]);
        server = new ServerFacade(this.serverUrl);
        Auth auth = server.register(data);
        this.repl.setAuthData(auth);
        this.repl.changeState(State.SIGNEDIN);
        return params[0] + " IS REGISTERED AND LOGGED IN";
    }

    private String getErrorMessage(String errorMessage) {
        return switch (errorMessage) {
            case "REFUSED" -> "CONNECTION REFUSED";
            case "403" -> "USERNAME ALREADY TAKEN";
            case "401" -> "INCORRECT USERNAME OR PASSWORD";
            default -> "SOMETHING WENT WRONG, PLEASE TRY AGAIN";
        };
    }
}