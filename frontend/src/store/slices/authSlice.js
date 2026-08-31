import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import authService from '../../services/authService';

export const loginUser = createAsyncThunk('auth/login', async (credentials, { rejectWithValue }) => {
  try {
    return await authService.login(credentials);
  } catch (err) {
    return rejectWithValue(err.response?.data?.message || 'Authentication failed');
  }
});

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    token: localStorage.getItem('taskguard_token') || null,
    user: JSON.parse(localStorage.getItem('taskguard_user') || 'null'),
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
    },
    setUser(state, action) {
      state.user = action.payload;
      state.token = action.payload?.token || state.token;
      state.isAuthenticated = true;
    },
    loginStart(state) {
      state.loading = true;
      state.error = null;
    },
    loginSuccess(state, action) {
      const { token, id, email, fullName, domainRole } = action.payload;
      state.loading = false;
      state.token = token;
      state.user = { id, email, fullName, domainRole };
      state.isAuthenticated = true;
      localStorage.setItem('taskguard_token', token);
      localStorage.setItem('taskguard_user', JSON.stringify({ id, email, fullName, domainRole }));
    },
    loginFailure(state, action) {
      state.loading = false;
      state.error = action.payload;
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
        const { token, id, email, fullName, domainRole } = action.payload;
        state.loading = false;
        state.token = token;
        state.user = { id, email, fullName, domainRole };
        state.isAuthenticated = true;
        localStorage.setItem('taskguard_token', token);
        localStorage.setItem('taskguard_user', JSON.stringify({ id, email, fullName, domainRole }));
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

export const { logout, setUser, loginStart, loginSuccess, loginFailure } = authSlice.actions;
export default authSlice.reducer;
