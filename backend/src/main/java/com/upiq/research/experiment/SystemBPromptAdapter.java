package com.upiq.research.experiment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/** System B receives only the raw transaction payload assigned to its input. */
@Component
public class SystemBPromptAdapter implements SystemPromptAdapter {
    private final ObjectMapper objectMapper;

    public SystemBPromptAdapter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ExperimentSystemId systemId() {
        return ExperimentSystemId.B;
    }

    @Override
    public String buildPrompt(SystemInput input) {
        SystemAPromptAdapter.requireSystem(input, ExperimentSystemId.B);
        RawTransactionContext rawContext = (RawTransactionContext) input.context();
        try {
            return "USER QUESTION\n" + input.question()
                    + "\n\nRAW TRANSACTION CONTEXT\n"
                    + "The following is raw user transaction context assigned to this case. "
                    + "It is provided as records, not as verified aggregate facts.\n"
                    + objectMapper.writeValueAsString(rawContext);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize raw transaction context", ex);
        }
    }
}
