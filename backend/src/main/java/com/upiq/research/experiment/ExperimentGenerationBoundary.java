package com.upiq.research.experiment;

/** Shared future LLM boundary contract. No Ollama or A/B/C generation implementation is added here. */
@FunctionalInterface
public interface ExperimentGenerationBoundary {
    SystemResult generate(SystemInput input);
}
