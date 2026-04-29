TRUNCATE holiday;
INSERT INTO holiday (name, start_date, end_date, id)
VALUES ('Tag der deutschen Einheit', '2026-10-03', '2026-10-03', '123e4567-e89b-12d3-a456-426614174000'),
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

truncate equipment cascade;
INSERT INTO equipment (is_active, name, description, id)
VALUES (true, 'Tisch', 'Ein stabiler Holzschreibtisch mit viel Platz für Arbeiten.', '123e4567-e89b-12d3-a456-426614174000'),
       (true, 'Stuhl', 'Ein ergonomischer Bürostuhl mit verstellbarer Höhe.', '123e4567-e89b-12d3-a456-426614174001'),
       (true, 'Whiteboard', 'Ein großes Whiteboard für Präsentationen und Brainstorming.', '123e4567-e89b-12d3-a456-426614174002'),
       (true, 'Projektor', 'Ein Full-HD Projektor für Präsentationen und Filme.', '123e4567-e89b-12d3-a456-426614174003'),
       (true, 'Bücherregal', 'Ein hohes Regal aus Holz zur Aufbewahrung von Büchern und Materialien.', '123e4567-e89b-12d3-a456-426614174004'),
       (true, 'Konferenztisch', 'Ein großer Tisch für Meetings mit Platz für bis zu 12 Personen.', '123e4567-e89b-12d3-a456-426614174005'),
       (true, 'Laptop', 'Ein tragbarer Laptop für mobile Arbeit und Präsentationen.', '123e4567-e89b-12d3-a456-426614174006'),
       (true, 'Kopierer', 'Ein Multifunktionsgerät zum Kopieren, Scannen und Drucken.', '123e4567-e89b-12d3-a456-426614174007');


truncate seating_type;
INSERT INTO seating_type (is_active, name, description, id)
VALUES (true, 'Reihenbestuhlung', 'Beschreibung von Reihenbestuhlung', '123e4567-e89b-12d3-a456-426614174000'),
       (true, 'Stadtrats- / Ausschussbestuhlunq', 'Beschreibung von Stadtrats-Ausschussbestuhlung.', '123e4567-e89b-12d3-a456-426614174001'),
       (true, 'Stehempfang', 'Beschreibung von Stehempfang.', '123e4567-e89b-12d3-a456-426614174002'),
       (true, 'Parlamentarische Bestuhlung', 'Beschreibung von parlamentarische Bestuhlung', '123e4567-e89b-12d3-a456-426614174003');

truncate person cascade;
insert into person (id, title, first_name, last_name, telefon_number, email)
values
    -- Deine bisherigen:
    ('123e4567-e89b-12d3-a456-426614174010', null, 'Max', 'Mustermann', '089-233-12345', 'max.mustermann@muenchen.de'),
    ('123e4567-e89b-12d3-a456-426614174011', null, 'Anna', 'Schmidt', '089-233-54321', 'anna.schmidt@muenchen.de'),
    ('123e4567-e89b-12d3-a456-426614174012', 'MR', 'Tom', 'Exzellent', '0171-9876543', 'tom@beispiel-it-gmbh.de'),
    ('123e4567-e89b-12d3-a456-426614174013', 'MS', 'Lisa', 'Meyer', null, 'lisa.meyer@externe-berater.de'),
    -- Neue externe Personen:
    ('123e4567-e89b-12d3-a456-426614174014', 'MR', 'Felix', 'Wagner', '0160-1122334', 'f.wagner@tech-muc.de'),
    ('123e4567-e89b-12d3-a456-426614174015', 'MS', 'Sarah', 'Klein', '0151-9988776', 'sklein@agile-coaches.de'),
    ('123e4567-e89b-12d3-a456-426614174016', 'MR', 'Michael', 'Bauer', null, 'mbauer@bauplanung-sued.de'),
    ('123e4567-e89b-12d3-a456-426614174017', 'NONE', 'Julia', 'Richter', '089-5551234', 'info@richter-legal.com'),
    ('123e4567-e89b-12d3-a456-426614174018', 'MS', 'David', 'Becker', '0172-3344556', 'd.becker@cloud-architects.io'),
    ('123e4567-e89b-12d3-a456-426614174019', 'MR', 'Elena', 'Weber', null, 'elena.weber@design-studio-muc.de'),
    ('123e4567-e89b-12d3-a456-426614174020', 'MR', 'Lukas', 'Hoffmann', '0162-4455667', 'l.hoffmann@event-pro.de'),
    ('123e4567-e89b-12d3-a456-426614174021', 'NONE', 'Sophie', 'Neumann', '089-7778889', 's.neumann@audit-partners.de'),
    ('123e4567-e89b-12d3-a456-426614174022', 'DIVERSE', 'Tim', 'Krüger', '0152-1122334', 'krueger@it-sec-consult.de'),
    ('123e4567-e89b-12d3-a456-426614174023', 'DIVERSE', 'Laura', 'Zimmermann', null, 'lz@zimmermann-pr.de');

