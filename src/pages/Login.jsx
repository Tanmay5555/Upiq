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
  Fingerprint,
  WalletCards,
  AudioLines,
  BadgeCheck,
  Lock,
} from 'lucide-react';
import { useFinancial } from '../context/FinancialContext';

export const Login = () => {
  const { login, supportedCurrencies } = useFinancial();

  const [name, setName] = useState('Varsha Sharma');
  const [vpa, setVpa] = useState('varsha.s@upiq.ai');
  const [password, setPassword] = useState('••••••••••••');
  const [selectedCurrency, setSelectedCurrency] = useState('INR');
  const [selectedCountry, setSelectedCountry] = useState('India');
  const [showPassword, setShowPassword] = useState(false);
  const [useBiometric, setUseBiometric] = useState(false);

  const [isSubmitting, setIsSubmitting] = useState(false);

  const triggerLoginProcess = (emailVal, passVal, currVal, nameVal) => {
    setIsSubmitting(true);
    // Smooth delay for Framer Motion exit animation to play out
    setTimeout(() => {
      login(emailVal, passVal, currVal, nameVal);
    }, 550);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    triggerLoginProcess(vpa, password, selectedCurrency, name);
  };

  const handleQuickDemo = () => {
    triggerLoginProcess('varsha.s@upiq.ai', 'demo123', selectedCurrency, name);
  };

  const handleCurrencySelect = (code, country) => {
    setSelectedCurrency(code);
    if (country) {
      setSelectedCountry(country);
    } else {
      const match = supportedCurrencies.find((item) => item.code === code);
      if (match) setSelectedCountry(match.country);
    }
  };

  const handleCountrySelect = (country) => {
    setSelectedCountry(country);
    const match = supportedCurrencies.find((item) => item.country === country);
    if (match) setSelectedCurrency(match.code);
  };

  const finsageFeatures = [
    {
      icon: Zap,
      color: 'text-indigo-400 bg-indigo-500/10 border-indigo-500/20',
      title: 'Automated ML Categorization & OCR',
      desc: 'Instant auto-tagging of UPI, credit card, and netbanking transactions + receipt scanner.',
    },
    {
      icon: AudioLines,
      color: 'text-purple-400 bg-purple-500/10 border-purple-500/20',
      title: 'Conversational Voice Assistant',
      desc: 'Native Web Speech API to query expenses, budgets, and savings goals in natural language.',
    },
    {
      icon: Bot,
      color: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/20',
      title: 'Predictive Budget Forecasting',
      desc: 'Machine learning velocity models predicting month-end spend with overage alerts.',
    },
    {
      icon: ShieldCheck,
      color: 'text-rose-400 bg-rose-500/10 border-rose-500/20',
      title: 'Guardian Fraud & Anomaly Radar',
      desc: 'Geo-mismatch detection, duplicate charge intercept, and instant card freeze drawer.',
    },
  ];

  // Framer Motion spring physics configurations for directional animations
  const leftSpringVariant = {
    hidden: { x: '-110%', opacity: 0, filter: 'blur(10px)' },
    visible: {
      x: 0,
      opacity: 1,
      filter: 'blur(0px)',
      transition: { type: 'spring', stiffness: 210, damping: 25, mass: 0.8 },
    },
    exit: {
      x: '-110%',
      opacity: 0,
      filter: 'blur(10px)',
      transition: { duration: 0.5, ease: [0.16, 1, 0.3, 1] },
    },
  };

  const rightSpringVariant = {
    hidden: { x: '110%', opacity: 0, filter: 'blur(10px)' },
    visible: {
      x: 0,
      opacity: 1,
      filter: 'blur(0px)',
      transition: { type: 'spring', stiffness: 210, damping: 25, mass: 0.8 },
    },
    exit: {
      x: '110%',
      opacity: 0,
      filter: 'blur(10px)',
      transition: { duration: 0.5, ease: [0.16, 1, 0.3, 1] },
    },
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col justify-between p-4 md:p-8 relative overflow-hidden selection:bg-indigo-500 selection:text-white">
      {/* Ambient background glowing radial blobs */}
      <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-indigo-600/15 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-purple-600/15 rounded-full blur-3xl pointer-events-none" />

      {/* Header Navigation */}
      <motion.header
        initial={{ y: -50, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        exit={{ y: -50, opacity: 0 }}
        transition={{ duration: 0.5, ease: 'easeOut' }}
        className="w-full max-w-7xl mx-auto flex items-center justify-between py-4 px-6 rounded-2xl glass-panel border border-slate-800/80 mb-8 z-20 backdrop-blur-xl"
      >
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-indigo-600 via-purple-600 to-emerald-500 flex items-center justify-center shadow-lg shadow-indigo-600/30">
            <WalletCards className="w-5 h-5 text-white" />
          </div>
          <span className="font-extrabold text-lg tracking-tight bg-gradient-to-r from-white via-slate-200 to-indigo-300 bg-clip-text text-transparent">
            UPIQ AI<span className="text-indigo-400">.</span>
          </span>
        </div>

        <nav className="hidden md:flex items-center gap-6 text-xs font-semibold text-slate-400">
          <a href="#features" className="hover:text-white transition-colors">Features</a>
          <a href="#security" className="hover:text-white transition-colors">Security</a>
          <a href="#currencies" className="hover:text-white transition-colors">Global Currencies</a>
        </nav>

        <div className="flex items-center gap-2">
          <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-bold">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-ping" />
            System Online
          </span>
        </div>
      </motion.header>

      {/* Main Split Grid Container with Framer Motion AnimatePresence */}
      <div className="w-full max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8 items-center z-10 flex-1 my-auto overflow-hidden p-2">
        <AnimatePresence mode="wait">
          {!isSubmitting && (
            <React.Fragment key="login-split-view">
              {/* LEFT COMPONENT: Features & Branding (Slides from/to LEFT direction) */}
              <motion.div
                variants={leftSpringVariant}
                initial="hidden"
                animate="visible"
                exit="exit"
                className="lg:col-span-7 space-y-8 pr-0 lg:pr-6"
              >
                <div className="space-y-4">
                  <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-indigo-500/10 border border-indigo-500/20 text-indigo-300 text-xs font-semibold">
                    <Sparkles className="w-3.5 h-3.5 text-purple-400" />
                    <span>UPIQ AI 2.0 — Fintech Operating System</span>
                  </div>

                  <h1 className="text-3xl md:text-5xl font-extrabold tracking-tight text-slate-100 leading-tight">
                    AI Finance Platform for{' '}
                    <span className="bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
                      Every Country & Currency
                    </span>
                  </h1>

                  <p className="text-sm md:text-base text-slate-400 max-w-xl leading-relaxed">
                    Experience Finsage-grade dashboard aesthetics combined with real-time ML anomaly detection, Web Speech voice interaction, and multi-currency global intelligence.
                  </p>
                </div>

                {/* Key Metrics Row */}
                <div className="grid grid-cols-3 gap-3 p-4 rounded-2xl glass-panel border border-slate-800/80 text-center">
                  <div>
                    <p className="text-lg font-extrabold text-indigo-400">Instant</p>
                    <p className="text-[10px] text-slate-400 font-medium uppercase tracking-wider">UPI Payments</p>
                  </div>
                  <div className="border-x border-slate-800">
                    <p className="text-lg font-extrabold text-emerald-400">Always On</p>
                    <p className="text-[10px] text-slate-400 font-medium uppercase tracking-wider">Fraud Protection</p>
                  </div>
                  <div>
                    <p className="text-lg font-extrabold text-purple-400">Multi-View</p>
                    <p className="text-[10px] text-slate-400 font-medium uppercase tracking-wider">All Accounts</p>
                  </div>
                </div>

                {/* Feature Showcase Cards */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4" id="features">
                  {finsageFeatures.map((feat) => {
                    const Icon = feat.icon;
                    return (
                      <motion.div
                        key={feat.title}
                        whileHover={{ y: -4, scale: 1.02 }}
                        className="rounded-2xl p-4 glass-panel border border-slate-800/80 hover:border-indigo-500/40 transition-all duration-300 space-y-2 group cursor-pointer"
                      >
                        <div className={`w-9 h-9 rounded-xl flex items-center justify-center border ${feat.color}`}>
                          <Icon className="w-4 h-4" />
                        </div>
                        <h4 className="text-xs font-bold text-slate-200 group-hover:text-indigo-300 transition-colors">{feat.title}</h4>
                        <p className="text-[11px] text-slate-400 leading-normal">{feat.desc}</p>
                      </motion.div>
                    );
                  })}
                </div>

                {/* Interactive Global Currency Switcher Bar */}
                <div className="p-4 rounded-2xl glass-panel border border-indigo-500/20 flex flex-wrap items-center justify-between gap-3 text-xs" id="currencies">
                  <div className="flex items-center gap-2 text-indigo-300 font-semibold">
                    <Globe className="w-4 h-4 text-emerald-400" />
                    <span>Global Currency Engine:</span>
                  </div>
                  <div className="flex flex-wrap items-center gap-2">
                    {supportedCurrencies.map((c) => (
                      <motion.button
                        key={c.code}
                        type="button"
                        whileHover={{ scale: 1.08 }}
                        whileTap={{ scale: 0.95 }}
                        onClick={() => handleCurrencySelect(c.code, c.country)}
                        className={`px-3 py-1.5 rounded-xl text-[11px] font-bold border transition-all duration-200 cursor-pointer ${
                          selectedCurrency === c.code
                            ? 'bg-indigo-600 text-white border-indigo-400 shadow-lg shadow-indigo-600/30 ring-2 ring-indigo-500/40'
                            : 'bg-slate-900/80 text-slate-300 border-slate-800 hover:border-slate-700 hover:text-white'
                        }`}
                        title={`Select ${c.name} (${c.symbol})`}
                      >
                        {c.symbol} {c.code}
                      </motion.button>
                    ))}
                  </div>
                </div>

                {/* Trust Badges */}
                <div className="flex flex-wrap items-center justify-between gap-3 pt-2 border-t border-slate-800/80 text-[11px] text-slate-400" id="security">
                  <span className="flex items-center gap-1.5">
                    <ShieldCheck className="w-4 h-4 text-emerald-400" /> NPCI Aligned
                  </span>
                  <span className="flex items-center gap-1.5">
                    <LockKeyhole className="w-4 h-4 text-indigo-400" /> Bank Grade Security
                  </span>
                  <span className="flex items-center gap-1.5">
                    <BadgeCheck className="w-4 h-4 text-purple-400" /> 256-Bit Encrypted
                  </span>
                </div>
              </motion.div>

              {/* RIGHT COMPONENT: Interactive Login Form Card (Slides from/to RIGHT direction) */}
              <motion.div
                variants={rightSpringVariant}
                initial="hidden"
                animate="visible"
                exit="exit"
                className="lg:col-span-5"
              >
                <div className="rounded-3xl glass-panel-glow border border-slate-800/80 p-6 md:p-8 space-y-6 bg-slate-900/90 backdrop-blur-xl shadow-2xl relative overflow-hidden">
                  {/* Top Rim Lighting */}
                  <div className="absolute top-0 left-0 right-0 h-[1px] bg-gradient-to-r from-transparent via-indigo-500/50 to-transparent" />

                  <div className="text-center space-y-2">
                    <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-indigo-600 via-purple-600 to-emerald-500 flex items-center justify-center mx-auto shadow-lg shadow-indigo-600/30">
                      <WalletCards className="w-6 h-6 text-white" />
                    </div>
                    <h2 className="text-xl font-bold text-slate-100">Sign In to UPIQ AI</h2>
                    <p className="text-xs text-slate-400">Select your country & currency preference</p>
                  </div>

                  <form onSubmit={handleSubmit} className="space-y-4">
                    {/* Full Name */}
                    <div>
                      <label htmlFor="full-name-field" className="block text-xs font-semibold text-slate-300 mb-1">
                        Full Name
                      </label>
                      <input
                        id="full-name-field"
                        type="text"
                        required
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        className="w-full px-4 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all"
                      />
                    </div>

                    {/* Email / UPI ID */}
                    <div>
                      <label htmlFor="vpa-field" className="block text-xs font-semibold text-slate-300 mb-1">
                        UPI ID or Email Address
                      </label>
                      <div className="relative">
                        <span className="absolute left-3.5 top-2.5 text-xs text-slate-500 font-mono">@</span>
                        <input
                          id="vpa-field"
                          type="text"
                          required
                          value={vpa}
                          onChange={(e) => setVpa(e.target.value)}
                          className="w-full pl-8 pr-4 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all"
                          placeholder="name@upiq.ai or mobile"
                        />
                      </div>
                    </div>

                    {/* Password / Passkey Toggle */}
                    <div>
                      <div className="flex items-center justify-between mb-1">
                        <label htmlFor="password-field" className="block text-xs font-semibold text-slate-300">
                          Password / Authentication
                        </label>
                        <button
                          type="button"
                          onClick={() => setUseBiometric(!useBiometric)}
                          className="text-[11px] font-semibold text-indigo-400 hover:text-indigo-300 flex items-center gap-1 transition-colors"
                        >
                          <Fingerprint className="w-3.5 h-3.5" />
                          {useBiometric ? 'Use Password' : 'Use Passkey'}
                        </button>
                      </div>

                      {useBiometric ? (
                        <motion.button
                          type="button"
                          whileHover={{ scale: 1.01 }}
                          whileTap={{ scale: 0.98 }}
                          onClick={() => setPassword('passkey-demo')}
                          className="w-full py-3 px-4 rounded-xl border border-indigo-500/30 bg-indigo-600/10 hover:bg-indigo-600/20 text-xs font-semibold text-indigo-300 flex items-center justify-between transition-all"
                        >
                          <span className="flex items-center gap-2">
                            <Fingerprint className="w-4 h-4 text-emerald-400" />
                            Authenticate with Device Passkey
                          </span>
                          <ArrowRight className="w-4 h-4" />
                        </motion.button>
                      ) : (
                        <div className="relative">
                          <input
                            id="password-field"
                            type={showPassword ? 'text' : 'password'}
                            required
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full pl-4 pr-12 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all"
                          />
                          <button
                            type="button"
                            onClick={() => setShowPassword(!showPassword)}
                            className="absolute right-3 top-2.5 text-[11px] font-semibold text-slate-400 hover:text-slate-200"
                          >
                            {showPassword ? 'Hide' : 'Show'}
                          </button>
                        </div>
                      )}
                    </div>

                    {/* Region & Currency Selector Grid */}
                    <div className="grid grid-cols-2 gap-3">
                      <div>
                        <label htmlFor="country-select-box" className="block text-xs font-semibold text-slate-300 mb-1">
                          Country / Region
                        </label>
                        <select
                          id="country-select-box"
                          value={selectedCountry}
                          onChange={(e) => handleCountrySelect(e.target.value)}
                          className="w-full px-3 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all"
                        >
                          {supportedCurrencies.map((c) => (
                            <option key={c.country} value={c.country}>
                              {c.country}
                            </option>
                          ))}
                        </select>
                      </div>

                      <div>
                        <label htmlFor="currency-select-box" className="block text-xs font-semibold text-slate-300 mb-1">
                          Display Currency
                        </label>
                        <select
                          id="currency-select-box"
                          value={selectedCurrency}
                          onChange={(e) => handleCurrencySelect(e.target.value, null)}
                          className="w-full px-3 py-2.5 bg-slate-950 border border-slate-800 rounded-xl text-xs text-slate-100 focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all"
                        >
                          {supportedCurrencies.map((c) => (
                            <option key={c.code} value={c.code}>
                              {c.code} ({c.symbol})
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>

                    {/* Primary Submit Button */}
                    <motion.button
                      type="submit"
                      whileHover={{ scale: 1.015 }}
                      whileTap={{ scale: 0.98 }}
                      className="w-full py-3 rounded-xl bg-gradient-to-r from-indigo-600 via-purple-600 to-indigo-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-xs shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2 transition-all cursor-pointer"
                    >
                      {isSubmitting ? (
                        <span className="flex items-center gap-2">
                          <span className="w-3.5 h-3.5 rounded-full border-2 border-white border-t-transparent animate-spin" />
                          Launching UPIQ AI...
                        </span>
                      ) : (
                        <>
                          <span>Login with UPI PIN / Biometrics</span>
                          <ArrowRight className="w-4 h-4" />
                        </>
                      )}
                    </motion.button>

                    {/* Quick 1-Click Demo Button */}
                    <motion.button
                      type="button"
                      whileHover={{ scale: 1.01 }}
                      whileTap={{ scale: 0.98 }}
                      onClick={handleQuickDemo}
                      className="w-full py-2.5 rounded-xl border border-slate-800 bg-slate-900/60 hover:bg-slate-800 text-xs font-semibold text-indigo-300 transition-all text-center flex items-center justify-center gap-2 cursor-pointer"
                    >
                      <Sparkles className="w-3.5 h-3.5 text-purple-400" />
                      Quick 1-Click Demo Login
                    </motion.button>
                  </form>

                  <div className="pt-2 border-t border-slate-800/80 text-center flex items-center justify-center gap-4 text-[10px] text-slate-500">
                    <span className="flex items-center gap-1">
                      <Lock className="w-3 h-3 text-emerald-400" /> 256-Bit SSL
                    </span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <CheckCircle2 className="w-3 h-3 text-emerald-400" /> Multi-Country Engine
                    </span>
                  </div>
                </div>
              </motion.div>
            </React.Fragment>
          )}
        </AnimatePresence>
      </div>

      {/* Footer */}
      <footer className="w-full max-w-7xl mx-auto pt-6 border-t border-slate-900 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 gap-2 z-10">
        <span>© 2026 UPIQ Financial OS</span>
        <span>Thoughtful tools for money in motion</span>
        <a href="mailto:support@upiq.ai" className="hover:text-indigo-400 transition-colors">
          Need help? &rarr;
        </a>
      </footer>
    </div>
  );
};
