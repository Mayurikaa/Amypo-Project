import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import projectTaskService from '../../services/projectTaskService';

export const fetchTasks = createAsyncThunk(
  'projectTasks/fetchAll',
  async ({ assigneeId, status, query, page, size } = {}, { rejectWithValue }) => {
    try {
      return await projectTaskService.getAll(assigneeId, status, query, page, size);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch tasks');
    }
  }
);

export const createTask = createAsyncThunk(
  'projectTasks/create',
  async (data, { rejectWithValue }) => {
    try {
      return await projectTaskService.create(data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to create task');
    }
  }
);

export const updateTask = createAsyncThunk(
  'projectTasks/update',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      return await projectTaskService.update(id, data);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to update task');
    }
  }
);

export const deleteTask = createAsyncThunk(
  'projectTasks/delete',
  async (id, { rejectWithValue }) => {
    try {
      await projectTaskService.remove(id);
      return id;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to delete task');
    }
  }
);

const projectTaskSlice = createSlice({
  name: 'projectTasks',
  initialState: {
    items: [],
    loading: false,
    error: null,
    totalPages: 0,
    currentPage: 0,
    selectedItem: null,
  },
  reducers: {
    selectTask(state, action) { state.selectedItem = action.payload; },
    clearSelectedTask(state) { state.selectedItem = null; },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchTasks.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchTasks.fulfilled, (state, action) => {
        state.loading = false;
        state.items = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 0;
        state.currentPage = action.payload.number || 0;
      })
      .addCase(fetchTasks.rejected, (state, action) => { state.loading = false; state.error = action.payload; })
      .addCase(deleteTask.fulfilled, (state, action) => {
        state.items = state.items.filter((t) => t.id !== action.payload);
      });
  },
});

export const { selectTask, clearSelectedTask } = projectTaskSlice.actions;
export default projectTaskSlice.reducer;
