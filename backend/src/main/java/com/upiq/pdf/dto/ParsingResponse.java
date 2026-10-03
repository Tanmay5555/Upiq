package com.upiq.pdf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsingResponse {
    private int totalTransactions;
    private int successfulParses;
    private int failedParses;
    private List<TransactionRequest> transactions;
    private List<String> errors;
    private String message;

    // Llama 3 Extracted Whole Statement Details
    private String bankName;
    private String accountNumber;
    private String statementPeriod;
    private Double totalCredits;
    private Double totalDebits;
    private Double openingBalance;
    private Double closingBalance;
    private String extractionEngine;
}
