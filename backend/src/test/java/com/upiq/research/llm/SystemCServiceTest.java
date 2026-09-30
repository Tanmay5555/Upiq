package com.upiq.research.llm;

import com.upiq.research.context.FinancialContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SystemCServiceTest {
    private final OllamaClient client = mock(OllamaClient.class);
    private final OllamaProperties properties = new OllamaProperties();
    private final SystemCService service = new SystemCService(
            new GroundedFinancialPromptBuilder(new ObjectMapper()), client);

    @Test
    void convertsSuccessPreservesMetadataAndRecordsLatency() {
        properties.setModel("configured-model");
        when(client.properties()).thenReturn(properties);
        when(client.generate(anyString())).thenReturn(OllamaGenerateResponse.builder()
                .model("llama3.2:latest").response("Verified explanation")
                .promptEvalCount(20).evalCount(8).totalDurationNs(99L).done(true).build());

        SystemCResponse result = service.answer("Explain", FinancialContext.builder().verified(true).build());

        assertEquals("Verified explanation", result.getAnswer());
        assertEquals("llama3.2:latest", result.getModel());
        assertTrue(result.getLatencyMs() >= 0);
        assertEquals(20, result.getPromptEvalCount());
        assertEquals(8, result.getEvalCount());
        assertEquals(99L, result.getOllamaTotalDurationNs());
    }

    @Test
    void configuredModelIsUsedWhenOllamaOmitsModel() {
        properties.setModel("configured-model");
        when(client.properties()).thenReturn(properties);
        when(client.generate(anyString())).thenReturn(OllamaGenerateResponse.builder().response("Answer").build());
        assertEquals("configured-model", service.answer("Explain", FinancialContext.builder().verified(true).build()).getModel());
    }

    @Test
    void propagatesExplicitOllamaFailuresWithoutCreatingAnswer() {
        for (OllamaClientException.Kind kind : new OllamaClientException.Kind[]{
                OllamaClientException.Kind.UNAVAILABLE, OllamaClientException.Kind.HTTP_ERROR,
                OllamaClientException.Kind.MALFORMED_RESPONSE, OllamaClientException.Kind.EMPTY_RESPONSE}) {
            reset(client);
            when(client.generate(anyString())).thenThrow(new OllamaClientException(kind, "Ollama failure"));
            OllamaClientException error = assertThrows(OllamaClientException.class,
                    () -> service.answer("Explain", FinancialContext.builder().verified(true).build()));
            assertEquals(kind, error.getKind());
        }
    }
}
