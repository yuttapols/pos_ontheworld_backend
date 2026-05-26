-- V1: Initial schema (baseline — existing tables documented here)
-- This migration is used as baseline for existing databases.
-- New databases will run this to create all tables.

CREATE TABLE IF NOT EXISTS branches (
    id          CHAR(36)    NOT NULL DEFAULT (UUID()) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    address     VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS categories (
    id          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(250),
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users (
    id          UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    role        VARCHAR(50)  NOT NULL,
    branch_id   UUID         REFERENCES branches(id),
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS products (
    id                UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    sku               VARCHAR(255) NOT NULL UNIQUE,
    barcode           VARCHAR(255) NOT NULL UNIQUE,
    name              VARCHAR(255) NOT NULL,
    category_id       UUID         REFERENCES categories(id),
    price             NUMERIC(19,2) NOT NULL,
    cost              NUMERIC(19,2) NOT NULL,
    reorder_threshold INTEGER       NOT NULL DEFAULT 10,
    image_url         VARCHAR(500),
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS customers (
    id             UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    phone          VARCHAR(255) UNIQUE,
    email          VARCHAR(255) UNIQUE,
    loyalty_points INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sales (
    id             UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    receipt_number VARCHAR(255)  NOT NULL UNIQUE,
    customer_id    UUID          REFERENCES customers(id),
    branch_id      UUID          NOT NULL REFERENCES branches(id),
    cashier_id     UUID          NOT NULL REFERENCES users(id),
    sub_total      NUMERIC(19,2) NOT NULL DEFAULT 0,
    discount       NUMERIC(19,2) NOT NULL DEFAULT 0,
    tax            NUMERIC(19,2) NOT NULL DEFAULT 0,
    total          NUMERIC(19,2) NOT NULL DEFAULT 0,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS sale_items (
    id          UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    sale_id     UUID          NOT NULL REFERENCES sales(id),
    product_id  UUID          NOT NULL REFERENCES products(id),
    quantity    INTEGER       NOT NULL,
    unit_price  NUMERIC(19,2) NOT NULL,
    discount    NUMERIC(19,2) NOT NULL DEFAULT 0,
    line_total  NUMERIC(19,2) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payments (
    id        UUID          NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    sale_id   UUID          NOT NULL REFERENCES sales(id),
    method    VARCHAR(100)  NOT NULL,
    amount    NUMERIC(19,2) NOT NULL,
    paid_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS product_stocks (
    id         UUID      NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id UUID      NOT NULL REFERENCES products(id),
    branch_id  UUID      NOT NULL REFERENCES branches(id),
    quantity   INTEGER   NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_product_branch UNIQUE (product_id, branch_id)
);

CREATE TABLE IF NOT EXISTS stock_movements (
    id              UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id      UUID         NOT NULL REFERENCES products(id),
    branch_id       UUID         NOT NULL REFERENCES branches(id),
    quantity_change INTEGER      NOT NULL,
    reason          VARCHAR(500) NOT NULL,
    occurred_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    user_id         UUID         REFERENCES users(id),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    entity_name VARCHAR(255) NOT NULL,
    entity_id   VARCHAR(255) NOT NULL,
    action      VARCHAR(1000) NOT NULL,
    detail      VARCHAR(4000) NOT NULL,
    user_id     UUID         REFERENCES users(id),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