-- Hier kommen jetzt die spezifischen externen Daten (verknüpft über die gleiche ID):
insert into external_person (id, company, street_address, postal_code_city, note)
values
    -- Deine bisherigen:
    ('123e4567-e89b-12d3-a456-426614174012', 'Beispiel IT GmbH', 'Marienplatz 8', '80331 München', 'Vollständig in die LHM-Systemlandschaft (z.B. SAP, Wilma) eingearbeitet und unterstützt die technische Umsetzung der OZG-Vorgaben.'),
    ('123e4567-e89b-12d3-a456-426614174013', 'Externe Berater AG', 'Consultingweg 42', '80807 München', null),
    -- Neue externe Firmen:
    ('123e4567-e89b-12d3-a456-426614174014', 'Tech MUC GmbH', 'Leopoldstraße 12', '80802 München', 'Sichert durch kontinuierliche Dokumentation den Know-how-Transfer an die internen Projektmitglieder in den Referaten.'),
    ('123e4567-e89b-12d3-a456-426614174015', 'Agile Coaches DE', 'Rosenheimer Str. 145', '81671 München', 'Optimiert agile städtische Prozesse.'),
    ('123e4567-e89b-12d3-a456-426614174016', 'Bauplanung Süd', 'Sendlinger Tor Platz 1', '80336 München', 'Verfügt über tiefgehende Kenntnisse der Münchner Bauordnung (BayBO) und unterstützt maßgeblich bei komplexen Genehmigungsverfahren.'),
    ('123e4567-e89b-12d3-a456-426614174017', 'Richter Legal Kanzlei', 'Nymphenburger Str. 4', '80335 München', 'Ist umfassend in die Sicherheitsrichtlinien und Datenschutzprotokolle (BayDSG) der Stadtverwaltung eingewiesen.'),
    ('123e4567-e89b-12d3-a456-426614174018', 'Cloud Architects IO', 'Arnulfstraße 21', '80335 München', 'Selten verfügbar.'),
    ('123e4567-e89b-12d3-a456-426614174019', 'Design Studio MUC', 'Schellingstraße 109', '80798 München', null),
    ('123e4567-e89b-12d3-a456-426614174020', 'Event Pro', 'Gärtnerplatz 2', '80469 München', null),
    ('123e4567-e89b-12d3-a456-426614174021', 'Audit Partners AG', 'Max-Joseph-Straße 5', '80333 München', null),
    ('123e4567-e89b-12d3-a456-426614174022', 'IT Sec Consult', 'Oskar-von-Miller-Ring 20', '80333 München', null),
    ('123e4567-e89b-12d3-a456-426614174023', 'Zimmermann PR', 'Kaufingerstraße 15', '80331 München', 'Die PR-Agentur fungiert als strategischer Partner bei der Kommunikation komplexer Infrastrukturprojekte der Stadt München. Durch ihre Expertise in der Bürgerbeteiligung und Krisenkommunikation stellt sie sicher, dass Stadtratsbeschlüsse transparent über alle städtischen Kanäle vermittelt werden. Besonders hervorzuheben ist die professionelle Steuerung der Pressearbeit sowie die Erstellung multilingualer Kampagnen, die die Akzeptanz für Großprojekte im urbanen Raum nachhaltig fördern.');
