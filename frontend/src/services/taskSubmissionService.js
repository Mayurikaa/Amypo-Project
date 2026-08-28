import api from './api';

const taskSubmissionService = {
  getAll: (taskId, page = 0, size = 10) =>
    api.get('/submissions', { params: { taskId, page, size } }).then((r) => r.data),
  submitWork: (data) => api.post('/submissions', data).then((r) => r.data),
  reviewSubmission: (id, data) => api.put(`/submissions/${id}/review`, data).then((r) => r.data),
  deleteSubmission: (id) => api.delete(`/submissions/${id}`).then((r) => r.data),
};

export default taskSubmissionService;
