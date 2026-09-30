package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.upiq.research.dataset.dto.ResearchProfileDefinition;
import com.upiq.research.dataset.dto.ResearchTransactionDefinition;

import java.util.List;
import java.util.Objects;

/** User profile and raw synthetic transaction rows for System B. */
@JsonPropertyOrder({"profile", "transactions"})
public record RawTransactionContext(
        ResearchProfileDefinition profile,
        List<ResearchTransactionDefinition> transactions) implements SystemContext {

    public RawTransactionContext {
        Objects.requireNonNull(profile, "profile is required");
        Objects.requireNonNull(transactions, "transactions is required");
        transactions = List.copyOf(transactions);
    }
}
