truncate equipment, seating_type, room, room_seating_type, room_equipment;

INSERT INTO equipment (name, description, id) VALUES
    ('Tisch', 'Ein stabiler Holzschreibtisch mit viel Platz für Arbeiten.', '123e4567-e89b-12d3-a456-426614174000'),
    ('Stuhl', 'Ein ergonomischer Bürostuhl mit verstellbarer Höhe.', '123e4567-e89b-12d3-a456-426614174001'),
    ('Whiteboard', 'Ein großes Whiteboard für Präsentationen und Brainstorming.', '123e4567-e89b-12d3-a456-426614174002'),
    ('Projektor', 'Ein Full-HD Projektor für Präsentationen und Filme.', '123e4567-e89b-12d3-a456-426614174003'),
    ('Bücherregal', 'Ein hohes Regal aus Holz zur Aufbewahrung von Büchern und Materialien.', '123e4567-e89b-12d3-a456-426614174004'),
    ('Konferenztisch', 'Ein großer Tisch für Meetings mit Platz für bis zu 12 Personen.', '123e4567-e89b-12d3-a456-426614174005'),
    ('Laptop', 'Ein tragbarer Laptop für mobile Arbeit und Präsentationen.', '123e4567-e89b-12d3-a456-426614174006'),
    ('Kopierer', 'Ein Multifunktionsgerät zum Kopieren, Scannen und Drucken.', '123e4567-e89b-12d3-a456-426614174007');


INSERT INTO seating_type (name, description, id) VALUES
    ('Reihenbestuhlung', 'Beschreibung von Reihenbestuhlung', '123e4567-e89b-12d3-a456-426614174000'),
    ('Stadtrats- / Ausschussbestuhlunq', 'Beschreibung von Stadtrats-Ausschussbestuhlung.', '123e4567-e89b-12d3-a456-426614174001'),
    ('Stehempfang', 'Beschreibung von Stehempfang.', '123e4567-e89b-12d3-a456-426614174002'),
    ('Parlamentarische Bestuhlung', 'Beschreibung von parlamentarische Bestuhlung', '123e4567-e89b-12d3-a456-426614174003');

INSERT INTO room (name, number, address, capacity, information, note, availability, area, id) VALUES
    ('Großer Saal', 100, 'Straße 1, 12345 Irgendwo, Deutschland', 1000, 'Ein großer Saal mit Stühlen und Tischen und Reihenbestuhlung und Stadtrats- / Ausschussbestuhlunq.', 'Kleiner Fleck rechts hinten im Eck.', true, 200, '123e4567-e89b-12d3-a456-426614175000'),
    ('Kleiner Saal', 101, 'Weg 2, 56789 Woanders, Deutschland', 1000, 'Ein kleiner Saal mit Projektor und Whiteboard und Stehempfang und Parlamentarische Bestuhlung.', 'Großer Fleck links vorne im Eck.', false, 20, '123e4567-e89b-12d3-a456-426614175001');


INSERT INTO room_seating_type (room_id, seating_type_id) VALUES
    ('123e4567-e89b-12d3-a456-426614175000', '123e4567-e89b-12d3-a456-426614174000'),
    ('123e4567-e89b-12d3-a456-426614175000', '123e4567-e89b-12d3-a456-426614174001'),
    ('123e4567-e89b-12d3-a456-426614175001', '123e4567-e89b-12d3-a456-426614174002'),
    ('123e4567-e89b-12d3-a456-426614175001', '123e4567-e89b-12d3-a456-426614174003');

INSERT INTO room_equipment (room_id, equipment_id) VALUES
    ('123e4567-e89b-12d3-a456-426614175000', '123e4567-e89b-12d3-a456-426614174000'),
    ('123e4567-e89b-12d3-a456-426614175000', '123e4567-e89b-12d3-a456-426614174001'),
    ('123e4567-e89b-12d3-a456-426614175001', '123e4567-e89b-12d3-a456-426614174002'),
    ('123e4567-e89b-12d3-a456-426614175001', '123e4567-e89b-12d3-a456-426614174003');

