package com.upiq.research.dataset.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EXPERIMENTAL: fixed benchmark question with enough metadata for Phase 3
 * deterministic ground-truth calculation. Does not store LLM answers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchmarkQuestionDefinition {

    private String id;
    private String text;
    @JsonProperty("reasoning_level")
    private String reasoningLevel;

    @JsonProperty("calculation_type")
    private String calculationType;

    @JsonProperty("profile_id")
    private String profileId;

    private String category;

    @JsonProperty("compare_category")
    private String compareCategory;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("compare_start_date")
    private String compareStartDate;

    @JsonProperty("compare_end_date")
    private String compareEndDate;

    @JsonProperty("baseline_start_date")
    private String baselineStartDate;

    @JsonProperty("baseline_end_date")
    private String baselineEndDate;

    @JsonProperty("transaction_type")
    private String transactionType;

    private Integer limit;

    @JsonProperty("recurring_only")
    private Boolean recurringOnly;
}
