package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/** Reproducibility metadata and ordered results for one A/B/C execution. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"runId", "timestamp", "model", "temperature", "ollamaBaseUrl", "ollamaTimeout", "datasetVersion", "numberOfCases", "systemsExecuted", "cases"})
public record ExperimentRun(
        String runId,
        Instant timestamp,
        String model,
        double temperature,
        String ollamaBaseUrl,
        Duration ollamaTimeout,
        String datasetVersion,
        int numberOfCases,
        List<ExperimentSystemId> systemsExecuted,
        List<ExperimentCaseResult> cases) {

    public ExperimentRun {
        if (runId == null || runId.isBlank()) throw new IllegalArgumentException("runId is required");
        Objects.requireNonNull(timestamp, "timestamp is required");
        if (model == null || model.isBlank()) throw new IllegalArgumentException("model is required");
        if (ollamaBaseUrl == null || ollamaBaseUrl.isBlank()) throw new IllegalArgumentException("ollamaBaseUrl is required");
        Objects.requireNonNull(ollamaTimeout, "ollamaTimeout is required");
        systemsExecuted = List.copyOf(systemsExecuted);
        cases = List.copyOf(cases);
        if (numberOfCases != cases.size()) throw new IllegalArgumentException("numberOfCases must match cases size");
    }
}
