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


truncate seating_type cascade;
INSERT INTO seating_type (is_active, name, description, id)
VALUES (true, 'Reihenbestuhlung', 'Beschreibung von Reihenbestuhlung', '123e4567-e89b-12d3-a456-426614174000'),
       (true, 'Stadtrats- / Ausschussbestuhlunq', 'Beschreibung von Stadtrats-Ausschussbestuhlung.', '123e4567-e89b-12d3-a456-426614174001'),
       (true, 'Stehempfang', 'Beschreibung von Stehempfang.', '123e4567-e89b-12d3-a456-426614174002'),
       (true, 'Parlamentarische Bestuhlung', 'Beschreibung von parlamentarische Bestuhlung', '123e4567-e89b-12d3-a456-426614174003');

truncate person cascade;
insert into person (id, title, first_name, last_name, telefon_number, email)
values
    -- externe Personen:
    ('123e4567-e89b-12d3-a456-426614174010', null, 'Max', 'Mustermann', '089-233-12345', 'max.mustermann@muenchen.de'),
    ('123e4567-e89b-12d3-a456-426614174011', null, 'Anna', 'Schmidt', '089-233-54321', 'anna.schmidt@muenchen.de'),
    ('123e4567-e89b-12d3-a456-426614174012', 'MR', 'Tom', 'Exzellent', '0171-9876543', 'tom@beispiel-it-gmbh.de'),
    ('123e4567-e89b-12d3-a456-426614174013', 'MS', 'Lisa', 'Meyer', null, 'lisa.meyer@externe-berater.de'),
    ('123e4567-e89b-12d3-a456-426614174014', 'MR', 'Felix', 'Wagner', '0160-1122334', 'f.wagner@tech-muc.de'),
    ('123e4567-e89b-12d3-a456-426614174015', 'MS', 'Sarah', 'Klein', '0151-9988776', 'sklein@agile-coaches.de'),
    ('123e4567-e89b-12d3-a456-426614174016', 'MR', 'Michael', 'Bauer', null, 'mbauer@bauplanung-sued.de'),
    ('123e4567-e89b-12d3-a456-426614174017', 'NONE', 'Julia', 'Richter', '089-5551234', 'info@richter-legal.com'),
    ('123e4567-e89b-12d3-a456-426614174018', 'MS', 'David', 'Becker', '0172-3344556', 'd.becker@cloud-architects.io'),
    ('123e4567-e89b-12d3-a456-426614174019', 'MR', 'Elena', 'Weber', null, 'elena.weber@design-studio-muc.de'),
    ('123e4567-e89b-12d3-a456-426614174020', 'MR', 'Lukas', 'Hoffmann', '0162-4455667', 'l.hoffmann@event-pro.de'),
    -- interne Personen:
    ('123e4567-e89b-12d3-a456-426614174021', 'NONE', 'Anwender', 'Anwender', '089-7778889', 'anwender@anwender.de'),
    ('123e4567-e89b-12d3-a456-426614174022', 'DIVERSE', 'Leseberechtigt', 'Leseberechtigt', '0152-1122334', 'leseberechtigt@leseberechtigt.de'),
    ('123e4567-e89b-12d3-a456-426614174023', 'DIVERSE', 'Terminorganisator', 'Terminorganisator', null, 'terminorganisator@terminorganisator.de'),
    ('123e4567-e89b-12d3-a456-426614174024', 'MR', 'Raumbuchung', 'Raumbuchung', '089-111111', 'raumbuchung@raumbuchung.de'),
    ('123e4567-e89b-12d3-a456-426614174025', 'MS', 'Raumadmin', 'Raumadmin', '089-222222', 'raumadmin@raumadmin.de'),
    ('123e4567-e89b-12d3-a456-426614174026', 'DIVERSE', 'Alex', 'Meyer', '089-333333', 'alex.meyer@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174027', 'MR', 'Sebastian', 'Wagner', '089-444444', 's.wagner@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174028', 'MS', 'Nicole', 'Becker', '089-555555', 'n.becker@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174029', 'MR', 'Florian', 'Hoffmann', '089-666666', 'f.hoffmann@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174030', 'MS', 'Sabine', 'Schäfer', null, 's.schäfer@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174031', 'NONE', 'Jan', 'Koch', '089-888888', 'j.koch@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174032', 'MR', 'Tobias', 'Bauer', '0170-1234567', 't.bauer@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174033', 'MS', 'Claudia', 'Richter', null, 'c.richter@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174034', 'DIVERSE', 'Kim', 'Klein', '0151-2233445', 'k.klein@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174035', 'MR', 'Dominik', 'Wolf', '089-999000', 'd.wolf@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174036', 'MS', 'Verena', 'Schröder', '089-112233', 'v.schroeder@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174037', 'MR', 'Patrick', 'Neumann', null, 'p.neumann@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174038', 'MS', 'Daniela', 'Schwarz', '0160-5566778', 'd.schwarz@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174039', 'NONE', 'Erik', 'Zimmermann', '089-445566', 'e.zimmermann@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174040', 'MR', 'Julian', 'Braun', '089-778899', 'j.braun@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174041', 'MS', 'Kathrin', 'Krüger', null, 'k.krueger@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174042', 'MR', 'Manuel', 'Hofmann', '0172-998877', 'm.hofmann@intern.de'),
    ('123e4567-e89b-12d3-a456-426614174043', 'DIVERSE', 'Robin', 'Lange', '089-121212', 'r.lange@intern.de');

