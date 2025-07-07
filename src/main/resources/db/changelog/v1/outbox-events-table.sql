-- liquibase formatted sql
-- changeset author:initial_schema
-- comment: Создание начальной схемы БД для Outbox Pattern
CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    sent BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_outbox_events_sent ON outbox_events (sent);
CREATE INDEX IF NOT EXISTS idx_outbox_events_created_at ON outbox_events (created_at);