package com.upiq.research.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upiq.research.context.FinancialContext;
import com.upiq.research.context.FinancialFacts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Opt-in live verification. Normal Maven runs skip this test; invoke with
 * -Dupiq.liveOllama=true after Ollama and the configured model are available.
 */
class SystemCLiveOllamaVerificationTest {

    private static final String BASE_URL = "http://localhost:11434";
    private static final String MODEL = "llama3.1:8b";
    private static final String QUESTION = "What was my total food expense?";
    private static final Pattern CURRENCY_MARKER = Pattern.compile(
            "(?i)(?<![A-Za-z])(?:USD|INR|EUR|GBP|JPY|CAD|AUD)(?![A-Za-z])|[$₹€£¥]");
    private static final Pattern UNSUPPORTED_TRANSACTION_CLAIM = Pattern.compile(
            "(?is)\\b(?:inspected|reviewed|analyzed|analysed|examined|counted|calculated|computed|derived|summed)\\b"
                    + ".{0,60}\\b(?:raw\\s+)?transactions?\\b"
                    + "|\\b(?:from|based on|after examining|after reviewing)\\b.{0,40}\\b(?:raw\\s+)?transactions?\\b");

    @Test
    @EnabledIfSystemProperty(named = "upiq.liveOllama", matches = "true")
    void sendsVerifiedFinancialContextToRealOllamaAndPrintsResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        FinancialContext context = FinancialContext.builder()
                .contextVersion(FinancialContext.CONTEXT_VERSION)
                .source(FinancialContext.SOURCE)
                .verified(true)
                .calculationType("CATEGORY_EXPENSE_TOTAL")
                .transactionType("expense")
                .facts(FinancialFacts.builder()
                        .category("Food")
                        .transactionType("expense")
                        .totalExpense(new BigDecimal("3620.00"))
                        .transactionCount(6L)
                        .percentageOfTotalExpense(new BigDecimal("27.50"))
                        .build())
                .build();

        OllamaProperties properties = new OllamaProperties();
        properties.setBaseUrl(BASE_URL);
        properties.setModel(MODEL);
        properties.setTemperature(0.0);
        // Allow first-time local model initialization without changing application defaults.
        properties.setTimeout(Duration.ofMinutes(5));

        CapturingPromptBuilder promptBuilder = new CapturingPromptBuilder(mapper);
        OllamaClient client = new OllamaClient(properties, RestClient.builder());
        SystemCResponse response = new SystemCService(promptBuilder, client).answer(QUESTION, context);

        String exactSerializedContext = mapper.writeValueAsString(context);
        assertTrue(promptBuilder.prompt.contains("USER QUESTION\n" + QUESTION));
        assertTrue(promptBuilder.prompt.contains("VERIFIED FINANCIAL FACTS\n" + exactSerializedContext),
                "the prompt sent by SystemCService must contain the exact supplied FinancialContext");
        assertTrue(promptBuilder.prompt.contains("\"total_expense\":3620.00"),
                "the prompt must pass the verified total rather than raw transactions");
        assertEquals(MODEL, response.getModel());
        assertNotNull(response.getAnswer());
        assertFalse(response.getAnswer().isBlank());
        assertTrue(response.getAnswer().replace(",", "").contains("3620"),
                "the response should state the supplied verified amount; raw answer: " + response.getAnswer());
        assertFalse(CURRENCY_MARKER.matcher(response.getAnswer()).find(),
                "the context has no currency, so the answer must not assign one; raw answer: " + response.getAnswer());
        assertFalse(UNSUPPORTED_TRANSACTION_CLAIM.matcher(response.getAnswer()).find(),
                "no raw transactions were supplied, so the answer must not claim transaction-level work; raw answer: "
                        + response.getAnswer());

        System.out.println("LIVE SYSTEM C QUESTION: " + QUESTION);
        System.out.println("VERIFIED CONTEXT SENT: " + exactSerializedContext);
        System.out.println("RAW MODEL RESPONSE: " + response.getAnswer());
        System.out.println("PARSED SystemCResponse: " + mapper.writeValueAsString(response));
    }

    private static final class CapturingPromptBuilder extends GroundedFinancialPromptBuilder {
        private String prompt;

        private CapturingPromptBuilder(ObjectMapper mapper) {
            super(mapper);
        }

        @Override
        public String build(String question, FinancialContext context) {
            prompt = super.build(question, context);
            return prompt;
        }
    }
}