insert into internal_person (id, organisation_id, organisation_unit, role_function)
values ('123e4567-e89b-12d3-a456-426614174010', 'ORG-RIT-001', 'it@M', 'Administrator'),
       ('123e4567-e89b-12d3-a456-426614174011', 'ORG-KVR-002', 'Kreisverwaltungsreferat', 'Sachbearbeiterin');

truncate room cascade;
insert into room (id, name, number, location, location_description, capacity, is_active, area, contact_person_id)
values ('770e8400-e29b-41d4-a716-446655440001', 'Großer Sitzungssaal', '101', 'Hauptgebäude, 1. OG', 'Direkt neben dem Aufzug', 50, true, 85, '123e4567-e89b-12d3-a456-426614174010'),
       ('770e8400-e29b-41d4-a716-446655440002', 'Besprechungszimmer Blau', '204', 'Nebengebäude A, 2. OG', 'Am Ende des Flurs links', 12, true, 30, '123e4567-e89b-12d3-a456-426614174011'),
       ('770e8400-e29b-41d4-a716-446655440003', 'Kreativ-Raum', '005', 'Untergeschoss', 'Industrie-Look, schallisoliert', 20, true, 45, '123e4567-e89b-12d3-a456-426614174012'),
       ('770e8400-e29b-41d4-a716-446655440004', 'Schulungsraum IT', '310', 'Hauptgebäude, 3. OG', 'Mit fest installierten PCs', 30, false, 60, '123e4567-e89b-12d3-a456-426614174013');

truncate room_equipment;
insert into room_equipment (room_id, equipment_id)
values ('770e8400-e29b-41d4-a716-446655440001', '123e4567-e89b-12d3-a456-426614174000'),
       ('770e8400-e29b-41d4-a716-446655440001', '123e4567-e89b-12d3-a456-426614174001'),
       ('770e8400-e29b-41d4-a716-446655440001', '123e4567-e89b-12d3-a456-426614174002'),
       ('770e8400-e29b-41d4-a716-446655440001', '123e4567-e89b-12d3-a456-426614174003'),
       ('770e8400-e29b-41d4-a716-446655440002', '123e4567-e89b-12d3-a456-426614174001'),
       ('770e8400-e29b-41d4-a716-446655440002', '123e4567-e89b-12d3-a456-426614174005'),
       ('770e8400-e29b-41d4-a716-446655440002', '123e4567-e89b-12d3-a456-426614174007'),
       ('770e8400-e29b-41d4-a716-446655440003', '123e4567-e89b-12d3-a456-426614174005'),
       ('770e8400-e29b-41d4-a716-446655440003', '123e4567-e89b-12d3-a456-426614174006'),
       ('770e8400-e29b-41d4-a716-446655440003', '123e4567-e89b-12d3-a456-426614174007'),
       ('770e8400-e29b-41d4-a716-446655440004', '123e4567-e89b-12d3-a456-426614174002'),
       ('770e8400-e29b-41d4-a716-446655440004', '123e4567-e89b-12d3-a456-426614174004'),
       ('770e8400-e29b-41d4-a716-446655440004', '123e4567-e89b-12d3-a456-426614174006');

