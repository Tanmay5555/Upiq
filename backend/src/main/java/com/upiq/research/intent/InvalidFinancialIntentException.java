package com.upiq.research.intent;

/**
 * Thrown when benchmark metadata is missing or invalid.
 * Callers must not generate financial context from an invalid intent.
 */
public class InvalidFinancialIntentException extends RuntimeException {

    public InvalidFinancialIntentException(String message) {
        super(message);
    }

    public InvalidFinancialIntentException(String message, Throwable cause) {
        super(message, cause);
    }
}
