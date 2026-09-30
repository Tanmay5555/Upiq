package com.upiq.research.llm;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OllamaGenerateRequest {

    String model;
    String prompt;
    @Builder.Default
    boolean stream = false;
    Map<String, Object> options;
}
