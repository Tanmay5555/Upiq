package com.upiq.research.intent;

import com.upiq.research.calculation.FinancialCalculationService;
import com.upiq.research.calculation.dto.CategoryComparisonResult;
import com.upiq.research.calculation.dto.CategoryTotalResult;
import com.upiq.research.calculation.dto.NetBalanceResult;
import com.upiq.research.calculation.dto.PeriodComparisonResult;
import com.upiq.research.calculation.dto.PeriodRange;
import com.upiq.research.calculation.dto.TopTransactionEntry;
import com.upiq.research.dataset.dto.BenchmarkQuestionDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.upiq.research.intent.FinancialIntentMapperTest.baseQuestion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FinancialIntentDispatcher")
class FinancialIntentDispatcherTest {

    private static final PeriodRange AUG = PeriodRange.of(LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 31));
    private static final PeriodRange JUL = PeriodRange.of(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));

    @Mock
    private FinancialCalculationService calculationService;

    @Spy
    private FinancialIntentMapper intentMapper = new FinancialIntentMapper();

    @InjectMocks
    private FinancialIntentDispatcher dispatcher;

    @Test
    @DisplayName("CATEGORY_EXPENSE_TOTAL → calculateCategoryTotal")
    void routesCategoryExpenseTotal() {
        BenchmarkQuestionDefinition q = baseQuestion("CATEGORY_EXPENSE_TOTAL");
        q.setCategory("Food");
        CategoryTotalResult engineResult = CategoryTotalResult.builder()
                .category("Food")
                .total(new BigDecimal("3620.00"))
                .transactionCount(6)
                .build();
        when(calculationService.calculateCategoryTotal(eq(1L), eq("Food"), eq("expense"), any()))
                .thenReturn(engineResult);

        DispatchedCalculation dispatched = dispatcher.dispatch(q, 1L);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.CATEGORY_EXPENSE_TOTAL);
        assertThat(dispatched.getVerifiedResult()).isSameAs(engineResult);
        verify(calculationService).calculateCategoryTotal(eq(1L), eq("Food"), eq("expense"), eq(AUG));
    }

    @Test
    @DisplayName("TOTAL_INCOME → calculateTotalIncome")
    void routesTotalIncome() {
        when(calculationService.calculateTotalIncome(1L, AUG)).thenReturn(new BigDecimal("85000.00"));

        DispatchedCalculation dispatched = dispatcher.dispatch(baseQuestion("TOTAL_INCOME"), 1L);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.TOTAL_INCOME);
        assertThat((BigDecimal) dispatched.getVerifiedResult()).isEqualByComparingTo("85000.00");
        verify(calculationService).calculateTotalIncome(1L, AUG);
    }

    @Test
    @DisplayName("TOTAL_EXPENSE → calculateTotalExpense")
    void routesTotalExpense() {
        when(calculationService.calculateTotalExpense(1L, AUG)).thenReturn(new BigDecimal("13166.00"));

        DispatchedCalculation dispatched = dispatcher.dispatch(baseQuestion("TOTAL_EXPENSE"), 1L);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.TOTAL_EXPENSE);
        assertThat((BigDecimal) dispatched.getVerifiedResult()).isEqualByComparingTo("13166.00");
        verify(calculationService).calculateTotalExpense(1L, AUG);
    }

    @Test
    @DisplayName("NET_BALANCE → existing calculateNetBalance")
    void routesNetBalance() {
        FinancialQueryIntent intent = FinancialQueryIntent.builder()
                .calculationType(CalculationType.NET_BALANCE)
                .userId(1L)
                .period(AUG)
                .build();
        NetBalanceResult engineResult = NetBalanceResult.builder()
                .netBalance(new BigDecimal("1380.00")).build();
        when(calculationService.calculateNetBalance(1L, AUG)).thenReturn(engineResult);

        DispatchedCalculation dispatched = dispatcher.dispatch(intent);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.NET_BALANCE);
        assertThat(dispatched.getVerifiedResult()).isSameAs(engineResult);
        verify(calculationService).calculateNetBalance(1L, AUG);
    }

    @Test
    @DisplayName("CATEGORY_COMPARE → compareCategories")
    void routesCategoryCompare() {
        BenchmarkQuestionDefinition q = baseQuestion("CATEGORY_COMPARE");
        q.setCategory("Food");
        q.setCompareCategory("Transportation");
        CategoryComparisonResult engineResult = CategoryComparisonResult.builder()
                .category1("Food")
                .category2("Transportation")
                .higherCategory("Food")
                .build();
        when(calculationService.compareCategories(eq(1L), eq("Food"), eq("Transportation"), eq("expense"), eq(AUG)))
                .thenReturn(engineResult);

        DispatchedCalculation dispatched = dispatcher.dispatch(q, 1L);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.CATEGORY_COMPARE);
        assertThat(dispatched.getVerifiedResult()).isSameAs(engineResult);
        verify(calculationService).compareCategories(1L, "Food", "Transportation", "expense", AUG);
    }

    @Test
    @DisplayName("PERIOD_EXPENSE_COMPARE → comparePeriods(compare=baseline, start=current)")
    void routesPeriodExpenseCompare() {
        BenchmarkQuestionDefinition q = baseQuestion("PERIOD_EXPENSE_COMPARE");
        q.setCompareStartDate("2026-07-01");
        q.setCompareEndDate("2026-07-31");
        PeriodComparisonResult engineResult = PeriodComparisonResult.builder()
                .period1Total(new BigDecimal("11356.00"))
                .period2Total(new BigDecimal("13166.00"))
                .build();
        when(calculationService.comparePeriods(1L, JUL, AUG, "expense")).thenReturn(engineResult);

        DispatchedCalculation dispatched = dispatcher.dispatch(q, 1L);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.PERIOD_EXPENSE_COMPARE);
        verify(calculationService).comparePeriods(1L, JUL, AUG, "expense");
        assertThat(dispatched.getVerifiedResult()).isSameAs(engineResult);
    }

    @Test
    @DisplayName("PERIOD_INCOME_COMPARE → comparePeriods with income")
    void routesPeriodIncomeCompare() {
        BenchmarkQuestionDefinition q = baseQuestion("PERIOD_INCOME_COMPARE");
        q.setStartDate("2026-07-01");
        q.setEndDate("2026-07-31");
        q.setCompareStartDate("2026-08-01");
        q.setCompareEndDate("2026-08-31");
        q.setTransactionType("income");
        PeriodComparisonResult engineResult = PeriodComparisonResult.builder().build();
        when(calculationService.comparePeriods(1L, AUG, JUL, "income")).thenReturn(engineResult);

        DispatchedCalculation dispatched = dispatcher.dispatch(q, 1L);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.PERIOD_INCOME_COMPARE);
        verify(calculationService).comparePeriods(1L, AUG, JUL, "income");
    }

    @Test
    @DisplayName("SPENDING_TREND → comparePeriods (same engine as period expense compare)")
    void routesSpendingTrend() {
        BenchmarkQuestionDefinition q = baseQuestion("SPENDING_TREND");
        q.setCompareStartDate("2026-07-01");
        q.setCompareEndDate("2026-07-31");
        PeriodComparisonResult engineResult = PeriodComparisonResult.builder()
                .absoluteDifference(new BigDecimal("1810.00"))
                .build();
        when(calculationService.comparePeriods(1L, JUL, AUG, "expense")).thenReturn(engineResult);

        DispatchedCalculation dispatched = dispatcher.dispatch(q, 1L);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.SPENDING_TREND);
        verify(calculationService).comparePeriods(1L, JUL, AUG, "expense");
        verify(calculationService, never()).calculateTotalExpense(any(), any());
    }

    @Test
    @DisplayName("LARGEST_TRANSACTION → findTopTransactions with limit 1 by default")
    void routesLargestTransaction() {
        List<TopTransactionEntry> engineResult = List.of(TopTransactionEntry.builder()
                .amount(new BigDecimal("2100.00"))
                .category("Utilities")
                .rank(1)
                .date(LocalDateTime.of(2026, 8, 6, 10, 0))
                .build());
        when(calculationService.findTopTransactions(1L, AUG, "expense", 1)).thenReturn(engineResult);

        DispatchedCalculation dispatched = dispatcher.dispatch(baseQuestion("LARGEST_TRANSACTION"), 1L);

        assertThat(dispatched.getIntent().getCalculationType()).isEqualTo(CalculationType.LARGEST_TRANSACTION);
        verify(calculationService).findTopTransactions(1L, AUG, "expense", 1);
        assertThat(dispatched.getVerifiedResult()).isSameAs(engineResult);
    }

    @ParameterizedTest
    @EnumSource(value = CalculationType.class, names = {
            "TOP_N_EXPENSE_CATEGORIES",
            "RECURRING_EXPENSE_TOTAL",
            "CATEGORY_WITH_LARGEST_INCREASE",
            "LARGEST_CATEGORY_TRANSACTION",
            "AVERAGE_MONTHLY_EXPENSE_COMPARE"
    })
    @DisplayName("known unsupported types fail clearly and do not call the engine")
    void unsupportedTypesFail(CalculationType type) {
        BenchmarkQuestionDefinition q = baseQuestion(type.name());
        applyUnsupportedMetadata(q, type);

        assertThatThrownBy(() -> dispatcher.dispatch(q, 1L))
                .isInstanceOf(UnsupportedCalculationTypeException.class)
                .satisfies(ex -> assertThat(((UnsupportedCalculationTypeException) ex).getCalculationType())
                        .isEqualTo(type));

        verify(calculationService, never()).calculateTotalExpense(any(), any());
        verify(calculationService, never()).calculateTotalIncome(any(), any());
        verify(calculationService, never()).calculateCategoryTotal(any(), any(), any(), any());
        verify(calculationService, never()).comparePeriods(any(), any(), any(), any());
        verify(calculationService, never()).compareCategories(any(), any(), any(), any(), any());
        verify(calculationService, never()).findTopTransactions(any(), any(), any(), anyInt());
    }

    private static void applyUnsupportedMetadata(BenchmarkQuestionDefinition q, CalculationType type) {
        switch (type) {
            case TOP_N_EXPENSE_CATEGORIES -> q.setLimit(3);
            case RECURRING_EXPENSE_TOTAL -> q.setRecurringOnly(true);
            case CATEGORY_WITH_LARGEST_INCREASE -> {
                q.setCompareStartDate("2026-07-01");
                q.setCompareEndDate("2026-07-31");
            }
            case LARGEST_CATEGORY_TRANSACTION -> q.setCategory("Food");
            case AVERAGE_MONTHLY_EXPENSE_COMPARE -> {
                q.setBaselineStartDate("2026-06-01");
                q.setBaselineEndDate("2026-07-31");
            }
            default -> {
            }
        }
    }
}
