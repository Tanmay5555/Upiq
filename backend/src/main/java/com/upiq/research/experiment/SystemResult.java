package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/** Structured result and optional Ollama metadata for one system condition. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"systemId", "answer", "model", "latencyMs", "promptEvalCount", "evalCount", "ollamaTotalDurationNs", "error"})
public record SystemResult(
        ExperimentSystemId systemId,
        String answer,
        String model,
        long latencyMs,
        Integer promptEvalCount,
        Integer evalCount,
        Long ollamaTotalDurationNs,
        ExperimentError error) {

    public SystemResult {
        Objects.requireNonNull(systemId, "systemId is required");
        if (latencyMs < 0) throw new IllegalArgumentException("latencyMs cannot be negative");
        if (error == null && (answer == null || answer.isBlank())) {
            throw new IllegalArgumentException("successful result requires an answer");
        }
        if (error != null && answer != null) {
            throw new IllegalArgumentException("failed result cannot contain an answer");
        }
    }

    public static SystemResult success(
            ExperimentSystemId systemId,
            String answer,
            String model,
            long latencyMs,
            Integer promptEvalCount,
            Integer evalCount,
            Long ollamaTotalDurationNs) {
        return new SystemResult(systemId, answer, model, latencyMs,
                promptEvalCount, evalCount, ollamaTotalDurationNs, null);
    }

    public static SystemResult failure(
            ExperimentSystemId systemId,
            String model,
            long latencyMs,
            ExperimentError error) {
        return new SystemResult(systemId, null, model, latencyMs, null, null, null,
                Objects.requireNonNull(error, "error is required"));
    }

    public boolean succeeded() {
        return error == null;
    }
}
