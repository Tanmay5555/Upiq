package com.upiq.research.experiment;

import com.upiq.research.llm.GroundedFinancialPromptBuilder;
import org.springframework.stereotype.Component;

/** System C delegates prompt creation to the existing grounded financial prompt builder. */
@Component
public class SystemCPromptAdapter implements SystemPromptAdapter {
    private final GroundedFinancialPromptBuilder groundedPromptBuilder;

    public SystemCPromptAdapter(GroundedFinancialPromptBuilder groundedPromptBuilder) {
        this.groundedPromptBuilder = groundedPromptBuilder;
    }

    @Override
    public ExperimentSystemId systemId() {
        return ExperimentSystemId.C;
    }

    @Override
    public String buildPrompt(SystemInput input) {
        SystemAPromptAdapter.requireSystem(input, ExperimentSystemId.C);
        VerifiedFinancialContextPayload payload = (VerifiedFinancialContextPayload) input.context();
        return groundedPromptBuilder.build(input.question(), payload.financialContext());
    }
}
