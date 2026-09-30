package com.upiq.research.experiment;

import java.util.Objects;

/** Explicit generation failure detail; a failed result must not contain a fabricated answer. */
public record ExperimentError(ExperimentErrorCode code, String message) {
    public ExperimentError {
        Objects.requireNonNull(code, "code is required");
        if (message == null || message.isBlank()) throw new IllegalArgumentException("message is required");
    }
}
