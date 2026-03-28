import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { drugAPI } from '../services/api';

function AddDrugPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    description: '',
  });
  const [drugs, setDrugs] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDrugs();
  }, []);

  const fetchDrugs = async () => {
    setIsLoading(true);
    try {
      const data = await drugAPI.getAllDrugs();
      setDrugs(data);
    } catch (err) {
      console.error('Error fetching drugs:', err);
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
      await drugAPI.addDrug(formData);
      setMessage('Drug added successfully!');
      setFormData({ name: '', description: '' });
      fetchDrugs();
      setTimeout(() => {
        navigate('/');
      }, 1500);
    } catch (err) {
      console.error('Error adding drug:', err);
      setError(
        err.response?.data?.error ||
        'Failed to add drug. Please try again.'
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this drug?')) return;

    try {
      await drugAPI.deleteDrug(id);
      fetchDrugs();
    } catch (err) {
      console.error('Error deleting drug:', err);
      setError('Failed to delete drug. Please try again.');
    }
  };

  return (
    <div className="add-drug-page">
      <div className="page-header">
        <h1>Add New Drug</h1>
        <p>Add drugs to the database for interaction checking</p>
      </div>

      <div className="page-content">
        <div className="form-section">
          <div className="card">
            <h2>Add Drug</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="name">Drug Name *</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  placeholder="Enter drug name"
                  required
                  disabled={isSubmitting}
                />
              </div>

              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="Enter drug description"
                  rows="3"
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
                  disabled={isSubmitting || !formData.name.trim()}
                >
                  {isSubmitting ? 'Adding...' : 'Add Drug'}
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
            <h2>Existing Drugs ({drugs.length})</h2>
            {isLoading ? (
              <p className="loading">Loading drugs...</p>
            ) : drugs.length === 0 ? (
              <p className="empty-message">No drugs in the database yet.</p>
            ) : (
              <ul className="drug-list">
                {drugs.map((drug) => (
                  <li key={drug.id} className="drug-item">
                    <div className="drug-info">
                      <strong>{drug.name}</strong>
                      {drug.description && <p>{drug.description}</p>}
                    </div>
                    <button
                      type="button"
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(drug.id)}
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

export default AddDrugPage;
