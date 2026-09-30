package com.upiq.research.intent;

import com.upiq.research.calculation.dto.PeriodRange;
import lombok.Builder;
import lombok.Value;

/**
 * Typed, deterministic intent derived from benchmark question metadata.
 * Contains only fields needed to route to {@code FinancialCalculationService}.
 */
@Value
@Builder(toBuilder = true)
public class FinancialQueryIntent {

    CalculationType calculationType;
    Long userId;
    String questionId;
    String category;
    String comparisonCategory;
    PeriodRange period;
    PeriodRange comparisonPeriod;
    PeriodRange baselinePeriod;
    String transactionType;
    Integer limit;
    Boolean recurringOnly;
}
