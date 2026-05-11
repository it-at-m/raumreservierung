create table booking
(
    id                uuid         not null,
    title             varchar(255) not null,
    participant_count int,
    catering_needed   boolean default false,
    internal_notes    varchar(500),
    additional_notes  varchar(500),
    occupancy_start   timestamp    not null,
    occupancy_end     timestamp    not null,
    appointment_start timestamp    not null,
    appointment_end   timestamp    not null,
    contact_person_id uuid         not null,
    room_id           uuid,
    recurring_rule    varchar(255),
    primary key (id)
);

CREATE TABLE booking_equipment
(
    booking_id   UUID NOT NULL,
    equipment_id UUID NOT NULL,

    PRIMARY KEY (booking_id, equipment_id),
    FOREIGN KEY (booking_id) REFERENCES booking (id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment (id) ON DELETE CASCADE
);

create table appointment
(
    id                uuid      not null,
    occupancy_start   timestamp not null,
    occupancy_end     timestamp not null,
    appointment_start timestamp not null,
    appointment_end   timestamp not null,
    booking_id        uuid      not null,
    primary key (id),
    foreign key (booking_id) references booking (id) on delete cascade
);



