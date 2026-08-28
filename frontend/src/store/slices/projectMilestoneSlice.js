import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import projectMilestoneService from '../../services/projectMilestoneService';

export const fetchMilestones = createAsyncThunk(
  'projectMilestones/fetchAll',
  async ({ initiativeId, status, page, size } = {}, { rejectWithValue }) => {
    try {
      return await projectMilestoneService.getAll(initiativeId, status, page, size);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch milestones');
    }
  }
);

export const createMilestone = createAsyncThunk(
  'projectMilestones/create',
  async (data, { rejectWithValue }) => {
    try {
      return await projectMilestoneService.create(data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to create milestone');
    }
  }
);

export const updateMilestone = createAsyncThunk(
  'projectMilestones/update',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      return await projectMilestoneService.update(id, data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to update milestone');
    }
  }
);

export const deleteMilestone = createAsyncThunk(
  'projectMilestones/delete',
  async (id, { rejectWithValue }) => {
    try {
      await projectMilestoneService.remove(id);
      return id;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to delete milestone');
    }
  }
);

const projectMilestoneSlice = createSlice({
  name: 'projectMilestones',
  initialState: {
    items: [],
    loading: false,
    error: null,
    totalPages: 0,
    currentPage: 0,
    selectedItem: null,
  },
  reducers: {
    selectMilestone(state, action) { state.selectedItem = action.payload; },
    clearSelectedMilestone(state) { state.selectedItem = null; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMilestones.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchMilestones.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
        state.currentPage = action.payload.number || 0;
      })
      .addCase(fetchMilestones.rejected, (state, action) => { state.loading = false; state.error = action.payload; })
      .addCase(deleteMilestone.fulfilled, (state, action) => {
        state.items = state.items.filter((m) => m.id !== action.payload);
      });
  },
});

export const { selectMilestone, clearSelectedMilestone } = projectMilestoneSlice.actions;
export default projectMilestoneSlice.reducer;
