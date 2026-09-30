package com.upiq.financial.dashboard;

import com.upiq.research.calculation.FinancialCalculationService;
import com.upiq.research.calculation.dto.CategoryTotalResult;
import com.upiq.research.calculation.dto.NetBalanceResult;
import com.upiq.research.calculation.dto.PeriodComparisonResult;
import com.upiq.research.calculation.dto.PeriodRange;
import com.upiq.transaction.model.Transaction;
import com.upiq.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialDashboardService {

    private final FinancialCalculationService calculationService;
    private final TransactionRepository transactionRepository;

    public FinancialDashboardResponse getDashboard(Long userId) {
        Optional<Transaction> latestTransaction = transactionRepository.findTopByUserIdOrderByDateDesc(userId);
        if (latestTransaction.isEmpty()) {
            return FinancialDashboardResponse.builder()
                    .hasTransactionData(false)
                    .categorySpending(List.of())
                    .build();
        }

        YearMonth latestMonth = YearMonth.from(latestTransaction.get().getDate());
        YearMonth previousMonth = latestMonth.minusMonths(1);
        PeriodRange currentPeriod = period(latestMonth);
        PeriodRange previousPeriod = period(previousMonth);

        NetBalanceResult summary = calculationService.calculateNetBalance(userId, currentPeriod);
        PeriodComparisonResult comparison = calculationService.comparePeriods(
                userId, previousPeriod, currentPeriod, "expense");

        List<String> categories = transactionRepository
                .findByUserIdAndTypeIgnoreCaseAndDateBetweenOrderByDateDesc(
                        userId, "expense", currentPeriod.getStartDate(), currentPeriod.getEndDate())
                .stream()
                .map(Transaction::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .toList();

        List<CategoryTotalResult> categorySpending = categories.stream()
                .map(category -> calculationService.calculateCategoryTotal(
                        userId, category, "expense", currentPeriod))
                .sorted(Comparator.comparing(CategoryTotalResult::getTotal).reversed())
                .toList();

        return FinancialDashboardResponse.builder()
                .hasTransactionData(true)
                .latestAvailableMonth(FinancialDashboardMonth.from(latestMonth))
                .previousAvailableMonth(FinancialDashboardMonth.from(previousMonth))
                .currentMonthSummary(summary)
                .monthlyExpenseComparison(comparison)
                .categorySpending(categorySpending)
                .build();
    }

    private static PeriodRange period(YearMonth month) {
        LocalDate start = month.atDay(1);
        return PeriodRange.of(start, month.atEndOfMonth());
    }
}
