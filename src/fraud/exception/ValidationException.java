package fraud.exception;

/**
 * Custom checked exception for validation errors in user input.
 * Demonstrates: Custom checked exception.
 */
public class ValidationException extends Exception {
    private final String fieldName;

    public ValidationException(String fieldName, String message) {
        super("Validation error on '" + fieldName + "': " + message);
        this.fieldName = fieldName;
    }

    public ValidationException(String message) {
        super(message);
        this.fieldName = "";
    }

    public String getFieldName() { return fieldName; }
}
