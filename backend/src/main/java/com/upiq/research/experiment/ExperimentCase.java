package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.upiq.research.context.FinancialContext;
import com.upiq.research.dataset.dto.BenchmarkQuestionDefinition;
import com.upiq.research.dataset.dto.ResearchProfileDefinition;
import com.upiq.research.dataset.dto.ResearchTransactionDefinition;

import java.util.List;
import java.util.Objects;

/** Immutable case data shared by A/B/C; benchmark definitions and research rows are reused. */
@JsonPropertyOrder({"caseId", "question", "benchmarkQuestion", "profile", "relevantTransactions", "verifiedFinancialContext"})
public record ExperimentCase(
        String caseId,
        String question,
        BenchmarkQuestionDefinition benchmarkQuestion,
        ResearchProfileDefinition profile,
        List<ResearchTransactionDefinition> relevantTransactions,
        FinancialContext verifiedFinancialContext) {

    public ExperimentCase {
        if (caseId == null || caseId.isBlank()) throw new IllegalArgumentException("caseId is required");
        if (question == null || question.isBlank()) throw new IllegalArgumentException("question is required");
        Objects.requireNonNull(benchmarkQuestion, "benchmarkQuestion is required");
        Objects.requireNonNull(profile, "profile is required");
        Objects.requireNonNull(relevantTransactions, "relevantTransactions is required");
        Objects.requireNonNull(verifiedFinancialContext, "verifiedFinancialContext is required");
        if (!caseId.equals(benchmarkQuestion.getId())) {
            throw new IllegalArgumentException("caseId must match benchmark question id");
        }
        if (!question.equals(benchmarkQuestion.getText())) {
            throw new IllegalArgumentException("question must match benchmark question text");
        }
        if (!profile.getId().equals(benchmarkQuestion.getProfileId())) {
            throw new IllegalArgumentException("profile must match benchmark question profile_id");
        }
        relevantTransactions = List.copyOf(relevantTransactions);
    }
}
