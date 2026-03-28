-- Drug Interaction & Prescription Safety System
-- MySQL Database Schema

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS drug_interaction_db;
USE drug_interaction_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS interaction;
DROP TABLE IF EXISTS drug;

-- Create Drug table
CREATE TABLE drug (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_drug_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create Interaction table
CREATE TABLE interaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    drug1_id BIGINT NOT NULL,
    drug2_id BIGINT NOT NULL,
    severity VARCHAR(10) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (drug1_id) REFERENCES drug(id) ON DELETE CASCADE,
    FOREIGN KEY (drug2_id) REFERENCES drug(id) ON DELETE CASCADE,
    UNIQUE KEY unique_interaction (drug1_id, drug2_id),
    INDEX idx_severity (severity),
    INDEX idx_drug1 (drug1_id),
    INDEX idx_drug2 (drug2_id),
    CONSTRAINT chk_severity CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample drugs
INSERT INTO drug (name, description) VALUES 
('Warfarin', 'Anticoagulant used to prevent blood clots'),
('Aspirin', 'Nonsteroidal anti-inflammatory drug (NSAID) used for pain, fever, and inflammation'),
('Ibuprofen', 'NSAID used for pain relief and inflammation'),
('Metformin', 'Oral diabetes medicine used to control blood sugar'),
('Lisinopril', 'ACE inhibitor used to treat high blood pressure'),
('Simvastatin', 'Statin used to lower cholesterol'),
('Omeprazole', 'Proton pump inhibitor used to reduce stomach acid'),
('Amoxicillin', 'Penicillin-type antibiotic used to treat infections'),
('Amlodipine', 'Calcium channel blocker used for high blood pressure'),
('Metoprolol', 'Beta blocker used for heart conditions');

-- Insert sample interactions
INSERT INTO interaction (drug1_id, drug2_id, severity, description) VALUES 
((SELECT id FROM drug WHERE name = 'Warfarin'), 
 (SELECT id FROM drug WHERE name = 'Aspirin'), 
 'HIGH', 
 'Concurrent use of Warfarin and Aspirin significantly increases the risk of bleeding. Monitor closely or avoid combination.'),

((SELECT id FROM drug WHERE name = 'Aspirin'), 
 (SELECT id FROM drug WHERE name = 'Ibuprofen'), 
 'HIGH', 
 'Ibuprofen can reduce the anticoagulant effect of Aspirin. Avoid using together or use with extreme caution.'),

((SELECT id FROM drug WHERE name = 'Metformin'), 
 (SELECT id FROM drug WHERE name = 'Omeprazole'), 
 'MEDIUM', 
 'Omeprazole may reduce the effectiveness of Metformin. Monitor blood sugar levels.'),

((SELECT id FROM drug WHERE name = 'Warfarin'), 
 (SELECT id FROM drug WHERE name = 'Ibuprofen'), 
 'HIGH', 
 'Concurrent use of Warfarin and Ibuprofen increases bleeding risk significantly. Avoid combination if possible.'),

((SELECT id FROM drug WHERE name = 'Simvastatin'), 
 (SELECT id FROM drug WHERE name = 'Amlodipine'), 
 'MEDIUM', 
 'Simvastatin levels may be increased by Amlodipine. Consider dose adjustment.'),

((SELECT id FROM drug WHERE name = 'Lisinopril'), 
 (SELECT id FROM drug WHERE name = 'Amlodipine'), 
 'LOW', 
 'Minor interaction. Lisinopril and Amlodipine may be used together under medical supervision.'),

((SELECT id FROM drug WHERE name = 'Metoprolol'), 
 (SELECT id FROM drug WHERE name = 'Amlodipine'), 
 'LOW', 
 'Metoprolol and Amlodipine can be used together. Monitor heart rate and blood pressure.'),

((SELECT id FROM drug WHERE name = 'Lisinopril'), 
 (SELECT id FROM drug WHERE name = 'Omeprazole'), 
 'MEDIUM', 
 'Omeprazole may slightly reduce Lisinopril effectiveness. Monitor blood pressure.');
