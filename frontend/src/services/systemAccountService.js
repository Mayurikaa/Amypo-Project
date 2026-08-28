import api from './api';

const systemAccountService = {
  getAll: (role = 'ALL', page = 0, size = 10) =>
    api.get('/accounts', { params: { role, page, size } }).then((r) => r.data),
  getById: (id) => api.get(`/accounts/${id}`).then((r) => r.data),
  register: (data) => api.post('/accounts/provision', data).then((r) => r.data),
  update: (id, data) => api.put(`/accounts/${id}`, data).then((r) => r.data),
  toggleStatus: (id) => api.patch(`/accounts/${id}/status`, {}).then((r) => r.data),
  remove: (id) => api.delete(`/accounts/${id}`).then((r) => r.data),
};

export default systemAccountService;
