import server.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the World of chess!\n");
        System.out.println("Have fun and Good luck!");
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).preLogin();
    }
}