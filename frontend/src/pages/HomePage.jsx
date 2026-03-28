import React, { useState } from 'react';
import DrugInput from '../components/DrugInput';
import InteractionResult from '../components/InteractionResult';
import { interactionAPI } from '../services/api';

function HomePage() {
  const [drugs, setDrugs] = useState([]);
  const [interactions, setInteractions] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [hasChecked, setHasChecked] = useState(false);

  const handleCheckInteractions = async () => {
    if (drugs.length < 2) return;

    setIsLoading(true);
    setError(null);
    setHasChecked(true);

    try {
      const result = await interactionAPI.checkInteractions(drugs);
      setInteractions(result);
    } catch (err) {
      console.error('Error checking interactions:', err);
      setError(
        err.response?.data?.error ||
        'Failed to check interactions. Please try again.'
      );
      setInteractions(null);
    } finally {
      setIsLoading(false);
    }
  };

  const handleClearResults = () => {
    setDrugs([]);
    setInteractions(null);
    setError(null);
    setHasChecked(false);
  };

  return (
    <div className="home-page">
      <div className="hero-section">
        <h1>Drug Interaction Checker</h1>
        <p>
          Check for potential interactions between multiple medications to ensure
          prescription safety.
        </p>
      </div>

      <div className="main-content">
        <div className="input-section">
          <div className="card">
            <DrugInput
              onCheckInteractions={handleCheckInteractions}
              drugs={drugs}
              setDrugs={setDrugs}
              isLoading={isLoading}
            />
          </div>

          {error && (
            <div className="error-message">
              <span className="error-icon">!</span>
              {error}
            </div>
          )}

          {hasChecked && !error && (
            <button
              type="button"
              className="btn btn-outline"
              onClick={handleClearResults}
            >
              Clear & Start New Check
            </button>
          )}
        </div>

        {hasChecked && !isLoading && (
          <div className="results-section">
            <InteractionResult interactions={interactions} />
          </div>
        )}
      </div>

      <div className="info-section">
        <div className="info-card">
          <h3>Severity Levels</h3>
          <div className="severity-info">
            <div className="severity-item severity-high">
              <span className="severity-badge">HIGH</span>
              <span>Severe interaction - Avoid combination if possible</span>
            </div>
            <div className="severity-item severity-medium">
              <span className="severity-badge">MEDIUM</span>
              <span>Moderate interaction - Use with caution</span>
            </div>
            <div className="severity-item severity-low">
              <span className="severity-badge">LOW</span>
              <span>Minor interaction - Monitor as needed</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default HomePage;
