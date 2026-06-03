CREATE TABLE room
(
    id                   UUID         NOT NULL,
    name                 VARCHAR(100) NOT NULL,
    number               VARCHAR(100) NOT NULL,
    location             VARCHAR(255),
    location_description VARCHAR(500),
    capacity             INT,
    is_active            BOOLEAN DEFAULT TRUE,
    area                 INT,
    contact_person_id    UUID,
    picture              bytea,

    PRIMARY KEY (id)
);

CREATE TABLE room_equipment
(
    room_id      UUID NOT NULL,
    equipment_id UUID NOT NULL,

    PRIMARY KEY (room_id, equipment_id),
    FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment (id) ON DELETE CASCADE
);

CREATE TABLE room_seating_capacity
(
    id              UUID    NOT NULL,
    seating_type_id UUID    NOT NULL,
    capacity        INTEGER NOT NULL,
    room_id         UUID    NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE CASCADE
);