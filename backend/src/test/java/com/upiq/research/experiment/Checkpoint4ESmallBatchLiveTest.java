package com.upiq.research.experiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upiq.research.llm.GroundedFinancialPromptBuilder;
import com.upiq.research.llm.OllamaClient;
import com.upiq.research.llm.OllamaProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Opt-in live experiment; normal Maven test execution always skips this test. */
class Checkpoint4ESmallBatchLiveTest {

    @Test
    @EnabledIfSystemProperty(named = "upiq.liveExperiment", matches = "true")
    void runsSelectedCasesThroughAllThreeSystemsAndWritesResearchArtifact() throws Exception {
        Checkpoint4ECaseFixture.PreparedCases prepared = Checkpoint4ECaseFixture.prepare();
        assertEquals(List.of("Q001", "Q006", "Q011"), prepared.selectedIds());
        assertEquals(3, prepared.cases().size());
        assertTrue(prepared.cases().stream().allMatch(experimentCase ->
                experimentCase.benchmarkQuestion().getCalculationType().equals(
                        experimentCase.verifiedFinancialContext().getCalculationType())));

        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        OllamaProperties properties = new OllamaProperties();
        properties.setBaseUrl(System.getProperty("ollama.baseUrl", "http://localhost:11434"));
        properties.setModel(System.getProperty("ollama.model", "llama3.1:8b"));
        properties.setTemperature(Double.parseDouble(System.getProperty("ollama.temperature", "0.0")));
        properties.setTimeout(Duration.ofSeconds(Long.parseLong(System.getProperty("ollama.timeoutSeconds", "180"))));

        OllamaClient client = new OllamaClient(properties, RestClient.builder());
        GroundedFinancialPromptBuilder groundedBuilder = new GroundedFinancialPromptBuilder(mapper);
        OllamaExperimentGenerationBoundary boundary = new OllamaExperimentGenerationBoundary(client, properties,
                List.of(new SystemAPromptAdapter(), new SystemBPromptAdapter(mapper),
                        new SystemCPromptAdapter(groundedBuilder)));
        ExperimentRun run = new ExperimentRunner(boundary, properties).run(prepared.cases());

        Path outputPath = Path.of(System.getProperty("experiment.output",
                "../research-results/checkpoint4e-small-batch.json"));
        Path written = new ExperimentResultWriter(mapper).write(run, outputPath);
        assertEquals(3, run.numberOfCases());
        assertEquals(9, run.cases().stream().mapToInt(result -> result.systemResults().size()).sum());
        assertEquals(List.of(ExperimentSystemId.A, ExperimentSystemId.B, ExperimentSystemId.C), run.systemsExecuted());
        assertTrue(run.cases().stream().flatMap(result -> result.systemResults().stream())
                .allMatch(result -> result.model() == null || "llama3.1:8b".equals(result.model())));

        System.out.println("Checkpoint 4E runId=" + run.runId() + " artifact=" + written);
        for (ExperimentCaseResult caseResult : run.cases()) {
            System.out.println("CASE " + caseResult.caseId() + " question=" + caseResult.question());
            System.out.println("REFERENCE " + mapper.writeValueAsString(caseResult.benchmarkReference()));
            for (ExperimentSystemRunResult systemResult : caseResult.systemResults()) {
                System.out.println("SYSTEM " + systemResult.systemId() + " status="
                        + (systemResult.succeeded() ? "success" : "failure") + " latencyMs="
                        + systemResult.latencyMs() + " model=" + systemResult.model());
                System.out.println("ANSWER " + (systemResult.answer() == null
                        ? "<none>" : systemResult.answer().replaceAll("[\\r\\n]+", " ")));
                System.out.println("METADATA promptTokens=" + systemResult.promptTokenCount()
                        + " outputTokens=" + systemResult.outputTokenCount()
                        + " ollamaDurationNs=" + systemResult.ollamaDurationNs()
                        + " error=" + systemResult.error());
            }
        }
    }
}
