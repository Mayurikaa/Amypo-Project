import axios from 'axios';
import api from './api';

const BASE = 'http://localhost:8082/api/v1/milestones';
const getHeaders = () => {
  const token = localStorage.getItem('taskguard_token') || localStorage.getItem('token');
  return { headers: { Authorization: token ? `Bearer ${token}` : undefined } };
};

export const getMilestones = (params) => axios.get(BASE, { params, ...getHeaders() });

const projectMilestoneService = {
  getAll: (initiativeId, status, page = 0, size = 10) =>
    api.get('/milestones', { params: { initiativeId, status, page, size } }).then((r) => r.data),
  create: (data) => api.post('/milestones', data).then((r) => r.data),
  update: (id, data) => api.put(`/milestones/${id}`, data).then((r) => r.data),
  remove: (id) => api.delete(`/milestones/${id}`).then((r) => r.data),
};

export default projectMilestoneService;

