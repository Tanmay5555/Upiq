package com.upiq.pdf.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upiq.pdf.dto.ParsingResponse;
import com.upiq.pdf.dto.TransactionRequest;
import com.upiq.research.llm.OllamaClient;
import com.upiq.research.llm.OllamaGenerateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LlamaStatementExtractor {

    @Autowired(required = false)
    private OllamaClient ollamaClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ParsingResponse extractWithLlama(String text, String fileName) {
        if (ollamaClient == null || text == null || text.trim().isEmpty()) {
            return null;
        }

        try {
            log.info("Sending document text ({} chars) to Llama 3 AI for whole detail extraction...", text.length());

            String prompt = "You are an expert bank statement transaction parser. Extract all details from this bank statement text.\n"
                    + "Return ONLY a valid JSON object matching this structure:\n"
                    + "{\n"
                    + "  \"bankName\": \"HDFC Bank\",\n"
                    + "  \"accountNumber\": \"XX1234\",\n"
                    + "  \"statementPeriod\": \"Sep 2026\",\n"
                    + "  \"totalCredits\": 5000.00,\n"
                    + "  \"totalDebits\": 1200.00,\n"
                    + "  \"transactions\": [\n"
                    + "    {\n"
                    + "      \"amount\": 150.00,\n"
                    + "      \"type\": \"expense\",\n"
                    + "      \"category\": \"Food & Dining\",\n"
                    + "      \"description\": \"Starbucks Coffee\",\n"
                    + "      \"paymentMethod\": \"UPI\"\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}\n\n"
                    + "Statement Text:\n" + text.substring(0, Math.min(3000, text.length()));

            OllamaGenerateResponse res = ollamaClient.generate(prompt);
            if (res == null || res.getResponse() == null || res.getResponse().isBlank()) {
                return null;
            }

            String jsonStr = extractJsonFromText(res.getResponse());
            JsonNode rootNode = objectMapper.readTree(jsonStr);

            List<TransactionRequest> transactions = new ArrayList<>();
            JsonNode txArray = rootNode.path("transactions");
            if (txArray.isArray()) {
                for (JsonNode item : txArray) {
                    double amt = item.path("amount").asDouble(0.0);
                    if (amt > 0) {
                        transactions.add(TransactionRequest.builder()
                                .amount(amt)
                                .type(item.path("type").asText("expense"))
                                .category(item.path("category").asText("General"))
                                .description(item.path("description").asText("Statement Transaction"))
                                .paymentMethod(item.path("paymentMethod").asText("UPI"))
                                .date(LocalDateTime.now())
                                .build());
                    }
                }
            }

            if (transactions.isEmpty()) {
                return null;
            }

            log.info("Llama 3 successfully extracted {} transactions from {}", transactions.size(), fileName);

            return ParsingResponse.builder()
                    .bankName(rootNode.path("bankName").asText("Detected Bank"))
                    .accountNumber(rootNode.path("accountNumber").asText("XX9876"))
                    .statementPeriod(rootNode.path("statementPeriod").asText("Current Period"))
                    .totalCredits(rootNode.path("totalCredits").asDouble(0.0))
                    .totalDebits(rootNode.path("totalDebits").asDouble(0.0))
                    .totalTransactions(transactions.size())
                    .successfulParses(transactions.size())
                    .failedParses(0)
                    .transactions(transactions)
                    .errors(new ArrayList<>())
                    .message("Llama 3 AI extracted full statement details successfully")
                    .extractionEngine("Llama 3 (Ollama AI)")
                    .build();

        } catch (Exception e) {
            log.warn("Llama 3 extraction note: {} (falling back to deterministic engine)", e.getMessage());
            return null;
        }
    }

    private String extractJsonFromText(String response) {
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }
}
