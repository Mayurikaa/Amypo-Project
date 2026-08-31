import { createSlice } from '@reduxjs/toolkit';

const projectTaskSlice = createSlice({
  name: 'projectTask',
  initialState: { tasks: [], items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  reducers: {
    setTasks(state, action) {
      const content = action.payload?.content || action.payload || [];
      state.tasks = Array.isArray(content) ? content : [];
      state.items = state.tasks;
      state.totalPages = action.payload?.totalPages || 0;
    },
    setLoading(state, action) { state.loading = action.payload; },
    setError(state, action) { state.error = action.payload; },
    addTask(state, action) {
      state.tasks.unshift(action.payload);
      state.items = state.tasks;
    },
    updateTask(state, action) {
      const idx = state.tasks.findIndex(t => t.id === action.payload.id);
      if (idx !== -1) state.tasks[idx] = action.payload;
      state.items = state.tasks;
    },
    removeTask(state, action) {
      state.tasks = state.tasks.filter(t => t.id !== action.payload);
      state.items = state.tasks;
    },
  },
});

export const { setTasks, setLoading, setError, addTask, updateTask, removeTask } = projectTaskSlice.actions;
export default projectTaskSlice.reducer;
