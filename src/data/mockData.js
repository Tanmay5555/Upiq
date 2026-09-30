export const supportedCurrencies = [
  { code: 'USD', symbol: '$', rate: 1.0, name: 'US Dollar', country: 'United States' },
  { code: 'INR', symbol: '₹', rate: 83.5, name: 'Indian Rupee', country: 'India' },
  { code: 'EUR', symbol: '€', rate: 0.92, name: 'Euro', country: 'European Union' },
  { code: 'GBP', symbol: '£', rate: 0.78, name: 'British Pound', country: 'United Kingdom' },
  { code: 'JPY', symbol: '¥', rate: 155.0, name: 'Japanese Yen', country: 'Japan' },
  { code: 'AED', symbol: 'د.إ', rate: 3.67, name: 'UAE Dirham', country: 'United Arab Emirates' },
  { code: 'CAD', symbol: 'CA$', rate: 1.36, name: 'Canadian Dollar', country: 'Canada' },
  { code: 'AUD', symbol: 'A$', rate: 1.52, name: 'Australian Dollar', country: 'Australia' },
];

export const testStandardUser = {
  id: "user-001",
  name: "User Account",
  email: "user@upiq.ai",
  avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=200",
  accountType: "UPIQ Pro AI",
  role: "user",
  currency: "INR",
  memberSince: "2026",
  unreadAlertsCount: 0,
};

export const testAdminUser = {
  id: "admin-001",
  name: "System Administrator",
  email: "admin@upiq.ai",
  avatar: "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&q=80&w=200",
  accountType: "UPIQ Super Admin",
  role: "admin",
  currency: "INR",
  memberSince: "2026",
  unreadAlertsCount: 0,
};

export const userProfile = testStandardUser;

export const kpiSummary = {
  totalBalance: 0,
  balanceChange: "0%",
  monthlyIncome: 0,
  incomeChange: "0%",
  monthlyExpense: 0,
  expenseChange: "0%",
  projectedSavings: 0,
  savingsChange: "0%",
};

export const categoryBreakdown = [];

export const incomeExpenseTrends = [];

export const mockTransactions = [];

export const fraudAlerts = [];

export const budgetForecasts = [];

export const investmentSuggestions = [];

export const monthlyReports = [];

export const adminMetrics = {
  activeUsers: 0,
  totalTransactionsProcessed: 0,
  systemUptime: "100%",
  securityThreatsBlocked: 0,
  systemLoad: "0%",
  mlAccuracy: "99.8%",
};