-- Hier kommen jetzt die spezifischen externen Daten (verknüpft über die gleiche ID):
insert into external_person (id, company, street_address, postal_code_city, note)
values ('123e4567-e89b-12d3-a456-426614174010', 'Ich AG', 'Kreisstraße 3', '80803 München', null),
       ('123e4567-e89b-12d3-a456-426614174011', 'IT und mehr', 'Linienstraße 4', '80804 München', null),
       ('123e4567-e89b-12d3-a456-426614174012', 'Beispiel IT GmbH', 'Marienplatz 8', '80331 München', 'Vollständig in die LHM-Systemlandschaft (z.B. SAP, Wilma) eingearbeitet und unterstützt die technische Umsetzung der OZG-Vorgaben.'),
       ('123e4567-e89b-12d3-a456-426614174013', 'Externe Berater AG', 'Consultingweg 42', '80807 München', null),
       ('123e4567-e89b-12d3-a456-426614174014', 'Tech MUC GmbH', 'Leopoldstraße 12', '80802 München', 'Sichert durch kontinuierliche Dokumentation den Know-how-Transfer an die internen Projektmitglieder in den Referaten.'),
       ('123e4567-e89b-12d3-a456-426614174015', 'Agile Coaches DE', 'Rosenheimer Str. 145', '81671 München', 'Optimiert agile städtische Prozesse.'),
       ('123e4567-e89b-12d3-a456-426614174016', 'Bauplanung Süd', 'Sendlinger Tor Platz 1', '80336 München', 'Verfügt über tiefgehende Kenntnisse der Münchner Bauordnung (BayBO) und unterstützt maßgeblich bei komplexen Genehmigungsverfahren.'),
       ('123e4567-e89b-12d3-a456-426614174017', 'Richter Legal Kanzlei', 'Nymphenburger Str. 4', '80335 München', 'Ist umfassend in die Sicherheitsrichtlinien und Datenschutzprotokolle (BayDSG) der Stadtverwaltung eingewiesen.'),
       ('123e4567-e89b-12d3-a456-426614174018', 'Cloud Architects IO', 'Arnulfstraße 21', '80335 München', 'Selten verfügbar.'),
       ('123e4567-e89b-12d3-a456-426614174019', 'Design Studio MUC', 'Schellingstraße 109', '80798 München', null),
       ('123e4567-e89b-12d3-a456-426614174020', 'Event Pro', 'Gärtnerplatz 2', '80469 München', null);

