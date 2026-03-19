TRUNCATE holiday;
INSERT INTO holiday (name, start_date, end_date, id) VALUES
('Tag der deutschen Einheit', '2026-10-03', '2026-10-03', '123e4567-e89b-12d3-a456-426614174000'),
('Weihnachten', '2026-12-24', '2026-12-24', '123e4567-e89b-12d3-a456-426614174001'),
('Weihnachtsferien', '2026-12-24', '2027-01-08', '123e4567-e89b-12d3-a456-426614174002'),
('Sommerferien', '2026-08-03', '2026-09-14', '123e4567-e89b-12d3-a456-426614174003'),
('Tag der deutschen Erdbeere', '2025-06-03', '2025-06-03', '123e4567-e89b-12d3-a456-426614174004'),
('Weihnachten', '2025-12-24', '2025-12-24', '123e4567-e89b-12d3-a456-426614174005'),
('Weihnachtsferien', '2025-12-24', '2026-01-08', '123e4567-e89b-12d3-a456-426614174006'),
('Sommerferien', '2025-08-03', '2025-09-14', '123e4567-e89b-12d3-a456-426614174007'),
('Tag der deutschen Einheit', '2030-10-03', '2030-10-03', '123e4567-e89b-12d3-a456-426614174008'),
('Weihnachten', '2030-12-24', '2030-12-24', '123e4567-e89b-12d3-a456-426614174009'),
('Weihnachtsferien', '2030-12-24', '2031-01-08', '123e4567-e89b-12d3-a456-426614174010'),
('Sommerferien', '2030-08-03', '2030-09-14', '123e4567-e89b-12d3-a456-426614174011');

truncate equipment;
INSERT INTO equipment (name, description, id) VALUES
    ('Tisch', 'Ein stabiler Holzschreibtisch mit viel Platz für Arbeiten.', '123e4567-e89b-12d3-a456-426614174000'),
    ('Stuhl', 'Ein ergonomischer Bürostuhl mit verstellbarer Höhe.', '123e4567-e89b-12d3-a456-426614174001'),
    ('Whiteboard', 'Ein großes Whiteboard für Präsentationen und Brainstorming.', '123e4567-e89b-12d3-a456-426614174002'),
    ('Projektor', 'Ein Full-HD Projektor für Präsentationen und Filme.', '123e4567-e89b-12d3-a456-426614174003'),
    ('Bücherregal', 'Ein hohes Regal aus Holz zur Aufbewahrung von Büchern und Materialien.', '123e4567-e89b-12d3-a456-426614174004'),
    ('Konferenztisch', 'Ein großer Tisch für Meetings mit Platz für bis zu 12 Personen.', '123e4567-e89b-12d3-a456-426614174005'),
    ('Laptop', 'Ein tragbarer Laptop für mobile Arbeit und Präsentationen.', '123e4567-e89b-12d3-a456-426614174006'),
    ('Kopierer', 'Ein Multifunktionsgerät zum Kopieren, Scannen und Drucken.', '123e4567-e89b-12d3-a456-426614174007');


truncate seating_type;
INSERT INTO seating_type (name, description, id) VALUES
    ('Reihenbestuhlung', 'Beschreibung von Reihenbestuhlung', '123e4567-e89b-12d3-a456-426614174000'),
    ('Stadtrats- / Ausschussbestuhlunq', 'Beschreibung von Stadtrats-Ausschussbestuhlung.', '123e4567-e89b-12d3-a456-426614174001'),
    ('Stehempfang', 'Beschreibung von Stehempfang.', '123e4567-e89b-12d3-a456-426614174002'),
    ('Parlamentarische Bestuhlung', 'Beschreibung von parlamentarische Bestuhlung', '123e4567-e89b-12d3-a456-426614174003');
