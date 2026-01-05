-- -----------------------------
-- FITNESSCENTER 
-- -----------------------------

-- RESERVATION
INSERT INTO reservation DEFAULT VALUES;
INSERT INTO reservation DEFAULT VALUES;
INSERT INTO reservation DEFAULT VALUES;
INSERT INTO reservation DEFAULT VALUES;
INSERT INTO reservation DEFAULT VALUES;
INSERT INTO reservation DEFAULT VALUES;

-- APPOINTMENT 
INSERT INTO appointment (start_time, end_time, max_capacity, reservation_id)
VALUES ('2026-01-03 10:00:00', '2026-01-03 11:30:00', 40,
        (SELECT TOP 1 r.reservation_id
         FROM reservation r
         WHERE NOT EXISTS (
           SELECT 1 FROM appointment a WHERE a.reservation_id = r.reservation_id
         )
         ORDER BY r.reservation_id));

INSERT INTO appointment (start_time, end_time, max_capacity, reservation_id)
VALUES ('2026-01-10 10:30:00', '2026-01-10 11:00:00', 25,
        (SELECT TOP 1 r.reservation_id
         FROM reservation r
         WHERE NOT EXISTS (
           SELECT 1 FROM appointment a WHERE a.reservation_id = r.reservation_id
         )
         ORDER BY r.reservation_id));

INSERT INTO appointment (start_time, end_time, max_capacity, reservation_id)
VALUES ('2026-01-28 11:30:00', '2026-01-28 12:00:00', 30,
        (SELECT TOP 1 r.reservation_id
         FROM reservation r
         WHERE NOT EXISTS (
           SELECT 1 FROM appointment a WHERE a.reservation_id = r.reservation_id
         )
         ORDER BY r.reservation_id));

INSERT INTO appointment (start_time, end_time, max_capacity, reservation_id)
VALUES ('2026-01-12 12:30:00', '2026-01-12 13:00:00', 50,
        (SELECT TOP 1 r.reservation_id
         FROM reservation r
         WHERE NOT EXISTS (
           SELECT 1 FROM appointment a WHERE a.reservation_id = r.reservation_id
         )
         ORDER BY r.reservation_id));

INSERT INTO appointment (start_time, end_time, max_capacity, reservation_id)
VALUES ('2026-01-14 13:30:00', '2026-01-14 14:00:00', 40,
        (SELECT TOP 1 r.reservation_id
         FROM reservation r
         WHERE NOT EXISTS (
           SELECT 1 FROM appointment a WHERE a.reservation_id = r.reservation_id
         )
         ORDER BY r.reservation_id));

INSERT INTO appointment (start_time, end_time, max_capacity, reservation_id)
VALUES ('2026-01-17 10:30:00', '2026-01-17 11:00:00', 35,
        (SELECT TOP 1 r.reservation_id
         FROM reservation r
         WHERE NOT EXISTS (
           SELECT 1 FROM appointment a WHERE a.reservation_id = r.reservation_id
         )
         ORDER BY r.reservation_id));

-- SERVICE
INSERT INTO service (name_service, price, appointment_id) VALUES
('Fitness',  50, (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-03 10:00:00')),
('Pilates',  35, (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-10 10:30:00')),
('Gym',      25, (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-28 11:30:00')),
('Pilates',  35, (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-12 12:30:00')),
('CrossFit', 40, (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-14 13:30:00')),
('Yoga',     37, (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-17 10:30:00'));

-- MEMBER
INSERT INTO member (member_name, member_surname, phone, roll, email, password, username, reservation_id) VALUES
('Igor',    'Stevanovic', '+381644002552', 'ROLE_USER',   'stefstev@gmail.com',        'stefanStevanovic', 'stefstev@gmail.com',        (SELECT MIN(reservation_id)     FROM reservation)),
('Lazar',   'Ilic',       '+381664022552', 'ROLE_USER',   'lazarIlic@gmail.com',       'LazarIlicc',       'lazarIlic@gmail.com',       (SELECT MIN(reservation_id) + 1 FROM reservation)),
('Milos',   'Lukic',      '+381630401152', 'ROLE_SELLER', 'milosLukk@gmail.com',       'Milos123L',        'milosLukk@gmail.com',       NULL),
('Uros',    'Lukic',      '+381620401152', 'ROLE_ADMIN',  'UrosLukk@gmail.com',        '$2a$10$ssV/7UrUGeK3T9aUz31iEuU.M3QcTUE7t9qxIj9.Lg25Wg0BW2FIi',         'UrosLukk@gmail.com',        NULL),
('Emilija', 'Gordanic',   '+381644001569', 'ROLE_USER',   'EmilijaGordanic@gmail.com', 'emilijaGordanic',  'EmilijaGordanic@gmail.com', (SELECT MIN(reservation_id) + 2 FROM reservation)),
('Filip',   'Filipovic',  '+381644002552', 'ROLE_USER',   'filipfilipovic@gmail.com',  'filipFilipovic',   'filipfilipovic@gmail.com',  (SELECT MIN(reservation_id) + 3 FROM reservation));

-- LOCATION
INSERT INTO location (location_name, location_adress, member_id, appointment_id) VALUES
('Spens centar',               'Radnicka 12a',
 (SELECT m.member_id FROM member m WHERE m.email = 'lazarIlic@gmail.com'),
 (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-03 10:00:00')),

('Promenada trening centar',   'Bulevar Oslobodjenja 119',
 (SELECT m.member_id FROM member m WHERE m.email = 'milosLukk@gmail.com'),
 (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-10 10:30:00')),

('Centar Novi Sad',            'Zmaj Jovina 112a',
 (SELECT m.member_id FROM member m WHERE m.email = 'UrosLukk@gmail.com'),
 (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-28 11:30:00')),

('Detelinara sportski centar', 'Branka Bajica 16a',
 (SELECT m.member_id FROM member m WHERE m.email = 'EmilijaGordanic@gmail.com'),
 (SELECT a.appointment_id FROM appointment a WHERE a.start_time = '2026-01-12 12:30:00'));
