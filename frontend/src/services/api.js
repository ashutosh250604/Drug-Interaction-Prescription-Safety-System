import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Drug API functions
export const drugAPI = {
  // Get all drugs
  getAllDrugs: async () => {
    const response = await api.get('/drugs');
    return response.data;
  },

  // Get drug by ID
  getDrugById: async (id) => {
    const response = await api.get(`/drugs/${id}`);
    return response.data;
  },

  // Add a new drug
  addDrug: async (drug) => {
    const response = await api.post('/drugs', drug);
    return response.data;
  },

  // Update a drug
  updateDrug: async (id, drug) => {
    const response = await api.put(`/drugs/${id}`, drug);
    return response.data;
  },

  // Delete a drug
  deleteDrug: async (id) => {
    await api.delete(`/drugs/${id}`);
  },

  // Search drugs by name
  searchDrugs: async (name) => {
    const response = await api.get(`/drugs/search`, { params: { name } });
    return response.data;
  },
};

// Interaction API functions
export const interactionAPI = {
  // Get all interactions
  getAllInteractions: async () => {
    const response = await api.get('/interactions');
    return response.data;
  },

  // Get interactions by severity
  getInteractionsBySeverity: async (severity) => {
    const response = await api.get('/interactions/by-severity', { params: { severity } });
    return response.data;
  },

  // Get interaction by ID
  getInteractionById: async (id) => {
    const response = await api.get(`/interactions/${id}`);
    return response.data;
  },

  // Add a new interaction
  addInteraction: async (interaction) => {
    const response = await api.post('/interactions', interaction);
    return response.data;
  },

  // Delete an interaction
  deleteInteraction: async (id) => {
    await api.delete(`/interactions/${id}`);
  },

  // Check drug interactions (MAIN FEATURE)
  checkInteractions: async (drugs) => {
    const response = await api.post('/check', { drugs });
    return response.data;
  },
};

export default api;
