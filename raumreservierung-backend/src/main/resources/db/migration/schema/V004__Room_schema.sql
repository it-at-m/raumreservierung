CREATE TABLE room
(
    id           UUID         NOT NULL,
    name         VARCHAR(100) NOT NULL,
    number       VARCHAR(10),
    address      VARCHAR(255),
    capacity     INT,
    information  VARCHAR(1000),
    note         VARCHAR(1000),
    availability BOOLEAN,
    area         INT,

    PRIMARY KEY (id)
);

CREATE TABLE room_seating_type
(
    room_id    UUID NOT NULL,
    seating_type_id UUID NOT NULL,

    PRIMARY KEY (room_id, seating_type_id),
    FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE CASCADE,
    FOREIGN KEY (seating_type_id) REFERENCES seating_type (id) ON DELETE CASCADE
);

CREATE TABLE room_equipment
(
    room_id      UUID NOT NULL,
    equipment_id UUID NOT NULL,

    PRIMARY KEY (room_id, equipment_id),
    FOREIGN KEY (room_id) REFERENCES room (id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipment (id) ON DELETE CASCADE
);