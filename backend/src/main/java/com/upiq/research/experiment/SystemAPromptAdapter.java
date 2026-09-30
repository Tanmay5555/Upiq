package com.upiq.research.experiment;

import org.springframework.stereotype.Component;

/** System A receives the question only. */
@Component
public class SystemAPromptAdapter implements SystemPromptAdapter {
    @Override
    public ExperimentSystemId systemId() {
        return ExperimentSystemId.A;
    }

    @Override
    public String buildPrompt(SystemInput input) {
        requireSystem(input, ExperimentSystemId.A);
        return "USER QUESTION\n" + input.question();
    }

    static void requireSystem(SystemInput input, ExperimentSystemId expected) {
        if (input == null || input.systemId() != expected) {
            throw new IllegalArgumentException("System input must belong to system " + expected);
        }
    }
}
