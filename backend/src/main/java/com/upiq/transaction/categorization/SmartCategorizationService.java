package com.upiq.transaction.categorization;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class SmartCategorizationService {

    private static final String UNCATEGORIZED = "Uncategorized";
    private static final List<CategoryRule> RULES = List.of(
            rule("Food", List.of("SWIGGY", "ZOMATO", "DOMINOS", "MCDONALDS", "KFC", "BURGER KING"),
                    List.of("RESTAURANT", "CAFE", "FOOD")),
            rule("Fuel", List.of("HPCL", "BPCL", "IOCL", "INDIAN OIL", "SHELL"), List.of("PETROL", "FUEL")),
            rule("Transport", List.of("UBER", "OLA", "RAPIDO", "IRCTC"), List.of("METRO", "TOLL PLAZA", "TRANSPORT")),
            rule("Shopping", List.of("AMAZON", "FLIPKART", "MYNTRA", "AJIO"), List.of("SHOPPING")),
            rule("Bills & Utilities", List.of("AIRTEL", "JIO", "BESCOM", "DISCOM"),
                    List.of("ELECTRICITY", "WATER BILL", "GAS BILL", "INTERNET", "BROADBAND")),
            rule("Health", List.of("APOLLO", "PHARMACY", "HOSPITAL", "CLINIC"), List.of("MEDICAL", "HEALTH", "PHARMA")),
            rule("Entertainment", List.of("NETFLIX", "SPOTIFY", "HOTSTAR", "PRIME VIDEO"), List.of("CINEMA", "MOVIE")),
            rule("Groceries", List.of("DMART", "BIGBASKET", "BLINKIT", "ZEPTO"),
                    List.of("RELIANCE FRESH", "GROCERY", "KIRANA")),
            rule("Education", List.of("UDEMY", "COURSERA"), List.of("COLLEGE", "UNIVERSITY", "SCHOOL", "COURSE")),
            rule("Rent", List.of("PG ACCOMMODATION", "PAYING GUEST", "PG", "RENTAL", "RENT",
                            "HOUSE RENT", "ROOM RENT", "HOTEL", "HOTELS", "HOTEL BOOKING", "HOTEL STAY",
                            "LODGE", "GUEST HOUSE", "GUESTHOUSE"), List.of()),
            rule("Insurance", List.of("INSURANCE", "LIC"), List.of()),
            rule("Cash Withdrawal", List.of("CASH WITHDRAWAL", "ATM"), List.of())
    );

    public SmartCategorizationResult categorize(String description, String merchant, String transactionType) {
        String normalized = normalizeForMatching(String.join(" ", safe(description), safe(merchant)));
        if ("income".equalsIgnoreCase(transactionType)) {
            List<String> salaryMatches = matchingSignals(normalized, List.of("SALARY", "PAYROLL"));
            if (!salaryMatches.isEmpty()) {
                return new SmartCategorizationResult("Salary", CategorizationConfidence.HIGH, salaryMatches);
            }
            List<String> investmentMatches = matchingSignals(normalized,
                    List.of("DIVIDEND", "MUTUAL FUND REDEMPTION", "INTEREST CREDIT"));
            if (!investmentMatches.isEmpty()) {
                return new SmartCategorizationResult("Investment", CategorizationConfidence.MEDIUM, investmentMatches);
            }
            return new SmartCategorizationResult(UNCATEGORIZED, CategorizationConfidence.LOW, List.of());
        }
        for (CategoryRule rule : RULES) {
            if ("Rent".equals(rule.category()) && !"expense".equalsIgnoreCase(transactionType)) {
                continue;
            }
            List<String> highMatches = matchingSignals(normalized, rule.highConfidenceSignals());
            if (!highMatches.isEmpty()) {
                return new SmartCategorizationResult(rule.category(), CategorizationConfidence.HIGH, highMatches);
            }
            List<String> mediumMatches = matchingSignals(normalized, rule.mediumConfidenceSignals());
            if (!mediumMatches.isEmpty()) {
                return new SmartCategorizationResult(rule.category(), CategorizationConfidence.MEDIUM, mediumMatches);
            }
        }
        return new SmartCategorizationResult(UNCATEGORIZED, CategorizationConfidence.LOW, List.of());
    }

    static String normalizeForMatching(String value) {
        String normalized = Normalizer.normalize(safe(value), Normalizer.Form.NFKC)
                .toUpperCase(Locale.ROOT)
                .replaceAll("\\b(?:ORDER|TXN|TRANSACTION|REF|REFERENCE|UTR|RRN|ID)\\s*[:#-]?\\s*[A-Z0-9-]+", " ")
                .replaceAll("\\b\\d{3,}\\b", " ")
                .replaceAll("[^A-Z0-9]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return normalized;
    }

    private static CategoryRule rule(String category, List<String> high, List<String> medium) {
        return new CategoryRule(category, high, medium);
    }

    private static List<String> matchingSignals(String normalized, List<String> signals) {
        return signals.stream()
                .filter(signal -> (" " + normalized + " ").contains(" " + normalizeForMatching(signal) + " "))
                .toList();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private record CategoryRule(String category, List<String> highConfidenceSignals,
                                List<String> mediumConfidenceSignals) {
    }
}
