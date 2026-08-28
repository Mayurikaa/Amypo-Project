import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import taskSubmissionService from '../../services/taskSubmissionService';

export const fetchSubmissions = createAsyncThunk(
  'taskSubmissions/fetchAll',
  async ({ taskId, page, size } = {}, { rejectWithValue }) => {
    try {
      return await taskSubmissionService.getAll(taskId, page, size);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch submissions');
    }
  }
);

export const submitWork = createAsyncThunk(
  'taskSubmissions/submit',
  async (data, { rejectWithValue }) => {
    try {
      return await taskSubmissionService.submitWork(data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to submit work');
    }
  }
);

export const reviewSubmission = createAsyncThunk(
  'taskSubmissions/review',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      return await taskSubmissionService.reviewSubmission(id, data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to review submission');
    }
  }
);

export const deleteSubmission = createAsyncThunk(
  'taskSubmissions/delete',
  async (id, { rejectWithValue }) => {
    try {
      await taskSubmissionService.deleteSubmission(id);
      return id;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to delete submission');
    }
  }
);

const taskSubmissionSlice = createSlice({
  name: 'taskSubmissions',
  initialState: {
    items: [],
    loading: false,
    error: null,
    totalPages: 0,
    currentPage: 0,
    selectedItem: null,
  },
  reducers: {
    selectSubmission(state, action) { state.selectedItem = action.payload; },
    clearSelectedSubmission(state) { state.selectedItem = null; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchSubmissions.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchSubmissions.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
        state.currentPage = action.payload.number || 0;
      })
      .addCase(fetchSubmissions.rejected, (state, action) => { state.loading = false; state.error = action.payload; })
      .addCase(deleteSubmission.fulfilled, (state, action) => {
        state.items = state.items.filter((s) => s.id !== action.payload);
      })
      .addCase(reviewSubmission.fulfilled, (state, action) => {
        const idx = state.items.findIndex((s) => s.id === action.payload.id);
        if (idx !== -1) state.items[idx] = action.payload;
      });
  },
});

export const { selectSubmission, clearSelectedSubmission } = taskSubmissionSlice.actions;
export default taskSubmissionSlice.reducer;
