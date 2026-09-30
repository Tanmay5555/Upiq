import { useEffect, useRef, useState } from "react";
import { AlertTriangle, BadgeCheck, Bot, Send, Sparkles, UserRound } from "lucide-react";
import FinancialChatService from "../services/financial-chat.service";

const EXAMPLES = [
  "How much did I spend this month?",
  "What was my largest expense?",
  "How much did I spend on Food?",
  "Compare my expenses with last month",
];

const formatValue = (value) => {
  if (value == null) return "Not available";
  if (typeof value === "object") return JSON.stringify(value);
  return String(value);
};

export default function FinancialAssistant() {
  const [messages, setMessages] = useState([]);
  const [question, setQuestion] = useState("");
  const [loading, setLoading] = useState(false);
  const [requestError, setRequestError] = useState("");
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth", block: "end" });
  }, [messages, loading]);

  const sendQuestion = async (submittedQuestion = question) => {
    const text = submittedQuestion.trim();
    if (!text || loading) return;

    setQuestion("");
    setRequestError("");
    setMessages((current) => [...current, { role: "user", text }]);
    setLoading(true);
    try {
      const envelope = await FinancialChatService.ask(text);
      const result = envelope?.data;
      setMessages((current) => [...current, {
        role: "assistant",
        text: result?.answer || "I couldn't prepare a response. Please try again.",
        result,
      }]);
    } catch {
      setRequestError("The financial assistant is unavailable right now. Your dashboard is still available.");
      setMessages((current) => [...current, {
        role: "assistant",
        text: "I couldn't reach the financial assistant. Please try again in a moment.",
        error: true,
      }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto flex min-h-[calc(100vh-13rem)] max-w-5xl flex-col gap-6">
      <header className="rounded-3xl border border-[var(--border-base)] bg-[var(--bg-card)] p-6 shadow-premium sm:p-8">
        <div className="flex items-start gap-4">
          <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-primary-500/10 text-primary-600 dark:text-primary-400">
            <Sparkles size={23} />
          </div>
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-primary-600 dark:text-primary-400">Grounded financial chat</p>
            <h1 className="mt-1 text-3xl font-black tracking-tight text-[var(--text-main)]">Financial Assistant</h1>
            <p className="mt-2 max-w-2xl text-sm leading-relaxed text-[var(--text-muted)]">
              Ask about income, expenses, category spending, monthly comparisons, trends, net balance, and your largest transactions. Answers use verified calculations from your account.
            </p>
          </div>
        </div>
      </header>

      <section className="flex min-h-[28rem] flex-1 flex-col overflow-hidden rounded-3xl border border-[var(--border-base)] bg-[var(--bg-card)] shadow-premium" aria-label="Financial assistant conversation">
        <div className="flex items-center justify-between border-b border-[var(--border-base)] px-5 py-4 sm:px-6">
          <div className="flex items-center gap-3">
            <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-emerald-500/10 text-emerald-600"><BadgeCheck size={19} /></span>
            <div><p className="text-sm font-bold text-[var(--text-main)]">Verified answer flow</p><p className="text-xs text-[var(--text-muted)]">Calculations first, grounded explanation second</p></div>
          </div>
          <span className="hidden rounded-full border border-emerald-500/20 bg-emerald-500/5 px-3 py-1 text-[10px] font-bold uppercase tracking-wider text-emerald-700 dark:text-emerald-400 sm:inline-flex">Private to your account</span>
        </div>

        <div className="flex-1 space-y-5 overflow-y-auto p-4 sm:p-6" aria-live="polite" aria-relevant="additions">
          {messages.length === 0 ? (
            <div className="mx-auto flex min-h-72 max-w-xl flex-col items-center justify-center text-center">
              <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-[var(--bg-surface)] text-primary-600 dark:text-primary-400"><Bot size={26} /></div>
              <h2 className="text-lg font-bold text-[var(--text-main)]">What would you like to understand?</h2>
              <p className="mt-2 text-sm text-[var(--text-muted)]">Choose a question or type one below. Unsupported questions receive a safe explanation instead of a guess.</p>
              <div className="mt-5 flex flex-wrap justify-center gap-2">
                {EXAMPLES.map((example) => (
                  <button key={example} type="button" onClick={() => sendQuestion(example)} disabled={loading}
                    className="rounded-full border border-[var(--border-base)] bg-[var(--bg-surface)] px-3 py-2 text-xs font-semibold text-[var(--text-main)] hover:border-primary-400 hover:text-primary-600 disabled:opacity-50">
                    {example}
                  </button>
                ))}
              </div>
            </div>
          ) : messages.map((message, index) => (
            <article key={`${message.role}-${index}`} className={`flex gap-3 ${message.role === "user" ? "flex-row-reverse" : ""}`}>
              <div className={`mt-1 flex h-9 w-9 shrink-0 items-center justify-center rounded-xl ${message.role === "user" ? "bg-primary-600 text-white" : message.error || message.result?.error ? "bg-amber-500/10 text-amber-600" : "bg-emerald-500/10 text-emerald-600"}`}>
                {message.role === "user" ? <UserRound size={18} /> : message.error || message.result?.error ? <AlertTriangle size={18} /> : <Bot size={18} />}
              </div>
              <div className={`max-w-[88%] min-w-0 sm:max-w-[78%] ${message.role === "user" ? "text-right" : ""}`}>
                <p className={`inline-block whitespace-pre-wrap break-words rounded-2xl px-4 py-3 text-left text-sm leading-relaxed ${message.role === "user" ? "rounded-tr-sm bg-primary-600 text-white" : "rounded-tl-sm border border-[var(--border-base)] bg-[var(--bg-surface)] text-[var(--text-main)]"}`}>
                  {message.text}
                </p>
                {message.role === "assistant" && message.result && (
                  <div className="mt-2 space-y-2 text-left">
                    {message.result.calculationType && <p className="text-[10px] font-bold uppercase tracking-wider text-emerald-700 dark:text-emerald-400">Verified calculation · {message.result.calculationType.replaceAll("_", " ")}</p>}
                    {message.result.financialContext?.facts && (
                      <details className="rounded-xl border border-[var(--border-base)] bg-[var(--bg-card)] p-3">
                        <summary className="cursor-pointer text-xs font-semibold text-[var(--text-muted)]">View verified facts</summary>
                        <dl className="mt-3 grid grid-cols-1 gap-x-5 gap-y-2 sm:grid-cols-2">
                          {Object.entries(message.result.financialContext.facts).map(([key, value]) => (
                            <div key={key} className="min-w-0"><dt className="text-[10px] font-semibold uppercase tracking-wide text-[var(--text-muted)]">{key.replaceAll("_", " ")}</dt><dd className="break-words text-xs font-medium text-[var(--text-main)]">{formatValue(value)}</dd></div>
                          ))}
                        </dl>
                        <p className="mt-3 text-[10px] text-[var(--text-muted)]">Source: deterministic financial calculation</p>
                      </details>
                    )}
                    {message.result.model && <p className="text-[10px] text-[var(--text-muted)]">Explained by {message.result.model}{message.result.latencyMs != null ? ` · ${message.result.latencyMs} ms` : ""}</p>}
                    {message.result.error && <p role="alert" className="text-xs text-amber-700 dark:text-amber-400">AI explanation unavailable; verified facts are still shown above.</p>}
                  </div>
                )}
              </div>
            </article>
          ))}
          {loading && (
            <div className="flex items-center gap-3" role="status" aria-live="polite">
              <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-emerald-500/10 text-emerald-600"><Bot size={18} /></span>
              <div className="rounded-2xl rounded-tl-sm border border-[var(--border-base)] bg-[var(--bg-surface)] px-4 py-3 text-sm text-[var(--text-muted)]"><span className="animate-pulse">Checking verified financial data and preparing an explanation…</span></div>
            </div>
          )}
          <div ref={bottomRef} />
        </div>

        {requestError && <p role="alert" className="border-t border-amber-500/20 bg-amber-500/5 px-5 py-2 text-xs text-amber-700 dark:text-amber-300">{requestError}</p>}
        {messages.length > 0 && (
          <div className="flex flex-wrap gap-2 border-t border-[var(--border-base)] px-4 py-3 sm:px-5">
            {EXAMPLES.slice(1, 3).map((example) => <button key={example} type="button" onClick={() => sendQuestion(example)} disabled={loading} className="rounded-full border border-[var(--border-base)] px-3 py-1.5 text-[11px] font-semibold text-[var(--text-muted)] hover:text-primary-600 disabled:opacity-50">{example}</button>)}
          </div>
        )}
        <form onSubmit={(event) => { event.preventDefault(); sendQuestion(); }} className="flex items-end gap-3 border-t border-[var(--border-base)] p-4 sm:p-5">
          <label htmlFor="financial-question" className="sr-only">Ask a financial question</label>
          <textarea id="financial-question" value={question} onChange={(event) => setQuestion(event.target.value)} rows={1} maxLength={500}
            onKeyDown={(event) => { if (event.key === "Enter" && !event.shiftKey) { event.preventDefault(); sendQuestion(); } }}
            placeholder="Ask about your income, spending, or transactions…" disabled={loading}
            className="max-h-32 min-h-12 flex-1 resize-y rounded-2xl border border-[var(--border-base)] bg-[var(--bg-surface)] px-4 py-3 text-sm text-[var(--text-main)] outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-500/15 disabled:opacity-60" />
          <button type="submit" disabled={!question.trim() || loading} aria-label="Send question"
            className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-primary-600 text-white shadow-sm hover:bg-primary-700 disabled:cursor-not-allowed disabled:opacity-45"><Send size={18} /></button>
        </form>
        <p className="px-5 pb-4 text-center text-[10px] text-[var(--text-muted)]">Financial figures come from your verified transaction calculations. The assistant only explains those facts.</p>
      </section>
    </div>
  );
}
