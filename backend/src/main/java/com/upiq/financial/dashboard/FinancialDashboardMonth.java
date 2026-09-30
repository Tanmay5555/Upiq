package com.upiq.financial.dashboard;

import lombok.Builder;
import lombok.Value;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

@Value
@Builder
public class FinancialDashboardMonth {
    int year;
    int month;
    String label;

    public static FinancialDashboardMonth from(YearMonth yearMonth) {
        return FinancialDashboardMonth.builder()
                .year(yearMonth.getYear())
                .month(yearMonth.getMonthValue())
                .label(yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        + " " + yearMonth.getYear())
                .build();
    }
}
