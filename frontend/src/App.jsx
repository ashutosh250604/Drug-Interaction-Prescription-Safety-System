import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Navbar from './components/Navbar';
import CheckerPage from './pages/CheckerPage';
import LibraryPage from './pages/LibraryPage';
import CataloguePage from './pages/CataloguePage';

function NotFound() {
  return (
    <div className="page">
      <div className="page-head">
        <h1>Page not found</h1>
        <p className="page-lede">That address does not exist in this application.</p>
      </div>
      <Link to="/" className="btn btn-primary">
        Go to the checker
      </Link>
    </div>
  );
}

function App() {
  return (
    <Router>
      <div className="app">
        <a className="skip-link" href="#main">
          Skip to content
        </a>
        <Navbar />
        <main id="main" className="main">
          <Routes>
            <Route path="/" element={<CheckerPage />} />
            <Route path="/library" element={<LibraryPage />} />
            <Route path="/catalogue" element={<CataloguePage />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </main>
        <footer className="app-footer">
          <p>
            Educational reference tool. It does not model doses, timing, organ function or
            the individual patient.
          </p>
          <p>Confirm every decision with a pharmacist or prescriber.</p>
        </footer>
      </div>
    </Router>
  );
}

export default App;
