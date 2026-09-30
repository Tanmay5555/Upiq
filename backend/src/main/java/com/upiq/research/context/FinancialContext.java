package com.upiq.research.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stable, machine-readable financial context assembled from verified engine results.
 *
 * <p>This object contains facts only. Prompt instructions for a future LLM belong
 * in a later checkpoint and must not be mixed in here.
 */
@Value
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "contextVersion",
        "source",
        "verified",
        "calculationType",
        "transactionType",
        "period",
        "comparisonPeriod",
        "facts",
        "definitions"
})
public class FinancialContext {

    public static final String CONTEXT_VERSION = "1.0";
    public static final String SOURCE = "UPIQ_DETERMINISTIC_FINANCIAL_ENGINE";

    String contextVersion;
    String source;
    boolean verified;
    String calculationType;
    String transactionType;
    PeriodWindow period;
    PeriodWindow comparisonPeriod;
    FinancialFacts facts;
    Map<String, String> definitions;

    public static Map<String, String> orderedDefinitions(Map<String, String> source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return new LinkedHashMap<>(source);
    }
}
