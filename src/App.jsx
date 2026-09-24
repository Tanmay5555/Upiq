import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ThemeProvider } from './context/ThemeContext';
import { FinancialProvider, useFinancial } from './context/FinancialContext';
import { MainLayout } from './components/layout/MainLayout';
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { Transactions } from './pages/Transactions';
import { AIAssistant } from './pages/AIAssistant';
import { Insights } from './pages/Insights';
import { Reports } from './pages/Reports';
import { Admin } from './pages/Admin';
import { Profile } from './pages/Profile';

const AppContent = () => {
  const { isAuthenticated, activeTab } = useFinancial();

  const renderPage = () => {
    switch (activeTab) {
      case 'dashboard':
        return <Dashboard />;
      case 'transactions':
        return <Transactions />;
      case 'ai-assistant':
        return <AIAssistant />;
      case 'insights':
      case 'security':
      case 'insights-tab':
        return <Insights />;
      case 'reports':
        return <Reports />;
      case 'admin':
        return <Admin />;
      case 'profile':
        return <Profile />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="min-h-screen bg-[var(--bg-primary)] text-[var(--text-primary)] transition-colors duration-300 overflow-hidden">
      <AnimatePresence mode="wait">
        {!isAuthenticated ? (
          <motion.div
            key="login-screen-view"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.3 }}
            className="w-full min-h-screen"
          >
            <Login />
          </motion.div>
        ) : (
          <motion.div
            key="authenticated-dashboard-view"
            initial={{ opacity: 0, scale: 0.97 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.97 }}
            transition={{ duration: 0.4, ease: [0.16, 1, 0.3, 1] }}
            className="w-full min-h-screen"
          >
            <MainLayout>{renderPage()}</MainLayout>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default function App() {
  return (
    <ThemeProvider>
      <FinancialProvider>
        <AppContent />
      </FinancialProvider>
    </ThemeProvider>
  );
}
