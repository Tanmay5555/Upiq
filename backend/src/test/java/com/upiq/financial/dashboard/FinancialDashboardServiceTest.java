package com.upiq.financial.dashboard;

import com.upiq.research.calculation.FinancialCalculationService;
import com.upiq.research.calculation.dto.PeriodComparisonResult;
import com.upiq.research.calculation.dto.PeriodRange;
import com.upiq.transaction.model.Transaction;
import com.upiq.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialDashboardServiceTest {

    @Mock
    private FinancialCalculationService calculationService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FinancialDashboardService dashboardService;

    @Test
    void usesAugustWhenAugustIsTheLatestAvailableMonth() {
        Long userId = 42L;
        stubTransactions(userId, LocalDateTime.of(2026, 8, 31, 10, 30));

        FinancialDashboardResponse response = dashboardService.getDashboard(userId);

        assertLatestMonth(response, 2026, 8);
        assertComparisonMonths(2026, 7);
    }

    @Test
    void usesSeptemberWhenUserHasAugustAndSeptemberTransactions() {
        Long userId = 42L;
        stubTransactions(userId,
                LocalDateTime.of(2026, 8, 31, 10, 30),
                LocalDateTime.of(2026, 9, 2, 8, 0));

        FinancialDashboardResponse response = dashboardService.getDashboard(userId);

        assertLatestMonth(response, 2026, 9);
        assertComparisonMonths(2026, 8);
    }

    @Test
    void usesJulyWhenJulyIsTheOnlyAvailableMonth() {
        Long userId = 42L;
        stubTransactions(userId, LocalDateTime.of(2026, 7, 1, 0, 0));

        FinancialDashboardResponse response = dashboardService.getDashboard(userId);

        assertLatestMonth(response, 2026, 7);
        assertComparisonMonths(2026, 6);
    }

    @Test
    void returnsExplicitEmptyStateForUserWithoutTransactions() {
        Long userId = 42L;
        when(transactionRepository.findTopByUserIdOrderByDateDesc(userId)).thenReturn(Optional.empty());

        FinancialDashboardResponse response = dashboardService.getDashboard(userId);

        assertFalse(response.isHasTransactionData());
        assertNull(response.getLatestAvailableMonth());
        assertNull(response.getPreviousAvailableMonth());
        assertNull(response.getCurrentMonthSummary());
        assertNull(response.getMonthlyExpenseComparison());
        assertEquals(List.of(), response.getCategorySpending());
        verify(calculationService, never()).calculateNetBalance(any(), any());
    }

    @Test
    void latestTransactionLookupIsScopedToEachRequestedUser() {
        Long userA = 1L;
        Long userB = 2L;
        stubTransactions(userA, LocalDateTime.of(2026, 8, 30, 23, 0), LocalDateTime.of(2026, 8, 31, 23, 0));
        stubTransactions(userB, LocalDateTime.of(2026, 7, 30, 23, 0), LocalDateTime.of(2026, 7, 31, 23, 0));

        FinancialDashboardResponse responseA = dashboardService.getDashboard(userA);
        FinancialDashboardResponse responseB = dashboardService.getDashboard(userB);

        assertLatestMonth(responseA, 2026, 8);
        assertLatestMonth(responseB, 2026, 7);
        verify(transactionRepository).findTopByUserIdOrderByDateDesc(userA);
        verify(transactionRepository).findTopByUserIdOrderByDateDesc(userB);
    }

    private void stubTransactions(Long userId, LocalDateTime... transactionDates) {
        when(transactionRepository.findTopByUserIdOrderByDateDesc(userId))
                .thenAnswer(invocation -> Arrays.stream(transactionDates)
                        .map(date -> Transaction.builder().userId(userId).date(date).build())
                        .max(Comparator.comparing(Transaction::getDate)));
        when(calculationService.calculateNetBalance(eq(userId), any(PeriodRange.class)))
                .thenReturn(null);
        when(calculationService.comparePeriods(eq(userId), any(PeriodRange.class), any(PeriodRange.class), eq("expense")))
                .thenReturn(PeriodComparisonResult.builder().build());
        when(transactionRepository.findByUserIdAndTypeIgnoreCaseAndDateBetweenOrderByDateDesc(
                eq(userId), eq("expense"), any(), any())).thenReturn(List.of());
    }

    private void assertLatestMonth(FinancialDashboardResponse response, int year, int month) {
        assertTrue(response.isHasTransactionData());
        assertEquals(year, response.getLatestAvailableMonth().getYear());
        assertEquals(month, response.getLatestAvailableMonth().getMonth());
        assertEquals(YearMonth.of(year, month).getMonth().name().charAt(0)
                        + YearMonth.of(year, month).getMonth().name().substring(1).toLowerCase()
                        + " " + year,
                response.getLatestAvailableMonth().getLabel());
    }

    private void assertComparisonMonths(int previousYear, int previousMonth) {
        ArgumentCaptor<PeriodRange> previousCaptor = ArgumentCaptor.forClass(PeriodRange.class);
        ArgumentCaptor<PeriodRange> latestCaptor = ArgumentCaptor.forClass(PeriodRange.class);
        verify(calculationService).comparePeriods(any(), previousCaptor.capture(), latestCaptor.capture(), eq("expense"));

        YearMonth previous = YearMonth.of(previousYear, previousMonth);
        YearMonth latest = previous.plusMonths(1);
        assertEquals(PeriodRange.of(previous.atDay(1), previous.atEndOfMonth()), previousCaptor.getValue());
        assertEquals(PeriodRange.of(latest.atDay(1), latest.atEndOfMonth()), latestCaptor.getValue());
    }
}