insert into internal_person (id, organisation_id, organisation_unit, role_function)
values ('123e4567-e89b-12d3-a456-426614174021', '0000001', 'it@M', 'Anwender'),
       ('123e4567-e89b-12d3-a456-426614174022', '0000002', 'POR', 'Leseberechtigt'),
       ('123e4567-e89b-12d3-a456-426614174023', '0000003', 'POR', 'Terminorganisator'),
       ('123e4567-e89b-12d3-a456-426614174024', '0000004', 'it@M', 'Raumbuchung'),
       ('123e4567-e89b-12d3-a456-426614174025', '0000005', 'POR', 'Raumadmin'),
       ('123e4567-e89b-12d3-a456-426614174026', '0000006', 'it@M', 'Chef'),
       ('123e4567-e89b-12d3-a456-426614174027', '0000007', 'Zentrale Buchung', 'Unterchef'),
       ('123e4567-e89b-12d3-a456-426614174028', '0000008', 'Sekretariat', 'Organisator'),
       ('123e4567-e89b-12d3-a456-426614174029', '0000009', 'Support', 'First Level'),
       ('123e4567-e89b-12d3-a456-426614174030', '00000010', 'Bürgerservice', 'Auskunft'),
       ('123e4567-e89b-12d3-a456-426614174031', '0000011', 'it@M', 'Infrastruktur'),
       ('123e4567-e89b-12d3-a456-426614174032', '0000012', 'Liegenschaften', 'Verwaltung'),
       ('123e4567-e89b-12d3-a456-426614174033', '0000013', 'it@M', 'Assistenz'),
       ('123e4567-e89b-12d3-a456-426614174034', '0000014', 'Personalrat', 'Mitglied'),
       ('123e4567-e89b-12d3-a456-426614174035', '0000015', 'it@M', 'Softwareentwickler'),
       ('123e4567-e89b-12d3-a456-426614174036', '0000016', 'Kreisverwaltungsreferat', 'Fachbereichleitung'),
       ('123e4567-e89b-12d3-a456-426614174037', '0000017', 'it@M', 'System-Engineer'),
       ('123e4567-e89b-12d3-a456-426614174038', '0000018', 'Standesamt', 'Urkundensachbearbeitung'),
       ('123e4567-e89b-12d3-a456-426614174039', '0000019', 'it@M', 'Netzwerktechnik'),
       ('123e4567-e89b-12d3-a456-426614174040', '0000020', 'Zulassungsstelle', 'Schalterdienst'),
       ('123e4567-e89b-12d3-a456-426614174041', '0000021', 'it@M', 'IT-Sicherheit'),
       ('123e4567-e89b-12d3-a456-426614174042', '0000022', 'Branddirektion', 'Einsatzplanung'),
       ('123e4567-e89b-12d3-a456-426614174043', '0000023', 'it@M', 'Projektleiter');

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
insert into booking (id, booked_for_id, organisation_unit, room_id, title, participant_count, catering_needed, internal_notes, additional_notes, occupancy_start, occupancy_end, appointment_start, appointment_end, booked_by_id)
values ('550e8400-e29b-41d4-a716-446655440011', '123e4567-e89b-12d3-a456-426614174015', 'it@M', '770e8400-e29b-41d4-a716-446655440001', 'Team-Meeting', 12, true, 'Kaffee bereitstellen', 'Standard-Setup', '2024-05-10 08:30:00', '2024-05-10 11:30:00', '2024-05-10 09:00:00', '2024-05-10 11:00:00', '123e4567-e89b-12d3-a456-426614174021'),
       ('550e8400-e29b-41d4-a716-446655440012', '123e4567-e89b-12d3-a456-426614174016', 'it@M', '770e8400-e29b-41d4-a716-446655440002', 'Projekt-Kickoff', 25, true, 'Beamer testen', 'Zusatzstühle', '2024-05-12 09:00:00', '2024-05-12 17:00:00', '2024-05-12 10:00:00', '2024-05-12 16:00:00', '123e4567-e89b-12d3-a456-426614174021'),
       ('550e8400-e29b-41d4-a716-446655440013', '123e4567-e89b-12d3-a456-426614174017', 'it@M', '770e8400-e29b-41d4-a716-446655440001', 'Vorstandssitzung', 8, true, 'VIP-Service', 'Wasser/Saft', '2024-05-15 13:30:00', '2024-05-15 18:30:00', '2024-05-15 14:00:00', '2024-05-15 18:00:00', '123e4567-e89b-12d3-a456-426614174021'),
       ('550e8400-e29b-41d4-a716-446655440014', '123e4567-e89b-12d3-a456-426614174018', 'POR', '770e8400-e29b-41d4-a716-446655440003', 'IT-Security Workshop', 15, false, 'LAN-Anschluss', 'Eigene Laptops', '2024-05-16 07:45:00', '2024-05-16 17:15:00', '2024-05-16 08:00:00', '2024-05-16 17:00:00', '123e4567-e89b-12d3-a456-426614174022'),
       ('550e8400-e29b-41d4-a716-446655440015', '123e4567-e89b-12d3-a456-426614174019', 'POR', '770e8400-e29b-41d4-a716-446655440001', 'Mittagessen Team Blau', 20, true, 'Buffet Nebenraum', 'Vegetarisch', '2024-05-17 11:45:00', '2024-05-17 14:00:00', '2024-05-17 12:00:00', '2024-05-17 13:30:00', '123e4567-e89b-12d3-a456-426614174022'),
       ('550e8400-e29b-41d4-a716-446655440016', '123e4567-e89b-12d3-a456-426614174020', 'POR', '770e8400-e29b-41d4-a716-446655440004', 'Bürgersprechstunde', 1, false, 'Diskretion', 'Barrierefrei', '2024-05-20 10:00:00', '2024-05-20 12:00:00', '2024-05-20 10:00:00', '2024-05-20 12:00:00', '123e4567-e89b-12d3-a456-426614174022'),
       ('550e8400-e29b-41d4-a716-446655440017', '123e4567-e89b-12d3-a456-426614174021', 'POR', '770e8400-e29b-41d4-a716-446655440002', 'Abteilungsleiter-Runde', 5, false, 'Protokoll XY', NULL, '2024-05-21 08:50:00', '2024-05-21 10:40:00', '2024-05-21 09:00:00', '2024-05-21 10:30:00', '123e4567-e89b-12d3-a456-426614174023'),
       ('550e8400-e29b-41d4-a716-446655440018', '123e4567-e89b-12d3-a456-426614174022', 'POR', '770e8400-e29b-41d4-a716-446655440003', 'Software-Demo', 50, false, 'Streaming-Check', 'Mikrofon', '2024-05-22 14:30:00', '2024-05-22 16:30:00', '2024-05-22 15:00:00', '2024-05-22 16:00:00', '123e4567-e89b-12d3-a456-426614174023'),
       ('550e8400-e29b-41d4-a716-446655440019', '123e4567-e89b-12d3-a456-426614174023', 'it@M', '770e8400-e29b-41d4-a716-446655440001', 'Yoga-Kurs', 15, false, 'Reinigung', 'Matten Schrank 4', '2024-05-23 16:45:00', '2024-05-23 18:15:00', '2024-05-23 17:00:00', '2024-05-23 18:00:00', '123e4567-e89b-12d3-a456-426614174024'),
       ('550e8400-e29b-41d4-a716-446655440020', '123e4567-e89b-12d3-a456-426614174024', 'it@M', '770e8400-e29b-41d4-a716-446655440004', 'Jahresfeier', 150, true, 'Security', 'DJ-Pult', '2024-12-20 14:00:00', '2024-12-21 03:00:00', '2024-12-20 18:00:00', '2024-12-21 01:00:00', '123e4567-e89b-12d3-a456-426614174024'),
       ('550e8400-e29b-41d4-a716-446655440021', '123e4567-e89b-12d3-a456-426614174025', 'POR', null, 'Jahresfeier nächstes Jahr', 150, true, 'Security', 'DJ-Pult und gute Laune', '2025-12-20 14:00:00', '2025-12-21 03:00:00', '2025-12-20 18:00:00', '2025-12-21 01:00:00', '123e4567-e89b-12d3-a456-426614174025'),
       ('550e8400-e29b-41d4-a716-446655440022', '123e4567-e89b-12d3-a456-426614174026', 'POR', null, 'Jahresfeier übernächstes Jahr', 150, true, 'Security', 'DJ-Pult und mehr Sekt', '2026-12-20 14:00:00', '2026-12-21 03:00:00', '2026-12-20 18:00:00', '2026-12-21 01:00:00', '123e4567-e89b-12d3-a456-426614174025');

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

