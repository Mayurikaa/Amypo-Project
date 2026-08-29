import api from './api';

const projectTaskService = {
  getAll: (assigneeId, status, query, page = 0, size = 10) =>
    api.get('/tasks', { params: { assigneeId, status, query, page, size } }).then((r) => r.data),
  getTasks: (params) => api.get('/tasks', { params }).then((r) => r.data),
  create: (data) => api.post('/tasks', data).then((r) => r.data),
  update: (id, data) => api.put(`/tasks/${id}`, data).then((r) => r.data),
  remove: (id) => api.delete(`/tasks/${id}`).then((r) => r.data),
  deleteTask: (id) => api.delete(`/tasks/${id}`, {}).then((r) => r.data),
  assignTask: (id, assigneeId) => api.put(`/tasks/${id}/assign/${assigneeId}`, {}).then((r) => r.data),
  updateTaskStatus: (id, status) => api.patch(`/tasks/${id}/status`, { status }, {}).then((r) => r.data),
};

export default projectTaskService;

// Named exports for components
export const getTasks = (params) => api.get('/tasks', { params }).then((r) => r.data);
export const createTask = (data) => api.post('/tasks', data).then((r) => r.data);
export const updateTask = (id, data) => api.put(`/tasks/${id}`, data).then((r) => r.data);
export const deleteTask = (id) => api.delete(`/tasks/${id}`, {}).then((r) => r.data);
export const updateTaskStatus = (id, status) => api.patch(`/tasks/${id}/status`, { status }, {}).then((r) => r.data);
