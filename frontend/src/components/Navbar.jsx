import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';

const LINKS = [
  { to: '/', label: 'Checker', end: true },
  { to: '/library', label: 'Interaction library' },
  { to: '/catalogue', label: 'Drug catalogue' },
];

function Navbar() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <header className="app-header">
      <div className="header-inner">
        <NavLink to="/" end className="brand" onClick={() => setIsOpen(false)}>
          <svg className="brand-mark" viewBox="0 0 24 24" aria-hidden="true">
            <rect x="2" y="8.5" width="20" height="7" rx="3.5" fill="currentColor" />
            <path d="M12 8.5v7" stroke="#fff" strokeWidth="1.6" />
          </svg>
          <span className="brand-name">Interaction Check</span>
        </NavLink>

        <button
          type="button"
          className="nav-toggle"
          aria-expanded={isOpen}
          aria-controls="primary-nav"
          aria-label="Menu"
          onClick={() => setIsOpen((open) => !open)}
        >
          <span />
          <span />
          <span />
        </button>

        <nav id="primary-nav" className={`main-nav${isOpen ? ' open' : ''}`} aria-label="Primary">
          {LINKS.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.end}
              className={({ isActive }) => `nav-link${isActive ? ' active' : ''}`}
              onClick={() => setIsOpen(false)}
            >
              {link.label}
            </NavLink>
          ))}
        </nav>
      </div>
    </header>
  );
}

export default Navbar;
