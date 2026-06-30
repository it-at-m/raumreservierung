ALTER TABLE room_seating_capacity
    ADD CONSTRAINT uq_room_seating_type UNIQUE (room_id, seating_type_id)