package fraud.util;

import fraud.exception.ValidationException;

/**
 * Input validation utility class.
 * Demonstrates: Static utility methods, custom exception throwing, String methods.
 */
public final class InputValidator {

    private InputValidator() {}

    /** Validate that a string is non-null and non-empty. */
    public static void requireNonEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, "must not be empty.");
        }
    }

    /** Validate email format (simple check). */
    public static void validateEmail(String email, String fieldName) throws ValidationException {
        requireNonEmpty(email, fieldName);
        if (!email.contains("@") || !email.contains(".")) {
            throw new ValidationException(fieldName, "must be a valid email address (e.g., user@example.com).");
        }
    }

    /** Validate phone format (digits, optional dashes/spaces, 7-15 chars). */
    public static void validatePhone(String phone, String fieldName) throws ValidationException {
        requireNonEmpty(phone, fieldName);
        String digitsOnly = phone.replaceAll("[\\s\\-\\(\\)\\+]", "");
        if (digitsOnly.length() < 7 || digitsOnly.length() > 15 || !digitsOnly.matches("\\d+")) {
            throw new ValidationException(fieldName, "must be a valid phone number (7-15 digits).");
        }
    }

    /** Validate that a numeric value is positive. */
    public static void validatePositiveAmount(double amount, String fieldName) throws ValidationException {
        if (amount < 0) {
            throw new ValidationException(fieldName, "must be a non-negative number.");
        }
    }

    /** Validate that a string represents a valid double. */
    public static double parseAmount(String value, String fieldName) throws ValidationException {
        requireNonEmpty(value, fieldName);
        try {
            double amount = Double.parseDouble(value.trim());
            validatePositiveAmount(amount, fieldName);
            return amount;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName, "must be a valid number (e.g., 50000.00).");
        }
    }
}
