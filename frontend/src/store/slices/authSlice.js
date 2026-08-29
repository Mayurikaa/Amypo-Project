import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import authService from '../../services/authService';

export const loginUser = createAsyncThunk('auth/login', async (credentials, { rejectWithValue }) => {
  try {
    return await authService.login(credentials);
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Authentication failed');
  }
});

const getStoredUser = () => {
  try {
    const user = JSON.parse(localStorage.getItem('taskguard_user') || 'null');
    if (user) return user;
  } catch (err) {
    // ignore malformed storage data
  }
  const role = localStorage.getItem('taskguard_role');
  return role ? { domainRole: role } : null;
};

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    token: localStorage.getItem('taskguard_token') || null,
    user: getStoredUser(),
    isAuthenticated: !!localStorage.getItem('taskguard_token'),
    loading: false,
    error: null,
    accessRevoked: false,
  },
  reducers: {
    logout(state) {
      state.token = null;
      state.user = null;
      state.isAuthenticated = false;
      state.accessRevoked = false;
      localStorage.removeItem('taskguard_token');
      localStorage.removeItem('taskguard_user');
      localStorage.removeItem('taskguard_role');
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(loginUser.pending, (state) => {
        state.loading = true;
        state.error = null;
        state.accessRevoked = false;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        const payload = action.payload || {};
        const token = payload.token || payload.accessToken || null;
        const user = payload.user || {
          id: payload.id,
          email: payload.email,
          fullName: payload.fullName || payload.name,
          domainRole: payload.domainRole || payload.role,
        };

        state.loading = false;
        state.token = token;
        state.user = user;
        state.isAuthenticated = Boolean(token);

        if (token) localStorage.setItem('taskguard_token', token);
        if (user) {
          localStorage.setItem('taskguard_user', JSON.stringify(user));
          if (user.domainRole) localStorage.setItem('taskguard_role', user.domainRole);
        }
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.loading = false;
        if (typeof action.payload === 'string' && action.payload.startsWith('ACCESS_REVOKED:')) {
          state.accessRevoked = true;
          state.error = null;
        } else {
          state.error = action.payload;
        }
      });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;
