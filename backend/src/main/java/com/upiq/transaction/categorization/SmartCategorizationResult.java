package com.upiq.transaction.categorization;

import java.util.List;

public record SmartCategorizationResult(
        String category,
        CategorizationConfidence confidence,
        List<String> matchedSignals) {
}
