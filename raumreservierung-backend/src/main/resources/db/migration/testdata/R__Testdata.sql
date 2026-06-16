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

DROP FUNCTION IF EXISTS relative_timestamp(integer, text);
CREATE OR REPLACE FUNCTION relative_timestamp(day_offset integer, time_str text)
    RETURNS timestamptz AS
$$
DECLARE
    local_datetime timestamp;
BEGIN
    local_datetime := (CURRENT_DATE + (day_offset * INTERVAL '1 day') + time_str::TIME)::timestamp;
    RETURN local_datetime AT TIME ZONE 'Europe/Berlin';
END;
$$ LANGUAGE plpgsql STABLE;

truncate booking cascade;
insert into booking (id, booked_for_id, seating_type_id, recurring_rule, organisation_unit, room_id, title, participant_count, catering_needed, internal_notes, additional_notes, occupancy_start, occupancy_end, appointment_start, appointment_end, booked_by_id)
values
    -- Booking with only one appointment in the future
    ('550e8400-e29b-41d4-a716-446655440010', '123e4567-e89b-12d3-a456-426614174016', '123e4567-e89b-12d3-a456-426614174000', null, 'it@M', '770e8400-e29b-41d4-a716-446655440004', 'Jahresfeier', 150, true, 'Security', 'DJ-Pult', relative_timestamp(10, '14:00:00'), relative_timestamp(10, '17:00:00'), relative_timestamp(10, '14:30:00'), relative_timestamp(10, '16:45:00'), '123e4567-e89b-12d3-a456-426614174024'),
    ('550e8400-e29b-41d4-a716-446655440011', '123e4567-e89b-12d3-a456-426614174016', '123e4567-e89b-12d3-a456-426614174000', null, 'it@M', '770e8400-e29b-41d4-a716-446655440004', 'Halbjahresfeier', 150, true, 'Security', 'DJ-Pult', relative_timestamp(600, '00:00:00'), relative_timestamp(600, '23:59:59'), relative_timestamp(600, '14:30:00'), relative_timestamp(600, '16:45:00'), '123e4567-e89b-12d3-a456-426614174024'),
    -- Booking with only one appointment in the future without room
    ('550e8400-e29b-41d4-a716-446655440020', '123e4567-e89b-12d3-a456-426614174017', '123e4567-e89b-12d3-a456-426614174001', null, 'POR', null, 'Jahresfeier nächstes Jahr', 150, true, 'Security', 'DJ-Pult und gute Laune', relative_timestamp(375, '14:00:00'), relative_timestamp(375, '17:00:00'), relative_timestamp(375, '14:30:00'), relative_timestamp(375, '16:45:00'), '123e4567-e89b-12d3-a456-426614174025'),
    -- Booking with only one appointment in the past
    ('550e8400-e29b-41d4-a716-446655440030', '123e4567-e89b-12d3-a456-426614174018', '123e4567-e89b-12d3-a456-426614174002', null, 'it@M', '770e8400-e29b-41d4-a716-446655440002', 'Projekt-Kickoff', 25, true, 'Alles nochmal reinigen', 'Der Beamer sollte nicht zu lange genutzt werden, Explosionsgefahr!', relative_timestamp(-1, '09:00:00'), relative_timestamp(-1, '16:00:00'), relative_timestamp(-1, '10:00:00'), relative_timestamp(-1, '16:00:00'), '123e4567-e89b-12d3-a456-426614174021'),
    -- Booking with only one appointment today
    ('550e8400-e29b-41d4-a716-446655440040', '123e4567-e89b-12d3-a456-426614174019', '123e4567-e89b-12d3-a456-426614174003', null, 'it@M', '770e8400-e29b-41d4-a716-446655440001', 'Ordentliche Jahreshauptversammlung des Organisationsausschusses: Umfassende Evaluation der Jahresbilanz, strategische Neuausrichtung der Budgetplanung für das kommende Quartal sowie feierliche Verabschiedung der ausscheidenden Vorstandsmitglieder im Saal.', 8, true, 'Logistik-Meldung für die Raumkonfiguration: Diese wiederkehrende Serie erfordert eine absolut symmetrische Bestuhlung für exakt fünfzehn Kern-Stakeholder. Das Facility-Management muss die Freiflächen im Außenbereich räumen und die zusätzlichen ergonomischen Stühle dreißig Minuten vor dem Start bereitstellen. Das Catering-Personal bringt die Getränke lautlos durch den hinteren Servicezugang, um die Eröffnungsrede nicht zu stören. Bei technischen Problemen sofort den Sicherheitsdienst rufen.',
     'Wir freuen uns sehr, Ihr Team-Mittagessen in unserem privaten Speisesaal auszurichten. Das saisonale Buffet bietet eine feine Auswahl an regionalen Bio-Gerichten, wobei vegane und glutenfreie Alternativen an den Stationen deutlich gekennzeichnet sind. Der Raum verfügt über eine integrierte Präsentationsleinwand sowie moderne Mikrofone für Ihre Ansprachen. Sollte sich die Personenanzahl kurzfristig ändern oder falls Sie Barrierefreiheit benötigen, wenden Sie sich an unseren Gästeservice.', relative_timestamp(0, '13:30:00'), relative_timestamp(0, '14:00:00'), relative_timestamp(0, '13:35:00'), relative_timestamp(0, '13:55:00'), '123e4567-e89b-12d3-a456-426614174021'),
    -- Booking with 5 appointments, all in the past, rrule RRULE:FREQ=WEEKLY;COUNT=5
    ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174020', '123e4567-e89b-12d3-a456-426614174000', 'RRULE:FREQ=WEEKLY;COUNT=5', 'POR', '770e8400-e29b-41d4-a716-446655440003', 'IT-Security Workshop', 15, false, 'LAN-Anschluss', 'Eigene Laptops', relative_timestamp(-37, '07:45:00'), relative_timestamp(-37, '17:15:00'), relative_timestamp(-37, '08:00:00'), relative_timestamp(-6, '17:00:00'), '123e4567-e89b-12d3-a456-426614174022'),
    -- Booking with 5 appointments, all in the future, rrule RRULE:FREQ=DAILY;INTERVAL=30;COUNT=5
    ('550e8400-e29b-41d4-a716-446655440060', '123e4567-e89b-12d3-a456-426614174021', '123e4567-e89b-12d3-a456-426614174001', 'RRULE:FREQ=DAILY;INTERVAL=30;COUNT=5', 'POR', '770e8400-e29b-41d4-a716-446655440001', 'Mittagessen Team Blau', 20, true, 'Buffet Nebenraum', 'Vegetarisch', relative_timestamp(3, '11:45:00'), relative_timestamp(3, '14:00:00'), relative_timestamp(3, '12:00:00'), relative_timestamp(3, '13:30:00'), '123e4567-e89b-12d3-a456-426614174022'),
    -- Booking with 5 appointments, 2 in the past, 3 in the future, rrule RRULE:FREQ=WEEKLY;INTERVAL=2;COUNT=5
    ('550e8400-e29b-41d4-a716-446655440070', '123e4567-e89b-12d3-a456-426614174022', '123e4567-e89b-12d3-a456-426614174002', 'RRULE:FREQ=WEEKLY;INTERVAL=2;COUNT=5', 'POR', '770e8400-e29b-41d4-a716-446655440004', 'Bürgersprechstunde', 1, false, 'Diskretion', 'Barrierefrei', relative_timestamp(-17, '10:00:00'), relative_timestamp(-10, '12:00:00'), relative_timestamp(-10, '10:05:00'), relative_timestamp(-10, '11:45:00'), '123e4567-e89b-12d3-a456-426614174022'),
    -- Booking with 40 appointments, 10 in the past, 30 in the future, rrule RRULE:FREQ=DAILY;INTERVAL=5;COUNT=40
    ('550e8400-e29b-41d4-a716-446655440080', '123e4567-e89b-12d3-a456-426614174023', '123e4567-e89b-12d3-a456-426614174003', 'RRULE:FREQ=DAILY;INTERVAL=5;COUNT=40', 'POR', '770e8400-e29b-41d4-a716-446655440002', 'Strategischer IT-Projekt-Kickoff zur globalen Cloud-Migration: Abstimmung der Abteilungsleiter über die neuen Sicherheitsrichtlinien, Ressourcenallokation, Meilensteine und die finale Freigabe des plattformübergreifenden Infrastruktur-Szenarios für 2026.', 5, false, 'WICHTIG: Abstimmung der Abteilungsleitung. Die AV-Infrastruktur muss bis spätestens 19:30 Uhr aktiv und vollständig gepatcht sein. Das Technik-Team übernimmt die Ende-zu-Ende-Signalprüfung für den Livestream, bevor sich externe Teilnehmer einwählen. Die Protokolle der Vorwoche zeigten leichte Latenzen auf dem sekundären Audiobus; bei Bedarf sofort die Backup-Hardware aktivieren. Der lokale Broadcast-Subnetzbereich ist zu beschränken. Handwerker im Gebäude müssen die Arbeit einstellen.',
     'Willkommen zu unserer technischen Workshop-Reihe! Um einen optimalen Ablauf zu garantieren, finden Sie sich bitte zehn Minuten vor Beginn im Raum ein, um die Registrierung und die Netzwerk-Anmeldung abzuschließen. Bringen Sie Ihren Firmen-Laptop und einen gültigen Mitarbeiterausweis für den Zugang zur Laborzone mit. Die WLAN-Zugangsdaten erhalten Sie direkt am Empfang. Bitte prüfen Sie vorab die digitalen Vorbereitungsunterlagen, die im gemeinsamen Projektverzeichnis hinterlegt wurden.', relative_timestamp(-48, '20:00:00'), relative_timestamp(-48, '23:00:00'), relative_timestamp(-48, '20:30:00'), relative_timestamp(-48, '22:45:00'), '123e4567-e89b-12d3-a456-426614174023'),
    -- Booking with 40 appointments, 30 in the past, 10 in the future, rrule RRULE:FREQ=DAILY;INTERVAL=5;COUNT=40
    ('550e8400-e29b-41d4-a716-446655440090', '123e4567-e89b-12d3-a456-426614174024', '123e4567-e89b-12d3-a456-426614174000', 'RRULE:FREQ=DAILY;INTERVAL=5;COUNT=40', 'POR', '770e8400-e29b-41d4-a716-446655440003', 'Software-Demo', 50, false, 'Streaming-Check', 'Mikrofon', relative_timestamp(-148, '7:00:00'), relative_timestamp(-148, '08:00:00'), relative_timestamp(-148, '07:00:00'), relative_timestamp(-148, '08:00:00'), '123e4567-e89b-12d3-a456-426614174023');

