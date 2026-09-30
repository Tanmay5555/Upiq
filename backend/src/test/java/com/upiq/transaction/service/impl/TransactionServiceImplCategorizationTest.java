package com.upiq.transaction.service.impl;

import com.upiq.category.service.CategoryService;
import com.upiq.transaction.categorization.CategorizationConfidence;
import com.upiq.transaction.categorization.SmartCategorizationResult;
import com.upiq.transaction.categorization.SmartCategorizationService;
import com.upiq.transaction.dto.CreateTransactionRequest;
import com.upiq.transaction.dto.TransactionResponse;
import com.upiq.transaction.model.Transaction;
import com.upiq.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplCategorizationTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private SmartCategorizationService categorizationService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void preservesExplicitManualCategoryOnCreate() {
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.addTransaction(request("Food", "Paid to local cafe"), 5L);

        assertEquals("Food", response.getCategory());
        verify(categorizationService, never()).categorize(any(), any(), any());
        verify(categoryService, never()).ensureCategoryExists(any(), any(), any());
    }

    @Test
    void automaticallyCategorizesOnlyFallbackCategoryOnCreate() {
        when(categorizationService.categorize("Paid to Spotify India", null, "expense"))
                .thenReturn(new SmartCategorizationResult("Entertainment", CategorizationConfidence.HIGH, List.of("SPOTIFY")));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.addTransaction(
                request("Uncategorized", "Paid to Spotify India"), 5L);

        assertEquals("Entertainment", response.getCategory());
        verify(categoryService).ensureCategoryExists("Entertainment", "expense", 5L);
    }

    @Test
    void preservesExplicitManualCategoryOnUpdate() {
        Transaction existing = Transaction.builder().id(20L).userId(5L).amount(90d)
                .type("expense").category("Uncategorized").description("Old description").build();
        when(repository.findById(20L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.updateTransaction(
                20L, request("Shopping", "Paid to Amazon"), 5L);

        assertEquals("Shopping", response.getCategory());
        verify(categorizationService, never()).categorize(any(), any(), any());
        verify(categoryService, never()).ensureCategoryExists(any(), any(), any());
        ArgumentCaptor<Transaction> savedCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).save(savedCaptor.capture());
        assertEquals("Shopping", savedCaptor.getValue().getCategory());
    }

    private CreateTransactionRequest request(String category, String description) {
        return new CreateTransactionRequest(100d, "expense", category, description, "UPI", null);
    }
}
