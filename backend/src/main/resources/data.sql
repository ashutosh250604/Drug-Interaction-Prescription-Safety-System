-- Seed data for the Drug Interaction System.
-- The script runs on every start, so every statement is repeatable: drugs use
-- ON DUPLICATE KEY and interactions are only inserted when that pair is absent.
-- Descriptions here summarise well documented pharmacology and are written for
-- a clinical reference tool, not as prescribing advice.

-- ---------------------------------------------------------------------------
-- Drugs
-- ---------------------------------------------------------------------------
INSERT INTO drug (name, drug_class, description) VALUES
('Warfarin', 'Anticoagulant', 'Vitamin K antagonist anticoagulant. Requires INR monitoring and dose titration.'),
('Apixaban', 'Anticoagulant', 'Direct factor Xa inhibitor used for atrial fibrillation and venous thromboembolism.'),
('Aspirin', 'Antiplatelet', 'Irreversible COX-1 inhibitor used as an antiplatelet at low dose and as an NSAID at higher doses.'),
('Clopidogrel', 'Antiplatelet', 'P2Y12 inhibitor prodrug that requires CYP2C19 activation.'),
('Ibuprofen', 'NSAID', 'Non-selective COX inhibitor used for pain, fever and inflammation.'),
('Naproxen', 'NSAID', 'Long acting non-selective COX inhibitor.'),
('Paracetamol', 'Analgesic', 'Centrally acting analgesic and antipyretic with minimal COX inhibition at usual doses.'),
('Metformin', 'Biguanide', 'First line oral agent for type 2 diabetes. Renally cleared.'),
('Glipizide', 'Sulfonylurea', 'Insulin secretagogue with a risk of hypoglycaemia.'),
('Insulin glargine', 'Insulin', 'Long acting basal insulin.'),
('Lisinopril', 'ACE inhibitor', 'ACE inhibitor used for hypertension, heart failure and diabetic nephropathy.'),
('Losartan', 'ARB', 'Angiotensin receptor blocker used for hypertension and renal protection.'),
('Amlodipine', 'Calcium channel blocker', 'Dihydropyridine calcium channel blocker used for hypertension and angina.'),
('Metoprolol', 'Beta blocker', 'Cardioselective beta adrenergic blocker.'),
('Furosemide', 'Loop diuretic', 'Loop diuretic used for oedema and heart failure. Causes potassium and magnesium loss.'),
('Hydrochlorothiazide', 'Thiazide diuretic', 'Thiazide diuretic used for hypertension.'),
('Spironolactone', 'Potassium-sparing diuretic', 'Mineralocorticoid receptor antagonist that retains potassium.'),
('Simvastatin', 'Statin', 'CYP3A4 metabolised statin with dose limits when combined with inhibitors.'),
('Atorvastatin', 'Statin', 'CYP3A4 metabolised statin used for cholesterol lowering.'),
('Omeprazole', 'Proton pump inhibitor', 'Irreversible proton pump inhibitor that inhibits CYP2C19.'),
('Amoxicillin', 'Penicillin antibiotic', 'Broad spectrum penicillin antibiotic.'),
('Clarithromycin', 'Macrolide antibiotic', 'Macrolide antibiotic and strong CYP3A4 inhibitor. Prolongs the QT interval.'),
('Ciprofloxacin', 'Fluoroquinolone antibiotic', 'Fluoroquinolone antibiotic with CYP1A2 inhibition and QT effects.'),
('Metronidazole', 'Nitroimidazole antibiotic', 'Antibiotic for anaerobic and protozoal infections. Inhibits CYP2C9.'),
('Co-trimoxazole', 'Sulfonamide antibiotic', 'Trimethoprim and sulfamethoxazole combination. Inhibits CYP2C9 and folate metabolism.'),
('Sertraline', 'SSRI', 'Selective serotonin reuptake inhibitor.'),
('Fluoxetine', 'SSRI', 'Long acting SSRI and moderate CYP2D6 inhibitor.'),
('Amitriptyline', 'Tricyclic antidepressant', 'Tricyclic antidepressant with anticholinergic and QT effects.'),
('Tramadol', 'Opioid analgesic', 'Weak opioid with serotonin and noradrenaline reuptake inhibition. Lowers the seizure threshold.'),
('Morphine', 'Opioid analgesic', 'Strong opioid agonist used for severe pain.'),
('Digoxin', 'Cardiac glycoside', 'Cardiac glycoside with a narrow therapeutic index. Cleared renally and by P-glycoprotein.'),
('Amiodarone', 'Antiarrhythmic', 'Class III antiarrhythmic with a very long half-life. Inhibits CYP3A4, CYP2C9 and P-glycoprotein.'),
('Verapamil', 'Calcium channel blocker', 'Non-dihydropyridine calcium channel blocker with negative chronotropic effects.'),
('Lithium', 'Mood stabiliser', 'Narrow therapeutic index mood stabiliser cleared entirely by the kidney.'),
('Levothyroxine', 'Thyroid hormone', 'Synthetic thyroxine for hypothyroidism. Absorption depends on gastric acidity.'),
('Allopurinol', 'Xanthine oxidase inhibitor', 'Urate lowering therapy that inhibits xanthine oxidase.'),
('Azathioprine', 'Immunosuppressant', 'Thiopurine immunosuppressant metabolised by xanthine oxidase.'),
('Methotrexate', 'DMARD', 'Antifolate used in inflammatory disease at low dose and in oncology at high dose. Renally cleared.'),
('Prednisolone', 'Corticosteroid', 'Systemic corticosteroid with metabolic and gastrointestinal effects.'),
('Carbamazepine', 'Anticonvulsant', 'Anticonvulsant and strong CYP3A4 inducer.')
ON DUPLICATE KEY UPDATE drug_class = VALUES(drug_class), description = VALUES(description);

