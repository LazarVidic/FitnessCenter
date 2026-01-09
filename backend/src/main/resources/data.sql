-- -----------------------------
-- FITNESSCENTER (MSSQL)
-- Spring Boot data.sql (safe reset + seed)
-- -----------------------------

-- 0) Disable constraints (dozvoljava DELETE bez FK problema)
ALTER TABLE dbo.reservation NOCHECK CONSTRAINT ALL;
ALTER TABLE dbo.appointment NOCHECK CONSTRAINT ALL;
ALTER TABLE dbo.member NOCHECK CONSTRAINT ALL;

-- 1) Cleanup (child -> parent)
DELETE FROM dbo.reservation;
DELETE FROM dbo.appointment;
DELETE FROM dbo.member;
DELETE FROM dbo.service;
DELETE FROM dbo.location;

-- 2) Reset identity (ako su IDENTITY kolone)
DBCC CHECKIDENT ('dbo.reservation', RESEED, 0);
DBCC CHECKIDENT ('dbo.appointment', RESEED, 0);
DBCC CHECKIDENT ('dbo.member', RESEED, 0);
DBCC CHECKIDENT ('dbo.service', RESEED, 0);
DBCC CHECKIDENT ('dbo.location', RESEED, 0);

-- 3) Insert LOCATION
INSERT INTO dbo.location (location_name, location_address) VALUES
('Spens centar',               'Radnicka 12a'),
('Promenada trening centar',   'Bulevar Oslobodjenja 119'),
('Centar Novi Sad',            'Zmaj Jovina 112a'),
('Detelinara sportski centar', 'Branka Bajica 16a');

-- 4) Insert SERVICE
INSERT INTO dbo.service (name_service, price) VALUES
('Fitness',  50),
('Pilates',  35),
('Gym',      25),
('CrossFit', 40),
('Yoga',     37);

-- 5) Insert MEMBER
INSERT INTO dbo.member (email, member_name, member_surname, password, phone, roll, username, location_id) VALUES
('stefstev@gmail.com',        'Igor',    'Stevanovic', '$2a$10$A0/S0pFFtH/LwXqWqt0/cu6xFERh/5KpxLSUx1mWpCXLpoil7slsW', '+381644002552', 'ROLE_USER',   'stefstev@gmail.com', 1),
('lazarIlic@gmail.com',       'Lazar',   'Ilic',       '$2a$10$LUMhPW/ABY6JcdAQMWSisO/T8BOyxnqy/9JBNYmyOFNM2IA63Suo6', '+381664022552', 'ROLE_USER',   'lazarIlic@gmail.com', 1),
('milosLukk@gmail.com',       'Milos',   'Lukic',      '$2a$10$XHIVJcVMk0h0WwliZeimaOTZGEqhgVA9UmOx/3BZCCWXrrabuNvIS', '+381630401152', 'ROLE_SELLER', 'milosLukk@gmail.com', 2),
('UrosLukk@gmail.com',        'Uros',    'Lukic',      '$2a$10$ssV/7UrUGeK3T9aUz31iEuU.M3QcTUE7t9qxIj9.Lg25Wg0BW2FIi', '+381620401152', 'ROLE_ADMIN',  'UrosLukk@gmail.com',  3),
('EmilijaGordanic@gmail.com', 'Emilija', 'Gordanic',   '$2a$10$A0/S0pFFtH/LwXqWqt0/cu6xFERh/5KpxLSUx1mWpCXLpoil7slsW', '+381644001569', 'ROLE_USER',   'EmilijaGordanic@gmail.com', 4),
('filipfilipovic@gmail.com',  'Filip',   'Filipovic',  '$2a$10$LUMhPW/ABY6JcdAQMWSisO/T8BOyxnqy/9JBNYmyOFNM2IA63Suo6', '+381644002552', 'ROLE_USER',   'filipfilipovic@gmail.com',  4);

-- 6) Insert APPOINTMENT
INSERT INTO dbo.appointment (location_id, service_id, start_time, end_time, max_capacity) VALUES
(1, 1, '2026-01-03 10:00:00', '2026-01-03 11:30:00', 40),
(2, 2, '2026-01-10 10:30:00', '2026-01-10 11:00:00', 25),
(3, 3, '2026-01-28 11:30:00', '2026-01-28 12:00:00', 30),
(4, 2, '2026-01-12 12:30:00', '2026-01-12 13:00:00', 50),
(1, 4, '2026-01-14 13:30:00', '2026-01-14 14:00:00', 40),
(2, 5, '2026-01-17 10:30:00', '2026-01-17 11:00:00', 35);

-- 7) Insert RESERVATION
INSERT INTO dbo.reservation (appointment_id, member_id, created_at) VALUES
(1, 1, '2026-01-01 09:00:00'),
(2, 2, '2026-01-02 09:00:00'),
(3, 5, '2026-01-03 09:00:00');

-- 8) Enable constraints back (bez retro-provere)
ALTER TABLE dbo.member     CHECK CONSTRAINT ALL;
ALTER TABLE dbo.appointment CHECK CONSTRAINT ALL;
ALTER TABLE dbo.reservation CHECK CONSTRAINT ALL;
