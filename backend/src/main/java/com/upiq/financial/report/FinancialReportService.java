package com.upiq.financial.report;

import com.upiq.financial.dashboard.FinancialDashboardMonth;
import com.upiq.financial.dashboard.FinancialDashboardResponse;
import com.upiq.financial.dashboard.FinancialDashboardService;
import com.upiq.research.calculation.FinancialCalculationService;
import com.upiq.research.calculation.dto.CategoryTotalResult;
import com.upiq.research.calculation.dto.NetBalanceResult;
import com.upiq.research.calculation.dto.PeriodComparisonResult;
import com.upiq.research.calculation.dto.PeriodRange;
import com.upiq.research.calculation.dto.TopTransactionEntry;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialReportService {

    private static final int TOP_TRANSACTION_LIMIT = 5;
    private static final float LEFT = 48;
    private static final float WIDTH = 516;
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final FinancialDashboardService dashboardService;
    private final FinancialCalculationService calculationService;

    public GeneratedFinancialReport generate(Long userId) {
        FinancialDashboardResponse dashboard = dashboardService.getDashboard(userId);
        String monthPart = dashboard.getLatestAvailableMonth() == null
                ? "no-data"
                : String.format("%04d-%02d", dashboard.getLatestAvailableMonth().getYear(),
                        dashboard.getLatestAvailableMonth().getMonth());
        List<TopTransactionEntry> largest = dashboard.isHasTransactionData()
                ? calculationService.findTopTransactions(userId, currentPeriod(dashboard.getLatestAvailableMonth()),
                        "expense", TOP_TRANSACTION_LIMIT)
                : List.of();
        return new GeneratedFinancialReport("upiq-financial-report-" + monthPart + ".pdf",
                createPdf(dashboard, largest, LocalDateTime.now()));
    }

    private byte[] createPdf(FinancialDashboardResponse dashboard, List<TopTransactionEntry> largest,
                             LocalDateTime generatedAt) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Page page = new Page(document);
            page.title("UPIQ AI - Personal Finance Report");
            page.paragraph("Report generated " + TIMESTAMP.format(generatedAt));
            page.gap(12);

            if (!dashboard.isHasTransactionData() || dashboard.getLatestAvailableMonth() == null) {
                page.section("Report period");
                page.paragraph("No transaction data is available for this account yet.");
                page.finish();
                document.save(output);
                return output.toByteArray();
            }

            FinancialDashboardMonth latest = dashboard.getLatestAvailableMonth();
            FinancialDashboardMonth previous = dashboard.getPreviousAvailableMonth();
            page.section("Report period");
            page.paragraph("Analyzed month: " + latest.getLabel()
                    + (previous == null ? "" : " | Comparison month: " + previous.getLabel()));

            NetBalanceResult summary = dashboard.getCurrentMonthSummary();
            if (summary != null) {
                page.section("Financial summary");
                page.tableHeader("Metric", "Verified amount");
                page.tableRow("Total income", amount(summary.getTotalIncome()));
                page.tableRow("Total expenses", amount(summary.getTotalExpense()));
                page.tableRow("Net balance", amount(summary.getNetBalance()));
                if (summary.getSavingsRate() != null) {
                    page.tableRow("Savings rate", amount(summary.getSavingsRate()) + "%");
                }
            }

            PeriodComparisonResult comparison = dashboard.getMonthlyExpenseComparison();
            if (comparison != null) {
                page.section("Expense comparison");
                page.tableHeader("Measure", "Value");
                page.tableRow("Previous period expense", amount(comparison.getPeriod1Total()));
                page.tableRow("Current period expense", amount(comparison.getPeriod2Total()));
                page.tableRow("Absolute difference", amount(comparison.getAbsoluteDifference()));
                page.tableRow("Percentage difference", comparison.getPercentageDifference() == null
                        ? "Unavailable (previous period is zero)"
                        : amount(comparison.getPercentageDifference()) + "%");
            }

            page.section("Category spending");
            List<CategoryTotalResult> categories = dashboard.getCategorySpending() == null
                    ? List.of() : dashboard.getCategorySpending();
            if (categories.isEmpty()) {
                page.paragraph("No category expense data is available for " + latest.getLabel() + ".");
            } else {
                page.tableHeader("Category", "Amount", "% of expenses");
                for (CategoryTotalResult category : categories) {
                    page.tableRow(category.getCategory(), amount(category.getTotal()),
                            category.getPercentageOfTotalExpense() == null
                                    ? "Unavailable" : amount(category.getPercentageOfTotalExpense()) + "%");
                }
            }

            page.section("Largest transactions");
            if (largest.isEmpty()) {
                page.paragraph("No expense transactions are available for " + latest.getLabel() + ".");
            } else {
                page.tableHeader("Date", "Description", "Category", "Amount");
                for (TopTransactionEntry transaction : largest) {
                    page.tableRow(transaction.getDate() == null ? "Unavailable" : DATE.format(transaction.getDate()),
                            blankFallback(transaction.getDescription(), "Description unavailable"),
                            blankFallback(transaction.getCategory(), "Uncategorized"),
                            amount(transaction.getAmount()));
                }
            }

            page.section("Verified financial insights");
            page.paragraph("Calculated from transaction data for " + latest.getLabel() + ".");
            if (!categories.isEmpty()) {
                CategoryTotalResult topCategory = categories.get(0);
                page.bullet(topCategory.getCategory() + " is the largest tracked expense category at "
                        + amount(topCategory.getTotal()) + (topCategory.getPercentageOfTotalExpense() == null
                        ? "." : " (" + amount(topCategory.getPercentageOfTotalExpense()) + "% of expenses)."));
            }
            if (comparison != null) {
                page.bullet("Expenses changed by " + amount(comparison.getAbsoluteDifference()) + " from "
                        + previous.getLabel() + " to " + latest.getLabel()
                        + (comparison.getPercentageDifference() == null ? "; percentage unavailable because the previous period is zero."
                        : " (" + amount(comparison.getPercentageDifference()) + "%)."));
            }
            if (summary != null) {
                page.bullet("Net balance for " + latest.getLabel() + " is " + amount(summary.getNetBalance()) + ".");
            }
            page.paragraph("All values are deterministic calculations from this account's transaction data. "
                    + "No currency is shown because transaction records do not specify one.");
            page.finish();
            document.save(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Could not generate financial report", e);
        }
    }

    private static PeriodRange currentPeriod(FinancialDashboardMonth month) {
        return PeriodRange.of(java.time.YearMonth.of(month.getYear(), month.getMonth()).atDay(1),
                java.time.YearMonth.of(month.getYear(), month.getMonth()).atEndOfMonth());
    }

    private static String amount(BigDecimal value) {
        return value == null ? "Unavailable" : value.toPlainString();
    }

    private static String blankFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String pdfSafe(String value) {
        return value == null ? "" : value.replaceAll("[^\\x20-\\x7E]", "?");
    }

    private static final class Page {
        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream stream;
        private float y;
        private final PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        private final PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        private Page(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        private void newPage() throws IOException {
            if (stream != null) stream.close();
            page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = page.getMediaBox().getHeight() - 52;
        }

        private void ensure(float height) throws IOException {
            if (y - height < 48) newPage();
        }

        private void text(String value, PDType1Font font, float size, float x, float baseline) throws IOException {
            stream.beginText();
            stream.setFont(font, size);
            stream.newLineAtOffset(x, baseline);
            stream.showText(pdfSafe(value));
            stream.endText();
        }

        private void title(String value) throws IOException {
            ensure(36);
            text(value, bold, 20, LEFT, y);
            y -= 28;
        }

        private void section(String value) throws IOException {
            ensure(30);
            y -= 8;
            text(value, bold, 13, LEFT, y);
            y -= 8;
            stream.setStrokingColor(new java.awt.Color(210, 220, 225));
            stream.moveTo(LEFT, y);
            stream.lineTo(LEFT + WIDTH, y);
            stream.stroke();
            y -= 15;
        }

        private void paragraph(String value) throws IOException {
            for (String line : wrap(value, 96)) {
                ensure(16);
                text(line, regular, 9, LEFT, y);
                y -= 14;
            }
        }

        private void bullet(String value) throws IOException {
            for (String line : wrap("- " + value, 96)) {
                ensure(16);
                text(line, regular, 9, LEFT + 8, y);
                y -= 14;
            }
        }

        private void tableHeader(String... columns) throws IOException {
            ensure(20);
            stream.setNonStrokingColor(new java.awt.Color(238, 243, 245));
            stream.addRect(LEFT, y - 4, WIDTH, 18);
            stream.fill();
            stream.setNonStrokingColor(java.awt.Color.BLACK);
            float[] positions = positions(columns.length);
            for (int i = 0; i < columns.length; i++) text(columns[i], bold, 8, positions[i], y + 2);
            y -= 20;
        }

        private void tableRow(String... columns) throws IOException {
            ensure(22);
            float[] positions = positions(columns.length);
            for (int i = 0; i < columns.length; i++) {
                String value = columns[i] == null ? "" : columns[i];
                text(truncate(value, i == 1 && columns.length >= 4 ? 29 : columns.length == 2 ? 68 : 30),
                        regular, 8, positions[i], y);
            }
            y -= 17;
        }

        private float[] positions(int count) {
            return switch (count) {
                case 2 -> new float[]{LEFT + 6, LEFT + 300};
                case 3 -> new float[]{LEFT + 6, LEFT + 270, LEFT + 410};
                default -> new float[]{LEFT + 6, LEFT + 88, LEFT + 310, LEFT + 425};
            };
        }

        private void gap(float amount) { y -= amount; }
        private void finish() throws IOException { if (stream != null) { stream.close(); stream = null; } }

        private static String truncate(String value, int max) {
            String safe = pdfSafe(value);
            return safe.length() <= max ? safe : safe.substring(0, Math.max(0, max - 3)) + "...";
        }

        private static List<String> wrap(String value, int max) {
            String safe = pdfSafe(value);
            java.util.ArrayList<String> lines = new java.util.ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (String word : safe.split("\\s+")) {
                if (line.length() > 0 && line.length() + word.length() + 1 > max) {
                    lines.add(line.toString());
                    line.setLength(0);
                }
                if (line.length() > 0) line.append(' ');
                line.append(word);
            }
            if (line.length() > 0) lines.add(line.toString());
            return lines;
        }
    }
}