truncate room_seating_capacity;
insert into room_seating_capacity (id, seating_type_id, capacity, room_id)
values ('550e8400-e29b-41d4-a716-446655449000', '123e4567-e89b-12d3-a456-426614174000', 100, '770e8400-e29b-41d4-a716-446655440001'),
       ('550e8400-e29b-41d4-a716-446655449001', '123e4567-e89b-12d3-a456-426614174001', 200, '770e8400-e29b-41d4-a716-446655440001'),
       ('550e8400-e29b-41d4-a716-446655449002', '123e4567-e89b-12d3-a456-426614174002', 300, '770e8400-e29b-41d4-a716-446655440001'),
       ('550e8400-e29b-41d4-a716-446655449003', '123e4567-e89b-12d3-a456-426614174003', 400, '770e8400-e29b-41d4-a716-446655440002'),
       ('550e8400-e29b-41d4-a716-446655449004', '123e4567-e89b-12d3-a456-426614174000', 500, '770e8400-e29b-41d4-a716-446655440002'),
       ('550e8400-e29b-41d4-a716-446655449005', '123e4567-e89b-12d3-a456-426614174001', 600, '770e8400-e29b-41d4-a716-446655440002'),
       ('550e8400-e29b-41d4-a716-446655449006', '123e4567-e89b-12d3-a456-426614174002', 700, '770e8400-e29b-41d4-a716-446655440003'),
       ('550e8400-e29b-41d4-a716-446655449007', '123e4567-e89b-12d3-a456-426614174003', 800, '770e8400-e29b-41d4-a716-446655440003'),
       ('550e8400-e29b-41d4-a716-446655449008', '123e4567-e89b-12d3-a456-426614174000', 900, '770e8400-e29b-41d4-a716-446655440003'),
       ('550e8400-e29b-41d4-a716-446655449009', '123e4567-e89b-12d3-a456-426614174001', 111, '770e8400-e29b-41d4-a716-446655440004'),
       ('550e8400-e29b-41d4-a716-446655449010', '123e4567-e89b-12d3-a456-426614174002', 222, '770e8400-e29b-41d4-a716-446655440004'),
       ('550e8400-e29b-41d4-a716-446655449011', '123e4567-e89b-12d3-a456-426614174003', 333, '770e8400-e29b-41d4-a716-446655440004');

truncate booking cascade;
insert into booking (id, room_id, title, participant_count, catering_needed, internal_notes, additional_notes, occupancy_start, occupancy_end, appointment_start, appointment_end, contact_person_id)
values ('550e8400-e29b-41d4-a716-446655440011', '770e8400-e29b-41d4-a716-446655440001', 'Team-Meeting', 12, true, 'Kaffee bereitstellen', 'Standard-Setup', '2024-05-10 08:30:00', '2024-05-10 11:30:00', '2024-05-10 09:00:00', '2024-05-10 11:00:00', '123e4567-e89b-12d3-a456-426614174010'),
       ('550e8400-e29b-41d4-a716-446655440012', '770e8400-e29b-41d4-a716-446655440002', 'Projekt-Kickoff', 25, true, 'Beamer testen', 'Zusatzstühle', '2024-05-12 09:00:00', '2024-05-12 17:00:00', '2024-05-12 10:00:00', '2024-05-12 16:00:00', '123e4567-e89b-12d3-a456-426614174011'),
       ('550e8400-e29b-41d4-a716-446655440013', '770e8400-e29b-41d4-a716-446655440001', 'Vorstandssitzung', 8, true, 'VIP-Service', 'Wasser/Saft', '2024-05-15 13:30:00', '2024-05-15 18:30:00', '2024-05-15 14:00:00', '2024-05-15 18:00:00', '123e4567-e89b-12d3-a456-426614174012'),
       ('550e8400-e29b-41d4-a716-446655440014', '770e8400-e29b-41d4-a716-446655440003', 'IT-Security Workshop', 15, false, 'LAN-Anschluss', 'Eigene Laptops', '2024-05-16 07:45:00', '2024-05-16 17:15:00', '2024-05-16 08:00:00', '2024-05-16 17:00:00', '123e4567-e89b-12d3-a456-426614174013'),
       ('550e8400-e29b-41d4-a716-446655440015', '770e8400-e29b-41d4-a716-446655440001', 'Mittagessen Team Blau', 20, true, 'Buffet Nebenraum', 'Vegetarisch', '2024-05-17 11:45:00', '2024-05-17 14:00:00', '2024-05-17 12:00:00', '2024-05-17 13:30:00', '123e4567-e89b-12d3-a456-426614174014'),
       ('550e8400-e29b-41d4-a716-446655440016', '770e8400-e29b-41d4-a716-446655440004', 'Bürgersprechstunde', 1, false, 'Diskretion', 'Barrierefrei', '2024-05-20 10:00:00', '2024-05-20 12:00:00', '2024-05-20 10:00:00', '2024-05-20 12:00:00', '123e4567-e89b-12d3-a456-426614174015'),
       ('550e8400-e29b-41d4-a716-446655440017', '770e8400-e29b-41d4-a716-446655440002', 'Abteilungsleiter-Runde', 5, false, 'Protokoll XY', NULL, '2024-05-21 08:50:00', '2024-05-21 10:40:00', '2024-05-21 09:00:00', '2024-05-21 10:30:00', '123e4567-e89b-12d3-a456-426614174016'),
       ('550e8400-e29b-41d4-a716-446655440018', '770e8400-e29b-41d4-a716-446655440003', 'Software-Demo', 50, false, 'Streaming-Check', 'Mikrofon', '2024-05-22 14:30:00', '2024-05-22 16:30:00', '2024-05-22 15:00:00', '2024-05-22 16:00:00', '123e4567-e89b-12d3-a456-426614174017'),
       ('550e8400-e29b-41d4-a716-446655440019', '770e8400-e29b-41d4-a716-446655440001', 'Yoga-Kurs', 15, false, 'Reinigung', 'Matten Schrank 4', '2024-05-23 16:45:00', '2024-05-23 18:15:00', '2024-05-23 17:00:00', '2024-05-23 18:00:00', '123e4567-e89b-12d3-a456-426614174018'),
       ('550e8400-e29b-41d4-a716-446655440020', '770e8400-e29b-41d4-a716-446655440004', 'Jahresfeier', 150, true, 'Security', 'DJ-Pult', '2024-12-20 14:00:00', '2024-12-21 03:00:00', '2024-12-20 18:00:00', '2024-12-21 01:00:00', '123e4567-e89b-12d3-a456-426614174019'),
       ('550e8400-e29b-41d4-a716-446655440021', null, 'Jahresfeier', 150, true, 'Security', 'DJ-Pult', '2024-12-20 14:00:00', '2024-12-21 03:00:00', '2024-12-20 18:00:00', '2024-12-21 01:00:00', '123e4567-e89b-12d3-a456-426614174019'),
       ('550e8400-e29b-41d4-a716-446655440022', null, 'Jahresfeier', 150, true, 'Security', 'DJ-Pult', '2024-12-20 14:00:00', '2024-12-21 03:00:00', '2024-12-20 18:00:00', '2024-12-21 01:00:00', '123e4567-e89b-12d3-a456-426614174019');

