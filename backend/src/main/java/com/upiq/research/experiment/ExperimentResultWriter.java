package com.upiq.research.experiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Writes a run to an explicitly selected JSON path without replacing an existing artifact. */
@Component
public class ExperimentResultWriter {
    private final ObjectMapper objectMapper;

    public ExperimentResultWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Path write(ExperimentRun run, Path outputPath) throws IOException {
        if (run == null) throw new IllegalArgumentException("experiment run is required");
        if (outputPath == null) throw new IllegalArgumentException("output path is required");
        Path absolutePath = outputPath.toAbsolutePath().normalize();
        Path parent = absolutePath.getParent();
        if (parent != null) Files.createDirectories(parent);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(run) + System.lineSeparator();
        Files.writeString(absolutePath, json, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        return absolutePath;
    }
}
