import api from './api';

const projectTaskService = {
  getAll: (assigneeId, status, query, page = 0, size = 10) =>
    api.get('/tasks', { params: { assigneeId, status, query, page, size } }).then((r) => r.data),
  create: (data) => api.post('/tasks', data).then((r) => r.data),
  update: (id, data) => api.put(`/tasks/${id}`, data).then((r) => r.data),
  remove: (id) => api.delete(`/tasks/${id}`).then((r) => r.data),
  assignTask: (id, assigneeId) => api.put(`/tasks/${id}/assign/${assigneeId}`, {}).then((r) => r.data),
};

export default projectTaskService;
