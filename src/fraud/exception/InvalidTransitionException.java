package fraud.exception;

/**
 * Custom checked exception thrown when an invalid workflow transition is attempted.
 * Demonstrates: Custom checked exception, throw/throws, constructor chaining.
 */
public class InvalidTransitionException extends Exception {
    private final String fromStatus;
    private final String toStatus;

    public InvalidTransitionException(String message) {
        super(message);
        this.fromStatus = "";
        this.toStatus = "";
    }

    public InvalidTransitionException(String fromStatus, String toStatus) {
        super("Invalid workflow transition: " + fromStatus + " -> " + toStatus +
              ". Only forward single-step transitions are allowed.");
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public InvalidTransitionException(String message, Throwable cause) {
        super(message, cause);
        this.fromStatus = "";
        this.toStatus = "";
    }

    public String getFromStatus() { return fromStatus; }
    public String getToStatus() { return toStatus; }
}
