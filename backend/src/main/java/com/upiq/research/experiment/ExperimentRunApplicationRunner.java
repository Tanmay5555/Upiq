package com.upiq.research.experiment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Explicit opt-in launcher for prepared cases; inactive on all normal application starts. */
@Slf4j
@Component
@ConditionalOnProperty(name = "upiq.runExperiment", havingValue = "true")
public class ExperimentRunApplicationRunner implements ApplicationRunner {
    private final ExperimentRunner experimentRunner;
    private final ExperimentResultWriter resultWriter;
    private final ObjectMapper objectMapper;
    private final String casesPath;
    private final String resultsPath;

    public ExperimentRunApplicationRunner(
            ExperimentRunner experimentRunner,
            ExperimentResultWriter resultWriter,
            ObjectMapper objectMapper,
            @Value("${upiq.experiment.cases-path:}") String casesPath,
            @Value("${upiq.experiment.results-path:}") String resultsPath) {
        this.experimentRunner = experimentRunner;
        this.resultWriter = resultWriter;
        this.objectMapper = objectMapper;
        this.casesPath = casesPath;
        this.resultsPath = resultsPath;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (casesPath == null || casesPath.isBlank() || resultsPath == null || resultsPath.isBlank()) {
            throw new IllegalStateException(
                    "Set upiq.experiment.cases-path and upiq.experiment.results-path for an explicit experiment run");
        }
        Path input = Path.of(casesPath).toAbsolutePath().normalize();
        if (!Files.isRegularFile(input)) throw new IllegalArgumentException("Experiment case file does not exist: " + input);
        List<ExperimentCase> cases = objectMapper.readValue(input.toFile(), new TypeReference<>() { });
        ExperimentRun run = experimentRunner.run(cases);
        Path output = resultWriter.write(run, Path.of(resultsPath));
        log.info("Experiment artifact written runId={} path={}", run.runId(), output);
    }
}
