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


truncate seating_type;
INSERT INTO seating_type (name, description, id) VALUES
    ('Reihenbestuhlung', 'Beschreibung von Reihenbestuhlung', '123e4567-e89b-12d3-a456-426614174000'),
    ('Stadtrats- / Ausschussbestuhlunq', 'Beschreibung von Stadtrats-Ausschussbestuhlung.', '123e4567-e89b-12d3-a456-426614174001'),
    ('Stehempfang', 'Beschreibung von Stehempfang.', '123e4567-e89b-12d3-a456-426614174002'),
    ('Parlamentarische Bestuhlung', 'Beschreibung von parlamentarische Bestuhlung', '123e4567-e89b-12d3-a456-426614174003');

truncate person cascade;
insert into person (id, name, telefon_number, email) values
    ('123e4567-e89b-12d3-a456-426614174010', 'Max Mustermann', '089-233-12345', 'max.mustermann@muenchen.de'),
    ('123e4567-e89b-12d3-a456-426614174011', 'Anna Schmidt', '089-233-54321', 'anna.schmidt@muenchen.de'),
    ('123e4567-e89b-12d3-a456-426614174012', 'Tom Exzellent', '0171-9876543', 'tom@beispiel-it-gmbh.de'),
    ('123e4567-e89b-12d3-a456-426614174013', 'Lisa Meyer', null, 'lisa.meyer@externe-berater.de');

insert into internal_person (id, organisation_id, organisation_unit, function) values
    ('123e4567-e89b-12d3-a456-426614174010', 'ORG-RIT-001', 'it@M', 'Administrator'),
    ('123e4567-e89b-12d3-a456-426614174011', 'ORG-KVR-002', 'Kreisverwaltungsreferat', 'Sachbearbeiterin');

insert into external_person (id, company, street_address, postal_code_city) values
    ('123e4567-e89b-12d3-a456-426614174012', 'Beispiel IT GmbH', 'Marienplatz 8', '80331 München'),
    ('123e4567-e89b-12d3-a456-426614174013', 'Externe Berater AG', 'Consultingweg 42', '80807 München');
