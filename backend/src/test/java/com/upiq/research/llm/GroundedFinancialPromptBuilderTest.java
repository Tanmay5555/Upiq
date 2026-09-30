package com.upiq.research.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upiq.research.context.FinancialContext;
import com.upiq.research.context.FinancialFacts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GroundedFinancialPromptBuilderTest {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final GroundedFinancialPromptBuilder builder = new GroundedFinancialPromptBuilder(mapper);

    @Test
    void includesQuestionExactSerializedContextAndGroundingInstructions() throws Exception {
        FinancialContext context = FinancialContext.builder().contextVersion("1.0").source("engine")
                .verified(true).calculationType("TOTAL_EXPENSE")
                .facts(FinancialFacts.builder().totalExpense(new BigDecimal("42.50")).build()).build();
        String serialized = mapper.writeValueAsString(context);

        String prompt = builder.build("How much did I spend?", context);

        assertTrue(prompt.contains("USER QUESTION\nHow much did I spend?"));
        assertTrue(prompt.contains("VERIFIED FINANCIAL FACTS\n" + serialized));
        assertTrue(prompt.contains("INSTRUCTIONS\n"));
        assertTrue(prompt.contains("Treat the serialized FinancialContext as the sole source of verified financial facts."));
        assertTrue(prompt.contains("Use only facts and values explicitly present in that context"));
        assertTrue(prompt.contains("never add or infer financial facts."));
        assertTrue(prompt.contains("Never assume or add a currency symbol, currency code, or currency"));
        assertTrue(prompt.contains("If the context does not specify a currency"));
        assertTrue(prompt.contains("Do not claim to have inspected, reviewed, counted, analyzed, or calculated from raw transactions"));
        assertTrue(prompt.contains("unless individual raw transactions are explicitly present in the context."));
        assertTrue(prompt.contains("Aggregate totals and counts do not imply that raw transaction records were supplied or inspected."));
        assertTrue(prompt.contains("Do not perform independent financial calculations or derive missing values."));
        assertTrue(prompt.contains("say it is unavailable from the supplied context instead of guessing."));
        assertTrue(prompt.contains("Clearly separate verified facts from explanation."));
        assertTrue(prompt.contains("must not introduce unsupported financial claims."));
        assertTrue(prompt.contains("do not force a fixed response template."));
        assertTrue(prompt.contains("Do not claim access to banks, accounts, external financial systems, or live data."));
        assertTrue(prompt.contains("Do not fabricate sources."));
    }
}
