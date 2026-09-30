package com.upiq.research.intent;

/**
 * Thrown when a benchmark {@code calculation_type} is recognized but cannot be
 * executed by Checkpoint 1's {@code FinancialCalculationService} without
 * producing an incorrect result.
 */
public class UnsupportedCalculationTypeException extends RuntimeException {

    private final CalculationType calculationType;

    public UnsupportedCalculationTypeException(CalculationType calculationType, String reason) {
        super("Unsupported calculation_type " + calculationType + ": " + reason);
        this.calculationType = calculationType;
    }

    public CalculationType getCalculationType() {
        return calculationType;
    }
}
