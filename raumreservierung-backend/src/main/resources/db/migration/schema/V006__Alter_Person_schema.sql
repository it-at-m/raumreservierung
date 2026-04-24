ALTER TABLE person
    ADD COLUMN first_name VARCHAR(255),
    ADD COLUMN last_name  VARCHAR(255),
    ADD COLUMN title      VARCHAR(255);

UPDATE person
SET first_name = split_part(name, ' ', 1),
    last_name  = split_part(name, ' ', -1);

ALTER TABLE person
    DROP COLUMN name;

ALTER TABLE external_person
    ADD COLUMN note VARCHAR(500);