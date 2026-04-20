-- ============================================
-- Garbage Collection Booking System - Schema
-- Database: garbage_booking_db
-- ============================================

CREATE DATABASE IF NOT EXISTS garbage_booking_db;
USE garbage_booking_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address TEXT,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    pickup_address TEXT NOT NULL,
    waste_type ENUM('GENERAL','RECYCLABLE','ORGANIC','HAZARDOUS','ELECTRONIC','CONSTRUCTION') NOT NULL,
    collection_date DATE NOT NULL,
    time_slot ENUM('MORNING','MID_MORNING','AFTERNOON','LATE_AFTERNOON','EVENING') NOT NULL,
    status ENUM('PENDING','CONFIRMED','COLLECTED','CANCELLED') DEFAULT 'PENDING',
    special_instructions TEXT,
    weight DECIMAL(10,2) DEFAULT 1.00,
    amount DOUBLE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_bookings_collection_date (collection_date),
    INDEX idx_bookings_status (status),
    INDEX idx_bookings_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL UNIQUE,
    amount DOUBLE NOT NULL,
    payment_method ENUM('CREDIT_CARD','DEBIT_CARD','NET_BANKING','CASH_ON_COLLECTION') NOT NULL,
    transaction_id VARCHAR(50) UNIQUE,
    status ENUM('PENDING','SUCCESS','FAILED','REFUNDED') DEFAULT 'PENDING',
    card_holder_name VARCHAR(100),
    card_last_four VARCHAR(4),
    payment_date DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_payments_status (status),
    INDEX idx_payments_created_at (created_at),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

-- Sample Admin User (password: admin123)
INSERT IGNORE INTO users (name, email, password, phone, address, role)
VALUES ('Admin', 'admin@greencollect.in', 'admin123', '9876543210', 'Chennai, Tamil Nadu', 'ADMIN');

-- Sample Test User (password: test1234)
INSERT IGNORE INTO users (name, email, password, phone, address, role)
VALUES ('Test User', 'test@greencollect.in', 'test1234', '9876543211', '12, Anna Nagar, Chennai - 600040', 'USER');
