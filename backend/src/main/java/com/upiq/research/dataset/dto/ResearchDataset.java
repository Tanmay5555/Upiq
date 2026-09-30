package com.upiq.research.dataset.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * EXPERIMENTAL: in-memory bundle of the classpath research dataset.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchDataset {

    @Builder.Default
    private List<ResearchProfileDefinition> profiles = new ArrayList<>();

    @Builder.Default
    private List<ResearchTransactionDefinition> transactions = new ArrayList<>();

    @Builder.Default
    private List<BenchmarkQuestionDefinition> questions = new ArrayList<>();
}
