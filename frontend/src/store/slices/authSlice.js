import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import authService from '../../services/authService';

export const loginUser = createAsyncThunk('auth/login', async (credentials, { rejectWithValue }) => {
  try {
    return await authService.login(credentials);
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Authentication failed');
  }
});

const normalizeUser = (user) => {
  if (!user || typeof user !== 'object') return null;

  const normalized = {
    ...user,
    fullName: user.fullName || user.name || '',
    email: user.email || '',
    domainRole: user.domainRole || user.role || null,
  };

  return normalized;
};

const getStoredUser = () => {
  try {
    const stored = JSON.parse(localStorage.getItem('taskguard_user') || 'null');
    if (stored) return normalizeUser(stored);
  } catch (err) {
    // ignore malformed storage data
  }

  const role = localStorage.getItem('taskguard_role') || localStorage.getItem('role');
  return role ? { domainRole: role } : null;
};

const persistAuthState = (token, user) => {
  const normalized = normalizeUser(user);

  if (token) {
    localStorage.setItem('taskguard_token', token);
    localStorage.setItem('token', token);
  }

  if (normalized) {
    localStorage.setItem('taskguard_user', JSON.stringify(normalized));
    if (normalized.domainRole) {
      localStorage.setItem('taskguard_role', normalized.domainRole);
      localStorage.setItem('role', normalized.domainRole);
    }
  }
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
    loginStart(state) {
      state.loading = true;
      state.error = null;
    },
    loginSuccess(state, action) {
      const payload = action.payload || {};
      const token = payload.token || payload.accessToken || null;
      const user = normalizeUser(payload.user || {
        id: payload.id,
        email: payload.email,
        fullName: payload.fullName || payload.name,
        domainRole: payload.domainRole || payload.role,
      });
      state.loading = false;
      state.token = token;
      state.user = user;
      state.isAuthenticated = Boolean(token);
      persistAuthState(token, user);
    },
    loginFailure(state, action) {
      state.loading = false;
      state.error = action.payload || 'Authentication failed';
    },
    logout(state) {
      state.token = null;
      state.user = null;
      state.isAuthenticated = false;
      state.accessRevoked = false;
      localStorage.removeItem('taskguard_token');
      localStorage.removeItem('taskguard_user');
      localStorage.removeItem('taskguard_role');
      localStorage.removeItem('token');
      localStorage.removeItem('role');
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
        const user = normalizeUser(payload.user || {
          id: payload.id,
          email: payload.email,
          fullName: payload.fullName || payload.name,
          domainRole: payload.domainRole || payload.role,
        });

        state.loading = false;
        state.token = token;
        state.user = user;
        state.isAuthenticated = Boolean(token);
        persistAuthState(token, user);
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

export const { loginStart, loginSuccess, loginFailure, logout } = authSlice.actions;
export default authSlice.reducer;
