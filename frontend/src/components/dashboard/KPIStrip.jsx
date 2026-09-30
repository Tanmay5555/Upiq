import { useMemo } from "react";
import { Wallet, TrendingUp, TrendingDown, Percent } from "lucide-react";
import { calculateBalance, calculateTotalIncome, calculateTotalExpenses, calculateSavingsRate, compareMonthOverMonth } from "../../utils/transactionUtils";
import clsx from "clsx";

const KPIStrip = ({ transactions }) => {
  const balance = useMemo(() => calculateBalance(transactions), [transactions]);
  const income = useMemo(() => calculateTotalIncome(transactions), [transactions]);
  const expenses = useMemo(() => calculateTotalExpenses(transactions), [transactions]);
  const savingsRate = useMemo(() => calculateSavingsRate(transactions), [transactions]);
  const momComparison = useMemo(() => compareMonthOverMonth(transactions), [transactions]);

  const kpis = [
    {
      label: "Balance",
      value: `₹${balance.toLocaleString('en-IN')}`,
      supporting: "Income minus expenses",
      icon: Wallet,
      bgColor: "bg-gradient-to-br from-blue-500 to-blue-600",
      trend: null
    },
    {
      label: "Income",
      value: `₹${income.toLocaleString('en-IN')}`,
      supporting: `${transactions.filter((item) => item.type?.toLowerCase() === 'income').length} recorded entries`,
      icon: TrendingUp,
      bgColor: "bg-gradient-to-br from-green-500 to-green-600",
      trend: null
    },
    {
      label: "Expenses",
      value: `₹${expenses.toLocaleString('en-IN')}`,
      supporting: momComparison ? "Compared with previous month" : "Across the selected period",
      icon: TrendingDown,
      bgColor: "bg-gradient-to-br from-red-500 to-red-600",
      trend: momComparison
    },
    {
      label: "Savings Rate",
      value: `${savingsRate.toFixed(1)}%`,
      supporting: "Net balance as a share of income",
      icon: Percent,
      bgColor: "bg-gradient-to-br from-purple-500 to-purple-600",
      trend: savingsRate >= 20 ? { direction: 'up', change: savingsRate } : savingsRate >= 10 ? { direction: 'neutral', change: savingsRate } : { direction: 'down', change: savingsRate }
    }
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 2xl:grid-cols-4 gap-4 xl:gap-5">
      {kpis.map((kpi, index) => {
        const Icon = kpi.icon;
        const trend = kpi.trend;

        return (
          <div
            key={index}
            className="relative overflow-hidden bg-[var(--bg-card)] rounded-2xl shadow-premium border border-[var(--border-base)] p-5 sm:p-6 hover:shadow-premium-hover transition-all duration-300 group"
          >
            <div className="flex items-start justify-between mb-4">
              <div className="p-2 rounded-xl bg-[var(--bg-surface)] border border-[var(--border-base)] text-primary-600 dark:text-primary-400">
                <Icon size={20} />
              </div>
              {trend && (
                <div className="flex items-center gap-1.5 px-2 py-1 rounded-full bg-[var(--bg-surface)] border border-[var(--border-base)]">
                  {trend.direction === 'down' ? (
                    <TrendingDown size={12} className="text-emerald-500" />
                  ) : trend.direction === 'up' ? (
                    <TrendingUp size={12} className="text-rose-500" />
                  ) : null}
                  <span className={clsx(
                    "text-[10px] font-bold uppercase tracking-wider",
                    trend.direction === 'down' ? "text-emerald-500" : trend.direction === 'up' ? "text-rose-500" : "text-[var(--text-muted)]"
                  )}>
                    {trend.change ? `${trend.change.toFixed(0)}%` : "stable"}
                  </span>
                </div>
              )}
            </div>

            <div>
              <p className="text-[var(--text-muted)] text-sm font-medium opacity-80">
                {kpi.label}
              </p>
              <h2 className="text-2xl sm:text-3xl font-black mt-1 text-[var(--text-main)] tracking-tight break-words">
                {kpi.value}
              </h2>
              <p className="mt-2 text-xs text-[var(--text-muted)]">{kpi.supporting}</p>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default KPIStrip;

