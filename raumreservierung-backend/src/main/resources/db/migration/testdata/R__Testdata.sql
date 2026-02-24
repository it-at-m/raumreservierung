truncate the_entity;

INSERT INTO the_entity (text_attribute, id) VALUES
('Alpha', '123e4567-e89b-12d3-a456-426614174000'),
('Bravo', '123e4567-e89b-12d3-a456-426614174001'),
('Charlie', '123e4567-e89b-12d3-a456-426614174002'),
('Delta', '123e4567-e89b-12d3-a456-426614174003'),
('Echo', '123e4567-e89b-12d3-a456-426614174004');

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
