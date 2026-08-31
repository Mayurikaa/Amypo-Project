import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import projectTaskService from '../../services/projectTaskService';

export const fetchTasks = createAsyncThunk(
  'projectTasks/fetchAll',
  async (params = {}, { rejectWithValue }) => {
    try {
      return await projectTaskService.getAll(
        params.assigneeId, params.status, params.query, params.page, params.size
      );
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Failed to fetch tasks');
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
    addTask(state, action) { state.items.push(action.payload); },
    updateTask(state, action) {
      const idx = state.items.findIndex((t) => t.id === action.payload.id);
      if (idx !== -1) state.items[idx] = action.payload;
    },
    setTasksDirectly(state, action) {
      state.items = action.payload.content || action.payload || [];
      state.totalPages = action.payload.totalPages || 0;
    },
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
      .addCase(fetchTasks.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { selectTask, clearSelectedTask, addTask, updateTask, setTasksDirectly } = projectTaskSlice.actions;
export default projectTaskSlice.reducer;
