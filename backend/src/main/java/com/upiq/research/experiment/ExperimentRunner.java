package com.upiq.research.experiment;

import com.upiq.research.llm.OllamaProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Executes prepared cases in stable case and A/B/C order; it performs no scoring or evaluation. */
@Slf4j
@Service
public class ExperimentRunner {
    private static final List<ExperimentSystemId> SYSTEM_ORDER =
            List.of(ExperimentSystemId.A, ExperimentSystemId.B, ExperimentSystemId.C);

    private final ExperimentGenerationBoundary generationBoundary;
    private final OllamaProperties properties;

    public ExperimentRunner(ExperimentGenerationBoundary generationBoundary, OllamaProperties properties) {
        this.generationBoundary = generationBoundary;
        this.properties = properties;
    }

    public ExperimentRun run(List<ExperimentCase> experimentCases) {
        if (experimentCases == null || experimentCases.isEmpty()) {
            throw new IllegalArgumentException("at least one experiment case is required");
        }
        if (properties.getModel() == null || properties.getModel().isBlank()) {
            throw new IllegalStateException("ollama.model must be configured before an experiment run");
        }
        List<ExperimentCase> orderedCases = experimentCases.stream()
                .sorted(Comparator.comparing(ExperimentCase::caseId))
                .toList();
        Set<String> caseIds = new HashSet<>();
        for (ExperimentCase experimentCase : orderedCases) {
            if (!caseIds.add(experimentCase.caseId())) {
                throw new IllegalArgumentException("duplicate experiment case id: " + experimentCase.caseId());
            }
        }

        String runId = UUID.randomUUID().toString();
        Instant timestamp = Instant.now();
        log.info("Experiment run started runId={} cases={} model={}", runId, orderedCases.size(), properties.getModel());
        List<ExperimentCaseResult> caseResults = new ArrayList<>();
        for (ExperimentCase experimentCase : orderedCases) {
            List<ExperimentSystemRunResult> systemResults = new ArrayList<>();
            for (ExperimentSystemId systemId : SYSTEM_ORDER) {
                SystemInput input = SystemInput.forCase(systemId, experimentCase);
                SystemResult result = generateSafely(input);
                systemResults.add(ExperimentSystemRunResult.from(
                        experimentCase.caseId(), experimentCase.question(), input, result));
                log.info("Experiment result runId={} caseId={} system={} status={} latencyMs={}",
                        runId, experimentCase.caseId(), systemId, result.succeeded() ? "success" : "failure",
                        result.latencyMs());
            }
            caseResults.add(new ExperimentCaseResult(
                    experimentCase.caseId(), experimentCase.question(), experimentCase.benchmarkQuestion(),
                    experimentCase.verifiedFinancialContext(), systemResults));
        }
        ExperimentRun run = new ExperimentRun(runId, timestamp, properties.getModel(), properties.getTemperature(),
                properties.getBaseUrl(), properties.getTimeout(), null, orderedCases.size(), SYSTEM_ORDER, caseResults);
        log.info("Experiment run completed runId={} cases={} systems={}", runId, run.numberOfCases(), SYSTEM_ORDER);
        return run;
    }

    private SystemResult generateSafely(SystemInput input) {
        try {
            SystemResult result = generationBoundary.generate(input);
            if (result == null || result.systemId() != input.systemId()) {
                return SystemResult.failure(input.systemId(), properties.getModel(), 0,
                        new ExperimentError(ExperimentErrorCode.GENERATION_ERROR,
                                "Generation boundary returned no result or a mismatched system identifier"));
            }
            return result;
        } catch (RuntimeException ex) {
            return SystemResult.failure(input.systemId(), properties.getModel(), 0,
                    new ExperimentError(ExperimentErrorCode.GENERATION_ERROR,
                            "Generation boundary failed unexpectedly"));
        }
    }
}
