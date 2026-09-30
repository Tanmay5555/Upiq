package com.upiq.research.experiment;

/** Converts one condition's typed input to the prompt sent through the shared Ollama boundary. */
public interface SystemPromptAdapter {
    ExperimentSystemId systemId();

    String buildPrompt(SystemInput input);
}
