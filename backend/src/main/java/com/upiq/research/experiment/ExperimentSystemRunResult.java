package com.upiq.research.experiment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** One case/system output together with the exact typed context supplied for reproducibility. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"caseId", "question", "systemId", "inputContext", "answer", "model", "latencyMs", "promptTokenCount", "outputTokenCount", "ollamaDurationNs", "error"})
public record ExperimentSystemRunResult(
        String caseId,
        String question,
        ExperimentSystemId systemId,
        SystemContext inputContext,
        String answer,
        String model,
        long latencyMs,
        Integer promptTokenCount,
        Integer outputTokenCount,
        Long ollamaDurationNs,
        ExperimentError error) {

    public static ExperimentSystemRunResult from(
            String caseId, String question, SystemInput input, SystemResult result) {
        return new ExperimentSystemRunResult(caseId, question, result.systemId(), input.context(),
                result.answer(), result.model(), result.latencyMs(), result.promptEvalCount(),
                result.evalCount(), result.ollamaTotalDurationNs(), result.error());
    }

    public boolean succeeded() {
        return error == null;
    }
}
