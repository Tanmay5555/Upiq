package com.upiq.research.llm;

import com.upiq.research.context.FinancialContext;
import org.springframework.stereotype.Service;

/** System C explanation flow; numerical answers remain owned by the deterministic engine. */
@Service
public class SystemCService {
    private final GroundedFinancialPromptBuilder promptBuilder;
    private final OllamaClient ollamaClient;

    public SystemCService(GroundedFinancialPromptBuilder promptBuilder, OllamaClient ollamaClient) {
        this.promptBuilder = promptBuilder;
        this.ollamaClient = ollamaClient;
    }

    public SystemCResponse answer(String question, FinancialContext context) {
        String prompt = promptBuilder.build(question, context);
        long start = System.nanoTime();
        OllamaGenerateResponse generated = ollamaClient.generate(prompt);
        long latencyMs = (System.nanoTime() - start) / 1_000_000;
        return SystemCResponse.builder()
                .answer(generated.getResponse())
                .model(generated.getModel() == null || generated.getModel().isBlank()
                        ? ollamaClient.properties().getModel() : generated.getModel())
                .latencyMs(latencyMs)
                .promptEvalCount(generated.getPromptEvalCount())
                .evalCount(generated.getEvalCount())
                .ollamaTotalDurationNs(generated.getTotalDurationNs())
                .build();
    }
}
