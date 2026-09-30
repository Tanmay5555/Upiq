package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Objects;

/** The common, serializable request shape passed to an experiment generation boundary. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"systemId", "question", "context"})
public record SystemInput(ExperimentSystemId systemId, String question, SystemContext context) {

    public SystemInput {
        Objects.requireNonNull(systemId, "systemId is required");
        if (question == null || question.isBlank()) throw new IllegalArgumentException("question is required");
        boolean validContext = switch (systemId) {
            case A -> context == null;
            case B -> context instanceof RawTransactionContext;
            case C -> context instanceof VerifiedFinancialContextPayload;
        };
        if (!validContext) {
            throw new IllegalArgumentException("context type must match experiment system " + systemId);
        }
    }

    /** Constructs the condition-specific payload while preserving one canonical question. */
    public static SystemInput forCase(ExperimentSystemId systemId, ExperimentCase experimentCase) {
        Objects.requireNonNull(experimentCase, "experimentCase is required");
        SystemContext context = switch (Objects.requireNonNull(systemId, "systemId is required")) {
            case A -> null;
            case B -> new RawTransactionContext(experimentCase.profile(), experimentCase.relevantTransactions());
            case C -> new VerifiedFinancialContextPayload(experimentCase.verifiedFinancialContext());
        };
        return new SystemInput(systemId, experimentCase.question(), context);
    }
}
