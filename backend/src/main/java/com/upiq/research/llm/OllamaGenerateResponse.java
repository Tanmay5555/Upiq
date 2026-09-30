package com.upiq.research.llm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * Subset of Ollama {@code /api/generate} (non-streaming) JSON that System C preserves.
 * Token counts are copied only when present; they are never invented.
 */
@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class OllamaGenerateResponse {

    String model;
    String response;
    Boolean done;

    @JsonProperty("prompt_eval_count")
    Integer promptEvalCount;

    @JsonProperty("eval_count")
    Integer evalCount;

    /** Nanoseconds, when Ollama includes it. */
    @JsonProperty("total_duration")
    Long totalDurationNs;
}
