package com.upiq.research.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.upiq.research.calculation.dto.CategoryComparisonResult;
import com.upiq.research.calculation.dto.CategoryTotalResult;
import com.upiq.research.calculation.dto.NetBalanceResult;
import com.upiq.research.calculation.dto.PeriodComparisonResult;
import com.upiq.research.calculation.dto.TopTransactionEntry;
import com.upiq.research.intent.CalculationType;
import com.upiq.research.intent.DispatchedCalculation;
import com.upiq.research.intent.FinancialQueryIntent;
import com.upiq.research.intent.InvalidFinancialIntentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts verified {@code FinancialCalculationService} results into a stable
 * {@link FinancialContext}. Generates facts only — no LLM prompt text.
 */
@Component
public class FinancialContextBuilder {

    private final ObjectMapper objectMapper;

    public FinancialContextBuilder() {
        this.objectMapper = createDeterministicMapper();
    }

    public FinancialContext build(DispatchedCalculation dispatched) {
        if (dispatched == null || dispatched.getIntent() == null) {
            throw new InvalidFinancialIntentException("dispatched calculation and intent are required");
        }
        return build(dispatched.getIntent(), dispatched.getVerifiedResult());
    }

    public FinancialContext build(FinancialQueryIntent intent, Object verifiedResult) {
        if (intent == null) {
            throw new InvalidFinancialIntentException("intent is required");
        }
        if (!intent.getCalculationType().isExecutable()) {
            throw new InvalidFinancialIntentException(
                    "Cannot build context for unsupported calculation_type " + intent.getCalculationType());
        }
        if (verifiedResult == null) {
            throw new InvalidFinancialIntentException("verified calculation result is required");
        }

        FinancialFacts facts = switch (intent.getCalculationType()) {
            case CATEGORY_EXPENSE_TOTAL -> categoryTotalFacts(intent, verifiedResult);
            case TOTAL_INCOME -> totalIncomeFacts(verifiedResult);
            case TOTAL_EXPENSE -> totalExpenseFacts(verifiedResult);
            case NET_BALANCE -> netBalanceFacts(verifiedResult);
            case CATEGORY_COMPARE -> categoryCompareFacts(intent, verifiedResult);
            case PERIOD_EXPENSE_COMPARE, PERIOD_INCOME_COMPARE, SPENDING_TREND ->
                    periodCompareFacts(intent, verifiedResult);
            case LARGEST_TRANSACTION -> largestTransactionFacts(verifiedResult);
            default -> throw new InvalidFinancialIntentException(
                    "No context mapping for " + intent.getCalculationType());
        };

        return FinancialContext.builder()
                .contextVersion(FinancialContext.CONTEXT_VERSION)
                .source(FinancialContext.SOURCE)
                .verified(true)
                .calculationType(intent.getCalculationType().name())
                .transactionType(intent.getTransactionType())
                .period(PeriodWindow.from(intent.getPeriod()))
                .comparisonPeriod(PeriodWindow.from(intent.getComparisonPeriod()))
                .facts(facts)
                .definitions(FinancialContext.orderedDefinitions(definitionsFor(intent.getCalculationType())))
                .build();
    }

    public String toJson(FinancialContext context) {
        try {
            return objectMapper.writeValueAsString(context);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize financial context", e);
        }
    }

    public String toJson(DispatchedCalculation dispatched) {
        return toJson(build(dispatched));
    }

    ObjectMapper objectMapper() {
        return objectMapper;
    }

    private static FinancialFacts categoryTotalFacts(FinancialQueryIntent intent, Object verifiedResult) {
        CategoryTotalResult result = requireType(verifiedResult, CategoryTotalResult.class);
        boolean expense = intent.getTransactionType() != null
                && "expense".equalsIgnoreCase(intent.getTransactionType());
        return FinancialFacts.builder()
                .category(result.getCategory())
                .transactionType(result.getTransactionType())
                .totalExpense(expense ? result.getTotal() : null)
                .totalIncome(!expense ? result.getTotal() : null)
                .transactionCount(result.getTransactionCount())
                .percentageOfTotalExpense(result.getPercentageOfTotalExpense())
                .build();
    }

    private static FinancialFacts totalIncomeFacts(Object verifiedResult) {
        BigDecimal total = requireType(verifiedResult, BigDecimal.class);
        return FinancialFacts.builder()
                .transactionType("income")
                .totalIncome(total)
                .build();
    }

    private static FinancialFacts totalExpenseFacts(Object verifiedResult) {
        BigDecimal total = requireType(verifiedResult, BigDecimal.class);
        return FinancialFacts.builder()
                .transactionType("expense")
                .totalExpense(total)
                .build();
    }

    private static FinancialFacts netBalanceFacts(Object verifiedResult) {
        NetBalanceResult result = requireType(verifiedResult, NetBalanceResult.class);
        return FinancialFacts.builder()
                .transactionType("income_and_expense")
                .totalIncome(result.getTotalIncome())
                .totalExpense(result.getTotalExpense())
                .netBalance(result.getNetBalance())
                .savingsRate(result.getSavingsRate())
                .incomeTransactionCount(result.getIncomeTransactionCount())
                .expenseTransactionCount(result.getExpenseTransactionCount())
                .build();
    }

