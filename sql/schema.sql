-- Flight Reservation System Database Schema
-- Run this script to create the database and tables

-- Create database
CREATE DATABASE IF NOT EXISTS flight_reservation;
USE flight_reservation;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Flights table
CREATE TABLE IF NOT EXISTS flights (
    id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(10) NOT NULL UNIQUE,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    available_seats INT NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    number_of_seats INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE
);

-- Sample data for testing
INSERT INTO users (username, email, phone) VALUES
    ('john_doe', 'john@example.com', '555-0101'),
    ('jane_smith', 'jane@example.com', '555-0102'),
    ('bob_wilson', 'bob@example.com', '555-0103');

INSERT INTO flights (flight_number, origin, destination, departure_time, arrival_time, available_seats, price) VALUES
    ('FL001', 'New York', 'Los Angeles', '2024-01-15 08:00:00', '2024-01-15 11:30:00', 150, 299.99),
    ('FL002', 'Chicago', 'Miami', '2024-01-15 10:00:00', '2024-01-15 14:00:00', 120, 249.99),
    ('FL003', 'Seattle', 'Denver', '2024-01-15 12:00:00', '2024-01-15 15:00:00', 100, 199.99),
    ('FL004', 'Boston', 'San Francisco', '2024-01-15 14:00:00', '2024-01-15 18:30:00', 80, 349.99),
    ('FL005', 'Dallas', 'Atlanta', '2024-01-15 16:00:00', '2024-01-15 19:00:00', 200, 179.99);

INSERT INTO bookings (user_id, flight_id, number_of_seats, status) VALUES
    (1, 1, 2, 'CONFIRMED'),
    (2, 3, 1, 'CONFIRMED'),
    (3, 5, 3, 'PENDING');
