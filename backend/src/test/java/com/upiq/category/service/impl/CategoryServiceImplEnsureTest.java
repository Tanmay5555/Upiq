package com.upiq.category.service.impl;

import com.upiq.category.model.Category;
import com.upiq.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplEnsureTest {

    @Mock
    private CategoryRepository repository;

    @Test
    void createsMissingFoodCategoryForOwningUser() {
        when(repository.existsByUserIdAndNameIgnoreCase(10L, "Food")).thenReturn(false);
        CategoryServiceImpl service = new CategoryServiceImpl(repository);

        service.ensureCategoryExists("Food", "expense", 10L);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repository).save(captor.capture());
        assertEquals("Food", captor.getValue().getName());
        assertEquals("expense", captor.getValue().getType());
        assertEquals(10L, captor.getValue().getUserId());
        verify(repository).existsByUserIdAndNameIgnoreCase(10L, "Food");
    }

    @Test
    void createsMissingRentCategoryForOwningUser() {
        when(repository.existsByUserIdAndNameIgnoreCase(10L, "Rent")).thenReturn(false);
        CategoryServiceImpl service = new CategoryServiceImpl(repository);

        service.ensureCategoryExists("Rent", "expense", 10L);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repository).save(captor.capture());
        assertEquals("Rent", captor.getValue().getName());
        assertEquals(10L, captor.getValue().getUserId());
    }

    @Test
    void reusesExistingCategoryIgnoringCase() {
        when(repository.existsByUserIdAndNameIgnoreCase(10L, "Food")).thenReturn(true);
        CategoryServiceImpl service = new CategoryServiceImpl(repository);

        service.ensureCategoryExists("Food", "expense", 10L);

        verify(repository, never()).save(any(Category.class));
    }

    @Test
    void repeatedEnsureCallsDoNotCreateDuplicateCategoryRecords() {
        Set<String> existingNamesForUser = new HashSet<>();
        when(repository.existsByUserIdAndNameIgnoreCase(10L, "Food"))
                .thenAnswer(invocation -> existingNamesForUser.contains("10:Food"));
        doAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            existingNamesForUser.add(category.getUserId() + ":" + category.getName());
            return category;
        }).when(repository).save(any(Category.class));
        CategoryServiceImpl service = new CategoryServiceImpl(repository);

        service.ensureCategoryExists("Food", "expense", 10L);
        service.ensureCategoryExists("Food", "expense", 10L);

        verify(repository).save(any(Category.class));
    }

    @Test
    void categoryExistenceIsScopedToUser() {
        when(repository.existsByUserIdAndNameIgnoreCase(10L, "Food")).thenReturn(false);
        when(repository.existsByUserIdAndNameIgnoreCase(20L, "Food")).thenReturn(false);
        CategoryServiceImpl service = new CategoryServiceImpl(repository);

        service.ensureCategoryExists("Food", "expense", 10L);
        service.ensureCategoryExists("Food", "expense", 20L);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(repository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertEquals(Set.of(10L, 20L), Set.of(captor.getAllValues().get(0).getUserId(),
                captor.getAllValues().get(1).getUserId()));
    }

    @Test
    void doesNotCreateUncategorizedRecord() {
        CategoryServiceImpl service = new CategoryServiceImpl(repository);

        service.ensureCategoryExists("Uncategorized", "expense", 10L);

        verify(repository, never()).existsByUserIdAndNameIgnoreCase(any(), any());
        verify(repository, never()).save(any(Category.class));
    }
}
