package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.upiq.research.context.FinancialContext;

import java.util.Objects;

/** Verified deterministic financial facts supplied to System C. */
@JsonPropertyOrder({"financialContext"})
public record VerifiedFinancialContextPayload(FinancialContext financialContext) implements SystemContext {
    public VerifiedFinancialContextPayload {
        Objects.requireNonNull(financialContext, "financialContext is required");
    }
}
