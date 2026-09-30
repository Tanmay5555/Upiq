package com.upiq.transaction.categorization;

import com.upiq.category.service.CategoryService;
import com.upiq.transaction.model.Transaction;
import com.upiq.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionCategorizationService {

    private final TransactionRepository transactionRepository;
    private final SmartCategorizationService categorizationService;
    private final CategoryService categoryService;

    @Transactional
    public CategorizationBatchResponse categorizeUncategorized(Long userId) {
        List<Transaction> uncategorized = transactionRepository.findUncategorizedByUserId(userId);
        List<Transaction> updated = new ArrayList<>();
        Set<String> ensuredCategories = new HashSet<>();

        for (Transaction transaction : uncategorized) {
            SmartCategorizationResult result = categorizationService.categorize(
                    transaction.getDescription(), null, transaction.getType());
            if (result.confidence() != CategorizationConfidence.LOW) {
                transaction.setCategory(result.category());
                updated.add(transaction);
                String categoryKey = result.category().toLowerCase(Locale.ROOT);
                if (!"uncategorized".equals(categoryKey) && ensuredCategories.add(categoryKey)) {
                    categoryService.ensureCategoryExists(result.category(), transaction.getType(), userId);
                }
            }
        }

        if (!updated.isEmpty()) {
            transactionRepository.saveAll(updated);
        }

        return CategorizationBatchResponse.builder()
                .transactionsProcessed(uncategorized.size())
                .categorized(updated.size())
                .remainingUncategorized(uncategorized.size() - updated.size())
                .build();
    }
}
