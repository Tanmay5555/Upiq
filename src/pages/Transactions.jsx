import React, { useState } from 'react';
import {
  Search,
  Plus,
  UploadCloud,
  Filter,
  ArrowUpDown,
  Trash2,
  Edit2,
  FileText,
  ShieldAlert,
  ArrowDownLeft,
  ArrowUpRight,
} from 'lucide-react';
import { useTransactions } from '../hooks/useTransactions';
import { useFinancial } from '../context/FinancialContext';
import { CategoryChip } from '../components/transactions/CategoryChip';
import { AddEditTransactionModal } from '../components/transactions/AddEditTransactionModal';
import { ReceiptScannerModal } from '../components/transactions/ReceiptScannerModal';
import { Button } from '../components/common/Button';
import { Badge } from '../components/common/Badge';

export const Transactions = () => {
  const {
    transactions,
    searchTerm,
    setSearchTerm,
    selectedCategory,
    setSelectedCategory,
    selectedMode,
    setSelectedMode,
    selectedType,
    setSelectedType,
    sortBy,
    setSortBy,
    deleteTransaction,
  } = useTransactions();

  const { setIsFraudDrawerOpen } = useFinancial();

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isReceiptModalOpen, setIsReceiptModalOpen] = useState(false);
  const [txToEdit, setTxToEdit] = useState(null);

  const categoriesList = [
    'All',
    'Food & Dining',
    'Shopping',
    'Bills & Utilities',
    'Transport & Fuel',
    'Entertainment',
    'Salary',
    'Investments',
  ];

  const handleEdit = (tx) => {
    setTxToEdit(tx);
    setIsAddModalOpen(true);
  };

  return (
    <div className="space-y-6">
      {/* Top Header & Actions */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight text-slate-100 light:text-slate-900">
            Automated Transaction Ledger
          </h1>
          <p className="text-xs text-slate-400 mt-1">
            Real-time categorized records, receipt uploads, and risk scores
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            onClick={() => setIsReceiptModalOpen(true)}
            variant="secondary"
            icon={UploadCloud}
          >
            Scan Receipt OCR
          </Button>
          <Button
            onClick={() => {
              setTxToEdit(null);
              setIsAddModalOpen(true);
            }}
            variant="emerald"
            icon={Plus}
          >
            New Transaction
          </Button>
        </div>
      </div>

      {/* Filter & Search Toolbar */}
      <div className="rounded-2xl p-4 glass-panel flex flex-col md:flex-row items-center justify-between gap-4">
        {/* Search */}
        <div className="relative flex-1 w-full">
          <Search className="absolute left-3.5 top-3 w-4 h-4 text-slate-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search by merchant, title, category..."
            className="w-full pl-10 pr-4 py-2 bg-slate-900/80 border border-slate-800 rounded-xl text-xs text-slate-100 light:bg-white light:border-slate-300 light:text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        {/* Category Pill Filters */}
        <div className="flex items-center gap-2 overflow-x-auto w-full md:w-auto pb-1 md:pb-0">
          <span className="text-xs text-slate-400 font-semibold flex items-center gap-1 flex-shrink-0">
            <Filter className="w-3.5 h-3.5" /> Filter:
          </span>

          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="px-3 py-1.5 bg-slate-900 border border-slate-800 rounded-lg text-xs text-slate-200 light:bg-white light:border-slate-300 light:text-slate-800"
          >
            {categoriesList.map((cat) => (
              <option key={cat} value={cat}>
                {cat === 'All' ? 'All Categories' : cat}
              </option>
            ))}
          </select>

          <select
            value={selectedMode}
            onChange={(e) => setSelectedMode(e.target.value)}
            className="px-3 py-1.5 bg-slate-900 border border-slate-800 rounded-lg text-xs text-slate-200 light:bg-white light:border-slate-300 light:text-slate-800"
          >
            <option value="All">All Modes</option>
            <option value="UPI">UPI Only</option>
            <option value="Card">Card Only</option>
            <option value="NetBanking">NetBanking Only</option>
          </select>

          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="px-3 py-1.5 bg-slate-900 border border-slate-800 rounded-lg text-xs text-slate-200 light:bg-white light:border-slate-300 light:text-slate-800"
          >
            <option value="date-desc">Newest First</option>
            <option value="date-asc">Oldest First</option>
            <option value="amount-desc">Highest Amount</option>
            <option value="amount-asc">Lowest Amount</option>
          </select>
        </div>
      </div>

      {/* Transactions Data Table */}
      <div className="rounded-2xl glass-panel overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="bg-slate-950/60 light:bg-slate-100 border-b border-slate-800 light:border-slate-200 text-slate-400 font-semibold uppercase">
                <th className="py-3.5 px-4">Date</th>
                <th className="py-3.5 px-4">Description & Merchant</th>
                <th className="py-3.5 px-4">Category</th>
                <th className="py-3.5 px-4">Payment Mode</th>
                <th className="py-3.5 px-4">AI Risk Indicator</th>
                <th className="py-3.5 px-4 text-right">Amount</th>
                <th className="py-3.5 px-4 text-center">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 light:divide-slate-200">
              {transactions.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center py-12 text-slate-400">
                    No transactions match your current search and filters.
                  </td>
                </tr>
              ) : (
                transactions.map((tx) => (
                  <tr key={tx.id} className="hover:bg-slate-800/40 light:hover:bg-slate-100 transition-colors">
                    <td className="py-4 px-4 text-slate-400 font-mono text-[11px]">
                      {new Date(tx.date).toLocaleDateString('en-US', {
                        month: 'short',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </td>
                    <td className="py-4 px-4">
                      <div className="flex items-center gap-3">
                        <div
                          className={`w-8 h-8 rounded-xl flex items-center justify-center flex-shrink-0 ${
                            tx.type === 'income'
                              ? 'bg-emerald-500/10 text-emerald-400'
                              : 'bg-rose-500/10 text-rose-400'
                          }`}
                        >
                          {tx.type === 'income' ? <ArrowDownLeft className="w-4 h-4" /> : <ArrowUpRight className="w-4 h-4" />}
                        </div>
                        <div>
                          <span className="font-bold text-slate-100 light:text-slate-900 block">
                            {tx.title}
                          </span>
                          <span className="text-[10px] text-slate-400">{tx.merchant}</span>
                        </div>
                      </div>
                    </td>
                    <td className="py-4 px-4">
                      <CategoryChip category={tx.category} />
                    </td>
                    <td className="py-4 px-4">
                      <span className="px-2.5 py-1 rounded bg-slate-900 text-slate-300 border border-slate-800 font-mono text-[11px] light:bg-slate-100 light:text-slate-800">
                        {tx.mode}
                      </span>
                    </td>
                    <td className="py-4 px-4">
                      {tx.isFraud ? (
                        <button onClick={() => setIsFraudDrawerOpen(true)} className="cursor-pointer">
                          <Badge variant="fraud">Flagged Fraud</Badge>
                        </button>
                      ) : tx.isAnomaly ? (
                        <Badge variant="anomaly">High Anomaly</Badge>
                      ) : (
                        <Badge variant="success">Normal</Badge>
                      )}
                    </td>
                    <td className="py-4 px-4 text-right font-extrabold text-sm">
                      <span className={tx.type === 'income' ? 'text-emerald-400' : 'text-slate-100 light:text-slate-900'}>
                        {tx.type === 'income' ? '+' : '-'}${tx.amount.toFixed(2)}
                      </span>
                    </td>
                    <td className="py-4 px-4 text-center">
                      <div className="flex items-center justify-center gap-2">
                        <button
                          onClick={() => handleEdit(tx)}
                          className="p-1.5 text-slate-400 hover:text-indigo-400 hover:bg-slate-800 rounded-lg transition-colors"
                          title="Edit Transaction"
                        >
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => deleteTransaction(tx.id)}
                          className="p-1.5 text-slate-400 hover:text-rose-400 hover:bg-slate-800 rounded-lg transition-colors"
                          title="Delete Transaction"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modals */}
      <AddEditTransactionModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        transactionToEdit={txToEdit}
      />
      <ReceiptScannerModal
        isOpen={isReceiptModalOpen}
        onClose={() => setIsReceiptModalOpen(false)}
      />
    </div>
  );
};
