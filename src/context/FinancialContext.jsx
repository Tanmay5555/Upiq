import React, { createContext, useContext, useState } from 'react';
import {
  userProfile as initialProfile,
  testStandardUser,
  testAdminUser,
  kpiSummary as initialKpi,
  categoryBreakdown as initialCategories,
  incomeExpenseTrends as initialTrends,
  mockTransactions as initialTransactions,
  fraudAlerts as initialFraudAlerts,
  budgetForecasts as initialBudgets,
  investmentSuggestions as initialInvestments,
  monthlyReports as initialReports,
  adminMetrics as initialAdminMetrics,
  supportedCurrencies,
} from '../data/mockData';

const FinancialContext = createContext();

export const FinancialProvider = ({ children }) => {
  // Helper to safely load initial state from localStorage or fallback
  const getInitialState = (key, fallback) => {
    try {
      const saved = localStorage.getItem(`upiq_${key}`);
      return saved ? JSON.parse(saved) : fallback;
    } catch {
      return fallback;
    }
  };

  const [isAuthenticated, setIsAuthenticated] = useState(() => getInitialState('auth', false));
  const [profile, setProfile] = useState(() => getInitialState('profile', initialProfile));
  const [selectedCurrencyCode, setSelectedCurrencyCode] = useState(() => {
    const savedProf = getInitialState('profile', null);
    if (savedProf?.currency) return savedProf.currency;
    return getInitialState('currency', 'INR');
  });
  const [kpi, setKpi] = useState(initialKpi);
  const [categories, setCategories] = useState(initialCategories);
  const [trends, setTrends] = useState(initialTrends);
  const [transactions, setTransactions] = useState(initialTransactions);
  const [fraudAlerts, setFraudAlerts] = useState(initialFraudAlerts);
  const [budgets, setBudgets] = useState(initialBudgets);
  const [investments, setInvestments] = useState(initialInvestments);
  const [reports, setReports] = useState(initialReports);
  const [adminMetrics, setAdminMetrics] = useState(initialAdminMetrics);

  // UI interaction states
  const [activeTab, setActiveTab] = useState('dashboard');
  const [isFraudDrawerOpen, setIsFraudDrawerOpen] = useState(false);
  const [aiPromptQuery, setAiPromptQuery] = useState('');
  const [toasts, setToasts] = useState([]);

  // Sync state to localStorage whenever changed
  const updateCurrency = (code) => {
    setSelectedCurrencyCode(code);
    setProfile((prev) => {
      const updated = { ...prev, currency: code };
      try { localStorage.setItem('upiq_profile', JSON.stringify(updated)); } catch {}
      return updated;
    });
    try { localStorage.setItem('upiq_currency', JSON.stringify(code)); } catch {}
  };

  // Active currency object
  const currentCurrency =
    supportedCurrencies.find((c) => c.code === selectedCurrencyCode) || supportedCurrencies[1] || supportedCurrencies[0];

  // Global Currency Formatting Helper
  const formatCurrency = (amountInUSD, decimals = 2) => {
    if (typeof amountInUSD !== 'number' || isNaN(amountInUSD)) return `${currentCurrency.symbol}0.00`;
    const converted = amountInUSD * currentCurrency.rate;

    // Formatting based on currency locale
    if (currentCurrency.code === 'JPY') {
      return `${currentCurrency.symbol}${Math.round(converted).toLocaleString()}`;
    }
    return `${currentCurrency.symbol}${converted.toLocaleString(undefined, {
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals,
    })}`;
  };

  // Auth operations
  const login = (email, password, currencyCode, userName, role) => {
    const isAdmin = (email && email.toLowerCase().includes('admin')) || role === 'admin';
    const basePreset = isAdmin ? testAdminUser : testStandardUser;

    const targetCurrency = currencyCode || selectedCurrencyCode || basePreset.currency || 'INR';
    setSelectedCurrencyCode(targetCurrency);

    const updatedProfile = {
      ...basePreset,
      email: email || basePreset.email,
      name: userName || basePreset.name,
      currency: targetCurrency,
      role: isAdmin ? 'admin' : 'user',
      accountType: isAdmin ? 'UPIQ Super Admin' : 'UPIQ Pro AI',
    };

    setProfile(updatedProfile);
    setIsAuthenticated(true);

    if (isAdmin) {
      setActiveTab('admin');
    } else {
      setActiveTab('dashboard');
    }

    try {
      localStorage.setItem('upiq_auth', JSON.stringify(true));
      localStorage.setItem('upiq_profile', JSON.stringify(updatedProfile));
      localStorage.setItem('upiq_currency', JSON.stringify(targetCurrency));
    } catch {}

    addToast(
      'Welcome to UPIQ AI',
      `Logged in as ${updatedProfile.name} (${isAdmin ? 'Admin View' : 'Standard User'})`,
      'success'
    );
  };

  const logout = () => {
    setIsAuthenticated(false);
    try {
      localStorage.removeItem('upiq_auth');
    } catch {}
    addToast('Logged Out', 'You have been signed out of UPIQ AI session', 'info');
  };

  // Profile Update Operation
  const updateProfile = (updatedFields) => {
    setProfile((prev) => {
      const newProf = { ...prev, ...updatedFields };
      if (updatedFields.currency) {
        setSelectedCurrencyCode(updatedFields.currency);
        try { localStorage.setItem('upiq_currency', JSON.stringify(updatedFields.currency)); } catch {}
      }
      try { localStorage.setItem('upiq_profile', JSON.stringify(newProf)); } catch {}
      return newProf;
    });
    addToast('Profile Saved', 'Personal information and preferences updated successfully', 'success');
  };

  // Toast helper
  const addToast = (title, message, type = 'info') => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, title, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4500);
  };

  const removeToast = (id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  // Predict Category helper
  const predictCategory = (title) => {
    const lower = title.toLowerCase();
    if (
      lower.includes('uber') ||
      lower.includes('fuel') ||
      lower.includes('shell') ||
      lower.includes('metro') ||
      lower.includes('cab')
    ) {
      return 'Transport & Fuel';
    }
    if (
      lower.includes('food') ||
      lower.includes('coffee') ||
      lower.includes('starbucks') ||
      lower.includes('restaurant') ||
      lower.includes('dine') ||
      lower.includes('burger') ||
      lower.includes('swiggy') ||
      lower.includes('zomato')
    ) {
      return 'Food & Dining';
    }
    if (
      lower.includes('amazon') ||
      lower.includes('store') ||
      lower.includes('buy') ||
      lower.includes('shop') ||
      lower.includes('apparel') ||
      lower.includes('zara')
    ) {
      return 'Shopping';
    }
    if (
      lower.includes('power') ||
      lower.includes('light') ||
      lower.includes('electric') ||
      lower.includes('water') ||
      lower.includes('internet') ||
      lower.includes('bill')
    ) {
      return 'Bills & Utilities';
    }
    if (
      lower.includes('netflix') ||
      lower.includes('spotify') ||
      lower.includes('cinema') ||
      lower.includes('movie') ||
      lower.includes('game')
    ) {
      return 'Entertainment';
    }
    if (lower.includes('salary') || lower.includes('payout') || lower.includes('payroll')) {
      return 'Salary';
    }
    return 'Food & Dining';
  };

  // Transaction Operations
  const addTransaction = (newTx) => {
    const category = newTx.category || predictCategory(newTx.title);
    const created = {
      id: `tx-${Date.now()}`,
      title: newTx.title,
      amount: parseFloat(newTx.amount),
      type: newTx.type || 'expense',
      category: category,
      mode: newTx.mode || 'UPI',
      date: newTx.date || new Date().toISOString(),
      status: 'Completed',
      isAnomaly: parseFloat(newTx.amount) > 1000,
      anomalyReason: parseFloat(newTx.amount) > 1000 ? 'High amount transaction requiring AI verification' : undefined,
      merchant: newTx.merchant || newTx.title,
      location: newTx.location || 'Local',
      upiAmount: newTx.upiAmount,
    };

    setTransactions((prev) => [created, ...prev]);

    setKpi((prev) => {
      const amt = parseFloat(newTx.amount);
      if (newTx.type === 'income') {
        return { ...prev, monthlyIncome: prev.monthlyIncome + amt, totalBalance: prev.totalBalance + amt };
      } else {
        return { ...prev, monthlyExpense: prev.monthlyExpense + amt, totalBalance: prev.totalBalance - amt };
      }
    });

    addToast('Transaction Added', `Recorded "${created.title}" (${formatCurrency(created.amount)})`, 'success');
  };

  const updateTransaction = (id, updatedFields) => {
    setTransactions((prev) => prev.map((t) => (t.id === id ? { ...t, ...updatedFields } : t)));
    addToast('Transaction Updated', 'Changes saved successfully', 'info');
  };

  const deleteTransaction = (id) => {
    setTransactions((prev) => prev.filter((t) => t.id !== id));
    addToast('Transaction Deleted', 'Transaction removed from ledger', 'warning');
  };

  // Fraud Alert Operations
  const resolveFraudAlert = (id, action) => {
    setFraudAlerts((prev) => prev.filter((a) => a.id !== id));
    setProfile((prev) => ({ ...prev, unreadAlertsCount: Math.max(0, prev.unreadAlertsCount - 1) }));

    if (action === 'flagged') {
      addToast('Fraud Confirmed', 'Card blocked & dispute initiated with card issuer', 'danger');
    } else {
      addToast('Alert Dismissed', 'Transaction marked as verified by user', 'success');
    }
  };

  const triggerAiQuery = (query) => {
    setAiPromptQuery(query);
    setActiveTab('ai-assistant');
  };

  return (
    <FinancialContext.Provider
      value={{
        isAuthenticated,
        login,
        logout,
        profile,
        updateProfile,
        supportedCurrencies,
        selectedCurrencyCode,
        setSelectedCurrencyCode: updateCurrency,
        updateCurrency,
        currentCurrency,
        formatCurrency,
        kpi,
        categories,
        trends,
        transactions,
        fraudAlerts,
        budgets,
        investments,
        reports,
        adminMetrics,
        activeTab,
        setActiveTab,
        isFraudDrawerOpen,
        setIsFraudDrawerOpen,
        aiPromptQuery,
        setAiPromptQuery,
        triggerAiQuery,
        toasts,
        addToast,
        removeToast,
        addTransaction,
        updateTransaction,
        deleteTransaction,
        resolveFraudAlert,
        predictCategory,
      }}
    >
      {children}
    </FinancialContext.Provider>
  );
};

export const useFinancial = () => {
  const context = useContext(FinancialContext);
  if (!context) throw new Error('useFinancial must be used within FinancialProvider');
  return context;
};
