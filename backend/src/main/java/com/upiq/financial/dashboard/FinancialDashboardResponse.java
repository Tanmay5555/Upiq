package com.upiq.financial.dashboard;

import com.upiq.research.calculation.dto.CategoryTotalResult;
import com.upiq.research.calculation.dto.NetBalanceResult;
import com.upiq.research.calculation.dto.PeriodComparisonResult;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FinancialDashboardResponse {
    boolean hasTransactionData;
    FinancialDashboardMonth latestAvailableMonth;
    FinancialDashboardMonth previousAvailableMonth;
    NetBalanceResult currentMonthSummary;
    PeriodComparisonResult monthlyExpenseComparison;
    List<CategoryTotalResult> categorySpending;
}