truncate booking_equipment;
insert into booking_equipment (booking_id, equipment_id)
values ('550e8400-e29b-41d4-a716-446655440010', '123e4567-e89b-12d3-a456-426614174000'),
       ('550e8400-e29b-41d4-a716-446655440010', '123e4567-e89b-12d3-a456-426614174001'),
       ('550e8400-e29b-41d4-a716-446655440010', '123e4567-e89b-12d3-a456-426614174002'),
       ('550e8400-e29b-41d4-a716-446655440010', '123e4567-e89b-12d3-a456-426614174003'),
       ('550e8400-e29b-41d4-a716-446655440020', '123e4567-e89b-12d3-a456-426614174001'),
       ('550e8400-e29b-41d4-a716-446655440020', '123e4567-e89b-12d3-a456-426614174005'),
       ('550e8400-e29b-41d4-a716-446655440030', '123e4567-e89b-12d3-a456-426614174007'),
       ('550e8400-e29b-41d4-a716-446655440040', '123e4567-e89b-12d3-a456-426614174005'),
       ('550e8400-e29b-41d4-a716-446655440040', '123e4567-e89b-12d3-a456-426614174006'),
       ('550e8400-e29b-41d4-a716-446655440040', '123e4567-e89b-12d3-a456-426614174007'),
       ('550e8400-e29b-41d4-a716-446655440040', '123e4567-e89b-12d3-a456-426614174002'),
       ('550e8400-e29b-41d4-a716-446655440040', '123e4567-e89b-12d3-a456-426614174004'),
       ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174000'),
       ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174001'),
       ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174002'),
       ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174003'),
       ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174004'),
       ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174005'),
       ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174006'),
       ('550e8400-e29b-41d4-a716-446655440050', '123e4567-e89b-12d3-a456-426614174007'),
       ('550e8400-e29b-41d4-a716-446655440060', '123e4567-e89b-12d3-a456-426614174002'),
       ('550e8400-e29b-41d4-a716-446655440060', '123e4567-e89b-12d3-a456-426614174003'),
       ('550e8400-e29b-41d4-a716-446655440060', '123e4567-e89b-12d3-a456-426614174004'),
       ('550e8400-e29b-41d4-a716-446655440060', '123e4567-e89b-12d3-a456-426614174006'),
       ('550e8400-e29b-41d4-a716-446655440060', '123e4567-e89b-12d3-a456-426614174007'),
       ('550e8400-e29b-41d4-a716-446655440070', '123e4567-e89b-12d3-a456-426614174001'),
       ('550e8400-e29b-41d4-a716-446655440070', '123e4567-e89b-12d3-a456-426614174002'),
       ('550e8400-e29b-41d4-a716-446655440070', '123e4567-e89b-12d3-a456-426614174003');

