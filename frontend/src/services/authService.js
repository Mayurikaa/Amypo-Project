import api from './api';

const authService = {
  login: (credentials) => api.post('/auth/login', credentials).then((r) => r.data),
  register: (credentials) => api.post('/auth/register', credentials).then((r) => r.data),
  logout: () => {
    localStorage.removeItem('taskguard_token');
    localStorage.removeItem('taskguard_user');
  },
};

export default authService;
