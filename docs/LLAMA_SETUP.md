# 🦙 Llama / Ollama AI Connection Guide — UPIQ AI

This document provides instructions for connecting and configuring **Llama 3** (via local **Ollama** engine) as the natural language financial assistant for **UPIQ AI**.

---

## 🏗️ Architecture & How It Works

UPIQ AI integrates Llama locally to perform bounded financial analysis, voice command interpretation, and natural language transaction assistance without transmitting sensitive user data to external cloud APIs.

```
       ┌───────────────────────────┐
       │   React Voice & Chat UI   │
       └─────────────┬─────────────┘
                     │ REST API (/api/chat)
       ┌─────────────▼─────────────┐
       │    Spring Boot Backend    │
       │  (FinancialChatService)   │
       └─────────────┬─────────────┘
                     │ HTTP POST /api/generate
       ┌─────────────▼─────────────┐
       │    Local Ollama Engine    │
       │    (Llama 3 / 3.1 / 3.2)  │
       └───────────────────────────┘
```

---

## ⚙️ Environment Configuration

Set the following variables in `.env` and `backend/.env`:

```env
# Ollama / Llama Configuration
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama3
OLLAMA_TEMPERATURE=0.0
OLLAMA_TIMEOUT=60s
```

In `backend/src/main/resources/application.yml`:
```yaml
ollama:
  base-url: ${OLLAMA_BASE_URL:http://localhost:11434}
  model: ${OLLAMA_MODEL:llama3}
  temperature: ${OLLAMA_TEMPERATURE:0.0}
  timeout: ${OLLAMA_TIMEOUT:60s}
```

---

## 🚀 Quick Setup Instructions

### Step 1: Install Ollama

Download and install Ollama for your operating system:
- **macOS / Linux / Windows**: Download from [ollama.com](https://ollama.com)

### Step 2: Download & Run Llama 3 Model

Pull the desired Llama model using the Ollama CLI:

```bash
# Recommended Llama 3 model
ollama pull llama3

# Or Llama 3.1 (8B) model
ollama pull llama3.1:8b
```

### Step 3: Verify Llama Connection

Run the project verification script to test connectivity and inference:

```bash
npm run llama:verify
```

---

## 🧪 Integration Testing

Run the full project integration test suite to verify end-to-end endpoint contracts:

```bash
npm run test
```
