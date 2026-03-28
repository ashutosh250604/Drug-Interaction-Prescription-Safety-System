import React, { useState } from 'react';

function DrugInput({ onCheckInteractions, drugs, setDrugs, isLoading }) {
  const [inputValue, setInputValue] = useState('');
  const [suggestions, setSuggestions] = useState([]);

  const handleInputChange = (e) => {
    const value = e.target.value;
    setInputValue(value);
  };

  const handleAddDrug = () => {
    const trimmedValue = inputValue.trim();
    if (trimmedValue && !drugs.includes(trimmedValue)) {
      setDrugs([...drugs, trimmedValue]);
      setInputValue('');
      setSuggestions([]);
    }
  };

  const handleRemoveDrug = (drugToRemove) => {
    setDrugs(drugs.filter((drug) => drug !== drugToRemove));
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleAddDrug();
    }
  };

  const handleSuggestionClick = (suggestion) => {
    if (!drugs.includes(suggestion.name)) {
      setDrugs([...drugs, suggestion.name]);
    }
    setInputValue('');
    setSuggestions([]);
  };

  return (
    <div className="drug-input-container">
      <div className="input-section">
        <label htmlFor="drug-input" className="input-label">
          Enter Drug Names:
        </label>
        <div className="input-row">
          <input
            type="text"
            id="drug-input"
            className="drug-input"
            value={inputValue}
            onChange={handleInputChange}
            onKeyPress={handleKeyPress}
            placeholder="Type a drug name and press Enter or Add"
            disabled={isLoading}
          />
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleAddDrug}
            disabled={!inputValue.trim() || isLoading}
          >
            Add
          </button>
        </div>
        <p className="input-hint">Add at least 2 drugs to check for interactions</p>
      </div>

      {drugs.length > 0 && (
        <div className="selected-drugs">
          <h4>Selected Drugs ({drugs.length}):</h4>
          <div className="drug-tags">
            {drugs.map((drug, index) => (
              <span key={index} className="drug-tag">
                {drug}
                <button
                  type="button"
                  className="remove-drug"
                  onClick={() => handleRemoveDrug(drug)}
                  disabled={isLoading}
                >
                  ×
                </button>
              </span>
            ))}
          </div>
        </div>
      )}

      <button
        type="button"
        className="btn btn-primary btn-check"
        onClick={onCheckInteractions}
        disabled={drugs.length < 2 || isLoading}
      >
        {isLoading ? 'Checking...' : 'Check Interactions'}
      </button>
    </div>
  );
}

export default DrugInput;
