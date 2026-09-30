package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Typed, serializable payload variants supplied to a system along with its question. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "context_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RawTransactionContext.class, name = "raw_transactions"),
        @JsonSubTypes.Type(value = VerifiedFinancialContextPayload.class, name = "verified_financial_context")
})
public sealed interface SystemContext permits RawTransactionContext, VerifiedFinancialContextPayload {
}
