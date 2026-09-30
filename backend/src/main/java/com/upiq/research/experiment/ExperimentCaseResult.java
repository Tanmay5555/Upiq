package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.upiq.research.context.FinancialContext;
import com.upiq.research.dataset.dto.BenchmarkQuestionDefinition;

import java.util.List;
import java.util.Objects;

/** Persisted case metadata and evaluation reference kept outside every model input. */
@JsonPropertyOrder({"caseId", "question", "benchmarkMetadata", "benchmarkReference", "systemResults"})
public record ExperimentCaseResult(
        String caseId,
        String question,
        BenchmarkQuestionDefinition benchmarkMetadata,
        FinancialContext benchmarkReference,
        List<ExperimentSystemRunResult> systemResults) {

    public ExperimentCaseResult {
        Objects.requireNonNull(caseId, "caseId is required");
        Objects.requireNonNull(question, "question is required");
        Objects.requireNonNull(benchmarkMetadata, "benchmarkMetadata is required");
        Objects.requireNonNull(benchmarkReference, "benchmarkReference is required");
        systemResults = List.copyOf(systemResults);
    }
}
