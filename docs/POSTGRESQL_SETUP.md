# 🐘 PostgreSQL Setup & Configuration Guide — UPIQ AI

This guide explains how PostgreSQL is integrated into **UPIQ AI**, including environment variables, database schema, initialization scripts, Docker containerization, and local development options.

---

## 📐 Database Overview & Architecture

UPIQ AI uses **PostgreSQL** as its relational database store for users, categories, and financial transactions.

```
       ┌────────────────────────┐
       │   React 19 / Vite UI   │
       └───────────┬────────────┘
                   │ HTTP / REST API
       ┌───────────▼────────────┐
       │   Spring Boot Backend  │
       │   (Spring Data JPA)    │
       └───────────┬────────────┘
                   │ JDBC Driver (org.postgresql.Driver)
       ┌───────────▼────────────┐
       │    PostgreSQL 16/18    │
       │  (Docker or Native)    │
       └────────────────────────┘
```

### Key Database Tables

| Table Name | Primary Key | Description |
| :--- | :--- | :--- |
| `users` | `id` (BIGSERIAL) | Authenticated user profiles, hashed passwords, roles (`ADMIN` / `USER`), and login metadata. |
| `categories` | `id` (BIGSERIAL) | Transaction spending & income categories with icons, colors, and user scoping. |
| `transactions` | `id` (BIGSERIAL) | Ledger entries (amount, category, type, date, payment method) indexed for high performance. |

---

## ⚙️ Environment Configuration

PostgreSQL parameters are defined in `.env` (root) and `backend/.env` as well as `backend/src/main/resources/application.yml`:

```env
# PostgreSQL Database Configuration
POSTGRES_DB=upiq
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
DB_HOST=localhost
DB_PORT=5432
DB_NAME=upiq
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_URL=jdbc:postgresql://localhost:5432/upiq
```

In `application.yml`:
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:upiq}}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```

---

## 🚀 Quick Setup Instructions

### Option 1: Using Docker Compose (Recommended)

Start PostgreSQL containerized with a single command:

```bash
# Start PostgreSQL container in background
npm run db:up

# View database logs
npm run db:logs

# Stop database container
npm run db:down
```

### Option 2: Local Native PostgreSQL

If you have PostgreSQL installed natively on Windows, macOS, or Linux:

1. Ensure the PostgreSQL service is running on port `5432`.
2. Run the database setup script to verify connection and initialize schema:
   ```bash
   npm run db:setup
   ```
3. Alternatively, execute `scripts/init-db.sql` manually in `psql` or PGAdmin:
   ```bash
   psql -U postgres -h localhost -d upiq -f scripts/init-db.sql
   ```

---

## 🔍 Database Verification & Testing

Run the full project integration test suite to verify endpoint contracts, models, and categorization rules:

```bash
npm run test
```
