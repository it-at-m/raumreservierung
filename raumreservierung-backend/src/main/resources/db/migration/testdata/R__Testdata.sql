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

truncate equipment cascade;
INSERT INTO equipment (is_active, name, description, id) VALUES
    (true, 'Tisch', 'Ein stabiler Holzschreibtisch mit viel Platz für Arbeiten.', '123e4567-e89b-12d3-a456-426614174000'),
    (true, 'Stuhl', 'Ein ergonomischer Bürostuhl mit verstellbarer Höhe.', '123e4567-e89b-12d3-a456-426614174001'),
    (true, 'Whiteboard', 'Ein großes Whiteboard für Präsentationen und Brainstorming.', '123e4567-e89b-12d3-a456-426614174002'),
    (true, 'Projektor', 'Ein Full-HD Projektor für Präsentationen und Filme.', '123e4567-e89b-12d3-a456-426614174003'),
    (true, 'Bücherregal', 'Ein hohes Regal aus Holz zur Aufbewahrung von Büchern und Materialien.', '123e4567-e89b-12d3-a456-426614174004'),
    (true, 'Konferenztisch', 'Ein großer Tisch für Meetings mit Platz für bis zu 12 Personen.', '123e4567-e89b-12d3-a456-426614174005'),
    (true, 'Laptop', 'Ein tragbarer Laptop für mobile Arbeit und Präsentationen.', '123e4567-e89b-12d3-a456-426614174006'),
    (true, 'Kopierer', 'Ein Multifunktionsgerät zum Kopieren, Scannen und Drucken.', '123e4567-e89b-12d3-a456-426614174007');


truncate seating_type;
INSERT INTO seating_type (is_active, name, description, id) VALUES
    (true, 'Reihenbestuhlung', 'Beschreibung von Reihenbestuhlung', '123e4567-e89b-12d3-a456-426614174000'),
    (true, 'Stadtrats- / Ausschussbestuhlunq', 'Beschreibung von Stadtrats-Ausschussbestuhlung.', '123e4567-e89b-12d3-a456-426614174001'),
    (true, 'Stehempfang', 'Beschreibung von Stehempfang.', '123e4567-e89b-12d3-a456-426614174002'),
    (true, 'Parlamentarische Bestuhlung', 'Beschreibung von parlamentarische Bestuhlung', '123e4567-e89b-12d3-a456-426614174003');

truncate person cascade;
insert into person (id, title, first_name, last_name, telefon_number, email) values
    -- Deine bisherigen:
    ('123e4567-e89b-12d3-a456-426614174010', null,'Max', 'Mustermann', '089-233-12345', 'max.mustermann@muenchen.de'),
    ('123e4567-e89b-12d3-a456-426614174011', null, 'Anna', 'Schmidt', '089-233-54321', 'anna.schmidt@muenchen.de'),
    ('123e4567-e89b-12d3-a456-426614174012', 'MR','Tom', 'Exzellent', '0171-9876543', 'tom@beispiel-it-gmbh.de'),
    ('123e4567-e89b-12d3-a456-426614174013', 'MS','Lisa', 'Meyer', null, 'lisa.meyer@externe-berater.de'),
    -- Neue externe Personen:
    ('123e4567-e89b-12d3-a456-426614174014', 'MR','Felix', 'Wagner', '0160-1122334', 'f.wagner@tech-muc.de'),
    ('123e4567-e89b-12d3-a456-426614174015', 'MS','Sarah', 'Klein', '0151-9988776', 'sklein@agile-coaches.de'),
    ('123e4567-e89b-12d3-a456-426614174016', 'MR','Michael', 'Bauer', null, 'mbauer@bauplanung-sued.de'),
    ('123e4567-e89b-12d3-a456-426614174017', 'NONE', 'Julia', 'Richter', '089-5551234', 'info@richter-legal.com'),
    ('123e4567-e89b-12d3-a456-426614174018', 'MS','David', 'Becker', '0172-3344556', 'd.becker@cloud-architects.io'),
    ('123e4567-e89b-12d3-a456-426614174019', 'MR','Elena', 'Weber', null, 'elena.weber@design-studio-muc.de'),
    ('123e4567-e89b-12d3-a456-426614174020', 'MR','Lukas', 'Hoffmann', '0162-4455667', 'l.hoffmann@event-pro.de'),
    ('123e4567-e89b-12d3-a456-426614174021', 'NONE', 'Sophie', 'Neumann', '089-7778889', 's.neumann@audit-partners.de'),
    ('123e4567-e89b-12d3-a456-426614174022', 'DIVERSE','Tim', 'Krüger', '0152-1122334', 'krueger@it-sec-consult.de'),
    ('123e4567-e89b-12d3-a456-426614174023', 'DIVERSE','Laura', 'Zimmermann', null, 'lz@zimmermann-pr.de');

-- Hier kommen jetzt die spezifischen externen Daten (verknüpft über die gleiche ID):
insert into external_person (id, company, street_address, postal_code_city, note) values
    -- Deine bisherigen:
    ('123e4567-e89b-12d3-a456-426614174012',  'Beispiel IT GmbH', 'Marienplatz 8', '80331 München', 'Vollständig in die LHM-Systemlandschaft (z.B. SAP, Wilma) eingearbeitet und unterstützt die technische Umsetzung der OZG-Vorgaben.'),
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
insert into internal_person (id, organisation_id, organisation_unit, role_function) values
    ('123e4567-e89b-12d3-a456-426614174010', 'ORG-RIT-001', 'it@M', 'Administrator'),
    ('123e4567-e89b-12d3-a456-426614174011', 'ORG-KVR-002', 'Kreisverwaltungsreferat', 'Sachbearbeiterin');
