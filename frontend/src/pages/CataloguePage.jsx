import React, { useEffect, useMemo, useState } from 'react';
import { drugAPI } from '../services/api';

const EMPTY_FORM = { name: '', drugClass: '', description: '' };

function CataloguePage() {
  const [drugs, setDrugs] = useState([]);
  const [query, setQuery] = useState('');
  const [form, setForm] = useState(EMPTY_FORM);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);

  const load = async () => {
    try {
      setDrugs(await drugAPI.getAllDrugs());
    } catch (err) {
      console.error(err);
      setError('Could not load the catalogue. Check that the backend is running.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const visible = useMemo(() => {
    const needle = query.trim().toLowerCase();
    if (!needle) return drugs;
    return drugs.filter(
      (drug) =>
        drug.name.toLowerCase().includes(needle) ||
        (drug.drugClass || '').toLowerCase().includes(needle) ||
        (drug.description || '').toLowerCase().includes(needle)
    );
  }, [drugs, query]);

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
      const created = await drugAPI.addDrug(form);
      setMessage(`${created.name} added.`);
      setForm(EMPTY_FORM);
      await load();
    } catch (err) {
      setError(err.response?.data?.error || 'Could not add the medication.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = async (drug) => {
    if (!window.confirm(`Delete ${drug.name}? Its interactions are removed with it.`)) return;
    try {
      await drugAPI.deleteDrug(drug.id);
      setMessage(`${drug.name} deleted.`);
      await load();
    } catch (err) {
      setError('Could not delete the medication.');
    }
  };

  return (
    <div className="page">
      <div className="page-head">
        <h1>Drug catalogue</h1>
        <p className="page-lede">
          {isLoading
            ? 'Loading medications…'
            : `${drugs.length} medications with the class used for duplicate-therapy checks.`}
        </p>
      </div>

      <div className="split">
        <section className="panel" aria-label="Medications">
          <div className="toolbar">
            <input
              type="search"
              className="input search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Filter by name, class or use"
              aria-label="Filter medications"
            />
          </div>

          {isLoading ? (
            <div className="loading-row" role="status">
              <span className="spinner" aria-hidden="true" /> Loading catalogue…
            </div>
          ) : visible.length === 0 ? (
            <p className="empty-line">No medication matches this filter.</p>
          ) : (
            <div className="table-wrap">
              <table className="table">
                <thead>
                  <tr>
                    <th scope="col">Medication</th>
                    <th scope="col">Class</th>
                    <th scope="col" className="col-use">
                      Notes
                    </th>
                    <th scope="col">
                      <span className="sr-only">Actions</span>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {visible.map((drug) => (
                    <tr key={drug.id}>
                      <td className="cell-name">{drug.name}</td>
                      <td>{drug.drugClass || '—'}</td>
                      <td className="cell-muted col-use">{drug.description || '—'}</td>
                      <td className="cell-action">
                        <button
                          type="button"
                          className="text-button danger"
                          onClick={() => handleDelete(drug)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <section className="panel" aria-label="Add medication">
          <div className="panel-head">
            <h2>Add a medication</h2>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="field">
              <label htmlFor="name">Name</label>
              <input
                id="name"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="e.g. Ramipril"
                minLength={2}
                maxLength={255}
                required
                disabled={isSaving}
              />
            </div>

            <div className="field">
              <label htmlFor="drugClass">Class</label>
              <input
                id="drugClass"
                name="drugClass"
                value={form.drugClass}
                onChange={handleChange}
                placeholder="e.g. ACE inhibitor"
                maxLength={80}
                list="drug-classes"
                disabled={isSaving}
              />
              <datalist id="drug-classes">
                {[...new Set(drugs.map((drug) => drug.drugClass).filter(Boolean))]
                  .sort()
                  .map((name) => (
                    <option key={name} value={name} />
                  ))}
              </datalist>
            </div>

            <div className="field">
              <label htmlFor="description">Use</label>
              <textarea
                id="description"
                name="description"
                value={form.description}
                onChange={handleChange}
                rows="3"
                maxLength={1000}
                disabled={isSaving}
                placeholder="What the drug is used for"
              />
            </div>

            {error && (
              <p className="form-error" role="alert">
                {error}
              </p>
            )}
            {message && (
              <p className="form-success" role="status">
                {message}
              </p>
            )}

            <button
              type="submit"
              className="btn btn-primary btn-block"
              disabled={isSaving || form.name.trim().length < 2}
            >
              {isSaving ? 'Adding…' : 'Add medication'}
            </button>
          </form>
        </section>
      </div>
    </div>
  );
}

export default CataloguePage;
