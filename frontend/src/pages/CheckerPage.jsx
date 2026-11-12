import React, { useEffect, useMemo, useState } from 'react';
import DrugInput from '../components/DrugInput';
import InteractionResult from '../components/InteractionResult';
import { drugAPI, interactionAPI } from '../services/api';

const EXAMPLES = [
  ['Warfarin', 'Aspirin'],
  ['Lithium', 'Ibuprofen'],
  ['Simvastatin', 'Clarithromycin'],
  ['Lisinopril', 'Spironolactone'],
];

function CheckerPage() {
  const [drugs, setDrugs] = useState([]);
  const [catalog, setCatalog] = useState([]);
  const [result, setResult] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;
    drugAPI
      .getAllDrugs()
      .then((all) => {
        if (isMounted) setCatalog(all);
      })
      .catch((err) => console.error('Catalogue unavailable:', err));
    return () => {
      isMounted = false;
    };
  }, []);

  const updateDrugs = (next) => {
    setDrugs(next);
    setResult(null);
    setError(null);
  };

  const runCheck = async (list) => {
    if (list.length < 2) return;
    setIsLoading(true);
    setError(null);

    try {
      const data = await interactionAPI.checkInteractions(list);
      // Tolerate the older array-only response from a stale backend.
      setResult(
        Array.isArray(data)
          ? { interactions: data, checkedDrugs: list, unknownDrugs: [], duplicateTherapies: [] }
          : data
      );
    } catch (err) {
      console.error('Check failed:', err);
      setError(
        err.response?.data?.error ||
          (err.response
            ? 'The server could not run the check.'
            : 'No connection to the API on port 8080. Start the backend and try again.')
      );
      setResult(null);
    } finally {
      setIsLoading(false);
    }
  };

  const handleExample = (names) => {
    setDrugs(names);
    setError(null);
    runCheck(names);
  };

  const counts = useMemo(() => {
    const base = { HIGH: 0, MEDIUM: 0, LOW: 0 };
    (result?.interactions || []).forEach((item) => {
      if (item.severity in base) base[item.severity] += 1;
    });
    return base;
  }, [result]);

  const unknown = result?.unknownDrugs || [];
  const duplicates = result?.duplicateTherapies || [];

  return (
    <div className="page checker-page">
      <div className="page-head">
        <h1>Interaction check</h1>
        <p className="page-lede">
          Add every medicine in the regimen. Every pair is checked, ordered by risk, with
          the mechanism and the action to take.
        </p>
      </div>

      <div className="workspace">
        <section className="panel regimen-panel" aria-label="Regimen">
          <div className="panel-head">
            <h2>Regimen</h2>
          </div>

          <DrugInput
            drugs={drugs}
            setDrugs={updateDrugs}
            availableDrugs={catalog}
            disabled={isLoading}
          />

          {drugs.length === 0 && (
            <div className="examples-block">
              <span className="field-label">Or start from a known pair</span>
              <div className="examples">
                {EXAMPLES.map((pair) => (
                  <button
                    key={pair.join('+')}
                    type="button"
                    className="example"
                    onClick={() => handleExample(pair)}
                  >
                    {pair.join(' + ')}
                  </button>
                ))}
              </div>
            </div>
          )}

          {!isLoading && drugs.length < 2 && (
            <p className="panel-hint">Add at least two medications to run the check.</p>
          )}

          <button
            type="button"
            className="btn btn-primary btn-block"
            onClick={() => runCheck(drugs)}
            disabled={drugs.length < 2 || isLoading}
          >
            {isLoading ? 'Checking…' : 'Check interactions'}
          </button>
          {drugs.length > 0 && (
            <button
              type="button"
              className="btn btn-ghost btn-block"
              onClick={() => updateDrugs([])}
              disabled={isLoading}
            >
              Clear regimen
            </button>
          )}

          {error && (
            <div className="notice notice-error" role="alert">
              <p>{error}</p>
              <button type="button" className="btn btn-secondary btn-sm" onClick={() => runCheck(drugs)}>
                Retry
              </button>
            </div>
          )}
        </section>

        <section className="panel findings-panel" aria-label="Findings" aria-live="polite">
          <div className="panel-head">
            <h2>Findings</h2>
            {result && (
              <button type="button" className="text-button" onClick={() => window.print()}>
                Print report
              </button>
            )}
          </div>

          {isLoading && (
            <div className="loading-row" role="status">
              <span className="spinner" aria-hidden="true" />
              Comparing every pair…
            </div>
          )}

          {!isLoading && !result && (
            <div className="empty-findings">
              <h3>Nothing checked yet</h3>
              <p>
                Every pair in the regimen is compared and returned here, highest risk first.
              </p>
              <ul className="result-preview">
                <li>Risk level for each pair, from high to minor</li>
                <li>Why the interaction happens</li>
                <li>The action to take, with the strength of evidence</li>
              </ul>
            </div>
          )}

          {!isLoading && result && (
            <>
              <div className="summary">
                <div className="stat">
                  <span className="stat-value">{result.interactions.length}</span>
                  <span className="stat-label">interactions</span>
                </div>
                <div className="stat">
                  <span className="stat-value sev-high-text">{counts.HIGH}</span>
                  <span className="stat-label">high risk</span>
                </div>
                <div className="stat">
                  <span className="stat-value sev-medium-text">{counts.MEDIUM}</span>
                  <span className="stat-label">moderate</span>
                </div>
                <div className="stat">
                  <span className="stat-value sev-low-text">{counts.LOW}</span>
                  <span className="stat-label">minor</span>
                </div>
              </div>

              {unknown.length > 0 && (
                <div className="notice notice-warning" role="alert">
                  <strong>Not checked: {unknown.join(', ')}</strong>
                  <p>
                    These names are not in the catalogue, so no interaction data exists for
                    them. Fix the spelling or add the drug first.
                  </p>
                </div>
              )}

              {duplicates.length > 0 && (
                <div className="notice notice-warning">
                  <strong>Duplicate therapy</strong>
                  {duplicates.map((entry) => (
                    <p key={entry.drugClass}>
                      {entry.drugs.join(' and ')} are both {entry.drugClass.toLowerCase()}s.
                      Review whether both are needed.
                    </p>
                  ))}
                </div>
              )}

              <InteractionResult
                interactions={result.interactions}
                checkedDrugs={result.checkedDrugs}
              />

              <p className="disclaimer">
                Reference aid only. Doses, timing, renal and hepatic function, and the
                individual patient are not considered.
              </p>
            </>
          )}
        </section>
      </div>
    </div>
  );
}

export default CheckerPage;