CREATE TEMP TABLE temp_generated_bookings AS
    -- one booking per row,
    -- choose amount of linked appointments,
    -- choose if appointments can be in the past and
    -- choose if the start and end times can differ from the booking start and end time
WITH booking_config(amount_appointments, in_past, differ) AS (VALUES (5, true, false),
                                                                     (5, false, true),
                                                                     (3, true, true),
                                                                     (1, false, false)),
    data_pools AS (SELECT ARRAY ['123e4567-e89b-12d3-a456-426614174017'::UUID,'123e4567-e89b-12d3-a456-426614174018'::UUID,'123e4567-e89b-12d3-a456-426614174019'::UUID,'123e4567-e89b-12d3-a456-426614174020'::UUID,'123e4567-e89b-12d3-a456-426614174021'::UUID,'123e4567-e89b-12d3-a456-426614174022'::UUID, '123e4567-e89b-12d3-a456-426614174023'::UUID, '123e4567-e89b-12d3-a456-426614174024'::UUID] AS booked_for_pool,
                           ARRAY ['it@M', 'POR', 'HR-Management', 'Finance & Controlling', 'Marketing']                                                                                                                                                                                                                                                                                                      AS org_unit_pool,
                           ARRAY ['770e8400-e29b-41d4-a716-446655440001'::UUID,'770e8400-e29b-41d4-a716-446655440002'::UUID, '770e8400-e29b-41d4-a716-446655440003'::UUID, '770e8400-e29b-41d4-a716-446655440004'::UUID, NULL]                                                                                                                                                                               AS room_pool,
                           ARRAY ['Yoga-Kurs', 'Jahresfeier', 'Team-Meeting', 'Sprint Planning', 'Kunden-Workshop', 'Daily Standup']                                                                                                                                                                                                                                                                         AS title_pool,
                           ARRAY [5, 10, 15, 20, 50, 100]                                                                                                                                                                                                                                                                                                                                                    AS participant_pool,
                           ARRAY [true, false]                                                                                                                                                                                                                                                                                                                                                               AS catering_pool,
                           ARRAY ['Reinigung beachten', 'Security benötigt', 'Standard-Setup', 'Keine Besonderheiten']                                                                                                                                                                                                                                                                                       AS internal_notes_pool,
                           ARRAY ['Matten Schrank 4 ist dreckig', 'DJ-Pult steht zu weit links', 'Gute Laune wird benötigt', 'Bitte Whiteboard bereitstellen', NULL]                                                                                                                                                                                                                                         AS additional_notes_pool,
                           ARRAY ['123e4567-e89b-12d3-a456-426614174021'::UUID, '123e4567-e89b-12d3-a456-426614174022'::UUID, '123e4567-e89b-12d3-a456-426614174023'::UUID, '123e4567-e89b-12d3-a456-426614174024'::UUID, '123e4567-e89b-12d3-a456-426614174025'::UUID, '123e4567-e89b-12d3-a456-426614174026'::UUID]                                                                                        AS booked_by_pool,
                           ARRAY [TIME '08:00:00', TIME '10:15:00', TIME '13:00:00', TIME '15:30:00', TIME '17:00:00']                                                                                                                                                                                                                                                                                       AS time_pool,
                           ARRAY [NULL, 'FREQ=DAILY;COUNT=3', 'FREQ=MONTHLY;COUNT=2', 'FREQ=WEEKLY;COUNT=5', 'FREQ=WEEKLY;BYDAY=TU,WE;INTERVAL=2;COUNT=4', 'FREQ=MONTHLY;BYDAY=2WE;COUNT=3', 'FREQ=MONTHLY;INTERVAL=2;BYMONTHDAY=15;COUNT=4', 'FREQ=WEEKLY;INTERVAL=6' ]                                                                                                                                     AS rrule_pool),
     raw_generated_data AS (SELECT gen_random_uuid()                                                                   AS b_id,
                                   cfg.amount_appointments,
                                   cfg.differ,
                                   CASE
                                       WHEN cfg.in_past = true
                                           THEN CURRENT_DATE + (floor(random() * 61) - 50 || ' days')::INTERVAL
                                       ELSE CURRENT_DATE + (floor(random() * 45) || ' days')::INTERVAL
                                       END::date                                                                       AS target_date,
                                   p.time_pool[floor(random() * cardinality(p.time_pool)) + 1]                         AS base_time,
                                   p.booked_for_pool[floor(random() * cardinality(p.booked_for_pool)) + 1]             AS booked_for_id,
                                   p.org_unit_pool[floor(random() * cardinality(p.org_unit_pool)) + 1]                 AS organisation_unit,
                                   p.room_pool[floor(random() * cardinality(p.room_pool)) + 1]                         AS room_id,
                                   p.title_pool[floor(random() * cardinality(p.title_pool)) + 1]                       AS title,
                                   p.participant_pool[floor(random() * cardinality(p.participant_pool)) + 1]           AS participant_count,
                                   p.catering_pool[floor(random() * cardinality(p.catering_pool)) + 1]                 AS catering_needed,
                                   p.internal_notes_pool[floor(random() * cardinality(p.internal_notes_pool)) + 1]     AS internal_notes,
                                   p.additional_notes_pool[floor(random() * cardinality(p.additional_notes_pool)) + 1] AS additional_notes,
                                   p.booked_by_pool[floor(random() * cardinality(p.booked_by_pool)) + 1]               AS booked_by_id,
                                   p.rrule_pool[floor(random() * cardinality(p.rrule_pool)) + 1]                       AS recurring_rule
                            FROM booking_config cfg
                                     CROSS JOIN data_pools p)
