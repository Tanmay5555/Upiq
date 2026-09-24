import React from 'react';
import { AlertTriangle, CheckCircle2, ShieldAlert, Sparkles, TrendingDown, TrendingUp } from 'lucide-react';

export const Badge = ({ children, variant = 'info', size = 'sm', icon: CustomIcon, className = '' }) => {
  const variantStyles = {
    income: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20 light:bg-emerald-50 light:text-emerald-700',
    expense: 'bg-rose-500/10 text-rose-400 border-rose-500/20 light:bg-rose-50 light:text-rose-700',
    fraud: 'bg-rose-600/20 text-rose-300 border-rose-500/40 animate-pulse light:bg-rose-100 light:text-rose-800',
    anomaly: 'bg-amber-500/15 text-amber-300 border-amber-500/30 light:bg-amber-50 light:text-amber-800',
    ai: 'bg-purple-500/15 text-purple-300 border-purple-500/30 light:bg-purple-50 light:text-purple-700',
    success: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
    warning: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
    info: 'bg-indigo-500/10 text-indigo-300 border-indigo-500/20',
    neutral: 'bg-slate-800 text-slate-300 border-slate-700 light:bg-slate-100 light:text-slate-700',
  };

  const sizeStyles = {
    sm: 'px-2.5 py-0.5 text-xs font-medium rounded-md gap-1',
    md: 'px-3 py-1 text-xs font-semibold rounded-lg gap-1.5',
  };

  const getIcon = () => {
    if (CustomIcon) return <CustomIcon className="w-3.5 h-3.5" />;
    if (variant === 'income') return <TrendingUp className="w-3.5 h-3.5" />;
    if (variant === 'expense') return <TrendingDown className="w-3.5 h-3.5" />;
    if (variant === 'fraud') return <ShieldAlert className="w-3.5 h-3.5 text-rose-400" />;
    if (variant === 'anomaly') return <AlertTriangle className="w-3.5 h-3.5 text-amber-400" />;
    if (variant === 'ai') return <Sparkles className="w-3.5 h-3.5 text-purple-400" />;
    if (variant === 'success') return <CheckCircle2 className="w-3.5 h-3.5" />;
    return null;
  };

  return (
    <span
      className={`inline-flex items-center border ${sizeStyles[size]} ${variantStyles[variant]} ${className}`}
    >
      {getIcon()}
      {children}
    </span>
  );
};
