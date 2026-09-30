package com.upiq.financial.chat;

import com.upiq.research.context.FinancialContext;
import com.upiq.research.context.FinancialContextBuilder;
import com.upiq.research.context.FinancialFacts;
import com.upiq.research.intent.CalculationType;
import com.upiq.research.intent.DispatchedCalculation;
import com.upiq.research.intent.FinancialIntentDispatcher;
import com.upiq.research.intent.FinancialQueryIntent;
import com.upiq.research.llm.OllamaClientException;
import com.upiq.research.llm.SystemCResponse;
import com.upiq.research.llm.SystemCService;
import com.upiq.transaction.model.Transaction;
import com.upiq.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialChatServiceTest {

    @Mock private TransactionRepository transactions;
    @Spy private FinancialChatIntentResolver intentResolver = new FinancialChatIntentResolver();
    @Mock private FinancialIntentDispatcher dispatcher;
    @Mock private FinancialContextBuilder contextBuilder;
    @Mock private SystemCService systemC;
    @InjectMocks private FinancialChatService service;

    @Test
    void supportedQuestionUsesAuthenticatedIdDeterministicDispatchAndVerifiedContext() {
        Long userId = 72L;
        mockUserDates(userId);
        FinancialQueryIntent intent = FinancialQueryIntent.builder()
                .calculationType(CalculationType.TOTAL_EXPENSE).userId(userId).build();
        DispatchedCalculation dispatched = new DispatchedCalculation(intent, new BigDecimal("3620.00"));
        FinancialContext context = verifiedExpenseContext();
        when(dispatcher.dispatch(any(com.upiq.research.dataset.dto.BenchmarkQuestionDefinition.class), eq(userId)))
                .thenReturn(dispatched);
        when(contextBuilder.build(dispatched)).thenReturn(context);
        when(systemC.answer("How much did I spend this month?", context))
                .thenReturn(SystemCResponse.builder().answer("Verified expenses were 3620.")
                        .model("llama3.1:8b").latencyMs(45).build());

        FinancialChatResponse response = service.answer("How much did I spend this month?", userId);

        assertTrue(response.isSupported());
        assertFalse(response.isError());
        assertEquals("TOTAL_EXPENSE", response.getCalculationType());
        assertSame(context, response.getFinancialContext());
        assertEquals("llama3.1:8b", response.getModel());
        assertEquals(45L, response.getLatencyMs());
        verify(dispatcher).dispatch(any(com.upiq.research.dataset.dto.BenchmarkQuestionDefinition.class), eq(userId));
        verify(systemC).answer("How much did I spend this month?", context);
        assertEquals(new BigDecimal("3620.00"), context.getFacts().getTotalExpense());
        assertEquals(null, context.getFacts().getDescription());
        assertFalse(new FinancialContextBuilder().toJson(context).contains("\"transactions\""));
    }

    @Test
    void unsupportedQuestionStopsBeforeCalculationAndModel() {
        mockUserDates(72L);

        FinancialChatResponse response = service.answer("Should I buy a new phone?", 72L);

        assertFalse(response.isSupported());
        assertFalse(response.isError());
        assertTrue(response.getAnswer().contains("category spending"));
        verify(dispatcher, never()).dispatch(any(com.upiq.research.dataset.dto.BenchmarkQuestionDefinition.class), any());
        verify(systemC, never()).answer(any(), any());
    }

    @Test
    void ollamaFailureReturnsGracefulErrorAndRetainsVerifiedFacts() {
        Long userId = 72L;
        mockUserDates(userId);
        FinancialQueryIntent intent = FinancialQueryIntent.builder()
                .calculationType(CalculationType.TOTAL_EXPENSE).userId(userId).build();
        DispatchedCalculation dispatched = new DispatchedCalculation(intent, BigDecimal.ZERO);
        FinancialContext context = verifiedExpenseContext();
        when(dispatcher.dispatch(any(com.upiq.research.dataset.dto.BenchmarkQuestionDefinition.class), eq(userId)))
                .thenReturn(dispatched);
        when(contextBuilder.build(dispatched)).thenReturn(context);
        when(systemC.answer("How much did I spend?", context))
                .thenThrow(new OllamaClientException(OllamaClientException.Kind.UNAVAILABLE, "offline"));

        FinancialChatResponse response = service.answer("How much did I spend?", userId);

        assertTrue(response.isSupported());
        assertTrue(response.isError());
        assertSame(context, response.getFinancialContext());
        assertTrue(response.getAnswer().contains("AI service"));
    }

    @Test
    void noTransactionsProducesHelpfulResponseWithoutCallingTheModel() {
        when(transactions.findTopByUserIdOrderByDateDesc(72L)).thenReturn(Optional.empty());

        FinancialChatResponse response = service.answer("What was my total income?", 72L);

        assertFalse(response.isSupported());
        assertTrue(response.getAnswer().contains("No transaction data"));
        verify(systemC, never()).answer(any(), any());
    }

    private void mockUserDates(Long userId) {
        when(transactions.findTopByUserIdOrderByDateDesc(userId)).thenReturn(Optional.of(Transaction.builder()
                .userId(userId).date(LocalDateTime.of(2026, 8, 12, 10, 0)).build()));
        when(transactions.findTopByUserIdOrderByDateAsc(userId)).thenReturn(Optional.of(Transaction.builder()
                .userId(userId).date(LocalDateTime.of(2026, 1, 4, 10, 0)).build()));
    }

    private FinancialContext verifiedExpenseContext() {
        return FinancialContext.builder().contextVersion("1.0").source(FinancialContext.SOURCE).verified(true)
                .calculationType("TOTAL_EXPENSE")
                .facts(FinancialFacts.builder().totalExpense(new BigDecimal("3620.00")).build()).build();
    }
}
