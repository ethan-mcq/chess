package handler;

/**
 * Represents an exception with an associated HTTP status code.
 */
public class problem extends Exception {
    private final int statusCode;

    /**
     * Constructs a new problem exception with the specified detail message and status code.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param statusCode The HTTP status code associated with this problem.
     */
    public problem(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * Returns the HTTP status code associated with this problem.
     *
     * @return The HTTP status code.
     */
    public int getStatusCode() {
        return statusCode;
    }
}