# UPIQ 2.0 - Unified Personal Income & Query

> **A modern, high-performance personal finance platform with automated statement parsing, intelligent categorization, and real-time analytics.**

UPIQ 2.0 is a complete full-stack financial management solution built for local development, rapid testing, and containerized deployment.

---

## 🏗️ Architecture

UPIQ 2.0 uses a modern unified architecture for high responsiveness, low resource consumption, and straightforward local development:

```
┌─────────────────────────────────────────────────────────┐
│              React 19 Frontend (Vite)                   │
│       Port: 5173 (Dev) / SPA Static Client             │
└────────────────────────────┬────────────────────────────┘
                             │ HTTP / REST (JWT Bearer)
                             ▼
┌─────────────────────────────────────────────────────────┐
│           Unified Spring Boot 3.3.1 Backend             │
│  ┌───────────────┬───────────────────┬───────────────┐  │
│  │  Auth Service │Transaction Service│Category Service│ │
│  │ (BCrypt/JJWT) │  (JPA / Ledger)   │(Custom Types) │  │
│  ├───────────────┴───────────────────┴───────────────┤  │
│  │    Intelligent Heuristic PDF & CSV Statement      │  │
│  │           Parsing Engine (Apache PDFBox)          │  │
│  └───────────────────────────────────────────────────┘  │
│                     Port: 8080                          │
└────────────────────────────┬────────────────────────────┘
                             │ JDBC
                             ▼
┌─────────────────────────────────────────────────────────┐
│              Local PostgreSQL 16 Database               │
│                     Port: 5432                          │
└─────────────────────────────────────────────────────────┘
```

---

## 📁 Repository Structure

```text
UPIQ-2.0/
├── .env.example              # Central environment variable template
├── .gitignore                # Git ignore rules for secrets and build artifacts
├── docker-compose.yml        # Multi-container local orchestration (Postgres + Backend)
├── README.md                 # Project documentation
│
├── backend/                  # Unified Spring Boot 3.3.1 Backend
│   ├── src/                  # Java source code and resources
│   │   ├── main/java/com/upiq/
│   │   │   ├── auth/         # Authentication and user management
│   │   │   ├── category/     # Category and budget management
│   │   │   ├── config/       # Security, CORS, and web configuration
│   │   │   ├── pdf/          # PDF/CSV statement parsing engine
│   │   │   └── transaction/  # Ledger transaction tracking
│   │   └── main/resources/   # application.yml & application-local.yml
│   ├── Dockerfile            # Multi-stage JDK 21 container build
│   ├── mvnw / mvnw.cmd       # Maven Wrapper
│   └── pom.xml               # Maven project descriptor
│
├── frontend/                 # React 19 + Vite + Tailwind CSS Frontend
│   ├── public/               # Static assets & icons
│   ├── src/                  # React components, pages, context, and hooks
│   ├── Dockerfile            # Container definition for frontend
│   ├── package.json          # Node dependencies and scripts
│   └── vite.config.js        # Vite configuration
│
├── docker/                   # Docker deployment configurations
│   └── docker-compose.yml
│
└── docs/                     # Documentation, architecture reports, screenshots
    ├── architecture_report.md
    └── Screenshots/
```

---

## ⚙️ Prerequisites

- **Java Development Kit (JDK)**: 21 (LTS recommended)
- **Node.js**: v18 or higher (Node 20+ recommended) & npm
- **Database**: PostgreSQL 16 (or Docker)
- **Docker & Docker Compose**: Optional (recommended for instant database setup)

---

## 🔐 Environment Variables

Copy `.env.example` to create your local `.env` files:

```bash
# Central / Docker configuration
cp .env.example .env

# Backend configuration
cp backend/.env.example backend/.env

# Frontend configuration
cp frontend/.env.example frontend/.env.local
```

### Key Environment Variables

| Variable | Default | Purpose |
| :--- | :--- | :--- |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `upiq` | PostgreSQL database name |
| `DB_USERNAME` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |
| `DB_URL` | `jdbc:postgresql://localhost:5432/upiq` | Full JDBC database connection string |
| `SERVER_PORT` | `8080` | Spring Boot backend server port |
| `SPRING_PROFILES_ACTIVE` | `local` | Spring profile (`local`, `prod`, `research`) |
| `JWT_SECRET` | *Dev Key* | Min 256-bit secret for signing JWT auth tokens |
| `VITE_API_BASE_URL` | `http://localhost:8080/api` | Backend API URL for frontend |

---

## 🚀 Local Setup & Startup

### Step 1: Start the Local Database

#### Option A: Using Docker (Recommended)

Start a clean local PostgreSQL container with one command:

```bash
docker compose up -d postgres
```

#### Option B: Using a Native PostgreSQL Instance

Create the database in your local PostgreSQL:

```sql
CREATE DATABASE upiq;
```

---

### Step 2: Start the Backend Service

Navigate to the `backend/` directory and run:

```bash
cd backend

# Using Maven Wrapper (Windows PowerShell / CMD)
.\mvnw.cmd spring-boot:run

# Using Maven Wrapper (Linux / macOS)
./mvnw spring-boot:run

# Or with system Maven
mvn spring-boot:run
```

The backend starts at: **`http://localhost:8080`**  
API health check endpoint: **`http://localhost:8080/health`**

---

### Step 3: Start the Frontend Application

In a separate terminal, navigate to the `frontend/` directory:

```bash
cd frontend

# Install dependencies
npm install

# Start Vite development server
npm run dev
```

The frontend application opens at: **`http://localhost:5173`**

---

## 🐳 Full Stack Docker Setup

To run the complete application (PostgreSQL + Backend) in Docker containers:

```bash
docker compose up -d --build
```

To stop all containers:

```bash
docker compose down
```

---

## 🧪 Testing and Verification

### Frontend Build
```bash
cd frontend
npm run build
```

### Backend Build
```bash
cd backend
mvn clean package -DskipTests
```

---

## 🔒 Security & Local Isolation

- **No Remote Dependencies**: All database connections and API endpoints default to `localhost`.
- **Zero Committed Secrets**: `.gitignore` prevents `.env` and credential files from being committed.
- **Service Ownership Validation**: IDOR protection is enforced across all transaction and category operations.
