ALTER TABLE person
    ADD COLUMN first_name VARCHAR(255),
    ADD COLUMN last_name VARCHAR(255);

ALTER TABLE person
    DROP COLUMN name;

ALTER TABLE external_person
    ADD COLUMN note VARCHAR(500),
    ADD COLUMN title    VARCHAR(255);
