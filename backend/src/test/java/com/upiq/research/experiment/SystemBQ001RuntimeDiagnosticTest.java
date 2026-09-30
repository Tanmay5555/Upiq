package com.upiq.research.experiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upiq.research.llm.GroundedFinancialPromptBuilder;
import com.upiq.research.llm.OllamaClient;
import com.upiq.research.llm.OllamaGenerateRequest;
import com.upiq.research.llm.OllamaProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** One-request, Q001/System B diagnostic. Disabled in the normal Maven test suite. */
class SystemBQ001RuntimeDiagnosticTest {
    private static final Duration DIAGNOSTIC_TIMEOUT = Duration.ofSeconds(300);

    @Test
    @EnabledIfSystemProperty(named = "upiq.liveSystemBDiagnostic", matches = "true")
    void sendsOneUnmodifiedQ001SystemBRequest() throws Exception {
        Checkpoint4ECaseFixture.PreparedCases prepared = Checkpoint4ECaseFixture.prepare();
        ExperimentCase q001 = prepared.cases().stream()
                .filter(experimentCase -> "Q001".equals(experimentCase.caseId()))
                .findFirst().orElseThrow();
        assertEquals("How much did I spend on Food in August 2026?", q001.question());

        SystemInput input = SystemInput.forCase(ExperimentSystemId.B, q001);
        RawTransactionContext rawContext = assertInstanceOf(RawTransactionContext.class, input.context());
        assertEquals(58, rawContext.transactions().size());
        assertFalse(input.toString().contains("benchmarkReference"));
        assertFalse(input.toString().contains("verifiedFinancialContext"));

        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        SystemBPromptAdapter adapter = new SystemBPromptAdapter(mapper);
        String prompt = adapter.buildPrompt(input);
        String serializedContext = mapper.writeValueAsString(rawContext);
        assertTrue(prompt.contains("USER QUESTION\n" + q001.question()));
        assertTrue(prompt.contains("RAW TRANSACTION CONTEXT"));
        assertTrue(prompt.endsWith(serializedContext));
        assertFalse(prompt.contains("benchmarkReference"));
        assertFalse(prompt.contains("verifiedFinancialContext"));

        OllamaProperties properties = new OllamaProperties();
        properties.setBaseUrl("http://localhost:11434");
        properties.setModel("llama3.1:8b");
        properties.setTemperature(0.0);
        properties.setTimeout(DIAGNOSTIC_TIMEOUT);

        byte[] promptBytes = prompt.getBytes(StandardCharsets.UTF_8);
        byte[] contextBytes = serializedContext.getBytes(StandardCharsets.UTF_8);
        int prefixLength = prompt.length() - serializedContext.length();
        byte[] requestBytes = mapper.writeValueAsBytes(OllamaGenerateRequest.builder()
                .model(properties.getModel())
                .prompt(prompt)
                .stream(false)
                .options(Map.of("temperature", properties.getTemperature()))
                .build());

        System.out.println("DIAGNOSTIC case=Q001 system=B");
        System.out.println("CONFIG model=" + properties.getModel() + " temperature="
                + properties.getTemperature() + " endpoint=" + properties.getBaseUrl()
                + " timeoutSeconds=" + properties.getTimeout().toSeconds());
        System.out.println("INPUT profileRecords=1 transactionRecords=" + rawContext.transactions().size()
                + " verifiedContext=false reference=false");
        System.out.println("PROMPT characters=" + prompt.length() + " utf8Bytes=" + promptBytes.length
                + " approxTokens=" + (int) Math.ceil(prompt.length() / 4.0));
        System.out.println("PROMPT serializedContextCharacters=" + serializedContext.length()
                + " serializedContextBytes=" + contextBytes.length
                + " questionAndInstructionPrefixCharacters=" + prefixLength);
        System.out.println("REQUEST jsonBodyBytes=" + requestBytes.length);

        GroundedFinancialPromptBuilder groundedBuilder = new GroundedFinancialPromptBuilder(mapper);
        OllamaExperimentGenerationBoundary boundary = new OllamaExperimentGenerationBoundary(
                new OllamaClient(properties, RestClient.builder()), properties,
                List.of(new SystemAPromptAdapter(), adapter, new SystemCPromptAdapter(groundedBuilder)));
        long start = System.nanoTime();
        SystemResult result = boundary.generate(input);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("RESULT status=" + (result.succeeded() ? "success" : "failure")
                + " latencyMs=" + elapsedMs + " model=" + result.model()
                + " timeoutSeconds=" + DIAGNOSTIC_TIMEOUT.toSeconds());
        System.out.println("OLLAMA promptTokens=" + result.promptEvalCount()
                + " outputTokens=" + result.evalCount()
                + " durationNs=" + result.ollamaTotalDurationNs());
        if (result.succeeded()) {
            System.out.println("RAW ANSWER\n" + result.answer());
        } else {
            System.out.println("TYPED ERROR " + result.error());
        }
        assertEquals(ExperimentSystemId.B, result.systemId());
    }
}