truncate appointment;
insert into appointment (id, occupancy_start, occupancy_end, appointment_start, appointment_end, booking_id)
values
    -- Appointment for booking '550e8400-e29b-41d4-a716-446655440010'
    ('990e8400-e29b-41d4-a716-446655440010', relative_timestamp(10, '14:00:00'), relative_timestamp(10, '17:00:00'), relative_timestamp(10, '14:30:00'), relative_timestamp(10, '16:45:00'), '550e8400-e29b-41d4-a716-446655440010'),
     -- Appointment for booking '550e8400-e29b-41d4-a716-446655440011'
    ('990e8400-e29b-41d4-a716-446655440011', relative_timestamp(600, '00:00:00'), relative_timestamp(600, '23:59:59'), relative_timestamp(600, '14:30:00'), relative_timestamp(600, '16:45:00'), '550e8400-e29b-41d4-a716-446655440011'),
    -- Appointment for booking '550e8400-e29b-41d4-a716-446655440020'
    ('990e8400-e29b-41d4-a716-446655440020', relative_timestamp(375, '14:00:00'), relative_timestamp(375, '17:00:00'), relative_timestamp(375, '14:30:00'), relative_timestamp(375, '16:45:00'), '550e8400-e29b-41d4-a716-446655440020'),
    -- Appointment for booking '550e8400-e29b-41d4-a716-446655440030'
    ('990e8400-e29b-41d4-a716-446655440030', relative_timestamp(-1, '09:00:00'), relative_timestamp(-1, '16:00:00'), relative_timestamp(-1, '10:00:00'), relative_timestamp(-1, '16:00:00'), '550e8400-e29b-41d4-a716-446655440030'),
    -- Appointment for booking '550e8400-e29b-41d4-a716-446655440040'
    ('990e8400-e29b-41d4-a716-446655440040', relative_timestamp(0, '13:30:00'), relative_timestamp(0, '14:00:00'), relative_timestamp(0, '13:35:00'), relative_timestamp(0, '13:55:00'), '550e8400-e29b-41d4-a716-446655440040'),
    -- Appointments for booking '550e8400-e29b-41d4-a716-446655440050' (Series of 5 weekly appointments in the past)
    ('990e8400-e29b-41d4-a716-446655440051', relative_timestamp(-37, '07:45:00'), relative_timestamp(-37, '17:15:00'), relative_timestamp(-37, '08:00:00'), relative_timestamp(-37, '17:00:00'), '550e8400-e29b-41d4-a716-446655440050'),
    ('990e8400-e29b-41d4-a716-446655440052', relative_timestamp(-30, '07:45:00'), relative_timestamp(-30, '17:15:00'), relative_timestamp(-30, '08:00:00'), relative_timestamp(-30, '17:00:00'), '550e8400-e29b-41d4-a716-446655440050'),
    ('990e8400-e29b-41d4-a716-446655440053', relative_timestamp(-23, '07:45:00'), relative_timestamp(-23, '17:15:00'), relative_timestamp(-23, '08:00:00'), relative_timestamp(-23, '17:00:00'), '550e8400-e29b-41d4-a716-446655440050'),
    ('990e8400-e29b-41d4-a716-446655440054', relative_timestamp(-16, '07:45:00'), relative_timestamp(-16, '17:15:00'), relative_timestamp(-16, '08:00:00'), relative_timestamp(-16, '17:00:00'), '550e8400-e29b-41d4-a716-446655440050'),
    ('990e8400-e29b-41d4-a716-446655440055', relative_timestamp(-9, '07:45:00'), relative_timestamp(-9, '17:15:00'), relative_timestamp(-9, '08:00:00'), relative_timestamp(-9, '17:00:00'), '550e8400-e29b-41d4-a716-446655440050'),
    -- Appointments for booking '550e8400-e29b-41d4-a716-446655440050' (5 monthly future appointments, using 30 day intervals)
    ('990e8400-e29b-41d4-a716-446655440061', relative_timestamp(3, '11:45:00'), relative_timestamp(3, '14:00:00'), relative_timestamp(3, '12:00:00'), relative_timestamp(3, '13:30:00'), '550e8400-e29b-41d4-a716-446655440060'),
    ('990e8400-e29b-41d4-a716-446655440062', relative_timestamp(33, '11:45:00'), relative_timestamp(33, '14:00:00'), relative_timestamp(33, '12:00:00'), relative_timestamp(33, '13:30:00'), '550e8400-e29b-41d4-a716-446655440060'),
    ('990e8400-e29b-41d4-a716-446655440063', relative_timestamp(63, '11:45:00'), relative_timestamp(63, '14:00:00'), relative_timestamp(63, '12:00:00'), relative_timestamp(63, '13:30:00'), '550e8400-e29b-41d4-a716-446655440060'),
    ('990e8400-e29b-41d4-a716-446655440064', relative_timestamp(93, '11:45:00'), relative_timestamp(93, '14:00:00'), relative_timestamp(93, '12:00:00'), relative_timestamp(93, '13:30:00'), '550e8400-e29b-41d4-a716-446655440060'),
    ('990e8400-e29b-41d4-a716-446655440065', relative_timestamp(123, '11:45:00'), relative_timestamp(123, '14:00:00'), relative_timestamp(123, '12:00:00'), relative_timestamp(123, '13:30:00'), '550e8400-e29b-41d4-a716-446655440060'),
    -- Appointments for booking '550e8400-e29b-41d4-a716-446655440060' (5 weekly appointments, every 4 weeks)
    ('990e8400-e29b-41d4-a716-446655440071', relative_timestamp(-17, '10:00:00'), relative_timestamp(-17, '12:00:00'), relative_timestamp(-17, '10:05:00'), relative_timestamp(-17, '11:45:00'), '550e8400-e29b-41d4-a716-446655440070'),
    ('990e8400-e29b-41d4-a716-446655440072', relative_timestamp(-3, '10:00:00'), relative_timestamp(-3, '12:00:00'), relative_timestamp(-3, '10:05:00'), relative_timestamp(-3, '11:45:00'), '550e8400-e29b-41d4-a716-446655440070'),
    ('990e8400-e29b-41d4-a716-446655440073', relative_timestamp(10, '10:00:00'), relative_timestamp(10, '12:00:00'), relative_timestamp(10, '10:05:00'), relative_timestamp(10, '11:45:00'), '550e8400-e29b-41d4-a716-446655440070'),
    ('990e8400-e29b-41d4-a716-446655440074', relative_timestamp(24, '10:00:00'), relative_timestamp(24, '12:00:00'), relative_timestamp(24, '10:05:00'), relative_timestamp(24, '11:45:00'), '550e8400-e29b-41d4-a716-446655440070'),
    ('990e8400-e29b-41d4-a716-446655440075', relative_timestamp(38, '10:00:00'), relative_timestamp(38, '12:00:00'), relative_timestamp(38, '10:05:00'), relative_timestamp(38, '11:45:00'), '550e8400-e29b-41d4-a716-446655440070'),
    -- Appointments for booking '550e8400-e29b-41d4-a716-446655440080' (40 appointments every 5 days, 10 in the past, 30 in the future)
    ('990e8400-e29b-41d4-a716-446655448000', relative_timestamp(-48, '20:00:00'), relative_timestamp(-48, '23:00:00'), relative_timestamp(-48, '20:30:00'), relative_timestamp(-48, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448001', relative_timestamp(-43, '20:00:00'), relative_timestamp(-43, '23:00:00'), relative_timestamp(-43, '20:30:00'), relative_timestamp(-43, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448002', relative_timestamp(-38, '20:00:00'), relative_timestamp(-38, '23:00:00'), relative_timestamp(-38, '20:30:00'), relative_timestamp(-38, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448003', relative_timestamp(-33, '20:00:00'), relative_timestamp(-33, '23:00:00'), relative_timestamp(-33, '20:30:00'), relative_timestamp(-33, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448004', relative_timestamp(-28, '20:00:00'), relative_timestamp(-28, '23:00:00'), relative_timestamp(-28, '20:30:00'), relative_timestamp(-28, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448005', relative_timestamp(-23, '20:00:00'), relative_timestamp(-23, '23:00:00'), relative_timestamp(-23, '20:30:00'), relative_timestamp(-23, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448006', relative_timestamp(-18, '20:00:00'), relative_timestamp(-18, '23:00:00'), relative_timestamp(-18, '20:30:00'), relative_timestamp(-18, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448007', relative_timestamp(-13, '20:00:00'), relative_timestamp(-13, '23:00:00'), relative_timestamp(-13, '20:30:00'), relative_timestamp(-13, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448008', relative_timestamp(-8, '20:00:00'), relative_timestamp(-8, '23:00:00'), relative_timestamp(-8, '20:30:00'), relative_timestamp(-8, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448009', relative_timestamp(-3, '20:00:00'), relative_timestamp(-3, '23:00:00'), relative_timestamp(-3, '20:30:00'), relative_timestamp(-3, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448010', relative_timestamp(2, '20:00:00'), relative_timestamp(2, '23:00:00'), relative_timestamp(2, '20:30:00'), relative_timestamp(2, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448011', relative_timestamp(7, '20:00:00'), relative_timestamp(7, '23:00:00'), relative_timestamp(7, '20:30:00'), relative_timestamp(7, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448012', relative_timestamp(12, '20:00:00'), relative_timestamp(12, '23:00:00'), relative_timestamp(12, '20:30:00'), relative_timestamp(12, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448013', relative_timestamp(17, '20:00:00'), relative_timestamp(17, '23:00:00'), relative_timestamp(17, '20:30:00'), relative_timestamp(17, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448014', relative_timestamp(22, '20:00:00'), relative_timestamp(22, '23:00:00'), relative_timestamp(22, '20:30:00'), relative_timestamp(22, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448015', relative_timestamp(27, '20:00:00'), relative_timestamp(27, '23:00:00'), relative_timestamp(27, '20:30:00'), relative_timestamp(27, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448016', relative_timestamp(32, '20:00:00'), relative_timestamp(32, '23:00:00'), relative_timestamp(32, '20:30:00'), relative_timestamp(32, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448017', relative_timestamp(37, '20:00:00'), relative_timestamp(37, '23:00:00'), relative_timestamp(37, '20:30:00'), relative_timestamp(37, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448018', relative_timestamp(42, '20:00:00'), relative_timestamp(42, '23:00:00'), relative_timestamp(42, '20:30:00'), relative_timestamp(42, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448019', relative_timestamp(47, '20:00:00'), relative_timestamp(47, '23:00:00'), relative_timestamp(47, '20:30:00'), relative_timestamp(47, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448020', relative_timestamp(52, '20:00:00'), relative_timestamp(52, '23:00:00'), relative_timestamp(52, '20:30:00'), relative_timestamp(52, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448021', relative_timestamp(57, '20:00:00'), relative_timestamp(57, '23:00:00'), relative_timestamp(57, '20:30:00'), relative_timestamp(57, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448022', relative_timestamp(62, '20:00:00'), relative_timestamp(62, '23:00:00'), relative_timestamp(62, '20:30:00'), relative_timestamp(62, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448023', relative_timestamp(67, '20:00:00'), relative_timestamp(67, '23:00:00'), relative_timestamp(67, '20:30:00'), relative_timestamp(67, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448024', relative_timestamp(72, '20:00:00'), relative_timestamp(72, '23:00:00'), relative_timestamp(72, '20:30:00'), relative_timestamp(72, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448025', relative_timestamp(77, '20:00:00'), relative_timestamp(77, '23:00:00'), relative_timestamp(77, '20:30:00'), relative_timestamp(77, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448026', relative_timestamp(82, '20:00:00'), relative_timestamp(82, '23:00:00'), relative_timestamp(82, '20:30:00'), relative_timestamp(82, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448027', relative_timestamp(87, '20:00:00'), relative_timestamp(87, '23:00:00'), relative_timestamp(87, '20:30:00'), relative_timestamp(87, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448028', relative_timestamp(92, '20:00:00'), relative_timestamp(92, '23:00:00'), relative_timestamp(92, '20:30:00'), relative_timestamp(92, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448029', relative_timestamp(97, '20:00:00'), relative_timestamp(97, '23:00:00'), relative_timestamp(97, '20:30:00'), relative_timestamp(97, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448030', relative_timestamp(102, '20:00:00'), relative_timestamp(102, '23:00:00'), relative_timestamp(102, '20:30:00'), relative_timestamp(102, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448031', relative_timestamp(107, '20:00:00'), relative_timestamp(107, '23:00:00'), relative_timestamp(107, '20:30:00'), relative_timestamp(107, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448032', relative_timestamp(112, '20:00:00'), relative_timestamp(112, '23:00:00'), relative_timestamp(112, '20:30:00'), relative_timestamp(112, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448033', relative_timestamp(117, '20:00:00'), relative_timestamp(117, '23:00:00'), relative_timestamp(117, '20:30:00'), relative_timestamp(117, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448034', relative_timestamp(122, '20:00:00'), relative_timestamp(122, '23:00:00'), relative_timestamp(122, '20:30:00'), relative_timestamp(122, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448035', relative_timestamp(127, '20:00:00'), relative_timestamp(127, '23:00:00'), relative_timestamp(127, '20:30:00'), relative_timestamp(127, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448036', relative_timestamp(132, '20:00:00'), relative_timestamp(132, '23:00:00'), relative_timestamp(132, '20:30:00'), relative_timestamp(132, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448037', relative_timestamp(137, '20:00:00'), relative_timestamp(137, '23:00:00'), relative_timestamp(137, '20:30:00'), relative_timestamp(137, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448038', relative_timestamp(142, '20:00:00'), relative_timestamp(142, '23:00:00'), relative_timestamp(142, '20:30:00'), relative_timestamp(142, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    ('990e8400-e29b-41d4-a716-446655448039', relative_timestamp(147, '20:00:00'), relative_timestamp(147, '23:00:00'), relative_timestamp(147, '20:30:00'), relative_timestamp(147, '22:45:00'), '550e8400-e29b-41d4-a716-446655440080'),
    -- Appointments for booking '550e8400-e29b-41d4-a716-446655440090' (40 appointments every 5 days, 30 in the past, 10 in the future)
    ('990e8400-e29b-41d4-a716-446655449000', relative_timestamp(-148, '07:00:00'), relative_timestamp(-148, '08:00:00'), relative_timestamp(-148, '07:00:00'), relative_timestamp(-148, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449001', relative_timestamp(-143, '07:00:00'), relative_timestamp(-143, '08:00:00'), relative_timestamp(-143, '07:00:00'), relative_timestamp(-143, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449002', relative_timestamp(-138, '07:00:00'), relative_timestamp(-138, '08:00:00'), relative_timestamp(-138, '07:00:00'), relative_timestamp(-138, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449003', relative_timestamp(-133, '07:00:00'), relative_timestamp(-133, '08:00:00'), relative_timestamp(-133, '07:00:00'), relative_timestamp(-133, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449004', relative_timestamp(-128, '07:00:00'), relative_timestamp(-128, '08:00:00'), relative_timestamp(-128, '07:00:00'), relative_timestamp(-128, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449005', relative_timestamp(-123, '07:00:00'), relative_timestamp(-123, '08:00:00'), relative_timestamp(-123, '07:00:00'), relative_timestamp(-123, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449006', relative_timestamp(-118, '07:00:00'), relative_timestamp(-118, '08:00:00'), relative_timestamp(-118, '07:00:00'), relative_timestamp(-118, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449007', relative_timestamp(-113, '07:00:00'), relative_timestamp(-113, '08:00:00'), relative_timestamp(-113, '07:00:00'), relative_timestamp(-113, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449008', relative_timestamp(-108, '07:00:00'), relative_timestamp(-108, '08:00:00'), relative_timestamp(-108, '07:00:00'), relative_timestamp(-108, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449009', relative_timestamp(-103, '07:00:00'), relative_timestamp(-103, '08:00:00'), relative_timestamp(-103, '07:00:00'), relative_timestamp(-103, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449010', relative_timestamp(-98, '07:00:00'), relative_timestamp(-98, '08:00:00'), relative_timestamp(-98, '07:00:00'), relative_timestamp(-98, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449011', relative_timestamp(-93, '07:00:00'), relative_timestamp(-93, '08:00:00'), relative_timestamp(-93, '07:00:00'), relative_timestamp(-93, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449012', relative_timestamp(-88, '07:00:00'), relative_timestamp(-88, '08:00:00'), relative_timestamp(-88, '07:00:00'), relative_timestamp(-88, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449013', relative_timestamp(-83, '07:00:00'), relative_timestamp(-83, '08:00:00'), relative_timestamp(-83, '07:00:00'), relative_timestamp(-83, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449014', relative_timestamp(-78, '07:00:00'), relative_timestamp(-78, '08:00:00'), relative_timestamp(-78, '07:00:00'), relative_timestamp(-78, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449015', relative_timestamp(-73, '07:00:00'), relative_timestamp(-73, '08:00:00'), relative_timestamp(-73, '07:00:00'), relative_timestamp(-73, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449016', relative_timestamp(-68, '07:00:00'), relative_timestamp(-68, '08:00:00'), relative_timestamp(-68, '07:00:00'), relative_timestamp(-68, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449017', relative_timestamp(-63, '07:00:00'), relative_timestamp(-63, '08:00:00'), relative_timestamp(-63, '07:00:00'), relative_timestamp(-63, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449018', relative_timestamp(-58, '07:00:00'), relative_timestamp(-58, '08:00:00'), relative_timestamp(-58, '07:00:00'), relative_timestamp(-58, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449019', relative_timestamp(-53, '07:00:00'), relative_timestamp(-53, '08:00:00'), relative_timestamp(-53, '07:00:00'), relative_timestamp(-53, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449020', relative_timestamp(-48, '07:00:00'), relative_timestamp(-48, '08:00:00'), relative_timestamp(-48, '07:00:00'), relative_timestamp(-48, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449021', relative_timestamp(-43, '07:00:00'), relative_timestamp(-43, '08:00:00'), relative_timestamp(-43, '07:00:00'), relative_timestamp(-43, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449022', relative_timestamp(-38, '07:00:00'), relative_timestamp(-38, '08:00:00'), relative_timestamp(-38, '07:00:00'), relative_timestamp(-38, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449023', relative_timestamp(-33, '07:00:00'), relative_timestamp(-33, '08:00:00'), relative_timestamp(-33, '07:00:00'), relative_timestamp(-33, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449024', relative_timestamp(-28, '07:00:00'), relative_timestamp(-28, '08:00:00'), relative_timestamp(-28, '07:00:00'), relative_timestamp(-28, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449025', relative_timestamp(-23, '07:00:00'), relative_timestamp(-23, '08:00:00'), relative_timestamp(-23, '07:00:00'), relative_timestamp(-23, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449026', relative_timestamp(-18, '07:00:00'), relative_timestamp(-18, '08:00:00'), relative_timestamp(-18, '07:00:00'), relative_timestamp(-18, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449027', relative_timestamp(-13, '07:00:00'), relative_timestamp(-13, '08:00:00'), relative_timestamp(-13, '07:00:00'), relative_timestamp(-13, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449028', relative_timestamp(-8, '07:00:00'), relative_timestamp(-8, '08:00:00'), relative_timestamp(-8, '07:00:00'), relative_timestamp(-8, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449029', relative_timestamp(-3, '07:00:00'), relative_timestamp(-3, '08:00:00'), relative_timestamp(-3, '07:00:00'), relative_timestamp(-3, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449030', relative_timestamp(2, '07:00:00'), relative_timestamp(2, '08:00:00'), relative_timestamp(2, '07:00:00'), relative_timestamp(2, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449031', relative_timestamp(7, '07:00:00'), relative_timestamp(7, '08:00:00'), relative_timestamp(7, '07:00:00'), relative_timestamp(7, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449032', relative_timestamp(12, '07:00:00'), relative_timestamp(12, '08:00:00'), relative_timestamp(12, '07:00:00'), relative_timestamp(12, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449033', relative_timestamp(17, '07:00:00'), relative_timestamp(17, '08:00:00'), relative_timestamp(17, '07:00:00'), relative_timestamp(17, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449034', relative_timestamp(22, '07:00:00'), relative_timestamp(22, '08:00:00'), relative_timestamp(22, '07:00:00'), relative_timestamp(22, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449035', relative_timestamp(27, '07:00:00'), relative_timestamp(27, '08:00:00'), relative_timestamp(27, '07:00:00'), relative_timestamp(27, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449036', relative_timestamp(32, '07:00:00'), relative_timestamp(32, '08:00:00'), relative_timestamp(32, '07:00:00'), relative_timestamp(32, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449037', relative_timestamp(37, '07:00:00'), relative_timestamp(37, '08:00:00'), relative_timestamp(37, '07:00:00'), relative_timestamp(37, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449038', relative_timestamp(42, '07:00:00'), relative_timestamp(42, '08:00:00'), relative_timestamp(42, '07:00:00'), relative_timestamp(42, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090'),
    ('990e8400-e29b-41d4-a716-446655449039', relative_timestamp(47, '07:00:00'), relative_timestamp(47, '08:00:00'), relative_timestamp(47, '07:00:00'), relative_timestamp(47, '08:00:00'), '550e8400-e29b-41d4-a716-446655440090');