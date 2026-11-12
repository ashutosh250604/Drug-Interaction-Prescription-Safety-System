import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const drugAPI = {
  getAllDrugs: async () => {
    const response = await api.get('/drugs');
    return response.data;
  },

  searchDrugs: async (name) => {
    const response = await api.get('/drugs/search', { params: { name } });
    return response.data;
  },

  addDrug: async (drug) => {
    const response = await api.post('/drugs', drug);
    return response.data;
  },

  updateDrug: async (id, drug) => {
    const response = await api.put(`/drugs/${id}`, drug);
    return response.data;
  },

  deleteDrug: async (id) => {
    await api.delete(`/drugs/${id}`);
  },
};

export const interactionAPI = {
  getAllInteractions: async () => {
    const response = await api.get('/interactions');
    return response.data;
  },

  filterInteractions: async ({ q, severity } = {}) => {
    const response = await api.get('/interactions/filter', {
      params: {
        q: q || undefined,
        severity: severity || undefined,
      },
    });
    return response.data;
  },

  addInteraction: async (interaction) => {
    const response = await api.post('/interactions', interaction);
    return response.data;
  },

  deleteInteraction: async (id) => {
    await api.delete(`/interactions/${id}`);
  },

  /**
   * Returns { interactions, checkedDrugs, unknownDrugs, duplicateTherapies }.
   */
  checkInteractions: async (drugs) => {
    const response = await api.post('/check', { drugs });
    return response.data;
  },
};

export default api;
