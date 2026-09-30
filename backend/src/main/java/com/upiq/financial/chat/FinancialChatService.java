package com.upiq.financial.chat;

import com.upiq.research.context.FinancialContext;
import com.upiq.research.context.FinancialContextBuilder;
import com.upiq.research.intent.DispatchedCalculation;
import com.upiq.research.intent.FinancialIntentDispatcher;
import com.upiq.research.llm.SystemCResponse;
import com.upiq.research.llm.SystemCService;
import com.upiq.transaction.model.Transaction;
import com.upiq.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinancialChatService {

    static final String UNSUPPORTED_ANSWER = "I can currently answer questions about your income, expenses, "
            + "category spending, period comparisons, spending trends, net balance, and largest transactions.";
    static final String AI_UNAVAILABLE_ANSWER = "Your verified calculation is ready, but the AI service is "
            + "currently unavailable. Please try again shortly.";

    private final TransactionRepository transactionRepository;
    private final FinancialChatIntentResolver intentResolver;
    private final FinancialIntentDispatcher intentDispatcher;
    private final FinancialContextBuilder contextBuilder;
    private final SystemCService systemCService;

    public FinancialChatResponse answer(String question, Long authenticatedUserId) {
        if (authenticatedUserId == null || question == null || question.isBlank()) {
            return unsupported();
        }

        Optional<Transaction> latest = transactionRepository.findTopByUserIdOrderByDateDesc(authenticatedUserId);
        if (latest.isEmpty()) {
            return FinancialChatResponse.builder()
                    .answer("No transaction data is available yet. Add transactions to ask a financial question.")
                    .supported(false)
                    .error(false)
                    .build();
        }
        YearMonth latestMonth = YearMonth.from(latest.get().getDate());
        Optional<Transaction> earliest = transactionRepository.findTopByUserIdOrderByDateAsc(authenticatedUserId);
        var benchmarkQuestion = intentResolver.resolve(question, latestMonth,
                earliest.map(Transaction::getDate).map(java.time.LocalDateTime::toLocalDate).orElse(null));
        if (benchmarkQuestion.isEmpty()) return unsupported();

        DispatchedCalculation calculation;
        FinancialContext context;
        try {
            calculation = intentDispatcher.dispatch(benchmarkQuestion.get(), authenticatedUserId);
            context = contextBuilder.build(calculation);
        } catch (RuntimeException exception) {
            return FinancialChatResponse.builder()
                    .answer("I couldn't complete the verified calculation right now. Please try again shortly.")
                    .calculationType(benchmarkQuestion.get().getCalculationType())
                    .supported(true)
                    .error(true)
                    .build();
        }

        try {
            SystemCResponse generated = systemCService.answer(question, context);
            return FinancialChatResponse.builder()
                    .answer(generated.getAnswer())
                    .calculationType(calculation.getIntent().getCalculationType().name())
                    .financialContext(context)
                    .model(generated.getModel())
                    .latencyMs(generated.getLatencyMs())
                    .supported(true)
                    .error(false)
                    .build();
        } catch (RuntimeException exception) {
            return FinancialChatResponse.builder()
                    .answer(AI_UNAVAILABLE_ANSWER)
                    .calculationType(calculation.getIntent().getCalculationType().name())
                    .financialContext(context)
                    .supported(true)
                    .error(true)
                    .build();
        }
    }

    private static FinancialChatResponse unsupported() {
        return FinancialChatResponse.builder()
                .answer(UNSUPPORTED_ANSWER)
                .supported(false)
                .error(false)
                .build();
    }
}
