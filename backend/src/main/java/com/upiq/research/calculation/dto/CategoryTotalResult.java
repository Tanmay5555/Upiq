package com.upiq.research.calculation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTotalResult {
    private Long userId;
    private String category;
    private String transactionType;
    private PeriodRange period;
    private BigDecimal total;
    private long transactionCount;
    /** Percentage of total expense for the same period. Null if transactionType is not expense. */
    private BigDecimal percentageOfTotalExpense;
}
