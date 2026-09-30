package com.upiq.research.llm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Local Ollama HTTP settings. Model name is configuration, not compiled-in.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ollama")
public class OllamaProperties {

    /**
     * Ollama HTTP origin. Default is the local daemon; override with {@code OLLAMA_BASE_URL}.
     */
    private String baseUrl = "http://localhost:11434";

    /**
     * Model tag as known to Ollama (e.g. {@code llama3.2} or a pinned digest/tag).
     * Must be set in configuration; the application does not pull models.
     */
    private String model = "";

    /**
     * Sampling temperature. Research default is {@code 0.0}; this does not guarantee
     * bit-identical output across hardware or Ollama versions.
     */
    private double temperature = 0.0;

    /** Connect and read timeout for the Ollama HTTP call. */
    private Duration timeout = Duration.ofSeconds(60);
}
