create table person (
    id uuid not null,
    name varchar(255) not null,
    telefon_number varchar(255),
    email varchar(255) not null,
    primary key (id)
);

create table internal_person (
    id uuid not null,
    organisation_id varchar(255) not null,
    organisation_unit varchar(255) not null,
    role_function varchar(255) not null,
    primary key (id),
    constraint fk_internal_person foreign key (id) references person (id),
    constraint uk_internal_person_org_id unique (organisation_id)
);

create table external_person (
    id uuid not null,
    company varchar(255),
    street_address varchar(255),
    postal_code_city varchar(255),
    primary key (id),
    constraint fk_external_person foreign key (id) references person (id)
);
