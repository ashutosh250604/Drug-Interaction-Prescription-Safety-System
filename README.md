# Drug Interaction & Prescription Safety System

A full-stack web application for checking potential drug interactions and supporting prescription safety.

## Overview

Healthcare professionals and students can:

- Build a medication list with autocomplete over the stored catalogue
- Check every pair in the list for known interactions
- See severity levels (HIGH, MEDIUM, LOW) ranked worst-first, with counts per level
- Be warned when a typed drug name is **not** in the database, so a typo can never
  masquerade as "no interactions"
- Print a clean interaction report for the patient file
- Add medications and interactions to the catalogue, and filter either list

## Technology Stack

### Backend

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **MySQL 8.x**
- **Maven**

### Frontend

- **React 18**
- **React Router 6**
- **Axios**
- **CSS3** (flat clinical design system, responsive, print styles)

### Screens

- **Checker** — build a regimen and review findings with mechanism and action
- **Interaction library** — search and filter all pairs, expand for detail, add pairs
- **Drug catalogue** — filter medications, add or remove them, see their class

## Project Structure

```
backend/
├── src/main/java/com/druginteraction/system/
│   ├── controller/          # REST controllers
│   ├── service/             # Business logic
│   ├── repository/          # JPA repositories
│   ├── entity/              # JPA entities
│   ├── dto/                 # Request/response objects
│   ├── exception/           # Global error handling
│   └── DrugInteractionSystemApplication.java
├── src/main/resources/
│   ├── application.properties
│   └── data.sql             # Idempotent seed data
├── sql/schema.sql           # Optional manual schema
└── pom.xml

frontend/
├── public/index.html
├── src/
│   ├── components/          # DrugInput, InteractionResult, Navbar
│   ├── pages/               # CheckerPage, LibraryPage, CataloguePage
│   ├── services/api.js      # API service layer
│   ├── App.jsx
│   ├── index.js
│   └── styles.css
└── package.json
```

## Setup & Installation

### Prerequisites

1. **JDK 17+** — verify with `java -version`
2. **Node.js & npm** — verify with `node -v` and `npm -v`
3. **MySQL 8.x** — verify with `mysql --version`

### Step 1: Database

The application creates the database and tables on startup
(`createDatabaseIfNotExist=true` plus `ddl-auto=update`), and seeds sample drugs
and interactions from `data.sql`. Create the database manually only if you prefer
not to rely on that:

```sql
CREATE DATABASE drug_interaction_db;
```

You can also apply `backend/sql/schema.sql` for a clean, explicit schema.

### Step 2: Configure the connection

`backend/src/main/resources/application.properties` reads every database setting
from the environment, falling back to a local MySQL default:

| Variable | Default |
|----------|---------|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `drug_interaction_db` |
| `DB_USERNAME` | `root` (development default — see the dedicated user below) |
| `DB_PASSWORD` | **required** — no default |
| `SERVER_PORT` | `8080` |

`DB_PASSWORD` has no default value, so credentials never live in source control. If it
is missing, the application stops before it touches the database and prints:

```text
***************************
APPLICATION FAILED TO START
***************************

Description:

The database password is not set (DB_PASSWORD is empty), so the application cannot connect to MySQL.

Action:

Set the DB_PASSWORD environment variable in the same shell that starts the backend:

  PowerShell:  $env:DB_PASSWORD = "your-mysql-password"; mvn spring-boot:run
  Git Bash:    export DB_PASSWORD=your-mysql-password && mvn spring-boot:run
  macOS/Linux: export DB_PASSWORD=your-mysql-password && mvn spring-boot:run

  ...
```

Set it in whichever shell starts the backend (or in the IDE run configuration's
environment variables):

```powershell
# PowerShell
$env:DB_PASSWORD = "your-password"
```

```bat
:: cmd.exe
set DB_PASSWORD=your-password
```

```bash
# Git Bash, macOS, Linux
export DB_PASSWORD='your-password'
```

The variable applies to that session only, so a new terminal or an IDE restart needs
it set again.

### Recommended: a dedicated database user

Running the backend as MySQL `root` gives it superuser rights it never needs. Create
a user scoped to this one schema, once, as an administrator:

```sql
CREATE USER 'drug_app'@'localhost' IDENTIFIED BY 'choose-a-strong-password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES
  ON drug_interaction_db.* TO 'drug_app'@'localhost';
```

The DDL grants are there because `ddl-auto=update` and `data.sql` run at startup;
`DROP` is deliberately omitted, since the application never drops tables or columns.
No global privileges are needed. Then point the application at it:

```bash
export DB_USERNAME=drug_app
export DB_PASSWORD='choose-a-strong-password'
```

A local `backend/.env.local` holding these two variables is git-ignored; load it with
`set -a; . ./.env.local; set +a` before starting the backend, and never commit it.

### Step 3: Start the backend

```bash
cd backend
mvn spring-boot:run
```

Backend: **http://localhost:8080**

### Step 4: Start the frontend

```bash
cd frontend
npm install
npm start
```

Frontend: **http://localhost:3000** (proxied to the backend on port 8080)

## API Endpoints

### Drug APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/drugs` | Get all drugs |
| GET | `/api/drugs/{id}` | Get drug by ID |
| POST | `/api/drugs` | Add new drug |
| PUT | `/api/drugs/{id}` | Update drug |
| DELETE | `/api/drugs/{id}` | Delete drug |
| GET | `/api/drugs/search?name=keyword` | Search drugs |

