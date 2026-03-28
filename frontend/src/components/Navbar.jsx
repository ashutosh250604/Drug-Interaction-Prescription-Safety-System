import React from 'react';
import { Link } from 'react-router-dom';

function Navbar() {
  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          <span className="brand-icon">💊</span>
          Drug Safety System
        </Link>
        <ul className="navbar-menu">
          <li>
            <Link to="/" className="nav-link">
              Home
            </Link>
          </li>
          <li>
            <Link to="/add-drug" className="nav-link">
              Add Drug
            </Link>
          </li>
          <li>
            <Link to="/add-interaction" className="nav-link">
              Add Interaction
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
}

export default Navbar;
