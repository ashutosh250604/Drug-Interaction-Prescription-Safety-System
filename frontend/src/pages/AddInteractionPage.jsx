import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { drugAPI, interactionAPI } from '../services/api';

function AddInteractionPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    drug1Name: '',
    drug2Name: '',
    severity: 'MEDIUM',
    description: '',
  });
  const [drugs, setDrugs] = useState([]);
  const [interactions, setInteractions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const [drugsData, interactionsData] = await Promise.all([
        drugAPI.getAllDrugs(),
        interactionAPI.getAllInteractions(),
      ]);
      setDrugs(drugsData);
      setInteractions(interactionsData);
    } catch (err) {
      console.error('Error fetching data:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    setMessage(null);

    try {
      await interactionAPI.addInteraction(formData);
      setMessage('Interaction added successfully!');
      setFormData({
        drug1Name: '',
        drug2Name: '',
        severity: 'MEDIUM',
        description: '',
      });
      fetchData();
      setTimeout(() => {
        navigate('/');
      }, 1500);
    } catch (err) {
      console.error('Error adding interaction:', err);
      setError(
        err.response?.data?.error ||
        'Failed to add interaction. Please try again.'
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this interaction?')) return;

    try {
      await interactionAPI.deleteInteraction(id);
      fetchData();
    } catch (err) {
      console.error('Error deleting interaction:', err);
      setError('Failed to delete interaction. Please try again.');
    }
  };

  const getSeverityClass = (severity) => {
    switch (severity) {
      case 'HIGH':
        return 'severity-high';
      case 'MEDIUM':
        return 'severity-medium';
      case 'LOW':
        return 'severity-low';
      default:
        return '';
    }
  };

  return (
    <div className="add-interaction-page">
      <div className="page-header">
        <h1>Add Drug Interaction</h1>
        <p>Add known drug interactions to the database</p>
      </div>

      <div className="page-content">
        <div className="form-section">
          <div className="card">
            <h2>Add Interaction</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="drug1Name">Drug 1 *</label>
                <select
                  id="drug1Name"
                  name="drug1Name"
                  value={formData.drug1Name}
                  onChange={handleInputChange}
                  required
                  disabled={isSubmitting}
                >
                  <option value="">Select first drug</option>
                  {drugs.map((drug) => (
                    <option key={drug.id} value={drug.name}>
                      {drug.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="drug2Name">Drug 2 *</label>
                <select
                  id="drug2Name"
                  name="drug2Name"
                  value={formData.drug2Name}
                  onChange={handleInputChange}
                  required
                  disabled={isSubmitting}
                >
                  <option value="">Select second drug</option>
                  {drugs.map((drug) => (
                    <option key={drug.id} value={drug.name}>
                      {drug.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="severity">Severity Level *</label>
                <select
                  id="severity"
                  name="severity"
                  value={formData.severity}
                  onChange={handleInputChange}
                  required
                  disabled={isSubmitting}
                >
                  <option value="LOW">LOW - Minor interaction</option>
                  <option value="MEDIUM">MEDIUM - Moderate interaction</option>
                  <option value="HIGH">HIGH - Severe interaction</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="description">Description *</label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="Describe the interaction and its effects"
                  rows="4"
                  required
                  disabled={isSubmitting}
                />
              </div>

              {error && (
                <div className="error-message">
                  <span className="error-icon">!</span>
                  {error}
                </div>
              )}

              {message && (
                <div className="success-message">
                  <span className="success-icon">✓</span>
                  {message}
                </div>
              )}

              <div className="form-actions">
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={
                    isSubmitting ||
                    !formData.drug1Name ||
                    !formData.drug2Name ||
                    !formData.description.trim()
                  }
                >
                  {isSubmitting ? 'Adding...' : 'Add Interaction'}
                </button>
                <Link to="/" className="btn btn-outline">
                  Back to Home
                </Link>
              </div>
            </form>
          </div>
        </div>

        <div className="list-section">
          <div className="card">
            <h2>Existing Interactions ({interactions.length})</h2>
            {isLoading ? (
              <p className="loading">Loading interactions...</p>
            ) : interactions.length === 0 ? (
              <p className="empty-message">No interactions in the database yet.</p>
            ) : (
              <ul className="interaction-list">
                {interactions.map((interaction) => (
                  <li key={interaction.id} className="interaction-item">
                    <div className="interaction-info">
                      <strong>
                        {interaction.drug1} + {interaction.drug2}
                      </strong>
                      <span className={`severity-badge ${getSeverityClass(interaction.severity)}`}>
                        {interaction.severity}
                      </span>
                      <p>{interaction.description}</p>
                    </div>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(interaction.id)}
                    >
                      Delete
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default AddInteractionPage;
