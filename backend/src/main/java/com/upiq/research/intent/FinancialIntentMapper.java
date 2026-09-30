package com.upiq.research.intent;

import com.upiq.research.calculation.dto.PeriodRange;
import com.upiq.research.dataset.dto.BenchmarkQuestionDefinition;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Maps {@link BenchmarkQuestionDefinition} metadata to a typed
 * {@link FinancialQueryIntent}. No LLM and no financial arithmetic.
 */
@Component
public class FinancialIntentMapper {

    public FinancialQueryIntent map(BenchmarkQuestionDefinition question, Long userId) {
        if (question == null) {
            throw new InvalidFinancialIntentException("benchmark question is required");
        }
        if (userId == null) {
            throw new InvalidFinancialIntentException("userId is required");
        }

        CalculationType type = CalculationType.fromBenchmarkValue(question.getCalculationType());
        PeriodRange period = requirePeriod(question.getStartDate(), question.getEndDate(), "start_date/end_date");

        PeriodRange comparisonPeriod = null;
        if (requiresComparisonPeriod(type)) {
            comparisonPeriod = requirePeriod(
                    question.getCompareStartDate(), question.getCompareEndDate(),
                    "compare_start_date/compare_end_date");
        } else if (hasAny(question.getCompareStartDate(), question.getCompareEndDate())) {
            comparisonPeriod = requirePeriod(
                    question.getCompareStartDate(), question.getCompareEndDate(),
                    "compare_start_date/compare_end_date");
        }

        PeriodRange baselinePeriod = null;
        if (hasAny(question.getBaselineStartDate(), question.getBaselineEndDate())) {
            baselinePeriod = requirePeriod(
                    question.getBaselineStartDate(), question.getBaselineEndDate(),
                    "baseline_start_date/baseline_end_date");
        }

        String category = blankToNull(question.getCategory());
        String comparisonCategory = blankToNull(question.getCompareCategory());
        String transactionType = blankToNull(question.getTransactionType());

        validateTypeSpecific(type, category, comparisonCategory, transactionType, question.getLimit(), baselinePeriod);

        return FinancialQueryIntent.builder()
                .calculationType(type)
                .userId(userId)
                .questionId(blankToNull(question.getId()))
                .category(category)
                .comparisonCategory(comparisonCategory)
                .period(period)
                .comparisonPeriod(comparisonPeriod)
                .baselinePeriod(baselinePeriod)
                .transactionType(transactionType)
                .limit(question.getLimit())
                .recurringOnly(question.getRecurringOnly())
                .build();
    }

    private static void validateTypeSpecific(CalculationType type, String category, String comparisonCategory,
                                             String transactionType, Integer limit, PeriodRange baselinePeriod) {
        switch (type) {
            case CATEGORY_EXPENSE_TOTAL -> requireText(category, "category");
            case CATEGORY_COMPARE -> {
                requireText(category, "category");
                requireText(comparisonCategory, "compare_category");
            }
            case LARGEST_CATEGORY_TRANSACTION -> requireText(category, "category");
            case TOP_N_EXPENSE_CATEGORIES -> {
                if (limit != null && limit < 1) {
                    throw new InvalidFinancialIntentException("limit must be >= 1");
                }
            }
            case AVERAGE_MONTHLY_EXPENSE_COMPARE -> {
                if (baselinePeriod == null) {
                    throw new InvalidFinancialIntentException(
                            "baseline_start_date/baseline_end_date are required for AVERAGE_MONTHLY_EXPENSE_COMPARE");
                }
            }
            default -> {
                // other types do not require a category
            }
        }
        if (transactionType == null && typeRequiresTransactionType(type)) {
            throw new InvalidFinancialIntentException("transaction_type is required");
        }
    }

    private static boolean typeRequiresTransactionType(CalculationType type) {
        return type == CalculationType.CATEGORY_EXPENSE_TOTAL
                || type == CalculationType.TOTAL_INCOME
                || type == CalculationType.TOTAL_EXPENSE
                || type == CalculationType.CATEGORY_COMPARE
                || type == CalculationType.PERIOD_EXPENSE_COMPARE
                || type == CalculationType.PERIOD_INCOME_COMPARE
                || type == CalculationType.SPENDING_TREND
                || type == CalculationType.LARGEST_TRANSACTION
                || type == CalculationType.LARGEST_CATEGORY_TRANSACTION
                || type == CalculationType.TOP_N_EXPENSE_CATEGORIES
                || type == CalculationType.RECURRING_EXPENSE_TOTAL
                || type == CalculationType.CATEGORY_WITH_LARGEST_INCREASE
                || type == CalculationType.AVERAGE_MONTHLY_EXPENSE_COMPARE;
    }

    private static boolean requiresComparisonPeriod(CalculationType type) {
        return type == CalculationType.PERIOD_EXPENSE_COMPARE
                || type == CalculationType.PERIOD_INCOME_COMPARE
                || type == CalculationType.SPENDING_TREND
                || type == CalculationType.CATEGORY_WITH_LARGEST_INCREASE;
    }

    static PeriodRange requirePeriod(String startIso, String endIso, String fieldLabel) {
        if (isBlank(startIso) || isBlank(endIso)) {
            throw new InvalidFinancialIntentException(fieldLabel + " is required");
        }
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startIso.substring(0, Math.min(10, startIso.length())));
            end = LocalDate.parse(endIso.substring(0, Math.min(10, endIso.length())));
        } catch (DateTimeParseException | StringIndexOutOfBoundsException ex) {
            throw new InvalidFinancialIntentException("Invalid date in " + fieldLabel + ": " + startIso + " / " + endIso, ex);
        }
        if (start.isAfter(end)) {
            throw new InvalidFinancialIntentException(
                    "Invalid date range for " + fieldLabel + ": start (" + start + ") is after end (" + end + ")");
        }
        return PeriodRange.of(start, end);
    }

    private static void requireText(String value, String field) {
        if (isBlank(value)) {
            throw new InvalidFinancialIntentException(field + " is required");
        }
    }

    private static boolean hasAny(String a, String b) {
        return !isBlank(a) || !isBlank(b);
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
