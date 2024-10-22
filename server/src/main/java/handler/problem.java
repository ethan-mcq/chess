package handler;

public class problem extends Exception {
    private final int statusCode;

    public problem(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}