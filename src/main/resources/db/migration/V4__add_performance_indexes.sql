-- V4: Performance indexes

-- Soft delete partial indexes (ค้นหาเฉพาะ active records)
CREATE INDEX IF NOT EXISTS idx_branches_not_deleted    ON branches(deleted_at)    WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_categories_not_deleted  ON categories(deleted_at)  WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_products_not_deleted    ON products(deleted_at)    WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_not_deleted       ON users(deleted_at)       WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_customers_not_deleted   ON customers(deleted_at)   WHERE deleted_at IS NULL;

-- Product lookup indexes (cashier scan barcode ทุกครั้ง)
CREATE INDEX IF NOT EXISTS idx_products_barcode ON products(barcode) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_products_sku     ON products(sku)     WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id) WHERE deleted_at IS NULL;

-- Sales query indexes
CREATE INDEX IF NOT EXISTS idx_sales_branch_created   ON sales(branch_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_sales_customer         ON sales(customer_id)  WHERE customer_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_sales_created_at       ON sales(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_sale_items_sale        ON sale_items(sale_id);
CREATE INDEX IF NOT EXISTS idx_payments_sale          ON payments(sale_id);

-- Stock indexes
CREATE INDEX IF NOT EXISTS idx_product_stocks_product ON product_stocks(product_id);
CREATE INDEX IF NOT EXISTS idx_product_stocks_branch  ON product_stocks(branch_id);
CREATE INDEX IF NOT EXISTS idx_stock_movements_product ON stock_movements(product_id, occurred_at DESC);

-- Audit log indexes
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user   ON audit_logs(user_id, created_at DESC);

-- User lookup
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_branch   ON users(branch_id) WHERE deleted_at IS NULL;

-- Customer lookup
CREATE INDEX IF NOT EXISTS idx_customers_phone ON customers(phone) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email) WHERE deleted_at IS NULL;
