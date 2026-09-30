package com.upiq.research.llm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upiq.research.context.FinancialContext;
import org.springframework.stereotype.Component;

/** Builds a prompt whose financial facts are exactly the serialized verified context. */
@Component
public class GroundedFinancialPromptBuilder {

    private static final String INSTRUCTIONS = """
            Treat the serialized FinancialContext as the sole source of verified financial facts.
            Use only facts and values explicitly present in that context; never add or infer financial facts.
            Do not invent amounts, dates, counts, categories, transactions, or other financial details.
            Never assume or add a currency symbol, currency code, or currency based on locale or wording.
            If the context does not specify a currency, give the supplied amount without a currency marker and say the currency is unavailable when relevant.
            Do not claim to have inspected, reviewed, counted, analyzed, or calculated from raw transactions unless individual raw transactions are explicitly present in the context.
            Aggregate totals and counts do not imply that raw transaction records were supplied or inspected.
            Do not perform independent financial calculations or derive missing values.
            If a requested detail is not explicitly present, say it is unavailable from the supplied context instead of guessing.
            Clearly separate verified facts from explanation. Explanations may clarify supplied facts but must not introduce unsupported financial claims.
            Explain the result clearly and naturally; do not force a fixed response template.
            Do not claim access to banks, accounts, external financial systems, or live data.
            Do not fabricate sources.
            The deterministic financial engine is the source of numerical truth.
            """;

    private final ObjectMapper objectMapper;

    public GroundedFinancialPromptBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String build(String question, FinancialContext context) {
        if (question == null || question.isBlank()) throw new IllegalArgumentException("user question is required");
        if (context == null) throw new IllegalArgumentException("FinancialContext is required");
        try {
            String serializedContext = objectMapper.writeValueAsString(context);
            return "USER QUESTION\n" + question + "\n\nVERIFIED FINANCIAL FACTS\n" + serializedContext
                    + "\n\nINSTRUCTIONS\n" + INSTRUCTIONS;
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize FinancialContext", ex);
        }
    }
}
