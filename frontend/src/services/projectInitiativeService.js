import api from './api';

const projectInitiativeService = {
  getAll: (query, status, page = 0, size = 10) =>
    api.get('/initiatives', { params: { query, status, page, size } }).then((r) => r.data),
  getById: (id) => api.get(`/initiatives/${id}`).then((r) => r.data),
  create: (data) => api.post('/initiatives', data).then((r) => r.data),
  update: (id, data) => api.put(`/initiatives/${id}`, data).then((r) => r.data),
  remove: (id) => api.delete(`/initiatives/${id}`).then((r) => r.data),
  getAnalytics: () => api.get('/initiatives/analytics').then((r) => r.data),
};

export default projectInitiativeService;
