import api from './api';

const projectMilestoneService = {
  getAll: (initiativeId, status, page = 0, size = 10) =>
    api.get('/milestones', { params: { initiativeId, status, page, size } }).then((r) => r.data),
  create: (data) => api.post('/milestones', data).then((r) => r.data),
  update: (id, data) => api.put(`/milestones/${id}`, data).then((r) => r.data),
  remove: (id) => api.delete(`/milestones/${id}`).then((r) => r.data),
};

export default projectMilestoneService;
