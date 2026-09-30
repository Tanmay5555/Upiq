package com.upiq.research.intent;

import com.upiq.research.calculation.FinancialCalculationService;
import com.upiq.research.calculation.dto.PeriodRange;
import com.upiq.research.dataset.dto.BenchmarkQuestionDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Routes benchmark question metadata to the matching Checkpoint 1 calculation.
 *
 * <p>Pipeline: metadata → typed intent → {@link FinancialCalculationService} → verified result.
 * Does not call an LLM and does not compute financial values itself.
 */
@Service
@RequiredArgsConstructor
public class FinancialIntentDispatcher {

    static final String UNSUPPORTED_TOP_N_CATEGORIES =
            "Checkpoint 1 has no category-aggregation method (only top-N individual transactions)";
    static final String UNSUPPORTED_RECURRING =
            "recurring is research-only JSON metadata and is not stored on Transaction, "
                    + "so calculateTotalExpense would silently include non-recurring rows";
    static final String UNSUPPORTED_LARGEST_CATEGORY =
            "findTopTransactions cannot filter by category; using it would return the wrong transaction";
    static final String UNSUPPORTED_LARGEST_INCREASE =
            "Checkpoint 1 cannot compare every category across two periods";
    static final String UNSUPPORTED_AVERAGE_MONTHLY =
            "Checkpoint 1 has no average-monthly comparison; dividing a baseline total in this layer "
                    + "would move arithmetic out of the verified calculation engine";

    private final FinancialIntentMapper intentMapper;
    private final FinancialCalculationService calculationService;

    public DispatchedCalculation dispatch(BenchmarkQuestionDefinition question, Long userId) {
        FinancialQueryIntent intent = intentMapper.map(question, userId);
        Object verifiedResult = execute(intent);
        return new DispatchedCalculation(intent, verifiedResult);
    }

    public DispatchedCalculation dispatch(FinancialQueryIntent intent) {
        if (intent == null) {
            throw new InvalidFinancialIntentException("intent is required");
        }
        Object verifiedResult = execute(intent);
        return new DispatchedCalculation(intent, verifiedResult);
    }

    private Object execute(FinancialQueryIntent intent) {
        CalculationType type = intent.getCalculationType();
        if (!type.isExecutable()) {
            throw unsupported(type);
        }

        Long userId = intent.getUserId();
        PeriodRange period = intent.getPeriod();

        return switch (type) {
            case CATEGORY_EXPENSE_TOTAL -> calculationService.calculateCategoryTotal(
                    userId, intent.getCategory(), intent.getTransactionType(), period);
            case TOTAL_INCOME -> calculationService.calculateTotalIncome(userId, period);
            case TOTAL_EXPENSE -> calculationService.calculateTotalExpense(userId, period);
            case NET_BALANCE -> calculationService.calculateNetBalance(userId, period);
            case CATEGORY_COMPARE -> calculationService.compareCategories(
                    userId,
                    intent.getCategory(),
                    intent.getComparisonCategory(),
                    intent.getTransactionType(),
                    period);
            case PERIOD_EXPENSE_COMPARE, PERIOD_INCOME_COMPARE, SPENDING_TREND -> {
                PeriodRange baseline = intent.getComparisonPeriod();
                yield calculationService.comparePeriods(
                        userId, baseline, period, intent.getTransactionType());
            }
            case LARGEST_TRANSACTION -> {
                int limit = intent.getLimit() != null ? intent.getLimit() : 1;
                yield calculationService.findTopTransactions(
                        userId, period, intent.getTransactionType(), limit);
            }
            default -> throw unsupported(type);
        };
    }

    private static UnsupportedCalculationTypeException unsupported(CalculationType type) {
        String reason = switch (type) {
            case TOP_N_EXPENSE_CATEGORIES -> UNSUPPORTED_TOP_N_CATEGORIES;
            case RECURRING_EXPENSE_TOTAL -> UNSUPPORTED_RECURRING;
            case LARGEST_CATEGORY_TRANSACTION -> UNSUPPORTED_LARGEST_CATEGORY;
            case CATEGORY_WITH_LARGEST_INCREASE -> UNSUPPORTED_LARGEST_INCREASE;
            case AVERAGE_MONTHLY_EXPENSE_COMPARE -> UNSUPPORTED_AVERAGE_MONTHLY;
            default -> "not implemented by FinancialCalculationService";
        };
        return new UnsupportedCalculationTypeException(type, reason);
    }
}
