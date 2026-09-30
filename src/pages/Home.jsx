import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Sparkles,
  ShieldCheck,
  Globe,
  ArrowRight,
  Bot,
  Zap,
  LockKeyhole,
  CheckCircle2,
  FileText,
  TrendingUp,
  BrainCircuit,
  CreditCard,
  HelpCircle,
  LogIn,
  Sun,
  Moon,
  ChevronRight,
  Receipt,
  UserCheck,
  ShieldAlert,
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
    formatCurrency,
  } = useFinancial();

  const { theme, toggleTheme } = useTheme();

  // Active home tab state: 'features' | 'security' | 'currency' | 'ai-help'
  const [activeSection, setActiveSection] = useState('features');

  const featureCards = [
    {
      id: 'categorization',
      icon: Zap,
      color: 'from-indigo-500 to-purple-600',
      textColor: 'text-indigo-400',
      bgColor: 'bg-indigo-500/10 border-indigo-500/20',
      title: 'Smart ML Auto-Categorization & OCR',
      desc: 'Instant auto-tagging of UPI, credit card, and netbanking transactions + automated receipt scanner.',
      badge: '99.4% Accuracy',
    },
    {
      id: 'forecasting',
      icon: BrainCircuit,
      color: 'from-purple-500 to-pink-600',
      textColor: 'text-purple-400',
      bgColor: 'bg-purple-500/10 border-purple-500/20',
      title: 'Predictive Budget Velocity Models',
      desc: 'Machine learning velocity models predicting month-end spend with real-time overage Intercept alerts.',
      badge: 'Real-time AI',
    },
    {
      id: 'parsing',
      icon: Receipt,
      color: 'from-emerald-500 to-teal-600',
      textColor: 'text-emerald-400',
      bgColor: 'bg-emerald-500/10 border-emerald-500/20',
      title: 'Bank Statement PDF & CSV Upload',
      desc: 'Upload bank statement PDFs or CSV files to automatically extract and verify transaction line items.',
      badge: 'PDF / CSV',
    },
    {
      id: 'reports',
      icon: FileText,
      color: 'from-amber-500 to-orange-600',
      textColor: 'text-amber-400',
      bgColor: 'bg-amber-500/10 border-amber-500/20',
      title: 'Monthly PDF Financial Reports',
      desc: 'Generate clean, exportable PDF summaries for income, expense category breakdowns, and audit logs.',
      badge: 'One-Click Export',
    },
    {
      id: 'analytics',
      icon: TrendingUp,
      color: 'from-cyan-500 to-blue-600',
      textColor: 'text-cyan-400',
      bgColor: 'bg-cyan-500/10 border-cyan-500/20',
      title: 'Interactive Recharts Analytics',
      desc: 'Visualize dynamic income vs. expense trends, category pie distributions, and savings velocity.',
      badge: 'Interactive Visuals',
    },
    {
      id: 'security-radar',
      icon: ShieldAlert,
      color: 'from-rose-500 to-red-600',
      textColor: 'text-rose-400',
      bgColor: 'bg-rose-500/10 border-rose-500/20',
      title: 'Guardian Fraud Intercept Radar',
      desc: 'Geo-mismatch detection, duplicate charge intercept, and instant card freeze protection drawer.',
      badge: '24/7 Protection',
    },
  ];

  const securityPillars = [
    {
      icon: LockKeyhole,
      title: 'Bank-Grade 256-Bit SSL & JWT',
      desc: 'Cryptographically signed JWT tokens with 256-bit encryption ensuring secure request authentication.',
    },
    {
      icon: Bot,
      title: 'Local Ollama Data Privacy',
      desc: 'Queries processed via local bounded Ollama LLM models, guaranteeing zero financial data leakage to external clouds.',
    },
    {
      icon: ShieldCheck,
      title: 'NPCI & Banking Standard Aligned',
      desc: 'Strict transaction formatting compliance following NPCI, VPA, and international SWIFT standard data schemas.',
    },
    {
      icon: UserCheck,
      title: 'Role-Based Access Control (RBAC)',
      desc: 'Protected admin views and role-gated endpoints ensuring regular users and system administrators are strictly isolated.',
    },
  ];

  const sampleConversionAmountUSD = 1250;

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
          <div
            onClick={() => setActiveSection('features')}
            className="flex items-center gap-3 cursor-pointer group"
          >
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

          {/* Center Navigation Links */}
          <nav className="hidden md:flex items-center gap-1.5 p-1 rounded-2xl bg-slate-900/90 border border-slate-800">
            <button
              onClick={() => setActiveSection('features')}
              className={`px-4 py-1.5 rounded-xl text-xs font-semibold transition-all cursor-pointer ${
                activeSection === 'features'
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
              }`}
            >
              Features
            </button>

            <button
              onClick={() => setActiveSection('security')}
              className={`px-4 py-1.5 rounded-xl text-xs font-semibold transition-all cursor-pointer ${
                activeSection === 'security'
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
              }`}
            >
              Security
            </button>

            <button
              onClick={() => setActiveSection('currency')}
              className={`px-4 py-1.5 rounded-xl text-xs font-semibold transition-all cursor-pointer ${
                activeSection === 'currency'
                  ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
              }`}
            >
              Global Currency
            </button>

            <button
              onClick={() => setActiveSection('ai-help')}
              className={`px-4 py-1.5 rounded-xl text-xs font-semibold transition-all cursor-pointer flex items-center gap-1.5 ${
                activeSection === 'ai-help'
                  ? 'bg-gradient-to-r from-purple-600 to-pink-600 text-white shadow-md shadow-purple-600/30'
                  : 'text-purple-300 hover:text-white hover:bg-purple-950/30'
              }`}
            >
              <Sparkles className="w-3.5 h-3.5 text-yellow-300 animate-pulse" />
              <span>Need Help / AI</span>
            </button>
          </nav>

          {/* Right Action Bar */}
          <div className="flex items-center gap-3">
            {/* Global Currency Select */}
            <div className="hidden sm:flex items-center gap-1 bg-slate-900 border border-slate-800 rounded-xl px-2 py-1 text-xs">
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
              className="p-2 rounded-xl bg-slate-900 border border-slate-800 text-slate-400 hover:text-white transition-colors cursor-pointer"
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
                className="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-bold shadow-lg shadow-indigo-600/30 flex items-center gap-2 transition-all cursor-pointer"
              >
                <LogIn className="w-4 h-4" />
                <span>Sign In</span>
              </button>
            )}
          </div>
        </div>
      </header>

      {/* HERO SECTION */}
      <section className="relative pt-12 pb-16 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto w-full text-center space-y-8 z-10">
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

      {/* SEPARATE HOME PAGE SUB-SECTIONS / TABS */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 w-full z-10 flex-1">
        {/* Section Navigation Tabs */}
        <div className="flex flex-wrap items-center justify-center gap-3 mb-10">
          <button
            onClick={() => setActiveSection('features')}
            className={`px-5 py-2.5 rounded-2xl text-xs font-bold transition-all cursor-pointer flex items-center gap-2 ${
              activeSection === 'features'
                ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30 ring-2 ring-indigo-500/40'
                : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
            }`}
          >
            <Zap className="w-4 h-4 text-indigo-400" />
            <span>[Features Overview]</span>
          </button>

          <button
            onClick={() => setActiveSection('security')}
            className={`px-5 py-2.5 rounded-2xl text-xs font-bold transition-all cursor-pointer flex items-center gap-2 ${
              activeSection === 'security'
                ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30 ring-2 ring-indigo-500/40'
                : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
            }`}
          >
            <LockKeyhole className="w-4 h-4 text-emerald-400" />
            <span>Security & Privacy</span>
          </button>

          <button
            onClick={() => setActiveSection('currency')}
            className={`px-5 py-2.5 rounded-2xl text-xs font-bold transition-all cursor-pointer flex items-center gap-2 ${
              activeSection === 'currency'
                ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30 ring-2 ring-indigo-500/40'
                : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
            }`}
          >
            <Globe className="w-4 h-4 text-cyan-400" />
            <span>Global Currency Engine</span>
          </button>

          <button
            onClick={() => setActiveSection('ai-help')}
            className={`px-5 py-2.5 rounded-2xl text-xs font-bold transition-all cursor-pointer flex items-center gap-2 ${
              activeSection === 'ai-help'
                ? 'bg-gradient-to-r from-purple-600 to-pink-600 text-white shadow-lg shadow-purple-600/30 ring-2 ring-purple-500/40'
                : 'bg-slate-900 text-purple-300 hover:text-white border border-slate-800'
            }`}
          >
            <HelpCircle className="w-4 h-4 text-yellow-300" />
            <span>Need Help / AI Assistant</span>
          </button>
        </div>

        {/* TAB CONTENT DISPLAY */}
        <AnimatePresence mode="wait">
          {/* TAB 1: FEATURES */}
          {activeSection === 'features' && (
            <motion.div
              key="features-tab-content"
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -15 }}
              transition={{ duration: 0.35 }}
              className="space-y-8"
            >
              <div className="text-center space-y-2">
                <h2 className="text-2xl sm:text-3xl font-bold text-slate-100">
                  Comprehensive Platform Features
                </h2>
                <p className="text-xs sm:text-sm text-slate-400 max-w-xl mx-auto">
                  Built for power users and personal finance enthusiasts who demand accuracy, automation, and speed.
                </p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {featureCards.map((feat) => {
                  const Icon = feat.icon;
                  return (
                    <motion.div
                      key={feat.id}
                      whileHover={{ y: -6, scale: 1.02 }}
                      className="rounded-3xl p-6 glass-panel border border-slate-800/80 hover:border-indigo-500/40 transition-all duration-300 flex flex-col justify-between space-y-4 group cursor-pointer"
                    >
                      <div className="space-y-3">
                        <div className="flex items-center justify-between">
                          <div className={`w-11 h-11 rounded-2xl flex items-center justify-center border ${feat.bgColor}`}>
                            <Icon className={`w-5 h-5 ${feat.textColor}`} />
                          </div>
                          <span className="text-[10px] font-bold px-2.5 py-1 rounded-full bg-slate-900 text-slate-400 border border-slate-800">
                            {feat.badge}
                          </span>
                        </div>
                        <h3 className="text-base font-bold text-slate-100 group-hover:text-indigo-300 transition-colors">
                          {feat.title}
                        </h3>
                        <p className="text-xs text-slate-400 leading-relaxed">
                          {feat.desc}
                        </p>
                      </div>

                      <button
                        onClick={() => (isAuthenticated ? navigateToApp(feat.id === 'forecasting' ? 'insights' : feat.id) : navigateToLogin('dashboard'))}
                        className="pt-3 border-t border-slate-800/80 text-xs font-semibold text-indigo-400 group-hover:text-indigo-300 flex items-center justify-between cursor-pointer"
                      >
                        <span>{isAuthenticated ? 'Open Feature' : 'Sign In for Details'}</span>
                        <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                      </button>
                    </motion.div>
                  );
                })}
              </div>
            </motion.div>
          )}

          {/* TAB 2: SECURITY & PRIVACY */}
          {activeSection === 'security' && (
            <motion.div
              key="security-tab-content"
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -15 }}
              transition={{ duration: 0.35 }}
              className="space-y-8 max-w-5xl mx-auto"
            >
              <div className="text-center space-y-2">
                <h2 className="text-2xl sm:text-3xl font-bold text-slate-100">
                  Bank-Grade Security & Privacy Protection
                </h2>
                <p className="text-xs sm:text-sm text-slate-400 max-w-xl mx-auto">
                  Your financial privacy is sacred. Built with local AI model execution and cryptographically signed session tokens.
                </p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {securityPillars.map((sec, idx) => {
                  const Icon = sec.icon;
                  return (
                    <div
                      key={idx}
                      className="p-6 rounded-3xl glass-panel border border-slate-800/80 space-y-3 relative overflow-hidden"
                    >
                      <div className="w-10 h-10 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center">
                        <Icon className="w-5 h-5" />
                      </div>
                      <h3 className="text-base font-bold text-slate-100">{sec.title}</h3>
                      <p className="text-xs text-slate-400 leading-relaxed">{sec.desc}</p>
                    </div>
                  );
                })}
              </div>

              <div className="p-6 rounded-3xl bg-gradient-to-r from-indigo-950/40 via-purple-950/40 to-slate-900 border border-indigo-500/30 flex flex-col md:flex-row items-center justify-between gap-4 text-center md:text-left">
                <div className="space-y-1">
                  <h4 className="text-sm font-bold text-slate-100">Want to inspect system security metrics?</h4>
                  <p className="text-xs text-slate-400">Sign in with an Administrator account to view real-time system health and security audits.</p>
                </div>
                <button
                  onClick={() => (isAuthenticated ? navigateToApp('admin') : navigateToLogin('admin'))}
                  className="px-5 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-bold shrink-0 cursor-pointer shadow-lg shadow-indigo-600/30"
                >
                  {isAuthenticated ? 'View Admin Security' : 'Sign In as Admin'}
                </button>
              </div>
            </motion.div>
          )}

          {/* TAB 3: GLOBAL CURRENCY ENGINE */}
          {activeSection === 'currency' && (
            <motion.div
              key="currency-tab-content"
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -15 }}
              transition={{ duration: 0.35 }}
              className="space-y-8 max-w-5xl mx-auto"
            >
              <div className="text-center space-y-2">
                <h2 className="text-2xl sm:text-3xl font-bold text-slate-100">
                  Global Multi-Currency Intelligence Engine
                </h2>
                <p className="text-xs sm:text-sm text-slate-400 max-w-xl mx-auto">
                  Seamlessly track income and expenses in your preferred country currency. Switch anytime with instant recalculation.
                </p>
              </div>

              {/* Currency Selector Buttons */}
              <div className="p-6 rounded-3xl glass-panel border border-indigo-500/20 space-y-6 text-center">
                <p className="text-xs font-bold text-indigo-300 uppercase tracking-wider">
                  Select Display Currency:
                </p>
                <div className="flex flex-wrap items-center justify-center gap-3">
                  {supportedCurrencies.map((c) => (
                    <button
                      key={c.code}
                      onClick={() => updateCurrency(c.code)}
                      className={`px-5 py-3 rounded-2xl text-xs font-bold border transition-all cursor-pointer flex items-center gap-2 ${
                        selectedCurrencyCode === c.code
                          ? 'bg-indigo-600 text-white border-indigo-400 shadow-xl shadow-indigo-600/40 ring-2 ring-indigo-500/50 scale-105'
                          : 'bg-slate-900 text-slate-300 border-slate-800 hover:border-slate-700 hover:text-white'
                      }`}
                    >
                      <span className="text-base">{c.symbol}</span>
                      <span>{c.code}</span>
                      <span className="text-[10px] opacity-75">({c.country})</span>
                    </button>
                  ))}
                </div>

                {/* Conversion Live Calculation Preview */}
                <div className="p-5 rounded-2xl bg-slate-950/80 border border-slate-800 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 text-left">
                  {supportedCurrencies.map((c) => {
                    const convertedVal = sampleConversionAmountUSD * c.rate;
                    return (
                      <div
                        key={c.code}
                        className={`p-3.5 rounded-xl border transition-colors ${
                          selectedCurrencyCode === c.code
                            ? 'bg-indigo-600/15 border-indigo-500/40 text-indigo-200'
                            : 'bg-slate-900/60 border-slate-800/80 text-slate-300'
                        }`}
                      >
                        <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">{c.country}</p>
                        <p className="text-lg font-black text-slate-100">
                          {c.symbol}
                          {convertedVal.toLocaleString(undefined, { maximumFractionDigits: 2 })}
                        </p>
                        <p className="text-[10px] text-slate-500 font-mono">1 USD = {c.rate} {c.code}</p>
                      </div>
                    );
                  })}
                </div>
              </div>
            </motion.div>
          )}

          {/* TAB 4: NEED HELP / AI ASSISTANT */}
          {activeSection === 'ai-help' && (
            <motion.div
              key="ai-help-tab-content"
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -15 }}
              transition={{ duration: 0.35 }}
              className="space-y-8 max-w-4xl mx-auto"
            >
              <div className="p-8 rounded-3xl glass-panel-glow border border-purple-500/40 bg-slate-900/90 text-center space-y-6 relative overflow-hidden shadow-2xl">
                <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-purple-600 to-indigo-600 flex items-center justify-center mx-auto shadow-xl shadow-purple-600/40">
                  <Sparkles className="w-8 h-8 text-yellow-300 animate-pulse" />
                </div>

                <div className="space-y-2 max-w-xl mx-auto">
                  <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-100">
                    Need Help? Talk to UPIQ AI Assistant
                  </h2>
                  <p className="text-xs sm:text-sm text-slate-400 leading-relaxed">
                    Ask questions about your budget, monthly expenses, fraud alerts, or financial forecasts in natural language. Powered by deterministic calculation engines & localized AI models.
                  </p>
                </div>

                {/* Sample Prompt Cards */}
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 text-left max-w-2xl mx-auto">
                  <div className="p-3.5 rounded-2xl bg-slate-950 border border-slate-800 text-xs text-slate-300">
                    <p className="font-bold text-purple-400 mb-1">“Category Query”</p>
                    <p className="text-[11px] text-slate-400">“How much did I spend on Dining and Food this month?”</p>
                  </div>
                  <div className="p-3.5 rounded-2xl bg-slate-950 border border-slate-800 text-xs text-slate-300">
                    <p className="font-bold text-indigo-400 mb-1">“Budget Alert”</p>
                    <p className="text-[11px] text-slate-400">“Am I on track to exceed my monthly budget limit?”</p>
                  </div>
                  <div className="p-3.5 rounded-2xl bg-slate-950 border border-slate-800 text-xs text-slate-300">
                    <p className="font-bold text-emerald-400 mb-1">“Savings Plan”</p>
                    <p className="text-[11px] text-slate-400">“Calculate how much I should save to reach my goal.”</p>
                  </div>
                </div>

                {/* Primary Action Button with Log-in Check */}
                <div className="pt-4">
                  <motion.button
                    whileHover={{ scale: 1.04 }}
                    whileTap={{ scale: 0.97 }}
                    onClick={handleNeedHelpClick}
                    className="px-8 py-4 rounded-2xl bg-gradient-to-r from-purple-600 via-indigo-600 to-purple-600 text-white font-extrabold text-sm shadow-xl shadow-purple-600/40 inline-flex items-center gap-3 cursor-pointer"
                  >
                    <Bot className="w-5 h-5 text-indigo-200" />
                    <span>{isAuthenticated ? 'Launch AI Assistant Now' : 'Sign In to Access AI Assistant'}</span>
                    <ArrowRight className="w-5 h-5" />
                  </motion.button>
                  <p className="text-[11px] text-slate-500 mt-2">
                    {isAuthenticated ? 'Direct access granted — session active' : 'Will open Login Page if you are not currently signed in'}
                  </p>
                </div>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </section>

      {/* FOOTER */}
      <footer className="w-full border-t border-slate-900 bg-slate-950 py-8 px-4 sm:px-6 lg:px-8 z-10">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between text-xs text-slate-500 gap-4">
          <div className="flex items-center gap-3">
            <img src="/upiq-logo.jpg" alt="UPIQ AI Logo" className="w-7 h-7 rounded-lg object-cover" />
            <span className="font-bold text-slate-400">© 2026 UPIQ Financial OS</span>
          </div>

          <div className="flex items-center gap-6">
            <button onClick={() => setActiveSection('features')} className="hover:text-white transition-colors">Features</button>
            <button onClick={() => setActiveSection('security')} className="hover:text-white transition-colors">Security</button>
            <button onClick={() => setActiveSection('currency')} className="hover:text-white transition-colors">Global Currency</button>
            <button onClick={handleNeedHelpClick} className="hover:text-purple-300 transition-colors flex items-center gap-1">
              <Sparkles className="w-3 h-3 text-yellow-300" /> Need Help?
            </button>
          </div>
        </div>
      </footer>
    </div>
  );
};
