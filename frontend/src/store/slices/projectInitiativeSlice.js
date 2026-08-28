import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import projectInitiativeService from '../../services/projectInitiativeService';

export const fetchInitiatives = createAsyncThunk(
  'projectInitiatives/fetchAll',
  async ({ query, status, page, size } = {}, { rejectWithValue }) => {
    try {
      return await projectInitiativeService.getAll(query, status, page, size);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch initiatives');
    }
  }
);

export const fetchAnalytics = createAsyncThunk(
  'projectInitiatives/fetchAnalytics',
  async (_, { rejectWithValue }) => {
    try {
      return await projectInitiativeService.getAnalytics();
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch analytics');
    }
  }
);

export const createInitiative = createAsyncThunk(
  'projectInitiatives/create',
  async (data, { rejectWithValue }) => {
    try {
      return await projectInitiativeService.create(data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to create initiative');
    }
  }
);

export const updateInitiative = createAsyncThunk(
  'projectInitiatives/update',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      return await projectInitiativeService.update(id, data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to update initiative');
    }
  }
);

export const deleteInitiative = createAsyncThunk(
  'projectInitiatives/delete',
  async (id, { rejectWithValue }) => {
    try {
      await projectInitiativeService.remove(id);
      return id;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to delete initiative');
    }
  }
);

const projectInitiativeSlice = createSlice({
  name: 'projectInitiatives',
  initialState: {
    items: [],
    analytics: {},
    loading: false,
    error: null,
    totalPages: 0,
    currentPage: 0,
    selectedItem: null,
  },
  reducers: {
    selectInitiative(state, action) { state.selectedItem = action.payload; },
    clearSelectedInitiative(state) { state.selectedItem = null; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchInitiatives.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchInitiatives.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
        state.currentPage = action.payload.number || 0;
      })
      .addCase(fetchInitiatives.rejected, (state, action) => { state.loading = false; state.error = action.payload; })
      .addCase(fetchAnalytics.fulfilled, (state, action) => { state.analytics = action.payload || {}; })
      .addCase(deleteInitiative.fulfilled, (state, action) => {
        state.items = state.items.filter((i) => i.id !== action.payload);
      });
  },
});

export const { selectInitiative, clearSelectedInitiative } = projectInitiativeSlice.actions;
export default projectInitiativeSlice.reducer;
