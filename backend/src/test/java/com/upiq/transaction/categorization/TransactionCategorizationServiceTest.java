package com.upiq.transaction.categorization;

import com.upiq.category.service.CategoryService;
import com.upiq.transaction.model.Transaction;
import com.upiq.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionCategorizationServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private SmartCategorizationService categorizationService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionCategorizationService service;

    @Test
    void updatesOnlyConfidentUncategorizedTransactionsForRequestedUser() {
        Long userId = 10L;
        Transaction food = Transaction.builder().userId(userId).type("expense").description("Paid to SWIGGY").category("Uncategorized").build();
        Transaction unknown = Transaction.builder().userId(userId).type("expense").description("Paid to XYZ").category("Uncategorized").build();
        Transaction music = Transaction.builder().userId(userId).type("expense").description("Spotify India").category("").build();
        when(repository.findUncategorizedByUserId(userId)).thenReturn(List.of(food, unknown, music));
        when(categorizationService.categorize("Paid to SWIGGY", null, "expense"))
                .thenReturn(new SmartCategorizationResult("Food", CategorizationConfidence.HIGH, List.of("SWIGGY")));
        when(categorizationService.categorize("Paid to XYZ", null, "expense"))
                .thenReturn(new SmartCategorizationResult("Uncategorized", CategorizationConfidence.LOW, List.of()));
        when(categorizationService.categorize("Spotify India", null, "expense"))
                .thenReturn(new SmartCategorizationResult("Entertainment", CategorizationConfidence.HIGH, List.of("SPOTIFY")));

        CategorizationBatchResponse response = service.categorizeUncategorized(userId);

        assertEquals(3, response.getTransactionsProcessed());
        assertEquals(2, response.getCategorized());
        assertEquals(1, response.getRemainingUncategorized());
        assertEquals("Food", food.getCategory());
        assertEquals("Uncategorized", unknown.getCategory());
        assertEquals("Entertainment", music.getCategory());

        ArgumentCaptor<List<Transaction>> updatedCaptor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(updatedCaptor.capture());
        assertEquals(List.of(food, music), updatedCaptor.getValue());
        verify(repository).findUncategorizedByUserId(userId);
        verify(categoryService).ensureCategoryExists("Food", "expense", userId);
        verify(categoryService).ensureCategoryExists("Entertainment", "expense", userId);
    }
}
