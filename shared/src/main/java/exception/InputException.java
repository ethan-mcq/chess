package exception;

public class InputException extends RuntimeException {
    final private int statusCode;
    public InputException(int statusCode, String message) {
      super(message);
      this.statusCode = statusCode;
    }
    public int getStatusCode() { return statusCode; }
}