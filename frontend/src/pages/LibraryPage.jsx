import React, { useEffect, useMemo, useState } from 'react';
import { drugAPI, interactionAPI } from '../services/api';

const SEVERITIES = ['', 'HIGH', 'MEDIUM', 'LOW'];

const severityClass = (severity) =>
  ({ HIGH: 'sev-high', MEDIUM: 'sev-medium', LOW: 'sev-low' }[severity] || 'sev-unknown');

const EMPTY_FORM = {
  drug1Name: '',
  drug2Name: '',
  severity: 'MEDIUM',
  description: '',
  mechanism: '',
  management: '',
  evidence: 'Established',
};

function LibraryPage() {
  const [interactions, setInteractions] = useState([]);
  const [drugs, setDrugs] = useState([]);
  const [query, setQuery] = useState('');
  const [severity, setSeverity] = useState('');
  const [openId, setOpenId] = useState(null);
  const [form, setForm] = useState(EMPTY_FORM);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);
  const [isFormOpen, setIsFormOpen] = useState(false);

  const load = async () => {
    try {
      const [allInteractions, allDrugs] = await Promise.all([
        interactionAPI.getAllInteractions(),
        drugAPI.getAllDrugs(),
      ]);
      setInteractions(allInteractions);
      setDrugs(allDrugs);
    } catch (err) {
      console.error(err);
      setError('Could not load the library. Check that the backend is running.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const severityCounts = useMemo(() => {
    const base = { HIGH: 0, MEDIUM: 0, LOW: 0 };
    interactions.forEach((item) => {
      if (item.severity in base) base[item.severity] += 1;
    });
    return base;
  }, [interactions]);

  const visible = useMemo(() => {
    const needle = query.trim().toLowerCase();
    return interactions.filter((item) => {
      const matchesSeverity = !severity || item.severity === severity;
      if (!matchesSeverity) return false;
      if (!needle) return true;
      return [item.drug1, item.drug2, item.description, item.mechanism, item.management]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(needle));
    });
  }, [interactions, query, severity]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsSaving(true);
    setError(null);
    setMessage(null);

    try {
      await interactionAPI.addInteraction(form);
      setMessage(`${form.drug1Name} + ${form.drug2Name} added to the library.`);
      setForm(EMPTY_FORM);
      setIsFormOpen(false);
      await load();
    } catch (err) {
      setError(err.response?.data?.error || 'Could not save the interaction.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = async (interaction) => {
    if (!window.confirm(`Delete ${interaction.drug1} + ${interaction.drug2}?`)) return;
    try {
      await interactionAPI.deleteInteraction(interaction.id);
      setMessage('Interaction deleted.');
      await load();
    } catch (err) {
      setError('Could not delete the interaction.');
    }
  };

  const sameDrug = form.drug1Name && form.drug1Name === form.drug2Name;

  return (
    <div className="page">
      <div className="page-head">
        <h1>Interaction library</h1>
        <p className="page-lede">
          Every pair in the database, with the mechanism, the recommended action and the
          strength of evidence.
        </p>
      </div>

      <div className="split">
        <section className="panel" aria-label="Interactions">
          <div className="toolbar">
            <input
              type="search"
              className="input search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Search drug, mechanism or action"
              aria-label="Search interactions"
            />
            <div className="seg" role="group" aria-label="Filter by severity">
              {SEVERITIES.map((level) => (
                <button
                  key={level || 'ALL'}
                  type="button"
                  aria-pressed={severity === level}
                  onClick={() => setSeverity(level)}
                >
                  {level || 'All'}
                  {!isLoading && (
                    <span className="seg-count">
                      {level ? severityCounts[level] : interactions.length}
                    </span>
                  )}
                </button>
              ))}
            </div>
          </div>

          {!isLoading && (
            <p className="result-count">
              {visible.length} of {interactions.length} pairs
            </p>
          )}

          {isLoading ? (
            <div className="loading-row" role="status">
              <span className="spinner" aria-hidden="true" /> Loading library…
            </div>
          ) : visible.length === 0 ? (
            <p className="empty-line">No interaction matches this filter.</p>
          ) : (
            <ul className="library">
              {visible.map((item) => (
                <li key={item.id} className="library-item">
                  <details
                    open={openId === item.id}
                    onToggle={(event) => {
                      if (event.currentTarget.open) setOpenId(item.id);
                      else if (openId === item.id) setOpenId(null);
                    }}
                  >
                    <summary>
                      <span className="library-pair">
                        {item.drug1} + {item.drug2}
                      </span>
                      <span className={`severity-tag ${severityClass(item.severity)}`}>
                        <span className="dot" aria-hidden="true" />
                        {item.severity}
                      </span>
                      <span className="library-evidence">{item.evidence || 'Unknown'}</span>
                    </summary>
                    <div className="library-body">
                      <p className="library-summary">{item.description}</p>
                      <div className="library-detail">
                        <span className="detail-label">Mechanism</span>
                        <p>{item.mechanism || 'Not recorded'}</p>
                      </div>
                      <div className="library-detail">
                        <span className="detail-label">What to do</span>
                        <p>{item.management || 'Not recorded'}</p>
                      </div>
                      <button
                        type="button"
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(item)}
                      >
                        Delete pair
                      </button>
                    </div>
                  </details>
                </li>
              ))}
            </ul>
          )}
        </section>

        <section className="panel" aria-label="Add interaction">
          <div className="panel-head">
            <h2>Add a pair</h2>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              aria-expanded={isFormOpen}
              aria-controls="add-pair-form"
              onClick={() => setIsFormOpen((open) => !open)}
            >
              {isFormOpen ? 'Close' : 'New pair'}
            </button>
          </div>

          {message && (
            <p className="form-success" role="status">
              {message}
            </p>
          )}

          {!isFormOpen && (
            <p className="panel-hint">
              Record an interaction that is not in the library yet.
            </p>
          )}

          {isFormOpen && (
          <form id="add-pair-form" onSubmit={handleSubmit}>
            <div className="field">
              <label htmlFor="drug1Name">Drug A</label>
              <select
                id="drug1Name"
                name="drug1Name"
                value={form.drug1Name}
                onChange={handleChange}
                required
                disabled={isSaving || drugs.length === 0}
              >
                <option value="">Select a drug</option>
                {drugs.map((drug) => (
                  <option key={drug.id} value={drug.name}>
                    {drug.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="field">
              <label htmlFor="drug2Name">Drug B</label>
              <select
                id="drug2Name"
                name="drug2Name"
                value={form.drug2Name}
                onChange={handleChange}
                required
                disabled={isSaving || drugs.length === 0}
              >
                <option value="">Select a drug</option>
                {drugs
                  .filter((drug) => drug.name !== form.drug1Name)
                  .map((drug) => (
                    <option key={drug.id} value={drug.name}>
                      {drug.name}
                    </option>
                  ))}
              </select>
            </div>

            <div className="field-row">
              <div className="field">
                <label htmlFor="severity">Severity</label>
                <select
                  id="severity"
                  name="severity"
                  value={form.severity}
                  onChange={handleChange}
                  disabled={isSaving}
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Moderate</option>
                  <option value="HIGH">High</option>
                </select>
              </div>
              <div className="field">
                <label htmlFor="evidence">Evidence</label>
                <select
                  id="evidence"
                  name="evidence"
                  value={form.evidence}
                  onChange={handleChange}
                  disabled={isSaving}
                >
                  <option value="Established">Established</option>
                  <option value="Moderate">Moderate</option>
                  <option value="Theoretical">Theoretical</option>
                </select>
              </div>
            </div>

            <div className="field">
              <label htmlFor="description">What happens</label>
              <textarea
                id="description"
                name="description"
                value={form.description}
                onChange={handleChange}
                rows="2"
                maxLength={1000}
                required
                disabled={isSaving}
                placeholder="The clinical consequence in one sentence"
              />
            </div>

            <div className="field">
              <label htmlFor="mechanism">Mechanism</label>
              <textarea
                id="mechanism"
                name="mechanism"
                value={form.mechanism}
                onChange={handleChange}
                rows="2"
                maxLength={1000}
                disabled={isSaving}
                placeholder="Why the pair interacts"
              />
            </div>

            <div className="field">
              <label htmlFor="management">What to do</label>
              <textarea
                id="management"
                name="management"
                value={form.management}
                onChange={handleChange}
                rows="2"
                maxLength={1000}
                disabled={isSaving}
                placeholder="Monitoring, dose change or alternative"
              />
            </div>

            {sameDrug && <p className="form-error">Choose two different drugs.</p>}
            {error && (
              <p className="form-error" role="alert">
                {error}
              </p>
            )}
            <button
              type="submit"
              className="btn btn-primary btn-block"
              disabled={
                isSaving || !form.drug1Name || !form.drug2Name || !form.description.trim() || Boolean(sameDrug)
              }
            >
              {isSaving ? 'Saving…' : 'Save interaction'}
            </button>
          </form>
          )}
        </section>
      </div>
    </div>
  );
}

export default LibraryPage;
