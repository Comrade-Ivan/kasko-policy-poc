-- liquibase formatted sql
-- changeset author:initial_schema
-- comment: Создание начальной схемы БД для системы КАСКО с учетом JPA-моделей

-- Таблица policies (Полисы)
CREATE TABLE IF NOT EXISTS policies (
    policy_id VARCHAR(20) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    issued_at TIMESTAMP,
    start_date DATE NOT NULL,
    end_date DATE,
    premium_amount NUMERIC(19, 2) CHECK (premium_amount >= 0),
    status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(20),
    is_cancelled BOOLEAN DEFAULT FALSE,
    cancellation_reason TEXT,
    s3_pdf_path VARCHAR(255),
    drivers JSONB,
    holder_id BIGINT
);

-- changeset author:initial_schema_2
-- comment: Создание индексов для таблицы policies
CREATE INDEX IF NOT EXISTS idx_policies_status ON policies(status);
CREATE INDEX IF NOT EXISTS idx_policies_created_at ON policies(created_at);
CREATE INDEX IF NOT EXISTS idx_policies_holder_id ON policies(holder_id);

-- Таблица policy_holders (Страхователи)
CREATE TABLE IF NOT EXISTS policy_holders (
    holder_id BIGSERIAL PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(12) NOT NULL,
    email VARCHAR(50)
);

-- changeset author:initial_schema_3
-- comment: Добавление внешнего ключа для связи policy_holders и policies
ALTER TABLE policies
ADD CONSTRAINT fk_policy_holder
FOREIGN KEY (holder_id)
REFERENCES policy_holders(holder_id);

-- Таблица vehicles (Транспортные средства)
CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id BIGSERIAL PRIMARY KEY,
    policy_id VARCHAR(20),
    vin VARCHAR(17) NOT NULL UNIQUE,
    mileage INTEGER CHECK (mileage >= 0),
    actual_value NUMERIC(19, 2) CHECK (actual_value >= 0),
    purchase_date DATE NOT NULL,
    usage_purpose VARCHAR(30),
    registration_number VARCHAR(15),
    CONSTRAINT fk_vehicle_policy FOREIGN KEY (policy_id) REFERENCES policies(policy_id) ON DELETE CASCADE
);

-- changeset author:initial_schema_4
-- comment: Создание индексов для таблицы vehicles
CREATE INDEX IF NOT EXISTS idx_vehicles_policy_id ON vehicles(policy_id);

-- Таблица status_transitions (История статусов)
CREATE TABLE IF NOT EXISTS status_transitions (
    transition_id BIGSERIAL PRIMARY KEY,
    policy_id VARCHAR(20) NOT NULL,
    from_status VARCHAR(50),
    to_status VARCHAR(50) NOT NULL,
    transition_time TIMESTAMP NOT NULL,
    comment TEXT,
    CONSTRAINT fk_transition_policy FOREIGN KEY (policy_id) REFERENCES policies(policy_id) ON DELETE CASCADE
);

-- changeset author:initial_schema_5
-- comment: Создание индексов для таблицы status_transitions
CREATE INDEX IF NOT EXISTS idx_status_transitions_policy_id ON status_transitions(policy_id);
CREATE INDEX IF NOT EXISTS idx_status_transitions_time ON status_transitions(transition_time);

-- Таблица documents (Документы)
CREATE TABLE IF NOT EXISTS documents (
    document_id BIGSERIAL PRIMARY KEY,
    policy_id VARCHAR(20) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(50),
    s3_path VARCHAR(255) NOT NULL,
    upload_time TIMESTAMP NOT NULL,
    CONSTRAINT fk_document_policy FOREIGN KEY (policy_id) REFERENCES policies(policy_id) ON DELETE CASCADE
);

-- changeset author:initial_schema_6
-- comment: Создание индексов для таблицы documents
CREATE INDEX IF NOT EXISTS idx_documents_policy_id ON documents(policy_id);