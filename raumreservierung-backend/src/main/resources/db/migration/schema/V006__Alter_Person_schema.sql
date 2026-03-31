ALTER TABLE person
    ADD COLUMN first_name VARCHAR(255),
    ADD COLUMN last_name VARCHAR(255);

ALTER TABLE person
    DROP COLUMN name;