    private static FinancialFacts categoryCompareFacts(FinancialQueryIntent intent, Object verifiedResult) {
        CategoryComparisonResult result = requireType(verifiedResult, CategoryComparisonResult.class);
        return FinancialFacts.builder()
                .category(result.getCategory1())
                .comparisonCategory(result.getCategory2())
                .transactionType(result.getTransactionType() != null
                        ? result.getTransactionType() : intent.getTransactionType())
                .category1(result.getCategory1())
                .category1Total(result.getCategory1Total())
                .category1TransactionCount(result.getCategory1TransactionCount())
                .category2(result.getCategory2())
                .category2Total(result.getCategory2Total())
                .category2TransactionCount(result.getCategory2TransactionCount())
                .absoluteDifference(result.getAbsoluteDifference())
                .percentageDifference(result.getPercentageDifference())
                .zeroDenominator(result.isZeroDenominator())
                .higherCategory(result.getHigherCategory())
                .build();
    }

    private static FinancialFacts periodCompareFacts(FinancialQueryIntent intent, Object verifiedResult) {
        PeriodComparisonResult result = requireType(verifiedResult, PeriodComparisonResult.class);
        boolean increased = result.getAbsoluteDifference() != null
                && result.getAbsoluteDifference().compareTo(BigDecimal.ZERO) > 0;
        return FinancialFacts.builder()
                .transactionType(result.getTransactionType() != null
                        ? result.getTransactionType() : intent.getTransactionType())
                .baselineTotal(result.getPeriod1Total())
                .currentTotal(result.getPeriod2Total())
                .baselineTransactionCount(result.getPeriod1TransactionCount())
                .currentTransactionCount(result.getPeriod2TransactionCount())
                .absoluteDifference(result.getAbsoluteDifference())
                .percentageDifference(result.getPercentageDifference())
                .zeroDenominator(result.isZeroDenominator())
                .increased(increased)
                .build();
    }

    @SuppressWarnings("unchecked")
    private static FinancialFacts largestTransactionFacts(Object verifiedResult) {
        if (!(verifiedResult instanceof List<?> list)) {
            throw new InvalidFinancialIntentException(
                    "Expected List<TopTransactionEntry> for LARGEST_TRANSACTION, got "
                            + verifiedResult.getClass().getName());
        }
        if (list.isEmpty()) {
            return FinancialFacts.builder()
                    .found(false)
                    .transactionCount(0L)
                    .build();
        }
        Object first = list.get(0);
        if (!(first instanceof TopTransactionEntry entry)) {
            throw new InvalidFinancialIntentException(
                    "Expected TopTransactionEntry, got " + first.getClass().getName());
        }
        return FinancialFacts.builder()
                .found(true)
                .transactionCount((long) list.size())
                .amount(entry.getAmount())
                .category(entry.getCategory())
                .transactionType(entry.getType())
                .description(entry.getDescription())
                .date(entry.getDate())
                .paymentMethod(entry.getPaymentMethod())
                .rank(entry.getRank())
                .transactionId(entry.getTransactionId())
                .build();
    }

    private static Map<String, String> definitionsFor(CalculationType type) {
        Map<String, String> defs = new LinkedHashMap<>();
        switch (type) {
            case CATEGORY_EXPENSE_TOTAL -> {
                defs.put("total_expense",
                        "Σ amount where type and category match the request in [start, end]");
                defs.put("percentage_of_total_expense",
                        "(categoryTotal / totalExpense) × 100; 0 when totalExpense = 0");
            }
            case TOTAL_INCOME -> defs.put("total_income",
                    "Σ amount where type = income in [start, end]");
            case TOTAL_EXPENSE -> defs.put("total_expense",
                    "Σ amount where type = expense in [start, end]");
            case NET_BALANCE -> {
                defs.put("net_balance", "total_income - total_expense, calculated by the deterministic engine");
                defs.put("savings_rate", "net_balance / total_income × 100; zero when total_income = 0");
            }
            case CATEGORY_COMPARE -> {
                defs.put("absolute_difference", "category1Total - category2Total");
                defs.put("percentage_difference",
                        "((category1Total - category2Total) / category2Total) × 100; undefined when category2Total = 0");
                defs.put("higher_category", "category with max(category1Total, category2Total)");
            }
            case PERIOD_EXPENSE_COMPARE, PERIOD_INCOME_COMPARE, SPENDING_TREND -> {
                defs.put("baseline_total",
                        "Σ amount in compare_start_date..compare_end_date (period 1)");
                defs.put("current_total",
                        "Σ amount in start_date..end_date (period 2)");
                defs.put("absolute_difference", "current_total - baseline_total");
                defs.put("percentage_difference",
                        "(absolute_difference / baseline_total) × 100; undefined when baseline_total = 0");
                defs.put("increased", "true when absolute_difference > 0");
            }
            case LARGEST_TRANSACTION -> defs.put("amount",
                    "amount of the first row of transactions ordered by amount DESC in [start, end]");
            default -> {
            }
        }
        return defs;
    }

    private static <T> T requireType(Object value, Class<T> type) {
        if (!type.isInstance(value)) {
            throw new InvalidFinancialIntentException(
                    "Expected " + type.getSimpleName() + " but got "
                            + (value == null ? "null" : value.getClass().getName()));
        }
        return type.cast(value);
    }

    static ObjectMapper createDeterministicMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false);
        return mapper;
    }
}
