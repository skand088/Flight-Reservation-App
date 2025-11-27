-- Flight Reservation System Database Schema
-- MySQL Database Creation Script

-- Create database
CREATE DATABASE IF NOT EXISTS flight_reservation_db;
USE flight_reservation_db;

-- ============================================================================
-- CORE USER TABLES
-- ============================================================================

-- Table: users (Base user table for all system users)
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    role ENUM('CUSTOMER', 'AGENT', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    account_status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED') DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_account_status (account_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: customers (Extended information for customer users)
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY,
    frequent_flyer_number VARCHAR(50) UNIQUE,
    loyalty_points INT DEFAULT 0,
    preferred_airline VARCHAR(100),
    address TEXT,
    FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_frequent_flyer (frequent_flyer_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: flight_agents (Extended information for agent users)
CREATE TABLE IF NOT EXISTS flight_agents (
    agent_id INT PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100),
    hire_date DATE NOT NULL,
    FOREIGN KEY (agent_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: system_administrators (Extended information for admin users)
CREATE TABLE IF NOT EXISTS system_administrators (
    admin_id INT PRIMARY KEY,
    admin_level ENUM('SUPER_ADMIN', 'ADMIN', 'MODERATOR') DEFAULT 'ADMIN',
    permissions TEXT,
    FOREIGN KEY (admin_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- AIRLINE AND AIRCRAFT TABLES
-- ============================================================================

-- Table: airlines
CREATE TABLE IF NOT EXISTS airlines (
    airline_id INT PRIMARY KEY AUTO_INCREMENT,
    airline_name VARCHAR(100) NOT NULL UNIQUE,
    airline_code VARCHAR(10) NOT NULL UNIQUE,
    contact_info TEXT,
    INDEX idx_airline_code (airline_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: aircraft
CREATE TABLE IF NOT EXISTS aircraft (
    aircraft_id INT PRIMARY KEY AUTO_INCREMENT,
    tail_number VARCHAR(20) NOT NULL UNIQUE,
    model VARCHAR(50) NOT NULL,
    manufacturer VARCHAR(50) NOT NULL,
    total_seats INT NOT NULL,
    seat_configuration TEXT,
    INDEX idx_tail_number (tail_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- ROUTE AND FLIGHT TABLES
-- ============================================================================

-- Table: routes
CREATE TABLE IF NOT EXISTS routes (
    route_id INT PRIMARY KEY AUTO_INCREMENT,
    origin_airport VARCHAR(10) NOT NULL,
    destination_airport VARCHAR(10) NOT NULL,
    distance INT,
    estimated_duration INT,
    INDEX idx_origin (origin_airport),
    INDEX idx_destination (destination_airport),
    UNIQUE KEY unique_route (origin_airport, destination_airport)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: flights
CREATE TABLE IF NOT EXISTS flights (
    flight_id INT PRIMARY KEY AUTO_INCREMENT,
    flight_number VARCHAR(20) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    duration INT,
    status ENUM('SCHEDULED', 'BOARDING', 'DEPARTED', 'ARRIVED', 'DELAYED', 'CANCELLED') DEFAULT 'SCHEDULED',
    base_price DECIMAL(10, 2) NOT NULL,
    available_seats INT NOT NULL,
    aircraft_id INT NOT NULL,
    route_id INT NOT NULL,
    airline_id INT NOT NULL,
    FOREIGN KEY (aircraft_id) REFERENCES aircraft(aircraft_id) ON DELETE RESTRICT,
    FOREIGN KEY (route_id) REFERENCES routes(route_id) ON DELETE RESTRICT,
    FOREIGN KEY (airline_id) REFERENCES airlines(airline_id) ON DELETE RESTRICT,
    INDEX idx_flight_number (flight_number),
    INDEX idx_departure_time (departure_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- SEAT MANAGEMENT
-- ============================================================================

-- Table: seats
CREATE TABLE IF NOT EXISTS seats (
    seat_id INT PRIMARY KEY AUTO_INCREMENT,
    seat_number VARCHAR(10) NOT NULL,
    seat_class ENUM('ECONOMY', 'BUSINESS', 'FIRST') NOT NULL DEFAULT 'ECONOMY',
    seat_type ENUM('WINDOW', 'MIDDLE', 'AISLE') NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    status ENUM('AVAILABLE', 'RESERVED', 'OCCUPIED', 'BLOCKED') DEFAULT 'AVAILABLE',
    flight_id INT NOT NULL,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE,
    INDEX idx_flight_seat (flight_id, seat_number),
    INDEX idx_seat_status (status),
    UNIQUE KEY unique_seat_per_flight (flight_id, seat_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- PASSENGER AND RESERVATION TABLES
-- ============================================================================

-- Table: passengers
CREATE TABLE IF NOT EXISTS passengers (
    passenger_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT,
    id_number VARCHAR(50) NOT NULL,
    id_type ENUM('PASSPORT', 'NATIONAL_ID', 'DRIVERS_LICENSE') NOT NULL,
    contact_email VARCHAR(100),
    contact_phone VARCHAR(20),
    INDEX idx_id_number (id_number),
    INDEX idx_last_name (last_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: reservations
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    confirmation_number VARCHAR(20) NOT NULL UNIQUE,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
    total_fare DECIMAL(10, 2) NOT NULL,
    customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE RESTRICT,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE RESTRICT,
    INDEX idx_confirmation_number (confirmation_number),
    INDEX idx_reservation_date (reservation_date),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: reservation_passengers (Junction table for many-to-many relationship)
CREATE TABLE IF NOT EXISTS reservation_passengers (
    reservation_id INT NOT NULL,
    passenger_id INT NOT NULL,
    seat_id INT NOT NULL,
    PRIMARY KEY (reservation_id, passenger_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id) ON DELETE RESTRICT,
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_seat_assignment (seat_id),
    INDEX idx_passenger_id (passenger_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- PAYMENT TABLES
-- ============================================================================

-- Table: payments
CREATE TABLE IF NOT EXISTS payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH') NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_id VARCHAR(100) UNIQUE,
    reservation_id INT NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE RESTRICT,
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_payment_date (payment_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- NOTIFICATION AND SESSION TABLES
-- ============================================================================

-- Table: newsletters (for admin to customer messages)
CREATE TABLE IF NOT EXISTS newsletters (
    newsletter_id INT PRIMARY KEY AUTO_INCREMENT,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    sent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sent_date (sent_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: sessions (User session tracking)
CREATE TABLE IF NOT EXISTS sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id INT NOT NULL,
    login_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_last_activity (last_activity_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- VIEWS FOR COMMON QUERIES
-- ============================================================================

-- View: Complete flight information with route and airline details
CREATE OR REPLACE VIEW flight_details AS
SELECT 
    f.flight_id,
    f.flight_number,
    a.airline_name,
    a.airline_code,
    r.origin_airport,
    r.destination_airport,
    r.distance,
    f.departure_time,
    f.arrival_time,
    f.duration,
    f.status,
    f.base_price,
    f.available_seats,
    ac.model AS aircraft_model,
    ac.tail_number,
    ac.total_seats
FROM flights f
JOIN airlines a ON f.airline_id = a.airline_id
JOIN routes r ON f.route_id = r.route_id
JOIN aircraft ac ON f.aircraft_id = ac.aircraft_id;

-- View: Reservation summary with customer and flight details
CREATE OR REPLACE VIEW reservation_summary AS
SELECT 
    r.reservation_id,
    r.confirmation_number,
    r.reservation_date,
    r.status AS reservation_status,
    r.total_fare,
    u.username AS customer_username,
    u.email AS customer_email,
    u.phone_number AS customer_phone,
    c.frequent_flyer_number,
    f.flight_number,
    f.departure_time,
    f.arrival_time,
    rt.origin_airport,
    rt.destination_airport,
    al.airline_name
FROM reservations r
JOIN customers c ON r.customer_id = c.customer_id
JOIN users u ON c.customer_id = u.user_id
JOIN flights f ON r.flight_id = f.flight_id
JOIN routes rt ON f.route_id = rt.route_id
JOIN airlines al ON f.airline_id = al.airline_id;

-- View: Passenger manifest for flights
CREATE OR REPLACE VIEW passenger_manifest AS
SELECT 
    f.flight_id,
    f.flight_number,
    f.departure_time,
    p.passenger_id,
    p.first_name,
    p.last_name,
    p.id_number,
    p.id_type,
    s.seat_number,
    s.seat_class,
    r.confirmation_number,
    r.status AS reservation_status
FROM reservation_passengers rp
JOIN reservations r ON rp.reservation_id = r.reservation_id
JOIN passengers p ON rp.passenger_id = p.passenger_id
JOIN seats s ON rp.seat_id = s.seat_id
JOIN flights f ON r.flight_id = f.flight_id
ORDER BY f.flight_id, s.seat_number;

-- ============================================================================
-- INITIAL DATA - TEST USERS
-- ============================================================================

-- Insert test users for all three roles
INSERT INTO users (username, password_hash, email, phone_number, role, account_status) VALUES
('admin', 'admin123', 'admin@flightreservation.com', '+1-800-ADMIN', 'ADMIN', 'ACTIVE'),
('agent1', 'agent123', 'agent1@flightreservation.com', '+1-800-AGENT', 'AGENT', 'ACTIVE'),
('customer1', 'customer123', 'customer1@email.com', '+1-555-0101', 'CUSTOMER', 'ACTIVE'),
('customer2', 'customer123', 'customer2@email.com', '+1-555-0102', 'CUSTOMER', 'ACTIVE')
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Get the user IDs for role-specific tables
SET @admin_id = (SELECT user_id FROM users WHERE username = 'admin');
SET @agent_id = (SELECT user_id FROM users WHERE username = 'agent1');
SET @customer1_id = (SELECT user_id FROM users WHERE username = 'customer1');
SET @customer2_id = (SELECT user_id FROM users WHERE username = 'customer2');

-- Insert admin details
INSERT INTO system_administrators (admin_id, admin_level, permissions) VALUES
(@admin_id, 'SUPER_ADMIN', 'ALL')
ON DUPLICATE KEY UPDATE admin_id=admin_id;

-- Insert agent details
INSERT INTO flight_agents (agent_id, employee_id, department, hire_date) VALUES
(@agent_id, 'EMP001', 'Customer Service', '2024-01-15')
ON DUPLICATE KEY UPDATE agent_id=agent_id;

-- Insert customer details
INSERT INTO customers (customer_id, frequent_flyer_number, loyalty_points, preferred_airline, address) VALUES
(@customer1_id, 'FF1234567', 5000, 'American Airlines', '123 Main St, New York, NY 10001'),
(@customer2_id, 'FF7654321', 2500, 'Delta Air Lines', '456 Oak Ave, Los Angeles, CA 90001')
ON DUPLICATE KEY UPDATE customer_id=customer_id;

-- ============================================================================
-- SAMPLE DATA - AIRLINES
-- ============================================================================

INSERT INTO airlines (airline_name, airline_code, contact_info) VALUES
('American Airlines', 'AA', 'contact@aa.com | +1-800-433-7300'),
('Delta Air Lines', 'DL', 'contact@delta.com | +1-800-221-1212'),
('United Airlines', 'UA', 'contact@united.com | +1-800-864-8331'),
('British Airways', 'BA', 'contact@ba.com | +44-344-493-0787'),
('Lufthansa', 'LH', 'contact@lufthansa.com | +49-69-86799799'),
('Emirates', 'EK', 'contact@emirates.com | +971-4-214-4444'),
('Air France', 'AF', 'contact@airfrance.com | +33-1-57-02-1000')
ON DUPLICATE KEY UPDATE airline_id=airline_id;

-- ============================================================================
-- SAMPLE DATA - AIRCRAFT
-- ============================================================================

INSERT INTO aircraft (tail_number, model, manufacturer, total_seats, seat_configuration) VALUES
('N12345', 'Boeing 737-800', 'Boeing', 189, '16 Business, 173 Economy'),
('N67890', 'Boeing 777-300ER', 'Boeing', 396, '42 Business, 354 Economy'),
('N13579', 'Airbus A320', 'Airbus', 180, '12 Business, 168 Economy'),
('N24680', 'Airbus A380', 'Airbus', 525, '85 Business, 440 Economy'),
('N98765', 'Boeing 787-9', 'Boeing', 290, '30 Business, 260 Economy'),
('N54321', 'Airbus A350-900', 'Airbus', 325, '48 Business, 277 Economy')
ON DUPLICATE KEY UPDATE aircraft_id=aircraft_id;

-- ============================================================================
-- SAMPLE DATA - ROUTES
-- ============================================================================

INSERT INTO routes (origin_airport, destination_airport, distance, estimated_duration) VALUES
('JFK', 'LAX', 2475, 360),  -- New York to Los Angeles
('LAX', 'ORD', 1745, 240),  -- Los Angeles to Chicago
('ORD', 'JFK', 740, 120),   -- Chicago to New York
('JFK', 'LHR', 3459, 420),  -- New York to London
('LHR', 'CDG', 215, 75),    -- London to Paris
('CDG', 'FRA', 276, 85),    -- Paris to Frankfurt
('DXB', 'JFK', 6840, 840),  -- Dubai to New York
('SFO', 'MIA', 2585, 330)   -- San Francisco to Miami
ON DUPLICATE KEY UPDATE route_id=route_id;

-- ============================================================================
-- SAMPLE DATA - FLIGHTS
-- ============================================================================

INSERT INTO flights (flight_number, departure_time, arrival_time, duration, status, base_price, available_seats, aircraft_id, route_id, airline_id) VALUES
('AA101', '2025-12-01 08:00:00', '2025-12-01 14:00:00', 360, 'SCHEDULED', 299.99, 189, 1, 1, 1),
('DL202', '2025-12-01 14:00:00', '2025-12-01 18:00:00', 240, 'SCHEDULED', 249.99, 180, 3, 2, 2),
('UA303', '2025-12-02 09:30:00', '2025-12-02 11:30:00', 120, 'SCHEDULED', 199.99, 189, 1, 3, 3),
('BA404', '2025-12-03 18:00:00', '2025-12-04 06:00:00', 420, 'SCHEDULED', 599.99, 396, 2, 4, 4),
('AF505', '2025-12-04 11:00:00', '2025-12-04 12:15:00', 75, 'SCHEDULED', 149.99, 180, 3, 5, 7),
('LH606', '2025-12-05 15:00:00', '2025-12-05 16:25:00', 85, 'SCHEDULED', 129.99, 189, 1, 6, 5),
('EK707', '2025-12-06 03:00:00', '2025-12-06 17:00:00', 840, 'SCHEDULED', 899.99, 525, 4, 7, 6),
('AA808', '2025-12-07 07:00:00', '2025-12-07 12:30:00', 330, 'SCHEDULED', 349.99, 290, 5, 8, 1)
ON DUPLICATE KEY UPDATE flight_id=flight_id;

-- ============================================================================
-- SAMPLE DATA - PASSENGERS
-- ============================================================================

INSERT INTO passengers (first_name, last_name, age, id_number, id_type, contact_email, contact_phone) VALUES
('John', 'Doe', 40, 'P12345678', 'PASSPORT', 'john.doe@email.com', '+1-555-0101'),
('Jane', 'Smith', 35, 'P23456789', 'PASSPORT', 'jane.smith@email.com', '+1-555-0102'),
('Michael', 'Johnson', 37, 'P34567890', 'PASSPORT', 'michael.j@email.com', '+1-555-0103'),
('Emily', 'Williams', 33, 'P45678901', 'PASSPORT', 'emily.w@email.com', '+44-20-1234-5678'),
('David', 'Brown', 38, 'P56789012', 'PASSPORT', 'david.b@email.com', '+1-555-0105')
ON DUPLICATE KEY UPDATE passenger_id=passenger_id;

-- ============================================================================
-- SAMPLE DATA - SEATS
-- ============================================================================

-- Generate seats for Flight 1 (AA101 - Boeing 737-800, 189 seats)
INSERT INTO seats (seat_number, seat_class, seat_type, price, seat_status, flight_id) VALUES
('1A', 'BUSINESS', 'WINDOW', 899.99, 'AVAILABLE', 1),
('1B', 'BUSINESS', 'MIDDLE', 899.99, 'AVAILABLE', 1),
('1C', 'BUSINESS', 'AISLE', 899.99, 'AVAILABLE', 1),
('12A', 'ECONOMY', 'WINDOW', 299.99, 'RESERVED', 1),
('12B', 'ECONOMY', 'MIDDLE', 299.99, 'AVAILABLE', 1),
('12C', 'ECONOMY', 'AISLE', 299.99, 'AVAILABLE', 1),
('15A', 'ECONOMY', 'WINDOW', 299.99, 'AVAILABLE', 1),
('15B', 'ECONOMY', 'MIDDLE', 299.99, 'AVAILABLE', 1),
('15C', 'ECONOMY', 'AISLE', 299.99, 'AVAILABLE', 1)
ON DUPLICATE KEY UPDATE seat_id=seat_id;

-- ============================================================================
-- SAMPLE DATA - RESERVATIONS
-- ============================================================================

INSERT INTO reservations (confirmation_number, reservation_date, status, total_fare, customer_id, flight_id) VALUES
('RES001ABC', '2025-11-20 10:30:00', 'CONFIRMED', 299.99, @customer1_id, 1),
('RES002DEF', '2025-11-21 14:15:00', 'CONFIRMED', 249.99, @customer2_id, 2)
ON DUPLICATE KEY UPDATE reservation_id=reservation_id;

-- Get reservation IDs
SET @res1_id = (SELECT reservation_id FROM reservations WHERE confirmation_number = 'RES001ABC');
SET @res2_id = (SELECT reservation_id FROM reservations WHERE confirmation_number = 'RES002DEF');

-- ============================================================================
-- SAMPLE DATA - RESERVATION PASSENGERS
-- ============================================================================

INSERT INTO reservation_passengers (reservation_id, passenger_id, seat_id) VALUES
(@res1_id, 1, 4),  -- John Doe on seat 12A
(@res2_id, 2, 1)   -- Jane Smith on seat 1A (business class on flight 2)
ON DUPLICATE KEY UPDATE reservation_id=reservation_id;

-- ============================================================================
-- SAMPLE DATA - PAYMENTS
-- ============================================================================

INSERT INTO payments (payment_date, amount, payment_method, status, transaction_id, reservation_id) VALUES
('2025-11-20 10:35:00', 299.99, 'CREDIT_CARD', 'COMPLETED', 'TXN001234567890', @res1_id),
('2025-11-21 14:20:00', 249.99, 'DEBIT_CARD', 'COMPLETED', 'TXN001234567891', @res2_id)
ON DUPLICATE KEY UPDATE payment_id=payment_id;

COMMIT;

-- ============================================================================
-- DISPLAY SUMMARY
-- ============================================================================

SELECT '========================================' AS '';
SELECT 'DATABASE SETUP COMPLETE!' AS '';
SELECT '========================================' AS '';
SELECT '' AS '';
SELECT 'Test User Credentials:' AS '';
SELECT '  Admin: admin / admin123' AS '';
SELECT '  Agent: agent1 / agent123' AS '';
SELECT '  Customer: customer1 / customer123' AS '';
SELECT '  Customer: customer2 / customer123' AS '';
SELECT '' AS '';
SELECT 'Sample Data Loaded:' AS '';
SELECT CONCAT('  Airlines: ', COUNT(*), ' records') AS '' FROM airlines;
SELECT CONCAT('  Aircraft: ', COUNT(*), ' records') AS '' FROM aircraft;
SELECT CONCAT('  Routes: ', COUNT(*), ' records') AS '' FROM routes;
SELECT CONCAT('  Flights: ', COUNT(*), ' records') AS '' FROM flights;
SELECT CONCAT('  Users: ', COUNT(*), ' records') AS '' FROM users;
SELECT CONCAT('  Reservations: ', COUNT(*), ' records') AS '' FROM reservations;
SELECT '' AS '';
SELECT 'Ready to use!' AS '';
