import React from 'react';
import { motion } from 'framer-motion';
import {
  Sparkles,
  Globe,
  ArrowRight,
  LogIn,
  Sun,
  Moon,
} from 'lucide-react';
import { useFinancial } from '../context/FinancialContext';
import { useTheme } from '../context/ThemeContext';

export const Home = () => {
  const {
    isAuthenticated,
    navigateToLogin,
    navigateToApp,
    handleNeedHelpClick,
    selectedCurrencyCode,
    updateCurrency,
    supportedCurrencies,
  } = useFinancial();

  const { theme, toggleTheme } = useTheme();

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-between relative overflow-x-hidden selection:bg-indigo-500 selection:text-white">
      {/* Background Ambient Glowing Orbs */}
      <div className="absolute top-[-10%] left-[-10%] w-[600px] h-[600px] bg-indigo-600/15 rounded-full blur-[140px] pointer-events-none" />
      <div className="absolute top-[40%] right-[-10%] w-[600px] h-[600px] bg-purple-600/15 rounded-full blur-[140px] pointer-events-none" />
      <div className="absolute bottom-[-10%] left-[20%] w-[600px] h-[600px] bg-emerald-600/10 rounded-full blur-[140px] pointer-events-none" />

      {/* TOP HEADER NAVIGATION */}
      <header className="sticky top-0 z-50 w-full backdrop-blur-2xl bg-slate-950/80 border-b border-slate-800/80">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3.5 flex items-center justify-between">
          {/* Logo & Brand */}
          <div className="flex items-center gap-3 cursor-pointer group">
            <img
              src="/upiq-logo.jpg"
              alt="UPIQ AI Logo"
              className="w-10 h-10 rounded-xl object-cover ring-2 ring-indigo-500/40 shadow-lg shadow-indigo-600/30 group-hover:scale-105 transition-transform"
            />
            <div>
              <span className="font-extrabold text-lg tracking-tight bg-gradient-to-r from-white via-slate-200 to-indigo-300 bg-clip-text text-transparent">
                UPIQ AI<span className="text-indigo-400">.</span>
              </span>
              <span className="block text-[10px] text-slate-400 font-medium tracking-wide">
                Smart Financial OS
              </span>
            </div>
          </div>

          {/* Right Action Bar */}
          <div className="flex items-center gap-3">
            {/* Global Currency Select */}
            <div className="hidden sm:flex items-center gap-1 bg-slate-900 border border-slate-800 rounded-xl px-2.5 py-1.5 text-xs">
              <Globe className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
              <select
                value={selectedCurrencyCode}
                onChange={(e) => updateCurrency(e.target.value)}
                className="bg-transparent text-slate-200 font-semibold focus:outline-none cursor-pointer"
              >
                {supportedCurrencies.map((c) => (
                  <option key={c.code} value={c.code} className="bg-slate-900 text-slate-100">
                    {c.symbol} {c.code}
                  </option>
                ))}
              </select>
            </div>

            {/* Theme Toggle */}
            <button
              onClick={toggleTheme}
              className="p-2.5 rounded-xl bg-slate-900 border border-slate-800 text-slate-400 hover:text-white transition-colors cursor-pointer"
              title="Toggle Color Theme"
            >
              {theme === 'dark' ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-indigo-400" />}
            </button>

            {/* Login / App Launch Button */}
            {isAuthenticated ? (
              <button
                onClick={() => navigateToApp('dashboard')}
                className="px-4 py-2 rounded-xl bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white text-xs font-bold shadow-lg shadow-indigo-600/30 flex items-center gap-2 transition-all cursor-pointer"
              >
                <span>Go to App Dashboard</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            ) : (
              <button
                onClick={() => navigateToLogin('dashboard')}
                className="px-4.5 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-bold shadow-lg shadow-indigo-600/30 flex items-center gap-2 transition-all cursor-pointer"
              >
                <LogIn className="w-4 h-4" />
                <span>Sign In</span>
              </button>
            )}
          </div>
        </div>
      </header>

      {/* HERO SECTION */}
      <section className="relative my-auto py-16 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full text-center space-y-10 z-10 flex-1 flex flex-col justify-center">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="space-y-6 max-w-4xl mx-auto"
        >
          {/* Top Pill Badge */}
          <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-indigo-500/10 border border-indigo-500/20 text-indigo-300 text-xs font-semibold shadow-inner">
            <Sparkles className="w-4 h-4 text-purple-400" />
            <span>UPIQ AI 2.0 — Multi-Currency Personal Finance Operating System</span>
          </div>

          {/* Main Title */}
          <h1 className="text-4xl sm:text-6xl font-black tracking-tight leading-tight text-slate-100">
            Smart Finance Management for{' '}
            <span className="bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
              Every Country & Currency
            </span>
          </h1>

          {/* Subtitle */}
          <p className="text-base sm:text-lg text-slate-400 max-w-2xl mx-auto leading-relaxed">
            Record transactions, upload statement PDFs, track predictive budget velocity, and query your financial health with localized AI privacy.
          </p>

          {/* Action Button Row */}
          <div className="flex flex-wrap items-center justify-center gap-4 pt-2">
            <motion.button
              whileHover={{ scale: 1.03 }}
              whileTap={{ scale: 0.97 }}
              onClick={() => (isAuthenticated ? navigateToApp('dashboard') : navigateToLogin('dashboard'))}
              className="px-6 py-3.5 rounded-2xl bg-gradient-to-r from-indigo-600 via-purple-600 to-indigo-600 text-white font-bold text-sm shadow-xl shadow-indigo-600/35 flex items-center gap-2.5 transition-all cursor-pointer"
            >
              <span>{isAuthenticated ? 'Open Financial Dashboard' : 'Sign In / Get Started'}</span>
              <ArrowRight className="w-4 h-4" />
            </motion.button>

            <motion.button
              whileHover={{ scale: 1.03 }}
              whileTap={{ scale: 0.97 }}
              onClick={handleNeedHelpClick}
              className="px-6 py-3.5 rounded-2xl bg-slate-900/90 border border-purple-500/30 hover:border-purple-500/60 text-purple-300 hover:text-white font-bold text-sm shadow-lg flex items-center gap-2.5 transition-all cursor-pointer"
            >
              <Sparkles className="w-4 h-4 text-yellow-300" />
              <span>Ask AI Assistant / Need Help</span>
            </motion.button>
          </div>
        </motion.div>

        {/* Feature Counters Grid */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 p-5 rounded-3xl glass-panel border border-slate-800/80 max-w-4xl mx-auto text-left">
          <div className="space-y-1 p-2">
            <p className="text-2xl font-black text-indigo-400">Instant</p>
            <p className="text-xs font-semibold text-slate-200">UPI & Statement Parsing</p>
            <p className="text-[11px] text-slate-500">Auto PDF OCR Extraction</p>
          </div>
          <div className="space-y-1 p-2 border-l border-slate-800">
            <p className="text-2xl font-black text-emerald-400">256-Bit</p>
            <p className="text-xs font-semibold text-slate-200">Bank SSL Encryption</p>
            <p className="text-[11px] text-slate-500">Cryptographic JWT Security</p>
          </div>
          <div className="space-y-1 p-2 border-l border-slate-800">
            <p className="text-2xl font-black text-purple-400">Private AI</p>
            <p className="text-xs font-semibold text-slate-200">Local Ollama LLM</p>
            <p className="text-[11px] text-slate-500">Zero Cloud Data Exposure</p>
          </div>
          <div className="space-y-1 p-2 border-l border-slate-800">
            <p className="text-2xl font-black text-amber-400">4 Global</p>
            <p className="text-xs font-semibold text-slate-200">Currencies Supported</p>
            <p className="text-[11px] text-slate-500">INR (₹), USD ($), EUR (€), GBP (£)</p>
          </div>
        </div>
      </section>

      {/* FOOTER */}
      <footer className="w-full border-t border-slate-900 bg-slate-950 py-6 px-4 sm:px-6 lg:px-8 z-10">
        <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 gap-3">
          <div className="flex items-center gap-3">
            <img src="/upiq-logo.jpg" alt="UPIQ AI Logo" className="w-7 h-7 rounded-lg object-cover" />
            <span className="font-bold text-slate-400">© 2026 UPIQ Financial OS</span>
          </div>

          <div className="flex items-center gap-4">
            <button onClick={handleNeedHelpClick} className="hover:text-purple-300 transition-colors flex items-center gap-1 cursor-pointer">
              <Sparkles className="w-3.5 h-3.5 text-yellow-300" /> Need Help?
            </button>
            <span>•</span>
            <a href="mailto:support@upiq.ai" className="hover:text-indigo-400 transition-colors">
              Contact Support
            </a>
          </div>
        </div>
      </footer>
    </div>
  );
};
