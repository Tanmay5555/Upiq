package com.upiq.transaction.categorization;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategorizationBatchResponse {
    int transactionsProcessed;
    int categorized;
    int remainingUncategorized;
}
