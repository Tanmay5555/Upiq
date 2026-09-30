package com.upiq.research.intent;

/**
 * Calculation types present in {@code benchmark-questions.json}.
 *
 * <p>{@code executable} is true only when Checkpoint 1's {@code FinancialCalculationService}
 * can produce a verified result without inventing a new calculation.
 */
public enum CalculationType {

    CATEGORY_EXPENSE_TOTAL(true),
    TOTAL_INCOME(true),
    TOTAL_EXPENSE(true),
    NET_BALANCE(true),
    CATEGORY_COMPARE(true),
    PERIOD_EXPENSE_COMPARE(true),
    PERIOD_INCOME_COMPARE(true),
    SPENDING_TREND(true),
    LARGEST_TRANSACTION(true),

    /** Requires grouping expenses by category; not in Checkpoint 1. */
    TOP_N_EXPENSE_CATEGORIES(false),
    /** {@code recurring} is research JSON metadata and is not persisted on {@code Transaction}. */
    RECURRING_EXPENSE_TOTAL(false),
    /** Requires per-category period diffs across all categories; not in Checkpoint 1. */
    CATEGORY_WITH_LARGEST_INCREASE(false),
    /** {@code findTopTransactions} cannot filter by category. */
    LARGEST_CATEGORY_TRANSACTION(false),
    /** Requires average of a multi-month baseline; not in Checkpoint 1. */
    AVERAGE_MONTHLY_EXPENSE_COMPARE(false);

    private final boolean executable;

    CalculationType(boolean executable) {
        this.executable = executable;
    }

    public boolean isExecutable() {
        return executable;
    }

    public static CalculationType fromBenchmarkValue(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidFinancialIntentException("calculation_type is required");
        }
        try {
            return CalculationType.valueOf(raw.trim());
        } catch (IllegalArgumentException ex) {
            throw new InvalidFinancialIntentException("Unknown calculation_type: " + raw);
        }
    }
}
