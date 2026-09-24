import React from 'react';
import { Activity, ArrowUpRight, Wallet, TrendingUp, TrendingDown, PiggyBank } from 'lucide-react';
import { useFinancial } from '../context/FinancialContext';
import { StatCard } from '../components/dashboard/StatCard';
import { IncomeExpenseChart } from '../components/dashboard/IncomeExpenseChart';
import { CategoryDonutChart } from '../components/dashboard/CategoryDonutChart';
import { RecentTransactionsWidget } from '../components/dashboard/RecentTransactionsWidget';
import { QuickAskBar } from '../components/dashboard/QuickAskBar';
import { UPIPaymentHub } from '../components/dashboard/UPIPaymentHub';

export const Dashboard = () => {
  const { kpi, profile } = useFinancial();

  return (
    <div className="space-y-7 md:space-y-9">
      <section className="relative isolate overflow-hidden rounded-[28px] border border-white/[0.075] bg-gradient-to-br from-[#151622]/95 via-[#101117]/80 to-[#101a19]/90 light:from-white light:via-slate-50 light:to-purple-50/40 light:border-slate-200 p-6 sm:p-8 lg:p-10 shadow-[0_25px_90px_-55px_rgba(99,102,241,0.45)] light:shadow-sm">
        <div aria-hidden="true" className="absolute -right-16 -top-32 -z-10 h-80 w-80 rounded-full bg-indigo-500/15 blur-[90px]" />
        <div aria-hidden="true" className="absolute -bottom-44 right-1/3 -z-10 h-72 w-72 rounded-full bg-emerald-400/[0.07] blur-[90px]" />
        <div className="flex flex-col gap-7 xl:flex-row xl:items-end xl:justify-between">
          <div className="max-w-2xl">
            <div className="mb-5 inline-flex items-center gap-2 rounded-full border border-emerald-400/15 bg-emerald-400/[0.06] light:border-emerald-300 light:bg-emerald-50 px-3 py-1.5 text-[11px] font-semibold tracking-wide text-emerald-300 light:text-emerald-700">
              <span className="relative flex h-2 w-2"><span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-emerald-400 opacity-50" /><span className="relative inline-flex h-2 w-2 rounded-full bg-emerald-400" /></span>
              LIVE FINANCIAL OVERVIEW
            </div>
            <h1 className="text-3xl font-semibold tracking-tight text-white light:text-slate-900 sm:text-4xl lg:text-[2.8rem]">
              Welcome back, {profile.name.split(' ')[0]}
              <span className="mt-1 block bg-gradient-to-r from-white via-zinc-200 to-zinc-500 light:from-indigo-600 light:via-purple-600 light:to-pink-600 bg-clip-text text-transparent">Money, in good hands.</span>
            </h1>
            <p className="mt-4 max-w-xl text-sm leading-6 text-zinc-400 light:text-slate-600 sm:text-[15px]">
              A clearer view of your cash flow, habits, and what’s ahead. Your September 2026 overview is ready.
            </p>
          </div>
          <div className="flex items-center gap-3 self-start rounded-2xl border border-white/[0.07] bg-black/20 light:bg-slate-100 light:border-slate-200 px-4 py-3 xl:self-auto">
            <div className="grid h-10 w-10 place-items-center rounded-xl border border-indigo-400/15 bg-indigo-400/10 light:bg-indigo-50 light:border-indigo-200 text-indigo-300 light:text-indigo-600"><Activity className="h-5 w-5" /></div>
            <div><p className="text-[10px] font-semibold uppercase tracking-[0.16em] text-zinc-500 light:text-slate-500">Account</p><p className="mt-0.5 text-sm font-medium text-zinc-200 light:text-slate-900">{profile.accountType}</p></div>
            <ArrowUpRight className="ml-3 h-4 w-4 text-zinc-600 light:text-slate-400" />
          </div>
        </div>
      </section>

      <UPIPaymentHub />

      <section aria-label="Financial summary" className="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-5">
        <StatCard title="Total Net Worth" value={kpi.totalBalance} change={kpi.balanceChange} isPositive icon={Wallet} color="indigo" featured className="sm:col-span-2" />
        <StatCard title="Monthly Income" value={kpi.monthlyIncome} change={kpi.incomeChange} isPositive icon={TrendingUp} color="emerald" />
        <StatCard title="Monthly Expenses" value={kpi.monthlyExpense} change={kpi.expenseChange} isPositive icon={TrendingDown} color="rose" />
        <StatCard title="Projected Savings" value={kpi.projectedSavings} change={kpi.savingsChange} isPositive icon={PiggyBank} color="purple" />
      </section>

      <QuickAskBar />

      <section aria-label="Financial analytics" className="grid grid-cols-1 gap-5 xl:grid-cols-12">
        <div className="xl:col-span-8"><IncomeExpenseChart /></div>
        <div className="xl:col-span-4"><CategoryDonutChart /></div>
      </section>

      <RecentTransactionsWidget />
    </div>
  );
};
