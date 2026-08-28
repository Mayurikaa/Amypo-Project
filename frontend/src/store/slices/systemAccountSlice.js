import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import systemAccountService from '../../services/systemAccountService';

export const fetchSystemAccounts = createAsyncThunk(
  'systemAccounts/fetchAll',
  async ({ role, page, size } = {}, { rejectWithValue }) => {
    try {
      return await systemAccountService.getAll(role, page, size);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch accounts');
    }
  }
);

export const registerUser = createAsyncThunk(
  'systemAccounts/register',
  async (data, { rejectWithValue }) => {
    try {
      return await systemAccountService.register(data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to register account');
    }
  }
);

export const updateAccount = createAsyncThunk(
  'systemAccounts/update',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      return await systemAccountService.update(id, data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to update account');
    }
  }
);

export const toggleAccountStatus = createAsyncThunk(
  'systemAccounts/toggleStatus',
  async (id, { rejectWithValue }) => {
    try {
      return await systemAccountService.toggleStatus(id);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to toggle status');
    }
  }
);

const systemAccountSlice = createSlice({
  name: 'systemAccounts',
  initialState: {
    items: [],
    loading: false,
    error: null,
    totalPages: 0,
    currentPage: 0,
    selectedItem: null,
  },
  reducers: {
    selectAccount(state, action) { state.selectedItem = action.payload; },
    clearSelectedAccount(state) { state.selectedItem = null; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchSystemAccounts.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchSystemAccounts.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
        state.currentPage = action.payload.number || 0;
      })
      .addCase(fetchSystemAccounts.rejected, (state, action) => { state.loading = false; state.error = action.payload; });
  },
});

export const { selectAccount, clearSelectedAccount } = systemAccountSlice.actions;
export default systemAccountSlice.reducer;
