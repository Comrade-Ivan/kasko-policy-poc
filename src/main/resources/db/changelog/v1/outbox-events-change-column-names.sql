-- liquibase formatted sql
-- changeset author:rename_outbox_columns
-- comment: Переименование колонок aggregate_id и aggregate_type в outbox_events

-- Rename column aggregate_id to aggregateid
ALTER TABLE outbox_events RENAME COLUMN aggregate_id TO aggregateid;

-- Rename column aggregate_type to aggregatetype
ALTER TABLE outbox_events RENAME COLUMN aggregate_type TO aggregatetype;

-- Update index idx_outbox_events_sent if necessary (optional)
-- If any index references renamed columns, they should be dropped and recreated.
-- In this case indexes are not affected since they don't reference renamed columns.

-- postconditions: проверяем, что колонки были переименованы
-- verify: SELECT * FROM outbox_events LIMIT 0;