-- ---------------------------------------------------------------------------
-- Remove rows written by earlier versions of this seed file so the curated
-- versions below replace them. Only the old seed wording is matched, so any
-- interaction added through the app is left untouched.
-- ---------------------------------------------------------------------------
DELETE FROM interaction WHERE description LIKE 'Concurrent use of Warfarin and Aspirin%';
DELETE FROM interaction WHERE description LIKE 'Ibuprofen can reduce the anticoagulant effect of Aspirin%';
DELETE FROM interaction WHERE description LIKE 'Omeprazole may reduce the effectiveness of Metformin%';
DELETE FROM interaction WHERE description LIKE 'Concurrent use of Warfarin and Ibuprofen%';
DELETE FROM interaction WHERE description LIKE 'Simvastatin levels may be increased by Amlodipine%';
DELETE FROM interaction WHERE description LIKE 'Minor interaction. Lisinopril and Amlodipine%';
DELETE FROM interaction WHERE description LIKE 'Metoprolol and Amlodipine can be used together%';
DELETE FROM interaction WHERE description LIKE 'Omeprazole may slightly reduce Lisinopril%';
DELETE FROM interaction WHERE description LIKE 'Antibiotics such as Amoxicillin can disturb gut bacteria%';

-- ---------------------------------------------------------------------------
-- Interactions
-- severity, what happens, why it happens, what to do, evidence strength
-- ---------------------------------------------------------------------------
INSERT INTO interaction (drug1_id, drug2_id, severity, description, mechanism, management, evidence)
SELECT d1.id, d2.id, s.severity, s.description, s.mechanism, s.management, s.evidence
FROM (
    SELECT 'Warfarin' AS n1, 'Aspirin' AS n2, 'HIGH' AS severity,
           'Bleeding risk rises sharply. Aspirin also injures the stomach lining.' AS description,
           'Aspirin blocks platelet COX-1 while warfarin reduces vitamin K dependent clotting factors II, VII, IX and X.' AS mechanism,
           'Avoid the combination. If it is required, add a proton pump inhibitor and check INR more often.' AS management,
           'Established' AS evidence
    UNION ALL SELECT 'Warfarin', 'Ibuprofen', 'HIGH',
        'Additive bleeding risk with direct gastric mucosal injury.',
        'NSAIDs inhibit platelet COX-1 and damage the gastroduodenal mucosa on top of warfarin anticoagulation.',
        'Use paracetamol for analgesia. If unavoidable, add gastroprotection and monitor INR and haemoglobin.',
        'Established'
    UNION ALL SELECT 'Warfarin', 'Naproxen', 'HIGH',
        'Additive bleeding risk with direct gastric mucosal injury.',
        'Naproxen inhibits platelet COX-1 and irritates the gastric mucosa, adding to anticoagulation.',
        'Prefer paracetamol. If the NSAID is essential, add a proton pump inhibitor and monitor INR.',
        'Established'
    UNION ALL SELECT 'Warfarin', 'Clarithromycin', 'HIGH',
        'INR can rise within days and bleeding has been reported.',
        'Clarithromycin inhibits CYP3A4 and CYP2C9, slowing warfarin metabolism.',
        'Choose a non-interacting antibiotic where possible. Otherwise check INR within 3 to 5 days and adjust the dose.',
        'Established'
    UNION ALL SELECT 'Warfarin', 'Metronidazole', 'HIGH',
        'INR rises and bleeding risk increases during the antibiotic course.',
        'Metronidazole inhibits CYP2C9, the main enzyme clearing S-warfarin.',
        'Reduce the warfarin dose pre-emptively and check INR after 3 to 5 days.',
        'Established'
    UNION ALL SELECT 'Warfarin', 'Co-trimoxazole', 'HIGH',
        'Marked INR elevation and bleeding during the antibiotic course.',
        'Trimethoprim inhibits CYP2C9 and the sulfonamide displaces warfarin from albumin.',
        'Prefer another antibiotic. If used, reduce warfarin and monitor INR closely.',
        'Established'
    UNION ALL SELECT 'Warfarin', 'Amiodarone', 'HIGH',
        'Warfarin effect climbs slowly over weeks and persists after amiodarone stops.',
        'Amiodarone inhibits CYP2C9 and has a half-life measured in weeks, so the effect accumulates.',
        'Reduce the warfarin dose by roughly a third to a half and check INR weekly.',
        'Established'
    UNION ALL SELECT 'Warfarin', 'Ciprofloxacin', 'MEDIUM',
        'INR can rise unpredictably during the course.',
        'Ciprofloxacin inhibits CYP1A2 and alters vitamin K producing gut flora.',
        'Monitor INR during the course and again after it ends.',
        'Moderate'
    UNION ALL SELECT 'Warfarin', 'Amoxicillin', 'MEDIUM',
        'INR may drift upward a few days into the course.',
        'Broad spectrum antibiotics reduce the gut bacteria that synthesise vitamin K.',
        'Check INR a few days into the course and adjust if needed.',
        'Moderate'
    UNION ALL SELECT 'Warfarin', 'Levothyroxine', 'MEDIUM',
        'Starting or increasing thyroid hormone raises INR.',
        'Thyroid hormone accelerates the catabolism of vitamin K dependent clotting factors.',
        'Check INR 1 to 2 weeks after a levothyroxine dose change.',
        'Established'
    UNION ALL SELECT 'Warfarin', 'Prednisolone', 'MEDIUM',
        'Variable INR effect with added gastrointestinal bleeding risk.',
        'Corticosteroids alter clotting factor turnover and impair gastric mucosal healing.',
        'Monitor INR and consider gastroprotection during the course.',
        'Moderate'
    UNION ALL SELECT 'Warfarin', 'Carbamazepine', 'HIGH',
        'Warfarin effect falls and INR drops, risking thromboembolism.',
        'Carbamazepine is a strong CYP3A4 and CYP2C9 inducer that accelerates warfarin metabolism.',
        'Monitor INR closely and expect to increase the warfarin dose. Recheck after carbamazepine stops.',
        'Established'
    UNION ALL SELECT 'Apixaban', 'Aspirin', 'HIGH',
        'Bleeding risk is additive across anticoagulant and antiplatelet pathways.',
        'Factor Xa inhibition combines with irreversible platelet COX-1 blockade.',
        'Avoid unless there is a clear indication. Add gastroprotection when the pair is used.',
        'Established'
    UNION ALL SELECT 'Apixaban', 'Ibuprofen', 'HIGH',
        'Gastrointestinal and systemic bleeding risk rises substantially.',
        'NSAID platelet inhibition and mucosal injury add to factor Xa inhibition.',
        'Prefer paracetamol. Ask the patient to report black stools or unusual bruising.',
        'Established'
    UNION ALL SELECT 'Apixaban', 'Clarithromycin', 'HIGH',
        'Apixaban exposure rises, increasing bleeding risk.',
        'Clarithromycin inhibits CYP3A4 and P-glycoprotein, the pathways that clear apixaban.',
        'Avoid the combination or reduce the apixaban dose as the product label directs.',
        'Established'
    UNION ALL SELECT 'Apixaban', 'Carbamazepine', 'HIGH',
        'Apixaban levels fall and anticoagulant protection may be lost.',
        'Carbamazepine induces CYP3A4 and P-glycoprotein, accelerating apixaban clearance.',
        'Avoid the combination. Choose an anticonvulsant that does not induce these pathways.',
        'Established'
    UNION ALL SELECT 'Aspirin', 'Ibuprofen', 'HIGH',
        'Aspirin loses part of its antiplatelet effect and both drugs irritate the stomach.',
        'Ibuprofen competes with aspirin at platelet COX-1 and both impair gastric mucosal defence.',
        'Do not take ibuprofen with low dose aspirin. Separating the doses does not reliably remove the interaction.',
        'Established'
    UNION ALL SELECT 'Aspirin', 'Naproxen', 'HIGH',
        'Antiplatelet effect of aspirin is blunted and gastrointestinal risk rises.',
        'Naproxen occupies COX-1 and adds mucosal injury to aspirin antiplatelet action.',
        'Avoid regular NSAID use with aspirin. Prefer paracetamol.',
        'Established'
    UNION ALL SELECT 'Aspirin', 'Clopidogrel', 'HIGH',
        'Dual antiplatelet therapy multiplies bleeding risk.',
        'Thromboxane and P2Y12 pathways are both blocked, so platelet aggregation is severely impaired.',
        'Use only for a defined indication and duration, with gastroprotection in higher risk patients.',
        'Established'
    UNION ALL SELECT 'Aspirin', 'Sertraline', 'MEDIUM',
        'Bruising and gastrointestinal bleeding become more likely.',
        'SSRIs deplete platelet serotonin, impairing aggregation on top of aspirin.',
        'Monitor for bruising and bleeding. Consider gastroprotection in older patients.',
        'Established'
    UNION ALL SELECT 'Aspirin', 'Fluoxetine', 'MEDIUM',
        'Bruising and gastrointestinal bleeding become more likely.',
        'Fluoxetine depletes platelet serotonin and inhibits CYP2C19, adding to aspirin bleeding risk.',
        'Monitor for bleeding and consider gastroprotection.',
        'Established'
    UNION ALL SELECT 'Clopidogrel', 'Omeprazole', 'HIGH',
        'Clopidogrel antiplatelet effect is reduced, raising cardiovascular risk.',
        'Omeprazole inhibits CYP2C19, the enzyme that converts clopidogrel to its active metabolite.',
        'Use pantoprazole or another proton pump inhibitor with less CYP2C19 inhibition.',
        'Established'
    UNION ALL SELECT 'Clopidogrel', 'Ibuprofen', 'MEDIUM',
        'Bleeding risk increases without a gain in benefit.',
        'Platelet P2Y12 blockade combines with NSAID COX inhibition and mucosal injury.',
        'Avoid regular NSAID use. Add gastroprotection if the pair is unavoidable.',
        'Moderate'
    UNION ALL SELECT 'Aspirin', 'Prednisolone', 'HIGH',
        'Peptic ulcer and gastrointestinal bleeding risk multiplies.',
        'Both drugs damage gastric mucosa and impair its repair.',
        'Add a proton pump inhibitor and monitor for dyspepsia or melaena.',
        'Established'
    UNION ALL SELECT 'Ibuprofen', 'Prednisolone', 'HIGH',
        'Peptic ulcer risk is substantially raised.',
        'NSAID mucosal injury combines with corticosteroid impairment of mucosal healing.',
        'Avoid the pair if possible. Otherwise add gastroprotection and review symptoms.',
        'Established'
    UNION ALL SELECT 'Ibuprofen', 'Lisinopril', 'HIGH',
        'Blood pressure control worsens and acute kidney injury becomes likely.',
        'NSAIDs block vasodilatory prostaglandins, reducing ACE inhibitor effect and renal perfusion.',
        'Avoid regular NSAID use. Monitor blood pressure and creatinine if the pair is unavoidable.',
        'Established'
    UNION ALL SELECT 'Ibuprofen', 'Lithium', 'HIGH',
        'Lithium levels can climb into the toxic range.',
        'NSAIDs reduce renal lithium clearance.',
        'Avoid the combination. If unavoidable, check lithium levels within a week.',
        'Established'
    UNION ALL SELECT 'Ibuprofen', 'Methotrexate', 'HIGH',
        'Methotrexate exposure rises, risking marrow suppression.',
        'NSAIDs reduce renal clearance of methotrexate.',
        'Avoid NSAIDs, especially with higher methotrexate doses. Use paracetamol instead.',
        'Established'
    UNION ALL SELECT 'Naproxen', 'Lithium', 'HIGH',
        'Lithium levels can climb into the toxic range.',
        'Naproxen reduces renal lithium clearance.',
        'Avoid the combination and check lithium levels if it is used.',
        'Established'
    UNION ALL SELECT 'Naproxen', 'Lisinopril', 'HIGH',
        'Blood pressure control worsens and kidney function may deteriorate.',
        'NSAID prostaglandin blockade opposes ACE inhibition and reduces renal perfusion.',
        'Avoid regular NSAID use and monitor creatinine if unavoidable.',
        'Established'
    UNION ALL SELECT 'Naproxen', 'Methotrexate', 'HIGH',
        'Methotrexate exposure rises, risking marrow suppression.',
        'Naproxen reduces renal clearance of methotrexate.',
        'Avoid NSAIDs with methotrexate. Use paracetamol for analgesia.',
        'Established'
    UNION ALL SELECT 'Ibuprofen', 'Spironolactone', 'HIGH',
        'Potassium rises and kidney function may fall.',
        'NSAID related potassium retention and reduced renal perfusion combine with mineralocorticoid blockade.',
        'Avoid the combination. If needed, monitor potassium and creatinine.',
        'Established'
    UNION ALL SELECT 'Ibuprofen', 'Furosemide', 'MEDIUM',
        'Diuretic effect weakens and kidney function may worsen.',
        'NSAIDs blunt natriuresis and reduce renal blood flow.',
        'Monitor weight, blood pressure and creatinine.',
        'Moderate'
    UNION ALL SELECT 'Aspirin', 'Methotrexate', 'HIGH',
        'Methotrexate toxicity risk rises, particularly at higher doses.',
        'Salicylates reduce renal tubular secretion of methotrexate.',
        'Avoid regular aspirin with methotrexate and monitor full blood count and renal function.',
        'Established'
    UNION ALL SELECT 'Lisinopril', 'Spironolactone', 'HIGH',
        'Hyperkalaemia risk is significant, especially in renal impairment.',
        'ACE inhibition reduces aldosterone while spironolactone blocks its receptor, so potassium is retained twice.',
        'Check potassium and creatinine within 1 to 2 weeks of starting or increasing either drug.',
        'Established'
    UNION ALL SELECT 'Lisinopril', 'Losartan', 'HIGH',
        'Hyperkalaemia and acute kidney injury risk rise without added benefit.',
        'Dual blockade of the renin angiotensin system compounds renal and potassium effects.',
        'Avoid combining an ACE inhibitor with an ARB.',
        'Established'
    UNION ALL SELECT 'Losartan', 'Spironolactone', 'HIGH',
        'Potassium can rise dangerously.',
        'Angiotensin receptor blockade plus mineralocorticoid receptor blockade both retain potassium.',
        'Monitor potassium and renal function closely.',
        'Established'
    UNION ALL SELECT 'Lisinopril', 'Furosemide', 'MEDIUM',
        'First doses can cause a steep fall in blood pressure.',
        'Diuretic induced volume depletion amplifies the hypotensive effect of ACE inhibition.',
        'Start at a low dose and review blood pressure and creatinine.',
        'Established'
    UNION ALL SELECT 'Lisinopril', 'Hydrochlorothiazide', 'LOW',
        'Blood pressure falls further, usually as intended.',
        'Complementary diuretic and ACE inhibitor effects on blood pressure.',
        'A common intentional combination. Monitor electrolytes and blood pressure.',
        'Established'
    UNION ALL SELECT 'Amlodipine', 'Simvastatin', 'MEDIUM',
        'Simvastatin exposure rises, increasing muscle injury risk.',
        'Amlodipine inhibits CYP3A4 and raises simvastatin levels.',
        'Cap simvastatin at 20 mg daily when combined with amlodipine.',
        'Established'
    UNION ALL SELECT 'Simvastatin', 'Clarithromycin', 'HIGH',
        'Rhabdomyolysis has been reported with this pair.',
        'Strong CYP3A4 inhibition raises simvastatin exposure many fold.',
        'Withhold simvastatin during the antibiotic course or choose azithromycin instead.',
        'Established'
    UNION ALL SELECT 'Atorvastatin', 'Clarithromycin', 'MEDIUM',
        'Atorvastatin exposure and myopathy risk increase.',
        'Clarithromycin inhibits CYP3A4 mediated atorvastatin metabolism.',
        'Use the lowest effective statin dose or pause it during the course.',
        'Established'
    UNION ALL SELECT 'Simvastatin', 'Amiodarone', 'MEDIUM',
        'Myopathy and rhabdomyolysis risk increase.',
        'Amiodarone inhibits CYP3A4 and adds its own myopathic effect.',
        'Cap simvastatin at 20 mg daily and ask about muscle pain or weakness.',
        'Established'
    UNION ALL SELECT 'Simvastatin', 'Verapamil', 'HIGH',
        'Simvastatin levels rise sharply, risking rhabdomyolysis.',
        'Verapamil is a moderate CYP3A4 inhibitor that raises simvastatin exposure.',
        'Cap simvastatin at 10 mg daily or switch to a statin with a different metabolic route.',
        'Established'
    UNION ALL SELECT 'Verapamil', 'Metoprolol', 'HIGH',
        'Bradycardia, heart block and hypotension can develop.',
        'Both drugs slow atrioventricular conduction and reduce myocardial contractility.',
        'Avoid the combination, especially with reduced left ventricular function. Monitor rate and blood pressure.',
        'Established'
    UNION ALL SELECT 'Amiodarone', 'Metoprolol', 'MEDIUM',
        'Bradycardia and heart block become more likely.',
        'Additive negative chronotropy and atrioventricular nodal blockade.',
        'Monitor heart rate and watch for dizziness or syncope.',
        'Established'
    UNION ALL SELECT 'Digoxin', 'Amiodarone', 'HIGH',
        'Digoxin toxicity can develop within days.',
        'Amiodarone inhibits P-glycoprotein and reduces digoxin clearance, raising levels by up to 70 percent.',
        'Halve the digoxin dose when amiodarone starts and check serum levels.',
        'Established'
    UNION ALL SELECT 'Digoxin', 'Verapamil', 'HIGH',
        'Digoxin levels rise with additive slowing of conduction.',
        'Verapamil inhibits P-glycoprotein and adds atrioventricular nodal blockade.',
        'Reduce digoxin and monitor levels, heart rate and conduction.',
        'Established'
    UNION ALL SELECT 'Digoxin', 'Furosemide', 'MEDIUM',
        'Digoxin toxicity risk rises when potassium falls.',
        'Loop diuretic induced hypokalaemia and hypomagnesaemia sensitise the heart to digoxin.',
        'Monitor and replace potassium and magnesium.',
        'Established'
    UNION ALL SELECT 'Digoxin', 'Spironolactone', 'MEDIUM',
        'Digoxin levels and potassium both rise.',
        'Spironolactone inhibits digoxin clearance and retains potassium.',
        'Check digoxin level and potassium after starting.',
        'Established'
    UNION ALL SELECT 'Amiodarone', 'Clarithromycin', 'HIGH',
        'Torsades de pointes risk rises.',
        'Both drugs prolong the QT interval.',
        'Avoid the combination. If unavoidable, correct electrolytes and monitor the ECG.',
        'Established'
    UNION ALL SELECT 'Amiodarone', 'Ciprofloxacin', 'HIGH',
        'QT prolongation and ventricular arrhythmia risk increase.',
        'Additive delay of ventricular repolarisation.',
        'Choose another antibiotic or monitor ECG and electrolytes.',
        'Established'
    UNION ALL SELECT 'Glipizide', 'Co-trimoxazole', 'HIGH',
        'Severe and prolonged hypoglycaemia can occur.',
        'Sulfonamides potentiate sulfonylureas and impair glucose recovery.',
        'Monitor glucose closely during the course or choose a different antibiotic.',
        'Established'
    UNION ALL SELECT 'Glipizide', 'Prednisolone', 'MEDIUM',
        'Glycaemic control worsens during steroid treatment.',
        'Corticosteroids raise blood glucose and oppose sulfonylurea action.',
        'Increase glucose monitoring and expect to adjust therapy.',
        'Established'
    UNION ALL SELECT 'Insulin glargine', 'Prednisolone', 'MEDIUM',
        'Insulin requirements rise during steroid treatment.',
        'Steroid induced insulin resistance increases basal insulin needs.',
        'Increase glucose monitoring and adjust the insulin dose.',
        'Established'
    UNION ALL SELECT 'Insulin glargine', 'Metoprolol', 'MEDIUM',
        'Hypoglycaemia is harder to recognise.',
        'Beta blockade masks the tremor and tachycardia of hypoglycaemia and can slow recovery.',
        'Warn the patient that sweating and hunger may be the only warning signs and monitor glucose.',
        'Established'
    UNION ALL SELECT 'Metformin', 'Omeprazole', 'MEDIUM',
        'Metformin exposure rises modestly.',
        'Omeprazole can reduce renal clearance of metformin.',
        'Monitor glucose and gastrointestinal tolerance.',
        'Moderate'
    UNION ALL SELECT 'Metformin', 'Furosemide', 'MEDIUM',
        'Lactic acidosis risk rises with dehydration or renal impairment.',
        'Diuretic induced volume depletion reduces metformin clearance.',
        'Hold metformin during acute illness or dehydration and check renal function.',
        'Moderate'
    UNION ALL SELECT 'Metformin', 'Prednisolone', 'MEDIUM',
        'Blood glucose rises and diabetes control worsens.',
        'Corticosteroids increase hepatic glucose output and insulin resistance.',
        'Monitor glucose and adjust metformin or add temporary therapy.',
        'Established'
    UNION ALL SELECT 'Sertraline', 'Tramadol', 'HIGH',
        'Serotonin syndrome and seizures are both possible.',
        'Both drugs increase serotonergic transmission and tramadol lowers the seizure threshold.',
        'Avoid the combination. Watch for agitation, tremor, clonus and hyperthermia.',
        'Established'
    UNION ALL SELECT 'Fluoxetine', 'Tramadol', 'HIGH',
        'Serotonin toxicity plus reduced analgesia and higher seizure risk.',
        'Fluoxetine inhibits CYP2D6, blocking tramadol activation, while both raise serotonin.',
        'Avoid the pair and choose a different analgesic.',
        'Established'
    UNION ALL SELECT 'Amitriptyline', 'Tramadol', 'HIGH',
        'Serotonin toxicity and seizure risk increase.',
        'Both agents raise serotonin and lower the seizure threshold.',
        'Avoid the pair and review the analgesic plan.',
        'Moderate'
    UNION ALL SELECT 'Morphine', 'Tramadol', 'HIGH',
        'Sedation and respiratory depression become dangerous.',
        'Additive central nervous system and respiratory depressant effects.',
        'Do not combine. If opioids are needed, titrate a single agent and monitor respiration.',
        'Established'
    UNION ALL SELECT 'Morphine', 'Amitriptyline', 'HIGH',
        'Sedation, respiratory depression and anticholinergic effects add up.',
        'Opioid and tricyclic central depressant effects combine.',
        'Avoid in frail or respiratory compromised patients. Monitor sedation if used.',
        'Established'
    UNION ALL SELECT 'Lithium', 'Hydrochlorothiazide', 'HIGH',
        'Lithium toxicity can develop within days.',
        'Thiazides cause sodium loss and increase proximal tubular lithium reabsorption.',
        'Avoid or reduce lithium and check levels within a week.',
        'Established'
    UNION ALL SELECT 'Lithium', 'Lisinopril', 'HIGH',
        'Lithium levels rise, risking tremor, confusion and vomiting.',
        'ACE inhibitors reduce renal lithium clearance.',
        'Check lithium levels and monitor for toxicity.',
        'Established'
    UNION ALL SELECT 'Lithium', 'Furosemide', 'MEDIUM',
        'Lithium levels rise with a variable time course.',
        'Loop diuretics reduce lithium clearance and alter sodium balance.',
        'Monitor lithium levels and hydration.',
        'Moderate'
    UNION ALL SELECT 'Carbamazepine', 'Amlodipine', 'MEDIUM',
        'Amlodipine effect is reduced, so blood pressure may rise.',
        'Carbamazepine induces CYP3A4 and accelerates amlodipine metabolism.',
        'Monitor blood pressure and titrate the calcium channel blocker.',
        'Established'
    UNION ALL SELECT 'Amitriptyline', 'Amiodarone', 'HIGH',
        'QT prolongation with a risk of torsades de pointes.',
        'Both drugs prolong the QT interval and amiodarone raises tricyclic levels.',
        'Avoid. If unavoidable, monitor the ECG and drug levels.',
        'Moderate'
    UNION ALL SELECT 'Levothyroxine', 'Omeprazole', 'MEDIUM',
        'Thyroid hormone absorption falls and TSH may rise.',
        'Reduced gastric acidity lowers levothyroxine dissolution and absorption.',
        'Check thyroid function after starting the proton pump inhibitor and adjust the dose.',
        'Established'
    UNION ALL SELECT 'Allopurinol', 'Azathioprine', 'HIGH',
        'Severe bone marrow suppression can occur.',
        'Allopurinol blocks xanthine oxidase, the enzyme that clears azathioprine metabolites.',
        'Reduce azathioprine to about a quarter of the usual dose and monitor the full blood count.',
        'Established'
    UNION ALL SELECT 'Methotrexate', 'Co-trimoxazole', 'HIGH',
        'Marrow suppression and mucositis risk rise sharply.',
        'Trimethoprim adds antifolate activity to methotrexate.',
        'Avoid the combination and monitor the full blood count.',
        'Established'
    UNION ALL SELECT 'Ciprofloxacin', 'Glipizide', 'MEDIUM',
        'Glucose can swing high or low unpredictably.',
        'Fluoroquinolones disturb pancreatic beta cell function and glucose regulation.',
        'Monitor glucose during the antibiotic course.',
        'Moderate'
) s
JOIN drug d1 ON d1.name = s.n1
JOIN drug d2 ON d2.name = s.n2
WHERE NOT EXISTS (
    SELECT 1 FROM interaction i
    WHERE (i.drug1_id = d1.id AND i.drug2_id = d2.id)
       OR (i.drug1_id = d2.id AND i.drug2_id = d1.id)
);

-- Rows created before the mechanism columns existed (or added through the app
-- without detail) still need to display sensibly.
UPDATE interaction
SET mechanism = COALESCE(mechanism, 'Mechanism not recorded'),
    management = COALESCE(management, 'Review the source literature before combining.'),
    evidence = COALESCE(evidence, 'Unknown')
WHERE mechanism IS NULL OR management IS NULL OR evidence IS NULL;
