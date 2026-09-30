import { useEffect, useState, useMemo } from "react";
import TransactionService from "../services/transaction.service";
import { useDateFilter } from "../context/DateFilterContext";
import { filterByDateRange } from "../utils/transactionUtils";
import KPIStrip from "../components/dashboard/KPIStrip";
import InsightCards from "../components/dashboard/InsightCards";
import CategoryBreakdown from "../components/dashboard/CategoryBreakdown";
import IncomeExpenseComparison from "../components/dashboard/IncomeExpenseComparison";
import RecentActivity from "../components/dashboard/RecentActivity";
import BudgetProgress from "../components/dashboard/BudgetProgress";
import DateRangeFilter from "../components/dashboard/DateRangeFilter";
import EmptyState from "../components/ui/EmptyState";
import Card from "../components/ui/Card";
import SkeletonLoader from "../components/ui/SkeletonLoader";
import VerifiedFinancialOverview from "../components/dashboard/VerifiedFinancialOverview";
import DownloadReportAction from "../components/dashboard/DownloadReportAction";

const Dashboard = () => {
    const [allTransactions, setAllTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const { startDate, endDate } = useDateFilter();

    // Filter transactions by date range
    const transactions = useMemo(() => {
        return filterByDateRange(allTransactions, startDate, endDate);
    }, [allTransactions, startDate, endDate]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await TransactionService.getAll();
                if (response.success) {
                    setAllTransactions(response.data);
                }
            } catch (err) {
                console.error("Failed to fetch transactions", err);
                setError("Failed to load dashboard data");
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, []);

    if (loading) {
        return (
            <div className="space-y-8">
                <div className="mb-8">
                    <div className="h-8 bg-[var(--bg-surface)] rounded-lg w-64 mb-3 animate-pulse"></div>
                    <div className="h-4 bg-[var(--bg-surface)] rounded w-96 max-w-full animate-pulse"></div>
                </div>
                <SkeletonLoader type="kpi" count={4} />
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <SkeletonLoader type="card" count={2} />
                </div>
                <SkeletonLoader type="card" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex h-full items-center justify-center min-h-[600px]">
                <div className="max-w-lg rounded-3xl border border-rose-200 bg-[var(--bg-card)] p-8 text-center shadow-premium dark:border-rose-900/50">
                    <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-2xl bg-rose-500/10 text-rose-600">!</div>
                    <p className="text-lg font-bold text-[var(--text-main)] mb-2">Dashboard data unavailable</p>
                    <p className="text-sm text-[var(--text-muted)] mb-5">{error}</p>
                    <button
                        onClick={() => window.location.reload()}
                        className="px-5 py-2.5 bg-primary-600 text-white rounded-xl hover:bg-primary-700 transition font-semibold"
                    >
                        Retry
                    </button>
                </div>
            </div>
        );
    }

    // Show empty state if no transactions
    if (allTransactions.length === 0 && !loading) {
        return (
            <div className="space-y-8">
                <div className="mb-8 flex flex-wrap items-center justify-between gap-4">
                    <div>
                        <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-[var(--text-main)] mb-2">Your financial overview</h1>
                        <p className="text-[var(--text-muted)]">A clear view of your cash flow, spending, and verified financial insights.</p>
                    </div>
                    <DownloadReportAction compact />
                </div>
                <VerifiedFinancialOverview />
                <Card>
                    <EmptyState type="dashboard" />
                </Card>
            </div>
        );
    }

    return (
        <div className="space-y-8">
            {/* Header */}
            <div className="mb-2 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-5 rounded-3xl border border-[var(--border-base)] bg-[var(--bg-card)] p-5 sm:p-7 shadow-premium">
                <div>
                    <p className="mb-2 text-xs font-bold uppercase tracking-[0.18em] text-primary-600 dark:text-primary-400">Your money, at a glance</p>
                    <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-[var(--text-main)] mb-2">Financial Dashboard</h1>
                    <p className="max-w-2xl text-sm sm:text-base text-[var(--text-muted)]">Income, expenses, activity and verified financial insights in one place.</p>
                </div>
                <div className="flex flex-wrap items-center gap-3">
                    <DateRangeFilter />
                    <DownloadReportAction compact />
                </div>
            </div>

            {/* UPIQ 2.0 deterministic overview; existing dashboard widgets remain below. */}
            <VerifiedFinancialOverview />

            {/* KPI Strip */}
            <section aria-label="Financial summary" className="space-y-4">
                <div>
                    <h2 className="text-lg font-bold text-[var(--text-main)]">Selected period</h2>
                    <p className="text-sm text-[var(--text-muted)]">Summary updates with your date filter.</p>
                </div>
                <KPIStrip transactions={transactions} />
            </section>

            {/* Insight Cards */}
            <InsightCards transactions={transactions} />

            {/* Primary Analytics Section */}
            <section aria-label="Spending and cash flow" className="space-y-4">
                <div>
                    <h2 className="text-lg font-bold text-[var(--text-main)]">Spending & cash flow</h2>
                    <p className="text-sm text-[var(--text-muted)]">Explore where expenses go and how they compare with income.</p>
                </div>
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-5 xl:gap-6">
                    <CategoryBreakdown transactions={transactions} />
                    <IncomeExpenseComparison transactions={transactions} />
                </div>
            </section>

            {/* Budget Tracking */}
            <BudgetProgress transactions={transactions} />

            {/* Recent Activity - Show latest 5 transactions from ALL time */}
            <RecentActivity transactions={allTransactions} />
        </div>
    );
};

export default Dashboard;