truncate booking_equipment;
insert into booking_equipment (booking_id, equipment_id)
values ('550e8400-e29b-41d4-a716-446655440011', '123e4567-e89b-12d3-a456-426614174000'),
       ('550e8400-e29b-41d4-a716-446655440011', '123e4567-e89b-12d3-a456-426614174001'),
       ('550e8400-e29b-41d4-a716-446655440011', '123e4567-e89b-12d3-a456-426614174002'),
       ('550e8400-e29b-41d4-a716-446655440011', '123e4567-e89b-12d3-a456-426614174003'),
       ('550e8400-e29b-41d4-a716-446655440012', '123e4567-e89b-12d3-a456-426614174001'),
       ('550e8400-e29b-41d4-a716-446655440012', '123e4567-e89b-12d3-a456-426614174005'),
       ('550e8400-e29b-41d4-a716-446655440013', '123e4567-e89b-12d3-a456-426614174007'),
       ('550e8400-e29b-41d4-a716-446655440014', '123e4567-e89b-12d3-a456-426614174005'),
       ('550e8400-e29b-41d4-a716-446655440014', '123e4567-e89b-12d3-a456-426614174006'),
       ('550e8400-e29b-41d4-a716-446655440014', '123e4567-e89b-12d3-a456-426614174007'),
       ('550e8400-e29b-41d4-a716-446655440014', '123e4567-e89b-12d3-a456-426614174002'),
       ('550e8400-e29b-41d4-a716-446655440014', '123e4567-e89b-12d3-a456-426614174004'),
       ('550e8400-e29b-41d4-a716-446655440015', '123e4567-e89b-12d3-a456-426614174006');

