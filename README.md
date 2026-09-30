# ⚡ UPIQ AI — Intelligent Fintech Operating System

[![React](https://img.shields.io/badge/React-19.2.8-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-8.3.0-646CFF?logo=vite&logoColor=white)](https://vitejs.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4.3.3-38B2AC?logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**UPIQ AI** is a state-of-the-art, high-tech fintech application designed to manage UPI payments, track financial ledgers, detect fraud in real-time, forecast budgets, and interact through natural language voice AI.

Built with modern glassmorphism aesthetics, fluid gradient ambient lighting, smooth Framer Motion animations, and full dark/light theme support.

---

## ✨ Key Features

- 💳 **Interactive UPI Payment Hub**: Send money via VPA, phone contacts, or interactive QR scanner with authentic 4/6-digit PIN verification and digital receipt generation.
- 🛡️ **Guardian Anomaly & Fraud Intercept**: Machine learning anomaly detector that flags geo-mismatch transactions, unexpected amounts, and duplicate charges with an instant card freeze drawer.
- 🎙️ **Conversational Voice Assistant**: Native Web Speech API integration enabling hands-free voice commands (*e.g., "How much did I spend on dining this month?"*).
- 🌐 **Global Multi-Currency Engine**: Live real-time currency converter supporting **USD ($), INR (₹), EUR (€), GBP (£), JPY (¥), AED (د.إ), CAD (CA$), and AUD (A$)**.
- 📊 **Predictive Analytics & Charts**: Spending velocity breakdown by category using interactive Recharts line & donut visualizers.
- 📄 **Monthly Reports & PDF Export**: Detailed financial statements with built-in printable/downloadable PDF previews.
- 🔐 **Role-Based Access Control (RBAC)**: Distinct permissions for **Standard Users** and **Super Admins** with secure route protection.
- 🎨 **Fluid Gradient UI & Themes**: Custom glassmorphism design system with responsive layouts and instant Dark/Light mode switching.

---

## 🚀 Quick Start Guide

### Prerequisites

Make sure you have [Node.js](https://nodejs.org/) (v18 or higher) installed on your machine.

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Tanmay5555/Upiq.git
   cd Upiq
   ```

2. **Install Dependencies**:
   ```bash
   npm install
   ```

3. **Start Development Server**:
   ```bash
   npm run dev
   ```
   Open `http://localhost:5173` in your browser.

4. **Build for Production**:
   ```bash
   npm run build
   ```

---

## 🔑 Demo Access Credentials

You can test the application using one-click demo buttons on the login screen or enter the following details:

| Account Type | Email / UPI ID | Role | Features Accessible |
| :--- | :--- | :--- | :--- |
| **Standard User** | `varsha.s@upiq.ai` | `user` | Dashboard, UPI Transfers, AI Assistant, Budgets, Reports, Profile |
| **Super Admin** | `admin@upiq.ai` | `admin` | Full Access + System Health Monitoring & Global Admin Analytics |

---

## 📁 Project Architecture

```
upiq-ai/
├── public/
│   └── upiq-logo.jpg              # High-res 3D metallic brand logo & favicon
├── src/
│   ├── assets/                    # Static image & vector assets
│   ├── components/
│   │   ├── ai/                    # Voice ripple wave components
│   │   ├── common/                # Reusable UI (Button, Card, Badge, Modal, GradientTransition, Toast)
│   │   ├── dashboard/             # UPIPaymentHub, StatCards, Charts, RecentTransactions
│   │   ├── insights/              # FraudAlertDrawer, AnomalyCards
│   │   ├── layout/                # MainLayout, Sidebar, Header, BottomNav
│   │   ├── reports/               # PDFPreviewModal, MonthlyReportCards
│   │   └── transactions/          # AddEditTransactionModal, TransactionTable
│   ├── context/
│   │   ├── FinancialContext.jsx   # Global financial state, multi-currency engine & RBAC
│   │   └── ThemeContext.jsx       # Dark / Light mode context controller
│   ├── hooks/                     # Custom React hooks (useVoiceInput, useTransactions)
│   ├── pages/                     # Dashboard, Transactions, AIAssistant, Insights, Reports, Admin, Profile, Login
│   ├── data/
│   │   └── mockData.js            # Initial dataset, currencies, and test accounts
│   ├── App.jsx                    # Root app routing & animated page controller
│   ├── index.css                  # Global Tailwind v4 CSS design tokens & gradient utilities
│   └── main.jsx                   # Application entry point
├── index.html                     # HTML shell & favicon links
└── package.json                   # Project dependencies & npm scripts
```

---

## 🛠️ Technology Stack

| Component | Technology Used |
| :--- | :--- |
| **Core Framework** | React 19 + Vite |
| **Styling** | Tailwind CSS v4 + Custom Vanilla CSS Utilities |
| **Animations** | Framer Motion (Spring physics & exit transitions) |
| **Data Visualization** | Recharts |
| **Iconography** | Lucide React |
| **Speech Engine** | Web Speech API |

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

Developed with ❤️ for **UPIQ AI**.
