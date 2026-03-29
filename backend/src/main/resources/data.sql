-- Sample Drugs
INSERT IGNORE INTO drug (name, description) VALUES 
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

-- Sample Interactions
-- Note: We need to get the IDs of the drugs first
INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'HIGH', 'Concurrent use of Warfarin and Aspirin significantly increases the risk of bleeding. Monitor closely or avoid combination.'
FROM drug d1, drug d2 
WHERE d1.name = 'Warfarin' AND d2.name = 'Aspirin';

INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'HIGH', 'Ibuprofen can reduce the anticoagulant effect of Aspirin. Avoid using together or use with extreme caution.'
FROM drug d1, drug d2 
WHERE d1.name = 'Aspirin' AND d2.name = 'Ibuprofen';

INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'MEDIUM', 'Omeprazole may reduce the effectiveness of Metformin. Monitor blood sugar levels.'
FROM drug d1, drug d2 
WHERE d1.name = 'Metformin' AND d2.name = 'Omeprazole';

INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'HIGH', 'Concurrent use of Warfarin and Ibuprofen increases bleeding risk significantly. Avoid combination if possible.'
FROM drug d1, drug d2 
WHERE d1.name = 'Warfarin' AND d2.name = 'Ibuprofen';

INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'MEDIUM', 'Simvastatin levels may be increased by Amlodipine. Consider dose adjustment.'
FROM drug d1, drug d2 
WHERE d1.name = 'Simvastatin' AND d2.name = 'Amlodipine';

INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'LOW', 'Minor interaction. Lisinopril and Amlodipine may be used together under medical supervision.'
FROM drug d1, drug d2 
WHERE d1.name = 'Lisinopril' AND d2.name = 'Amlodipine';

INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'LOW', 'Metoprolol and Amlodipine can be used together. Monitor heart rate and blood pressure.'
FROM drug d1, drug d2 
WHERE d1.name = 'Metoprolol' AND d2.name = 'Amlodipine';

INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'MEDIUM', 'Omeprazole may slightly reduce Lisinopril effectiveness. Monitor blood pressure.'
FROM drug d1, drug d2 
WHERE d1.name = 'Lisinopril' AND d2.name = 'Omeprazole';

INSERT IGNORE INTO interaction (drug1_id, drug2_id, severity, description) 
SELECT d1.id, d2.id, 'HIGH', 'Amoxicillin (antibiotic) effectiveness may be reduced when taken with Metronidazole if not properly prescribed.'
FROM drug d1, drug d2 
WHERE d1.name = 'Amoxicillin' AND d2.name = 'Metformin';
