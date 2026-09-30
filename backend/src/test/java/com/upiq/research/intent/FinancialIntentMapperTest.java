package com.upiq.research.intent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upiq.research.dataset.dto.BenchmarkQuestionDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FinancialIntentMapper")
class FinancialIntentMapperTest {

    private final FinancialIntentMapper mapper = new FinancialIntentMapper();

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            "CATEGORY_EXPENSE_TOTAL, CATEGORY_EXPENSE_TOTAL",
            "TOTAL_INCOME, TOTAL_INCOME",
            "TOTAL_EXPENSE, TOTAL_EXPENSE",
            "NET_BALANCE, NET_BALANCE",
            "CATEGORY_COMPARE, CATEGORY_COMPARE",
            "PERIOD_EXPENSE_COMPARE, PERIOD_EXPENSE_COMPARE",
            "PERIOD_INCOME_COMPARE, PERIOD_INCOME_COMPARE",
            "SPENDING_TREND, SPENDING_TREND",
            "LARGEST_TRANSACTION, LARGEST_TRANSACTION",
            "TOP_N_EXPENSE_CATEGORIES, TOP_N_EXPENSE_CATEGORIES",
            "RECURRING_EXPENSE_TOTAL, RECURRING_EXPENSE_TOTAL",
            "CATEGORY_WITH_LARGEST_INCREASE, CATEGORY_WITH_LARGEST_INCREASE",
            "LARGEST_CATEGORY_TRANSACTION, LARGEST_CATEGORY_TRANSACTION",
            "AVERAGE_MONTHLY_EXPENSE_COMPARE, AVERAGE_MONTHLY_EXPENSE_COMPARE"
    })
    @DisplayName("maps every benchmark calculation_type to the typed enum")
    void mapsEveryKnownType(String raw, CalculationType expected) {
        BenchmarkQuestionDefinition question = baseQuestion(raw);
        applyTypeSpecificMetadata(question, expected);

        FinancialQueryIntent intent = mapper.map(question, 1L);

        assertThat(intent.getCalculationType()).isEqualTo(expected);
        assertThat(intent.getUserId()).isEqualTo(1L);
        assertThat(intent.getPeriod().getStartDate().toLocalDate()).isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(intent.getPeriod().getEndDate().toLocalDate()).isEqualTo(LocalDate.of(2026, 8, 31));
    }

    @Test
    @DisplayName("loads every calculation_type from benchmark-questions.json")
    void coversEntireBenchmarkFile() throws Exception {
        ObjectMapper json = new ObjectMapper();
        try (InputStream in = getClass().getResourceAsStream("/research/questions/benchmark-questions.json")) {
            List<BenchmarkQuestionDefinition> questions = json.readValue(in, new TypeReference<>() {
            });
            Set<String> types = questions.stream()
                    .map(BenchmarkQuestionDefinition::getCalculationType)
                    .collect(Collectors.toSet());

            assertThat(types).containsExactlyInAnyOrder(
                    "CATEGORY_EXPENSE_TOTAL",
                    "TOTAL_INCOME",
                    "TOTAL_EXPENSE",
                    "CATEGORY_COMPARE",
                    "PERIOD_EXPENSE_COMPARE",
                    "TOP_N_EXPENSE_CATEGORIES",
                    "RECURRING_EXPENSE_TOTAL",
                    "SPENDING_TREND",
                    "CATEGORY_WITH_LARGEST_INCREASE",
                    "LARGEST_TRANSACTION",
                    "LARGEST_CATEGORY_TRANSACTION",
                    "AVERAGE_MONTHLY_EXPENSE_COMPARE",
                    "PERIOD_INCOME_COMPARE");

            for (BenchmarkQuestionDefinition question : questions) {
                FinancialQueryIntent intent = mapper.map(question, 99L);
                assertThat(intent.getCalculationType().name()).isEqualTo(question.getCalculationType());
                assertThat(intent.getUserId()).isEqualTo(99L);
            }
        }
    }

    @Test
    @DisplayName("CATEGORY_COMPARE captures both categories")
    void categoryCompareFields() {
        BenchmarkQuestionDefinition q = baseQuestion("CATEGORY_COMPARE");
        q.setCategory("Food");
        q.setCompareCategory("Transportation");
        q.setTransactionType("expense");

        FinancialQueryIntent intent = mapper.map(q, 1L);

        assertThat(intent.getCategory()).isEqualTo("Food");
        assertThat(intent.getComparisonCategory()).isEqualTo("Transportation");
    }

    @Test
    @DisplayName("period compare uses compare_* dates as the comparison period")
    void periodCompareDates() {
        BenchmarkQuestionDefinition q = baseQuestion("PERIOD_EXPENSE_COMPARE");
        q.setCompareStartDate("2026-07-01");
        q.setCompareEndDate("2026-07-31");
        q.setTransactionType("expense");

        FinancialQueryIntent intent = mapper.map(q, 1L);

        assertThat(intent.getComparisonPeriod().getStartDate().toLocalDate())
                .isEqualTo(LocalDate.of(2026, 7, 1));
        assertThat(intent.getComparisonPeriod().getEndDate().toLocalDate())
                .isEqualTo(LocalDate.of(2026, 7, 31));
    }

    @Nested
    @DisplayName("validation")
    class ValidationTests {

        @Test
        void missingCalculationType() {
            BenchmarkQuestionDefinition q = baseQuestion("TOTAL_INCOME");
            q.setCalculationType(null);
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("calculation_type");
        }

        @Test
        void unknownCalculationType() {
            BenchmarkQuestionDefinition q = baseQuestion("NOT_A_REAL_TYPE");
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("Unknown calculation_type");
        }

        @Test
        void missingUserId() {
            assertThatThrownBy(() -> mapper.map(baseQuestion("TOTAL_INCOME"), null))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("userId");
        }

        @Test
        void nullQuestion() {
            assertThatThrownBy(() -> mapper.map(null, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class);
        }

        @Test
        void missingDates() {
            BenchmarkQuestionDefinition q = baseQuestion("TOTAL_EXPENSE");
            q.setStartDate(null);
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("start_date");
        }

        @Test
        void invalidDateRangeStartAfterEnd() {
            BenchmarkQuestionDefinition q = baseQuestion("TOTAL_EXPENSE");
            q.setStartDate("2026-08-31");
            q.setEndDate("2026-08-01");
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("Invalid date range");
        }

        @Test
        void missingCategoryForCategoryTotal() {
            BenchmarkQuestionDefinition q = baseQuestion("CATEGORY_EXPENSE_TOTAL");
            q.setCategory(null);
            q.setTransactionType("expense");
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("category");
        }

        @Test
        void missingCompareCategory() {
            BenchmarkQuestionDefinition q = baseQuestion("CATEGORY_COMPARE");
            q.setCategory("Food");
            q.setCompareCategory(null);
            q.setTransactionType("expense");
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("compare_category");
        }

        @Test
        void missingCompareDatesForPeriodCompare() {
            BenchmarkQuestionDefinition q = baseQuestion("PERIOD_EXPENSE_COMPARE");
            q.setTransactionType("expense");
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("compare_start_date");
        }

        @Test
        void missingTransactionType() {
            BenchmarkQuestionDefinition q = baseQuestion("TOTAL_INCOME");
            q.setTransactionType(null);
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("transaction_type");
        }

        @Test
        void optionalCategoryAllowedForTotalIncome() {
            BenchmarkQuestionDefinition q = baseQuestion("TOTAL_INCOME");
            q.setCategory(null);
            FinancialQueryIntent intent = mapper.map(q, 1L);
            assertThat(intent.getCategory()).isNull();
            assertThat(intent.getCalculationType()).isEqualTo(CalculationType.TOTAL_INCOME);
        }

        @Test
        void averageMonthlyRequiresBaselineDates() {
            BenchmarkQuestionDefinition q = baseQuestion("AVERAGE_MONTHLY_EXPENSE_COMPARE");
            q.setTransactionType("expense");
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class)
                    .hasMessageContaining("baseline");
        }

        @ValueSource(strings = {"not-a-date", "2026-13-01"})
        @ParameterizedTest
        void unparseableDates(String bad) {
            BenchmarkQuestionDefinition q = baseQuestion("TOTAL_EXPENSE");
            q.setStartDate(bad);
            assertThatThrownBy(() -> mapper.map(q, 1L))
                    .isInstanceOf(InvalidFinancialIntentException.class);
        }
    }

    static BenchmarkQuestionDefinition baseQuestion(String calculationType) {
        return BenchmarkQuestionDefinition.builder()
                .id("QTEST")
                .text("test question")
                .calculationType(calculationType)
                .profileId("profile_01")
                .transactionType(transactionTypeFor(calculationType))
                .startDate("2026-08-01")
                .endDate("2026-08-31")
                .build();
    }

    private static String transactionTypeFor(String calculationType) {
        if ("TOTAL_INCOME".equals(calculationType) || "PERIOD_INCOME_COMPARE".equals(calculationType)) {
            return "income";
        }
        return "expense";
    }

    private static void applyTypeSpecificMetadata(BenchmarkQuestionDefinition q, CalculationType type) {
        switch (type) {
            case CATEGORY_EXPENSE_TOTAL, LARGEST_CATEGORY_TRANSACTION -> q.setCategory("Food");
            case CATEGORY_COMPARE -> {
                q.setCategory("Food");
                q.setCompareCategory("Transportation");
            }
            case PERIOD_EXPENSE_COMPARE, PERIOD_INCOME_COMPARE, SPENDING_TREND, CATEGORY_WITH_LARGEST_INCREASE -> {
                q.setCompareStartDate("2026-07-01");
                q.setCompareEndDate("2026-07-31");
            }
            case TOP_N_EXPENSE_CATEGORIES -> q.setLimit(3);
            case RECURRING_EXPENSE_TOTAL -> q.setRecurringOnly(true);
            case AVERAGE_MONTHLY_EXPENSE_COMPARE -> {
                q.setBaselineStartDate("2026-06-01");
                q.setBaselineEndDate("2026-07-31");
            }
            default -> {
            }
        }
    }
}
