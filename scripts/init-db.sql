-- ========================================================
-- UPIQ AI - PostgreSQL Database Initialization & Schema
-- ========================================================

-- Create Database (Run as superuser if db does not exist)
-- CREATE DATABASE upiq;

-- Ensure extension for UUID generation if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- --------------------------------------------------------
-- 1. Users Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    last_login_ip VARCHAR(255),
    last_login_at TIMESTAMP
);

-- --------------------------------------------------------
-- 2. Categories Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    color VARCHAR(50),
    icon VARCHAR(50),
    user_id BIGINT NOT NULL DEFAULT 1
);

-- --------------------------------------------------------
-- 3. Transactions Table
-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    type VARCHAR(50) NOT NULL,
    category VARCHAR(255) NOT NULL,
    description TEXT,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(100)
);

-- Indexes for high-performance transaction queries
CREATE INDEX IF NOT EXISTS idx_tx_user_date ON transactions (user_id, date);
CREATE INDEX IF NOT EXISTS idx_tx_user_type_date ON transactions (user_id, type, date);
CREATE INDEX IF NOT EXISTS idx_tx_user_cat_date ON transactions (user_id, category, date);

-- --------------------------------------------------------
-- 4. Initial Seed Data (Demo Users & Default Categories)
-- --------------------------------------------------------
INSERT INTO users (email, username, password, full_name, role, active)
VALUES 
    ('admin@upiq.ai', 'admin', '$2a$10$e7W5O7s.3d/vOqg5sY9x0.eXyG1h0ZJ8pE0.3d/vOqg5sY9x0.eXy', 'Super Admin', 'ADMIN', TRUE),
    ('varsha.s@upiq.ai', 'varsha', '$2a$10$e7W5O7s.3d/vOqg5sY9x0.eXyG1h0ZJ8pE0.3d/vOqg5sY9x0.eXy', 'Varsha S', 'USER', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO categories (name, type, description, color, icon, user_id)
VALUES
    ('Food & Dining', 'EXPENSE', 'Restaurants, Groceries, and Delivery', '#EF4444', 'utensils', 1),
    ('Transport & Fuel', 'EXPENSE', 'Uber, Fuel, Public Transit', '#F59E0B', 'car', 1),
    ('Shopping', 'EXPENSE', 'E-commerce, Clothing, Electronics', '#8B5CF6', 'shopping-bag', 1),
    ('Bills & Utilities', 'EXPENSE', 'Electricity, Water, Mobile, Internet', '#3B82F6', 'zap', 1),
    ('Entertainment', 'EXPENSE', 'Movies, Streaming Services, Games', '#EC4899', 'film', 1),
    ('Salary', 'INCOME', 'Monthly Employer Salary Deposit', '#10B981', 'dollar-sign', 1),
    ('Investments', 'INCOME', 'Mutual Funds, Stocks, Dividends', '#06B6D4', 'trending-up', 1)
ON CONFLICT DO NOTHING;
