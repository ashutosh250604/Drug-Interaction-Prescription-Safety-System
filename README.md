# Drug Interaction & Prescription Safety System

A full-stack web application for checking potential drug interactions and ensuring prescription safety.

## 📋 Overview

This system allows healthcare professionals and patients to:
- Input multiple drug names
- Check for possible interactions between them
- View severity levels (LOW, MEDIUM, HIGH) with color-coded warnings
- Add new drugs and interactions to the database

## 🏗️ Technology Stack

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
- **CSS3**

## 📁 Project Structure

```
Drug Interaction & Prescription Safety System/
├── backend/
│   ├── src/main/java/com/druginteraction/system/
│   │   ├── controller/          # REST Controllers
│   │   ├── service/            # Business Logic
│   │   ├── repository/         # JPA Repositories
│   │   ├── entity/             # JPA Entities
│   │   ├── dto/                # Data Transfer Objects
│   │   └── DrugInteractionSystemApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql
│   ├── sql/
│   │   └── schema.sql
│   └── pom.xml
├── frontend/
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/         # React Components
│   │   ├── pages/             # Page Components
│   │   ├── services/          # API Service Layer
│   │   ├── App.jsx
│   │   ├── index.js
│   │   └── styles.css
│   └── package.json
└── README.md
```

## 🚀 Setup & Installation

### Prerequisites

1. **Java Development Kit (JDK) 17+**
   - Download from: https://adoptium.net/
   - Verify: `java -version`

2. **Node.js & npm**
   - Download from: https://nodejs.org/
   - Verify: `node -v` and `npm -v`

3. **MySQL 8.x**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Verify: `mysql --version`

### Step 1: Database Setup

1. Open MySQL command line or MySQL Workbench

2. Create the database:
```sql
CREATE DATABASE drug_interaction_db;
```

3. (Optional) Run the schema script for a clean setup:
```bash
mysql -u root -p drug_interaction_db < backend/sql/schema.sql
```

### Step 2: Configure Database Connection

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/drug_interaction_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### Step 3: Start the Backend

```bash
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend will start on **http://localhost:8080**

### Step 4: Start the Frontend

Open a new terminal:

```bash
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend will start on **http://localhost:3000**

## 📡 API Endpoints

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
| GET | `/api/interactions` | Get all interactions |
| GET | `/api/interactions/{id}` | Get interaction by ID |
| POST | `/api/interactions` | Add new interaction |
| DELETE | `/api/interactions/{id}` | Delete interaction |
| POST | `/api/check` | **Check drug interactions** |

### Check Interactions (Main Feature)

**POST** `/api/check`

Request:
```json
{
  "drugs": ["Warfarin", "Aspirin", "Ibuprofen"]
}
```

Response:
```json
[
  {
    "drug1": "Warfarin",
    "drug2": "Aspirin",
    "severity": "HIGH",
    "description": "Concurrent use significantly increases bleeding risk"
  },
  {
    "drug1": "Aspirin",
    "drug2": "Ibuprofen",
    "severity": "HIGH",
    "description": "Ibuprofen can reduce anticoagulant effect"
  }
]
```

## 🎨 Severity Levels

| Level | Color | Meaning |
|-------|-------|---------|
| **HIGH** | 🔴 Red | Severe interaction - Avoid combination if possible |
| **MEDIUM** | 🟡 Yellow | Moderate interaction - Use with caution |
| **LOW** | 🟢 Green | Minor interaction - Monitor as needed |

## 🧪 Testing the Application

### Test Scenarios

1. **Check Interactions**
   - Go to Home page
   - Add "Warfarin" and "Aspirin"
   - Click "Check Interactions"
   - Should show HIGH severity interaction

2. **Add New Drug**
   - Go to "Add Drug" page
   - Enter drug name and description
   - Submit the form

3. **Add New Interaction**
   - Go to "Add Interaction" page
   - Select two drugs
   - Choose severity level
   - Add description

## 📝 Sample Data

The application includes sample drugs and interactions:

**Sample Drugs:**
- Warfarin (Anticoagulant)
- Aspirin (NSAID)
- Ibuprofen (NSAID)
- Metformin (Diabetes)
- Lisinopril (Blood Pressure)
- Simvastatin (Cholesterol)
- Omeprazole (Acid Reflux)
- Amoxicillin (Antibiotic)
- Amlodipine (Blood Pressure)
- Metoprolol (Heart)

**Sample Interactions:**
- Warfarin + Aspirin → HIGH
- Aspirin + Ibuprofen → HIGH
- Warfarin + Ibuprofen → HIGH
- Metformin + Omeprazole → MEDIUM
- Simvastatin + Amlodipine → MEDIUM
- Lisinopril + Amlodipine → LOW
- Metoprolol + Amlodipine → LOW

## 🛠️ Troubleshooting

### Common Issues

1. **MySQL Connection Error**
   - Verify MySQL is running
   - Check username/password in application.properties
   - Ensure the database exists

2. **Port Already in Use**
   - Backend: Change `server.port` in application.properties
   - Frontend: Set `PORT` environment variable

3. **CORS Error**
   - Backend includes CORS configuration for localhost:3000
   - If needed, modify `@CrossOrigin` annotations

### Database Reset

To reset the database:
```bash
mysql -u root -p -e "DROP DATABASE drug_interaction_db;"
mysql -u root -p -e "CREATE DATABASE drug_interaction_db;"
```

Then restart the Spring Boot application.

## 📄 License

This project is for educational and reference purposes only. Always consult healthcare professionals for medical advice.

## 🙏 Acknowledgments

- Built with Spring Boot and React
- Designed for prescription safety awareness
- Educational reference tool

---

**⚠️ Disclaimer:** This system is for reference only. Always consult with qualified healthcare professionals before making any medication decisions.
