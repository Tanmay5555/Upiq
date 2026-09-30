package com.upiq.research.experiment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upiq.research.calculation.FinancialCalculationService;
import com.upiq.research.context.FinancialContextBuilder;
import com.upiq.research.dataset.ResearchDatasetLoader;
import com.upiq.research.dataset.dto.BenchmarkQuestionDefinition;
import com.upiq.research.dataset.dto.ResearchDataset;
import com.upiq.research.dataset.dto.ResearchProfileDefinition;
import com.upiq.research.dataset.dto.ResearchTransactionDefinition;
import com.upiq.research.intent.FinancialIntentDispatcher;
import com.upiq.research.intent.FinancialIntentMapper;
import com.upiq.transaction.model.Transaction;
import com.upiq.transaction.repository.TransactionRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Builds the selected benchmark cases from the checked-in synthetic dataset for 4E tests. */
final class Checkpoint4ECaseFixture {
    static final String SELECTION_RESOURCE = "research/experiment-case-selection.json";

    private Checkpoint4ECaseFixture() { }

    static PreparedCases prepare() throws IOException {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        ResearchDataset dataset = new ResearchDatasetLoader(mapper).load();
        List<String> selectedIds;
        try (InputStream stream = Checkpoint4ECaseFixture.class.getClassLoader()
                .getResourceAsStream(SELECTION_RESOURCE)) {
            if (stream == null) throw new IllegalStateException("Missing " + SELECTION_RESOURCE);
            selectedIds = mapper.readValue(stream, new TypeReference<>() { });
        }

        List<ResearchProfileDefinition> orderedProfiles = dataset.getProfiles().stream()
                .sorted(Comparator.comparing(ResearchProfileDefinition::getId)).toList();
        Map<String, Long> userIds = java.util.stream.IntStream.range(0, orderedProfiles.size()).boxed()
                .collect(java.util.stream.Collectors.toMap(
                        index -> orderedProfiles.get(index).getId(), index -> index + 1L));
        List<Transaction> transactions = toTransactions(dataset, userIds);
        TransactionRepository repository = inMemoryRepository(transactions);
        FinancialIntentMapper intentMapper = new FinancialIntentMapper();
        FinancialIntentDispatcher dispatcher = new FinancialIntentDispatcher(
                intentMapper, new FinancialCalculationService(repository));
        FinancialContextBuilder contextBuilder = new FinancialContextBuilder();

        List<ExperimentCase> cases = selectedIds.stream().map(id -> {
            BenchmarkQuestionDefinition question = dataset.getQuestions().stream()
                    .filter(candidate -> id.equals(candidate.getId())).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown benchmark case ID: " + id));
            ResearchProfileDefinition profile = dataset.getProfiles().stream()
                    .filter(candidate -> question.getProfileId().equals(candidate.getId())).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Missing profile: " + question.getProfileId()));
            List<ResearchTransactionDefinition> rawTransactions = dataset.getTransactions().stream()
                    .filter(tx -> profile.getId().equals(tx.getProfileId()))
                    .sorted(Comparator.comparing(ResearchTransactionDefinition::getDate))
                    .toList();
            var dispatched = dispatcher.dispatch(question, userIds.get(profile.getId()));
            var verifiedContext = contextBuilder.build(dispatched);
            return new ExperimentCase(question.getId(), question.getText(), question, profile,
                    rawTransactions, verifiedContext);
        }).toList();
        return new PreparedCases(dataset, selectedIds, cases);
    }

    private static List<Transaction> toTransactions(ResearchDataset dataset, Map<String, Long> userIds) {
        return java.util.stream.IntStream.range(0, dataset.getTransactions().size())
                .mapToObj(index -> {
                    ResearchTransactionDefinition row = dataset.getTransactions().get(index);
                    return Transaction.builder().id(index + 1L).userId(userIds.get(row.getProfileId()))
                            .amount(row.getAmount()).type(row.getType()).category(row.getCategory())
                            .description(row.getDescription()).date(row.getDate())
                            .paymentMethod(row.getPaymentMethod()).build();
                }).toList();
    }

    private static TransactionRepository inMemoryRepository(List<Transaction> transactions) {
        TransactionRepository repository = mock(TransactionRepository.class);
        when(repository.findByUserIdAndTypeIgnoreCaseAndDateBetweenOrderByDateDesc(
                anyLong(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> select(transactions,
                        (Long) invocation.getArgument(0), (String) invocation.getArgument(1), null,
                        (LocalDateTime) invocation.getArgument(2), (LocalDateTime) invocation.getArgument(3),
                        Comparator.comparing(Transaction::getDate).reversed()));
        when(repository.findByUserIdAndTypeIgnoreCaseAndCategoryIgnoreCaseAndDateBetweenOrderByDateDesc(
                anyLong(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> select(transactions,
                        (Long) invocation.getArgument(0), (String) invocation.getArgument(1),
                        (String) invocation.getArgument(2), (LocalDateTime) invocation.getArgument(3),
                        (LocalDateTime) invocation.getArgument(4),
                        Comparator.comparing(Transaction::getDate).reversed()));
        when(repository.findByUserIdAndTypeIgnoreCaseAndDateBetweenOrderByAmountDesc(
                anyLong(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> select(transactions,
                        (Long) invocation.getArgument(0), (String) invocation.getArgument(1), null,
                        (LocalDateTime) invocation.getArgument(2), (LocalDateTime) invocation.getArgument(3),
                        Comparator.comparing(Transaction::getAmount).reversed()));
        return repository;
    }

    private static List<Transaction> select(List<Transaction> source, Long userId, String type, String category,
                                            LocalDateTime start, LocalDateTime end,
                                            Comparator<Transaction> order) {
        Predicate<Transaction> categoryFilter = category == null
                ? ignored -> true : tx -> tx.getCategory().equalsIgnoreCase(category);
        return source.stream().filter(tx -> userId.equals(tx.getUserId()))
                .filter(tx -> tx.getType().equalsIgnoreCase(type)).filter(categoryFilter)
                .filter(tx -> !tx.getDate().isBefore(start) && !tx.getDate().isAfter(end))
                .sorted(order).toList();
    }

    record PreparedCases(ResearchDataset dataset, List<String> selectedIds, List<ExperimentCase> cases) { }
}
