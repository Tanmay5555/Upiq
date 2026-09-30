package com.upiq.research.experiment;

/** Stable failure categories for a system generation result. */
public enum ExperimentErrorCode {
    UNAVAILABLE,
    TIMEOUT,
    HTTP_ERROR,
    MALFORMED_RESPONSE,
    EMPTY_RESPONSE,
    INVALID_CONFIGURATION,
    GENERATION_ERROR
}
