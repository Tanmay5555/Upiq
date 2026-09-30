package com.upiq.financial.chat;

import com.upiq.research.context.FinancialContext;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FinancialChatResponse {
    String answer;
    String calculationType;
    FinancialContext financialContext;
    String model;
    Long latencyMs;
    boolean supported;
    boolean error;
}
