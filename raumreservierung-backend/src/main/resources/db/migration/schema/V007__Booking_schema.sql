create table booking
(
    id                      uuid         not null,
    booking_state           varchar(255) not null,
    booking_sub_status      varchar(255) not null,
    title                   varchar(255) not null,
    participant_count       int,
    special_seating_request varchar(500),
    catering_needed         boolean default false,
    catering_coordination   varchar(500),
    internal_notes          varchar(2000),
    primary key (id)
);

CREATE TABLE booking_service_times
(
    booking_id uuid        not null,
    title      varchar(32) not null,
    start_time timestamp   not null,
    end_time   timestamp   not null,

    constraint fk_booking_service_times_booking
        foreign key (booking_id)
            references booking (id)
            on delete cascade
);

CREATE INDEX idx_booking_service_times_booking_id ON booking_service_times (booking_id);

CREATE TABLE booking_equipment_list
(
    booking_id        UUID NOT NULL,
    equipment_list_id UUID NOT NULL,

    PRIMARY KEY (booking_id, equipment_list_id),
    FOREIGN KEY (booking_id) REFERENCES booking (id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_list_id) REFERENCES equipment (id) ON DELETE CASCADE
);