### Interaction APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/interactions` | All interactions, most severe first |
| GET | `/api/interactions/{id}` | Interaction by ID |
| GET | `/api/interactions/by-severity?severity=HIGH` | Filter by severity |
| GET | `/api/interactions/filter?q=bleeding&severity=HIGH` | Search the library |
| POST | `/api/interactions` | Add interaction |
| DELETE | `/api/interactions/{id}` | Delete interaction |
| POST | `/api/check` | **Check a medication list** |

### Check interactions

**POST** `/api/check`

Request:

```json
{
  "drugs": ["Warfarin", "Aspirin", "Ibuprofen"]
}
```

Response:

```json
{
  "interactions": [
    {
      "id": 1,
      "drug1": "Warfarin",
      "drug2": "Aspirin",
      "severity": "HIGH",
      "description": "Bleeding risk rises sharply. Aspirin also injures the stomach lining.",
      "mechanism": "Aspirin blocks platelet COX-1 while warfarin reduces vitamin K dependent clotting factors II, VII, IX and X.",
      "management": "Avoid the combination. If it is required, add a proton pump inhibitor and check INR more often.",
      "evidence": "Established"
    }
  ],
  "checkedDrugs": ["Aspirin", "Warfarin"],
  "unknownDrugs": ["Ibuprofenn"],
  "duplicateTherapies": [
    { "drugClass": "NSAID", "drugs": ["Ibuprofen", "Naproxen"] }
  ]
}
```

What each part is:

- `interactions` is sorted **HIGH → MEDIUM → LOW**, so the most dangerous pair is first.
- `checkedDrugs` lists only the names that matched the catalogue.
- `unknownDrugs` lists names that matched nothing. The frontend shows these as a
  warning rather than silently ignoring them, because dropping a typo would produce
  a falsely reassuring "no interactions" result.
- `duplicateTherapies` groups medications that share a class (two NSAIDs, two
  anticoagulants) where the risk comes from the summed effect rather than one pair.

Validation errors and business errors both return `{"error": "..."}` with a 4xx
status, which the frontend displays directly.

## Severity Levels

| Level | Color | Meaning |
|-------|-------|---------|
| **HIGH** | 🔴 Red | Severe interaction — avoid the combination if possible |
| **MEDIUM** | 🟠 Amber | Moderate interaction — use with caution and monitor |
| **LOW** | 🟢 Green | Minor interaction — usually compatible, keep monitoring |

## Testing the Application

1. **Check interactions** — add `Warfarin` and `Aspirin`, run the check, confirm a
   HIGH result appears at the top.
2. **Unknown drug handling** — add `Asprin` (typo) and `Warfarin`. The check must
   report `Asprin` as unchecked instead of claiming the pair is clear.
3. **Autocomplete** — type `war` in the checker and confirm `Warfarin` is suggested;
   arrow keys plus Enter should select it.
4. **Add drug** — add a medication, confirm it appears in the catalogue and in
   autocomplete.
5. **Add interaction** — record a pair, then try the same pair reversed; it must be
   rejected as a duplicate.
6. **Print** — run a check and use *Print report*; navigation and controls should be
   hidden and only the summary plus interaction cards printed.

## Sample Data

40 medications and 73 curated pairs, seeded on first start and safe to re-run (no
rows are duplicated on restart). Every pair carries a mechanism, a recommended
action and an evidence level.

- **Drugs:** Warfarin, Aspirin, Ibuprofen, Metformin, Lisinopril, Simvastatin,
  Omeprazole, Amoxicillin, Amlodipine, Metoprolol
- **Interactions:** Warfarin+Aspirin (HIGH), Warfarin+Ibuprofen (HIGH),
  Aspirin+Ibuprofen (HIGH), Amoxicillin+Warfarin (HIGH), Metformin+Omeprazole (MEDIUM),
  Simvastatin+Amlodipine (MEDIUM), Lisinopril+Omeprazole (MEDIUM),
  Lisinopril+Amlodipine (LOW), Metoprolol+Amlodipine (LOW)

## Troubleshooting

**MySQL connection error** — check MySQL is running, and that `DB_USERNAME` /
`DB_PASSWORD` are correct.

**Port already in use** — backend: set `SERVER_PORT`; frontend: set `PORT`.

**CORS error** — the controllers allow all origins; make sure requests go to
`http://localhost:8080/api`.

**Duplicate interactions in an old database** — earlier versions inserted seed rows
without a duplicate guard. The API collapses repeated pairs, and `data.sql` no longer
duplicates rows, but you can clean an existing database with:

```sql
CREATE TEMPORARY TABLE keep AS
  SELECT MIN(id) AS id FROM interaction
  GROUP BY LEAST(drug1_id, drug2_id), GREATEST(drug1_id, drug2_id);
DELETE FROM interaction WHERE id NOT IN (SELECT id FROM keep);
```

### Reset the database

```bash
mysql -u root -p -e "DROP DATABASE drug_interaction_db;"
mysql -u root -p -e "CREATE DATABASE drug_interaction_db;"
```

Then restart the Spring Boot application.

## License

Educational and reference use only. Always consult healthcare professionals for
medical advice.

---

**⚠️ Disclaimer:** This system is a reference aid, not a clinical decision tool. It
does not model doses, timing, comorbidities or the full interaction literature.
Always consult a qualified healthcare professional before making medication decisions.
