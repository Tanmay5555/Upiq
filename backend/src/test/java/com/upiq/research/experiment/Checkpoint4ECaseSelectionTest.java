package com.upiq.research.experiment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Checkpoint4ECaseSelectionTest {
    @Test
    void selectedCasesComeFromDatasetAndProduceVerifiedContextsThroughCalculationPipeline() throws Exception {
        Checkpoint4ECaseFixture.PreparedCases prepared = Checkpoint4ECaseFixture.prepare();

        assertEquals(28, prepared.dataset().getQuestions().size());
        assertEquals(java.util.List.of("Q001", "Q006", "Q011"), prepared.selectedIds());
        assertEquals(java.util.List.of("Q001", "Q006", "Q011"),
                prepared.cases().stream().map(ExperimentCase::caseId).toList());
        assertEquals(java.util.List.of("CATEGORY_EXPENSE_TOTAL", "PERIOD_EXPENSE_COMPARE", "LARGEST_TRANSACTION"),
                prepared.cases().stream().map(experimentCase ->
                        experimentCase.verifiedFinancialContext().getCalculationType()).toList());
        assertTrue(prepared.cases().stream().allMatch(experimentCase ->
                experimentCase.verifiedFinancialContext().isVerified()
                        && !experimentCase.relevantTransactions().isEmpty()));
    }
}
