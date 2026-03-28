import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import AddDrugPage from './pages/AddDrugPage';
import AddInteractionPage from './pages/AddInteractionPage';

function App() {
  return (
    <Router>
      <div className="app">
        <Navbar />
        <main className="main-content-wrapper">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/add-drug" element={<AddDrugPage />} />
            <Route path="/add-interaction" element={<AddInteractionPage />} />
          </Routes>
        </main>
        <footer className="footer">
          <p>
            Drug Interaction & Prescription Safety System - For medical reference only.
            Always consult a healthcare professional.
          </p>
        </footer>
      </div>
    </Router>
  );
}

export default App;
