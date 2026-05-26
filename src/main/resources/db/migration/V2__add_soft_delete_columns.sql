-- V2: Add soft delete columns to master tables
-- Affects: branches, categories, products, users, customers

ALTER TABLE branches
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted_by  VARCHAR(100);

ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted_by  VARCHAR(100);

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted_by  VARCHAR(100);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted_by  VARCHAR(100);

ALTER TABLE customers
    ADD COLUMN IF NOT EXISTS deleted_at  TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted_by  VARCHAR(100);
