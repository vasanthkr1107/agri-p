import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  try {
    const raw = localStorage.getItem('smart_crop_auth');
    if (raw) {
      const { token } = JSON.parse(raw);
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
  } catch {
    /* ignore */
  }
  return config;
});

// Public registration
export const createUser = (data) => api.post('/users', data);

// Admin-only user management (requires Bearer token + ADMIN role)
export const getAdminUsers = () => api.get('/admin/users');
export const getAdminUserById = (id) => api.get(`/admin/users/${id}`);
export const updateAdminUser = (id, data) => api.put(`/admin/users/${id}`, data);
export const deleteAdminUser = (id) => api.delete(`/admin/users/${id}`);

// Diseases
export const getDiseases = () => api.get('/diseases');
export const getDiseaseById = (id) => api.get(`/diseases/${id}`);
export const createDisease = (data) => api.post('/diseases', data);
export const updateDisease = (id, data) => api.put(`/diseases/${id}`, data);
export const deleteDisease = (id) => api.delete(`/diseases/${id}`);

// Predictions
export const getPredictions = () => api.get('/predictions');
export const getPredictionById = (id) => api.get(`/predictions/${id}`);
export const createPrediction = (data) => api.post('/predictions', data);
export const updatePrediction = (id, data) => api.put(`/predictions/${id}`, data);
export const deletePrediction = (id) => api.delete(`/predictions/${id}`);

// Advisories
export const getAdvisories = () => api.get('/advisories');
export const getAdvisoryById = (id) => api.get(`/advisories/${id}`);
export const createAdvisory = (data) => api.post('/advisories', data);
export const updateAdvisory = (id, data) => api.put(`/advisories/${id}`, data);
export const deleteAdvisory = (id) => api.delete(`/advisories/${id}`);

// Weather
export const getWeather = () => api.get('/weather');
export const getWeatherForecast = (params) => api.get('/weather/forecast', { params });

// Crops — AI suggestion (Spring → FastAPI ML → MySQL)
export const suggestCropAi = (data) => api.post('/crops/suggest-ai', data);

// Crop prediction history (requires auth token — linked to logged-in user)
export const getCropHistory = () => api.get('/crop-history/me');

// Plant Disease Detection via Multipart Form Data
export const detectDiseaseAi = (formData) => api.post('/diseases/detect', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
});

// Pesticide Recommendation (Spring -> FastAPI -> DB)
export const recommendPesticideAi = (data) => api.post('/pesticides/recommend', data);

export default api;
