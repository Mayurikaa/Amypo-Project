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
      return rejectWithValue({
        status: err.response?.status,
        message: err.response?.data?.message || err.response?.data?.error || 'Failed to create task',
      });
    }
  }
);

export const updateTaskAsync = createAsyncThunk(
  'projectTasks/update',
  async ({ id, data }, { rejectWithValue }) => {
    try {
      return await projectTaskService.update(id, data);
    } catch (err) {
      return rejectWithValue({
        status: err.response?.status,
        message: err.response?.data?.message || err.response?.data?.error || 'Failed to update task',
      });
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
    tasks: [],
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
    setTasks(state, action) { 
      state.tasks = action.payload?.content || action.payload || [];
      state.items = state.tasks;
    },
    addTask(state, action) { 
      state.tasks.push(action.payload);
      state.items.push(action.payload);
    },
    removeTask(state, action) { 
      state.tasks = state.tasks.filter((t) => t.id !== action.payload);
      state.items = state.items.filter((t) => t.id !== action.payload);
    },
    replaceTask(state, action) {
      const index = state.tasks.findIndex((t) => t.id === action.payload.id);
      if (index !== -1) {
        state.tasks[index] = action.payload;
      }
      const itemIndex = state.items.findIndex((t) => t.id === action.payload.id);
      if (itemIndex !== -1) {
        state.items[itemIndex] = action.payload;
      }
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchTasks.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchTasks.fulfilled, (state, action) => {
        state.loading = false;
        state.tasks = action.payload.content || [];
        state.items = state.tasks;
        state.totalPages = action.payload.totalPages || 0;
        state.currentPage = action.payload.number || 0;
      })
      .addCase(fetchTasks.rejected, (state, action) => { state.loading = false; state.error = action.payload; })
      .addCase(updateTaskAsync.fulfilled, (state, action) => {
        const index = state.tasks.findIndex((t) => t.id === action.payload.id);
        if (index !== -1) {
          state.tasks[index] = action.payload;
        }
        const itemIndex = state.items.findIndex((t) => t.id === action.payload.id);
        if (itemIndex !== -1) {
          state.items[itemIndex] = action.payload;
        }
      })
      .addCase(deleteTask.fulfilled, (state, action) => {
        state.tasks = state.tasks.filter((t) => t.id !== action.payload);
        state.items = state.items.filter((t) => t.id !== action.payload);
      });
  },
});

const { selectTask: selectTaskAction, clearSelectedTask: clearSelectedTaskAction, setTasks: setTasksAction, addTask: addTaskAction, removeTask: removeTaskAction, replaceTask } = projectTaskSlice.actions;
export const selectTask = selectTaskAction;
export const clearSelectedTask = clearSelectedTaskAction;
export const setTasks = setTasksAction;
export const addTask = addTaskAction;
export const removeTask = removeTaskAction;
export const updateTask = replaceTask;
export default projectTaskSlice.reducer;
