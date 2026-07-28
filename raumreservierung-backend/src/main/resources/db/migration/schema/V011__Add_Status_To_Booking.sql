ALTER TABLE booking
    ADD COLUMN status VARCHAR NOT NULL DEFAULT 'NEW',
    ADD COLUMN reason_for_status_change VARCHAR(255);