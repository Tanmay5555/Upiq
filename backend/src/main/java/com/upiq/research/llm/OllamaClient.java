package com.upiq.research.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Local Ollama HTTP client ({@code POST /api/generate}, non-streaming).
 * Does not download models and does not call cloud LLM APIs.
 */
@Slf4j
@Component
public class OllamaClient {

    static final String GENERATE_PATH = "/api/generate";

    private final OllamaProperties properties;
    private final RestClient restClient;

    public OllamaClient(OllamaProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        Duration timeout = properties.getTimeout() != null ? properties.getTimeout() : Duration.ofSeconds(60);
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);
        this.restClient = restClientBuilder
                .baseUrl(trimTrailingSlash(properties.getBaseUrl()))
                .requestFactory(requestFactory)
                .build();
    }

    public OllamaGenerateResponse generate(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new OllamaClientException(
                    OllamaClientException.Kind.INVALID_CONFIGURATION, "prompt is required");
        }
        String model = properties.getModel();
        if (model == null || model.isBlank()) {
            throw new OllamaClientException(
                    OllamaClientException.Kind.INVALID_CONFIGURATION,
                    "ollama.model is not configured");
        }

        OllamaGenerateRequest request = OllamaGenerateRequest.builder()
                .model(model)
                .prompt(prompt)
                .stream(false)
                .options(optionsMap(properties.getTemperature()))
                .build();

        log.debug("Calling Ollama generate model={} temperature={} timeout={}",
                model, properties.getTemperature(), properties.getTimeout());

        OllamaGenerateResponse body;
        try {
            body = restClient.post()
                    .uri(GENERATE_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(OllamaGenerateResponse.class);
        } catch (RestClientResponseException ex) {
            throw new OllamaClientException(
                    OllamaClientException.Kind.HTTP_ERROR,
                    "Ollama HTTP " + ex.getStatusCode().value() + " from " + GENERATE_PATH,
                    ex);
        } catch (ResourceAccessException ex) {
            throw mapTransportFailure(ex);
        } catch (RestClientException ex) {
            if (hasCauseNamed(ex, "HttpMessageNotReadableException")) {
                throw new OllamaClientException(
                        OllamaClientException.Kind.MALFORMED_RESPONSE,
                        "Ollama returned a malformed JSON response",
                        ex);
            }
            throw new OllamaClientException(
                    OllamaClientException.Kind.UNAVAILABLE,
                    "Ollama request failed: " + ex.getMessage(),
                    ex);
        }

        if (body == null) {
            throw new OllamaClientException(
                    OllamaClientException.Kind.MALFORMED_RESPONSE,
                    "Ollama returned an empty HTTP body");
        }
        if (body.getResponse() == null || body.getResponse().isBlank()) {
            throw new OllamaClientException(
                    OllamaClientException.Kind.EMPTY_RESPONSE,
                    "Ollama returned an empty model response");
        }
        return body;
    }

    OllamaProperties properties() {
        return properties;
    }

    private static Map<String, Object> optionsMap(double temperature) {
        Map<String, Object> options = new LinkedHashMap<>();
        options.put("temperature", temperature);
        return options;
    }

    private static OllamaClientException mapTransportFailure(ResourceAccessException ex) {
        if (isTimeout(ex)) {
            return new OllamaClientException(
                    OllamaClientException.Kind.TIMEOUT,
                    "Ollama request timed out",
                    ex);
        }
        return new OllamaClientException(
                OllamaClientException.Kind.UNAVAILABLE,
                "Ollama is unavailable at the configured base-url",
                ex);
    }

    private static boolean isTimeout(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof SocketTimeoutException) {
                return true;
            }
            String name = current.getClass().getName();
            if (name.contains("Timeout") || name.contains("timed out")) {
                return true;
            }
            if (current.getMessage() != null && current.getMessage().toLowerCase().contains("timed out")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static boolean hasCauseNamed(Throwable ex, String simpleClassName) {
        Throwable current = ex;
        while (current != null) {
            if (current.getClass().getSimpleName().equals(simpleClassName)) return true;
            current = current.getCause();
        }
        return false;
    }

    private static String trimTrailingSlash(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "http://localhost:11434";
        }
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
