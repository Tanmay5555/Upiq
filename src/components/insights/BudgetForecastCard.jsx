import React from 'react';
import { AlertTriangle, CheckCircle, TrendingUp } from 'lucide-react';
import { Card } from '../common/Card';
import { Badge } from '../common/Badge';

export const BudgetForecastCard = ({ forecast }) => {
  const percentSpent = Math.min(100, Math.round((forecast.spent / forecast.budget) * 100));
  const percentPredicted = Math.min(100, Math.round((forecast.predicted / forecast.budget) * 100));

  const isOver = forecast.spent > forecast.budget;
  const isNear = percentSpent >= 85 && !isOver;

  return (
    <Card glow={isOver}>
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div
            className="w-10 h-10 rounded-xl flex items-center justify-center font-bold text-white shadow-md"
            style={{ backgroundColor: forecast.color }}
          >
            {forecast.category.charAt(0)}
          </div>
          <div>
            <h4 className="text-base font-bold text-slate-100 light:text-slate-900">
              {forecast.category}
            </h4>
            <span className="text-xs text-slate-400">Budget Limit: ${forecast.budget.toLocaleString()}</span>
          </div>
        </div>

        {isOver ? (
          <Badge variant="expense" icon={AlertTriangle}>
            Over Budget
          </Badge>
        ) : isNear ? (
          <Badge variant="warning" icon={AlertTriangle}>
            Near Limit
          </Badge>
        ) : (
          <Badge variant="success" icon={CheckCircle}>
            On Track
          </Badge>
        )}
      </div>

      {/* Figures */}
      <div className="mt-4 grid grid-cols-2 gap-2 text-xs bg-slate-950/60 light:bg-slate-100 p-3 rounded-xl border border-slate-800 light:border-slate-200">
        <div>
          <span className="text-slate-400 block">Spent so far:</span>
          <span className="font-extrabold text-sm text-slate-100 light:text-slate-900">
            ${forecast.spent.toLocaleString()}
          </span>
        </div>
        <div>
          <span className="text-slate-400 block">AI Month-End Forecast:</span>
          <span className={`font-extrabold text-sm ${forecast.predicted > forecast.budget ? 'text-rose-400' : 'text-indigo-400'}`}>
            ${forecast.predicted.toLocaleString()}
          </span>
        </div>
      </div>

      {/* Progress Bars */}
      <div className="mt-4 space-y-2">
        <div className="flex items-center justify-between text-[11px] font-semibold">
          <span className="text-slate-300">Spent: {percentSpent}%</span>
          <span className="text-slate-400">AI Predicts: {percentPredicted}%</span>
        </div>
        <div className="w-full h-3 bg-slate-950 rounded-full overflow-hidden border border-slate-800 relative">
          {/* Actual spent bar */}
          <div
            className={`h-full transition-all duration-500 rounded-full ${
              isOver ? 'bg-rose-500' : isNear ? 'bg-amber-500' : 'bg-emerald-500'
            }`}
            style={{ width: `${percentSpent}%` }}
          />
        </div>
      </div>
    </Card>
  );
};
