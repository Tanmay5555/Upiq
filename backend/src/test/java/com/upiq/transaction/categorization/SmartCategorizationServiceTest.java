package com.upiq.transaction.categorization;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmartCategorizationServiceTest {

    private final SmartCategorizationService service = new SmartCategorizationService();

    @Test
    void classifiesFoodMerchant() {
        assertCategory("SWIGGY ORDER", "Food", CategorizationConfidence.HIGH);
    }

    @Test
    void classifiesFuelMerchant() {
        assertCategory("HPCL PETROL", "Fuel", CategorizationConfidence.HIGH);
    }

    @Test
    void classifiesTransportMerchant() {
        assertCategory("UBER", "Transport", CategorizationConfidence.HIGH);
    }

    @Test
    void classifiesElectricityBillWithMediumConfidence() {
        assertCategory("Electricity Bill", "Bills & Utilities", CategorizationConfidence.MEDIUM);
    }

    @Test
    void classifiesShoppingMerchant() {
        assertCategory("AMAZON", "Shopping", CategorizationConfidence.HIGH);
    }

    @Test
    void classifiesHealthcareUsingExistingUiCategoryName() {
        assertCategory("APOLLO PHARMACY", "Health", CategorizationConfidence.HIGH);
    }

    @Test
    void leavesUnknownMerchantUncategorizedWithLowConfidence() {
        assertCategory("XYZ RANDOM MERCHANT 123", "Uncategorized", CategorizationConfidence.LOW);
    }

    @Test
    void normalizesTransactionReferencesAndPunctuationAcrossMerchantVariants() {
        List<String> descriptions = List.of("SWIGGY*12345", "SWIGGY ORDER 9281", "SWIGGY-IN");

        for (String description : descriptions) {
            assertCategory(description, "Food", CategorizationConfidence.HIGH);
        }
    }

    @Test
    void usesMerchantFieldWhenProvidedSeparately() {
        SmartCategorizationResult result = service.categorize("UPI PAYMENT", "Spotify India Pvt Ltd", "expense");

        assertEquals("Entertainment", result.category());
        assertEquals(CategorizationConfidence.HIGH, result.confidence());
        assertEquals(List.of("SPOTIFY"), result.matchedSignals());
    }

    @Test
    void doesNotTreatIncomeFromAirtelPaymentsBankAsAUtilityExpense() {
        SmartCategorizationResult result = service.categorize(
                "Deposit to Airtel Payments Bank ltd.", null, "income");

        assertEquals("Uncategorized", result.category());
        assertEquals(CategorizationConfidence.LOW, result.confidence());
    }

    @Test
    void categorizesSalaryIncomeWithoutApplyingExpenseMerchantRules() {
        SmartCategorizationResult result = service.categorize("Salary credit", null, "income");

        assertEquals("Salary", result.category());
        assertEquals(CategorizationConfidence.HIGH, result.confidence());
    }

    @Test
    void classifiesRequestedAccommodationExpenseSignalsAsRent() {
        for (String signal : List.of("PG RENT", "PAYING GUEST", "Hotel Booking", "HOTEL STAY",
                "Guest House", "House Rent", "PG ACCOMMODATION", "RENTAL", "ROOM RENT",
                "HOTELS", "LODGE", "GUESTHOUSE")) {
            assertCategory(signal, "Rent", CategorizationConfidence.HIGH);
        }
    }

    @Test
    void doesNotClassifyAccommodationSignalsForIncome() {
        for (String signal : List.of("PG", "HOTEL")) {
            SmartCategorizationResult result = service.categorize(signal, null, "income");
            assertEquals("Uncategorized", result.category());
            assertEquals(CategorizationConfidence.LOW, result.confidence());
        }
    }

    @Test
    void accommodationSignalsAreMatchedAsWholeWords() {
        assertCategory("PGBANK HOTELIER", "Uncategorized", CategorizationConfidence.LOW);
    }

    @Test
    void doesNotApplyRentRulesToNonExpenseTransactionTypes() {
        SmartCategorizationResult result = service.categorize("HOTEL", null, "transfer");
        assertEquals("Uncategorized", result.category());
        assertEquals(CategorizationConfidence.LOW, result.confidence());
    }

    private void assertCategory(String description, String expectedCategory, CategorizationConfidence confidence) {
        SmartCategorizationResult result = service.categorize(description, null, "expense");

        assertEquals(expectedCategory, result.category());
        assertEquals(confidence, result.confidence());
    }
}
