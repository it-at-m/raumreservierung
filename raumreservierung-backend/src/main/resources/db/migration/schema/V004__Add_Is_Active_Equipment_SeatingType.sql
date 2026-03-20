alter table equipment
    add column is_active boolean not null default true;

alter table seating_type
    add column is_active boolean not null default true;