SELECT r.*,
       r.target_date + r.base_time - INTERVAL '30 minutes'                      AS b_occ_start,
       r.target_date + r.base_time + INTERVAL '2 hours' + INTERVAL '30 minutes' AS b_occ_end,
       r.target_date + r.base_time                                              AS b_app_start,
       r.target_date + r.base_time + INTERVAL '2 hours'                         AS b_app_end
FROM raw_generated_data r;


INSERT INTO booking (id, booked_for_id, organisation_unit, room_id, title, participant_count,
                     catering_needed, internal_notes, additional_notes, occupancy_start,
                     occupancy_end, appointment_start, appointment_end, booked_by_id, recurring_rule)
SELECT b_id,
       booked_for_id,
       organisation_unit,
       room_id,
       title,
       participant_count,
       catering_needed,
       internal_notes,
       additional_notes,
       b_occ_start,
       b_occ_end,
       b_app_start,
       b_app_end,
       booked_by_id,
       recurring_rule
FROM temp_generated_bookings
ON CONFLICT (id) DO NOTHING;


INSERT INTO appointment (id, occupancy_start, occupancy_end, appointment_start, appointment_end, booking_id)
WITH configured_intervals AS (SELECT t.b_id,
                                     t.differ,
                                     t.amount_appointments,
                                     (ARRAY [
                                         INTERVAL '1 day',
                                         INTERVAL '3 days',
                                         INTERVAL '7 days',
                                         INTERVAL '14 days'
                                         ])[floor(random() * 4) + 1] AS chosen_interval
                              FROM temp_generated_bookings t),
     expanded_series AS (SELECT t.*,
                                ((t.target_date + ((seq.idx - 1) * ci.chosen_interval))::date) AS calc_date
                         FROM temp_generated_bookings t
                                  JOIN configured_intervals ci ON t.b_id = ci.b_id
                                  CROSS JOIN LATERAL generate_series(1, t.amount_appointments) AS seq(idx)),
     base_appointment_datetimes AS (SELECT b_id,
                                           differ,
                                           calc_date + base_time - INTERVAL '30 minutes'                      AS base_occ_start,
                                           calc_date + base_time + INTERVAL '2 hours' + INTERVAL '30 minutes' AS base_occ_end,
                                           calc_date + base_time                                              AS base_app_start,
                                           calc_date + base_time + INTERVAL '2 hours'                         AS base_app_end
                                    FROM expanded_series),
     calculated_appointments AS (SELECT gen_random_uuid() AS id,
                                        b_id,
                                        CASE
                                            WHEN differ AND random() < 0.20
                                                THEN base_occ_start + (floor(random() * 16) || ' minutes')::INTERVAL
                                            ELSE base_occ_start
                                            END           AS occ_start,

                                        CASE
                                            WHEN differ AND random() < 0.20
                                                THEN base_occ_end - (floor(random() * 21) || ' minutes')::INTERVAL
                                            ELSE base_occ_end
                                            END           AS occ_end,

                                        CASE
                                            WHEN differ AND random() < 0.20
                                                THEN GREATEST(base_app_start + (floor(random() * 11) || ' minutes')::INTERVAL, base_occ_start)
                                            ELSE base_app_start
                                            END           AS app_start,

                                        CASE
                                            WHEN differ AND random() < 0.20
                                                THEN LEAST(base_app_end - (floor(random() * 21) || ' minutes')::INTERVAL, base_occ_end)
                                            ELSE base_app_end
                                            END           AS app_end
                                 FROM base_appointment_datetimes)
SELECT id,
       occ_start,
       occ_end,
       app_start,
       app_end,
       b_id AS booking_id
FROM calculated_appointments t
WHERE EXISTS (SELECT 1 FROM booking b WHERE b.id = t.b_id);

DROP TABLE temp_generated_bookings;