import React, { useEffect, useMemo, useRef, useState } from 'react';

const MAX_SUGGESTIONS = 7;

const sameName = (a, b) => a.trim().toLowerCase() === b.trim().toLowerCase();

/**
 * Regimen builder: type to search the catalogue, Enter or click to add.
 * Unknown names are still accepted so the API can report them as unchecked.
 */
function DrugInput({ drugs, setDrugs, availableDrugs = [], disabled = false }) {
  const [inputValue, setInputValue] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState(0);
  const [notice, setNotice] = useState(null);
  const inputRef = useRef(null);

  const term = inputValue.trim();

  const selected = useMemo(
    () => new Set(drugs.map((drug) => drug.toLowerCase())),
    [drugs]
  );

  const classFor = (name) => {
    const match = availableDrugs.find((drug) => sameName(drug.name, name));
    return match?.drugClass || null;
  };

  const options = useMemo(() => {
    const pool = availableDrugs.filter((drug) => !selected.has(drug.name.toLowerCase()));
    const needle = term.toLowerCase();
    const matches = !needle
      ? pool.slice(0, 5)
      : pool
          .filter(
            (drug) =>
              drug.name.toLowerCase().includes(needle) ||
              (drug.drugClass || '').toLowerCase().includes(needle)
          )
          .slice(0, MAX_SUGGESTIONS);

    const withCustom =
      term && !matches.some((drug) => sameName(drug.name, term))
        ? [{ id: 'custom', name: term, drugClass: 'Not in the catalogue', isCustom: true }, ...matches]
        : matches;

    return withCustom.map((drug) => ({ ...drug, isCustom: Boolean(drug.isCustom) }));
  }, [availableDrugs, selected, term]);

  useEffect(() => {
    if (!notice) return undefined;
    const timeout = setTimeout(() => setNotice(null), 2400);
    return () => clearTimeout(timeout);
  }, [notice]);

  const addDrug = (rawName) => {
    const name = rawName.trim();
    if (!name) return;

    if (drugs.some((drug) => sameName(drug, name))) {
      setNotice(`${name} is already in the regimen.`);
      setInputValue('');
      setIsOpen(false);
      return;
    }

    const known = availableDrugs.find((drug) => sameName(drug.name, name));
    setDrugs([...drugs, known ? known.name : name]);
    setInputValue('');
    setIsOpen(false);
    setActiveIndex(0);
    setNotice(null);
  };

  const handleChange = (event) => {
    setInputValue(event.target.value);
    setIsOpen(true);
    setActiveIndex(0);
  };

  const handleKeyDown = (event) => {
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      setIsOpen(true);
      setActiveIndex((index) => (options.length === 0 ? -1 : (index + 1) % options.length));
      return;
    }
    if (event.key === 'ArrowUp') {
      event.preventDefault();
      setActiveIndex((index) =>
        options.length === 0 ? -1 : (index - 1 + options.length) % options.length
      );
      return;
    }
    if (event.key === 'Escape') {
      setIsOpen(false);
      return;
    }
    if (event.key === 'Enter') {
      event.preventDefault();
      const highlighted = isOpen && activeIndex >= 0 ? options[activeIndex] : null;
      addDrug(highlighted ? highlighted.name : inputValue);
      return;
    }
    if (event.key === 'Tab') {
      setIsOpen(false);
    }
  };

  const showList = isOpen && options.length > 0;

  return (
    <div className="regimen-builder">
      <label htmlFor="drug-search" className="field-label">
        Add a medication
      </label>
      <div className="combo">
        <input
          ref={inputRef}
          id="drug-search"
          type="text"
          className="input"
          value={inputValue}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
          onFocus={() => setIsOpen(true)}
          onBlur={() => window.setTimeout(() => setIsOpen(false), 120)}
          placeholder="Search by name or class"
          autoComplete="off"
          role="combobox"
          aria-expanded={showList}
          aria-controls="drug-options"
          aria-autocomplete="list"
          aria-activedescendant={
            isOpen && activeIndex >= 0 ? `drug-option-${options[activeIndex]?.id}` : undefined
          }
          disabled={disabled}
        />
        {showList && (
          <ul className="autocomplete" id="drug-options" role="listbox">
            {options.map((option, index) => (
              <li
                key={option.id}
                id={`drug-option-${option.id}`}
                role="option"
                aria-selected={index === activeIndex}
                className={`suggestion${index === activeIndex ? ' active' : ''}`}
                onMouseDown={(event) => event.preventDefault()}
                onMouseEnter={() => setActiveIndex(index)}
                onClick={() => addDrug(option.name)}
              >
                <span className="suggestion-name">{option.name}</span>
                <span className="suggestion-meta">{option.drugClass}</span>
              </li>
            ))}
          </ul>
        )}
      </div>

      {notice && (
        <p className="hint notice-inline" role="status">
          {notice}
        </p>
      )}

      <div className="regimen">
        <div className="regimen-head">
          <span className="field-label">In this regimen</span>
          <span className="hint">{drugs.length} medication{drugs.length === 1 ? '' : 's'}</span>
        </div>

        {drugs.length === 0 ? (
          <p className="empty-line">Nothing added yet.</p>
        ) : (
          <ul className="regimen-list">
            {drugs.map((drug) => (
              <li key={drug} className="regimen-item">
                <span className="regimen-name">{drug}</span>
                <span className="regimen-class">{classFor(drug) || 'Not in catalogue'}</span>
                <button
                  type="button"
                  className="text-button"
                  onClick={() => setDrugs(drugs.filter((item) => item !== drug))}
                  aria-label={`Remove ${drug}`}
                  disabled={disabled}
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

export default DrugInput;
