package com.upiq.financial.report;

import com.upiq.financial.dashboard.FinancialDashboardMonth;
import com.upiq.financial.dashboard.FinancialDashboardResponse;
import com.upiq.financial.dashboard.FinancialDashboardService;
import com.upiq.research.calculation.FinancialCalculationService;
import com.upiq.research.calculation.dto.CategoryTotalResult;
import com.upiq.research.calculation.dto.NetBalanceResult;
import com.upiq.research.calculation.dto.PeriodComparisonResult;
import com.upiq.research.calculation.dto.PeriodRange;
import com.upiq.research.calculation.dto.TopTransactionEntry;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialReportServiceTest {
    @Mock private FinancialDashboardService dashboardService;
    @Mock private FinancialCalculationService calculationService;
    @InjectMocks private FinancialReportService reportService;

    @Test
    void reportUsesAuthenticatedUserDashboardTotalsLatestMonthAndEngineTransactions() throws Exception {
        long userA = 11L;
        FinancialDashboardResponse dashboard = dashboard(2026, 8);
        when(dashboardService.getDashboard(userA)).thenReturn(dashboard);
        when(calculationService.findTopTransactions(eq(userA), any(PeriodRange.class), eq("expense"), eq(5)))
                .thenReturn(List.of(TopTransactionEntry.builder().userId(userA).amount(new BigDecimal("75.00"))
                        .description("Cafe").category("Food").date(LocalDateTime.of(2026, 8, 12, 10, 0)).build()));

        GeneratedFinancialReport generated = reportService.generate(userA);

        assertEquals("upiq-financial-report-2026-08.pdf", generated.filename());
        try (var document = Loader.loadPDF(generated.content())) {
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("UPIQ AI - Personal Finance Report"));
            assertTrue(text.contains("August 2026"));
            assertTrue(text.contains("July 2026"));
            assertTrue(text.contains("1234.50"));
            assertTrue(text.contains("875.25"));
            assertTrue(text.contains("359.25"));
            assertTrue(text.contains("Food"));
            assertTrue(text.contains("Cafe"));
            assertTrue(text.contains("Calculated from transaction data"));
        }
        verify(dashboardService).getDashboard(userA);
        verify(calculationService).findTopTransactions(eq(userA), eq(PeriodRange.of(
                java.time.LocalDate.of(2026, 8, 1), java.time.LocalDate.of(2026, 8, 31))), eq("expense"), eq(5));
        verify(dashboardService, never()).getDashboard(22L);
    }

    @Test
    void emptyUserReceivesSafePdfWithoutCalculationLookups() throws Exception {
        when(dashboardService.getDashboard(7L)).thenReturn(FinancialDashboardResponse.builder()
                .hasTransactionData(false).categorySpending(List.of()).build());

        GeneratedFinancialReport generated = reportService.generate(7L);

        assertEquals("upiq-financial-report-no-data.pdf", generated.filename());
        try (var document = Loader.loadPDF(generated.content())) {
            String text = new PDFTextStripper().getText(document);
            assertTrue(text.contains("No transaction data is available"));
        }
        verifyNoInteractions(calculationService);
    }

    @Test
    void distinctUserIdsArePassedToEveryDataService() {
        when(dashboardService.getDashboard(1L)).thenReturn(FinancialDashboardResponse.builder().build());
        when(dashboardService.getDashboard(2L)).thenReturn(FinancialDashboardResponse.builder().build());

        reportService.generate(1L);
        reportService.generate(2L);

        verify(dashboardService).getDashboard(1L);
        verify(dashboardService).getDashboard(2L);
        verifyNoInteractions(calculationService);
    }

    private static FinancialDashboardResponse dashboard(int year, int month) {
        FinancialDashboardMonth latest = FinancialDashboardMonth.from(java.time.YearMonth.of(year, month));
        FinancialDashboardMonth previous = FinancialDashboardMonth.from(java.time.YearMonth.of(year, month).minusMonths(1));
        return FinancialDashboardResponse.builder().hasTransactionData(true)
                .latestAvailableMonth(latest).previousAvailableMonth(previous)
                .currentMonthSummary(NetBalanceResult.builder().totalIncome(new BigDecimal("1234.50"))
                        .totalExpense(new BigDecimal("875.25")).netBalance(new BigDecimal("359.25"))
                        .savingsRate(new BigDecimal("29.10")).build())
                .monthlyExpenseComparison(PeriodComparisonResult.builder().period1Total(new BigDecimal("700.00"))
                        .period2Total(new BigDecimal("875.25")).absoluteDifference(new BigDecimal("175.25"))
                        .percentageDifference(new BigDecimal("25.04")).build())
                .categorySpending(List.of(CategoryTotalResult.builder().category("Food")
                        .total(new BigDecimal("500.00")).percentageOfTotalExpense(new BigDecimal("57.14")).build()))
                .build();
    }
}
