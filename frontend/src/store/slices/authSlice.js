import { createSlice } from '@reduxjs/toolkit';

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    token: localStorage.getItem('taskguard_token') || localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('taskguard_user') || localStorage.getItem('user') || 'null'),
    loading: false,
    error: null,
  },
  reducers: {
    loginStart(state) { state.loading = true; state.error = null; },
    loginSuccess(state, action) {
      state.loading = false;
      state.token = action.payload.token;
      state.user = action.payload;
      localStorage.setItem('taskguard_token', action.payload.token);
      localStorage.setItem('taskguard_user', JSON.stringify(action.payload));
      localStorage.setItem('token', action.payload.token);
      localStorage.setItem('user', JSON.stringify(action.payload));
    },
    loginFailure(state, action) { state.loading = false; state.error = action.payload; },
    logout(state) {
      state.token = null;
      state.user = null;
      localStorage.removeItem('taskguard_token');
      localStorage.removeItem('taskguard_user');
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    },
  },
});

export const { loginStart, loginSuccess, loginFailure, logout } = authSlice.actions;
export default authSlice.reducer;
