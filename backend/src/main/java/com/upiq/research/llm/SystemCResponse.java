package com.upiq.research.llm;

import lombok.Builder;
import lombok.Value;

/** Structured explanation returned by the System C LLM layer. */
@Value
@Builder
public class SystemCResponse {
    String answer;
    String model;
    long latencyMs;
    Integer promptEvalCount;
    Integer evalCount;
    Long ollamaTotalDurationNs;
}
