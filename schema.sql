-- ==========================================================
-- College Student Registration Database Schema
-- Compatible with MySQL and MariaDB
-- ==========================================================

-- 1. Create database if it doesn't already exist
CREATE DATABASE IF NOT EXISTS college_db;
USE college_db;

-- 2. Create the students table
-- Primary Key on student_id enforces uniqueness and prevents duplicates
CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(20) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    year_of_study INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Optional sample records for quick testing
INSERT INTO students (student_id, name, department, year_of_study, email)
VALUES 
    ('STU2024001', 'Aarav Sharma', 'Computer Science', 2, 'aarav.sharma@college.edu'),
    ('STU2024002', 'Priya Patel', 'Electronics', 3, 'priya.patel@college.edu'),
    ('STU2024003', 'Rohan Verma', 'Information Tech', 1, 'rohan.verma@college.edu')
ON DUPLICATE KEY UPDATE name=name;

-- 4. Verify table structure and content
DESCRIBE students;
SELECT * FROM students;