truncate appointment;
insert into appointment (id, occupancy_start, occupancy_end, appointment_start, appointment_end, booking_id)
values ('f1010001-8b3d-4e92-9a31-111111111111', '2026-05-01 08:00:00', '2026-05-01 10:00:00', '2026-05-01 08:30:00', '2026-05-01 09:30:00', '550e8400-e29b-41d4-a716-446655440011'),
       ('f1010002-8b3d-4e92-9a31-111111111112', '2026-05-01 10:00:00', '2026-05-01 12:00:00', '2026-05-01 10:30:00', '2026-05-01 11:30:00', '550e8400-e29b-41d4-a716-446655440012'),
       ('f1010003-8b3d-4e92-9a31-111111111113', '2026-05-01 13:00:00', '2026-05-01 15:00:00', '2026-05-01 13:30:00', '2026-05-01 14:30:00', '550e8400-e29b-41d4-a716-446655440013'),
       ('f1010004-8b3d-4e92-9a31-111111111114', '2026-05-01 15:00:00', '2026-05-01 17:00:00', '2026-05-01 15:30:00', '2026-05-01 16:30:00', '550e8400-e29b-41d4-a716-446655440014'),
       ('f1010005-8b3d-4e92-9a31-111111111115', '2026-05-02 08:00:00', '2026-05-02 10:00:00', '2026-05-02 08:30:00', '2026-05-02 09:30:00', '550e8400-e29b-41d4-a716-446655440015'),
       ('f2020002-8b3d-4e92-9a31-111111111116', '2026-05-04 08:00:00', '2026-05-04 12:00:00', '2026-05-04 10:30:00', '2026-05-04 11:30:00', '550e8400-e29b-41d4-a716-446655440016'),
       ('f2020004-8b3d-4e92-9a31-111111111117', '2026-05-04 13:00:00', '2026-05-04 17:00:00', '2026-05-04 15:30:00', '2026-05-04 16:30:00', '550e8400-e29b-41d4-a716-446655440017'),
       ('f2020006-8b3d-4e92-9a31-111111111118', '2026-05-05 08:00:00', '2026-05-05 12:00:00', '2026-05-05 10:30:00', '2026-05-05 11:30:00', '550e8400-e29b-41d4-a716-446655440018'),
       ('f3030001-8b3d-4e92-9a31-333333333301', '2026-05-07 08:00:00', '2026-05-07 14:00:00', '2026-05-07 08:30:00', '2026-05-07 10:00:00', '550e8400-e29b-41d4-a716-446655440019'),
       ('f3030002-8b3d-4e92-9a31-333333333302', '2026-05-07 08:00:00', '2026-05-07 14:00:00', '2026-05-07 10:30:00', '2026-05-07 12:00:00', '550e8400-e29b-41d4-a716-446655440019'),
       ('f3030003-8b3d-4e92-9a31-333333333303', '2026-05-07 08:00:00', '2026-05-07 14:00:00', '2026-05-07 12:30:00', '2026-05-07 13:30:00', '550e8400-e29b-41d4-a716-446655440019'),
       ('f4040001-8b3d-4e92-9a31-444444444401', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 08:15:00', '2026-05-10 09:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040002-8b3d-4e92-9a31-444444444402', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 09:15:00', '2026-05-10 10:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040003-8b3d-4e92-9a31-444444444403', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 10:15:00', '2026-05-10 11:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040004-8b3d-4e92-9a31-444444444404', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 11:15:00', '2026-05-10 12:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040005-8b3d-4e92-9a31-444444444405', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 12:15:00', '2026-05-10 13:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040006-8b3d-4e92-9a31-444444444406', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 13:15:00', '2026-05-10 14:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040007-8b3d-4e92-9a31-444444444407', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 14:15:00', '2026-05-10 15:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040008-8b3d-4e92-9a31-444444444408', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 15:15:00', '2026-05-10 16:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040009-8b3d-4e92-9a31-444444444409', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 16:15:00', '2026-05-10 17:00:00', '550e8400-e29b-41d4-a716-446655440020'),
       ('f4040010-8b3d-4e92-9a31-444444444410', '2026-05-10 08:00:00', '2026-05-10 20:00:00', '2026-05-10 17:15:00', '2026-05-10 18:00:00', '550e8400-e29b-41d4-a716-446655440020');



