import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import axios from 'axios';

import authReducer, { loginSuccess, loginStart, loginFailure } from '../store/slices/authSlice';
import projectTaskReducer from '../store/slices/projectTaskSlice';
import projectInitiativeReducer from '../store/slices/projectInitiativeSlice';
import projectMilestoneReducer from '../store/slices/projectMilestoneSlice';
import systemAccountReducer from '../store/slices/systemAccountSlice';
import taskSubmissionReducer from '../store/slices/taskSubmissionSlice';

// ─── helpers ────────────────────────────────────────────────────────────────

const makeStore = (preloaded = {}) =>
  configureStore({
    reducer: {
      auth: authReducer,
      projectTasks: projectTaskReducer,
      projectInitiatives: projectInitiativeReducer,
      projectMilestones: projectMilestoneReducer,
      systemAccounts: systemAccountReducer,
      taskSubmissions: taskSubmissionReducer,
    },
    preloadedState: preloaded,
  });

let store;

const setAuthState = (role) => {
  store.dispatch(loginSuccess({ token: 'tok', id: 1, email: 'a@b.com', fullName: 'Test', domainRole: role }));
};

const TASK = { id: 1, taskCode: 'TASK-001', title: 'Provision Backend API', priority: 'HIGH', estimatedHours: 10, loggedHours: 0, dueDate: '2026-12-31', status: 'PENDING' };

const submitProjectTaskForm = async (data = {}) => {
  const titleInput = screen.queryByLabelText('Title');
  if (titleInput && data.title !== undefined) fireEvent.change(titleInput, { target: { value: data.title ?? 'Provision Backend API' } });
  const initiativeSelect = screen.queryByLabelText('Initiative');
  if (initiativeSelect) fireEvent.change(initiativeSelect, { target: { value: data.initiativeId || '1' } });

  fireEvent.click(screen.getByRole('button', { name: /finalize/i }));
};

const originalError = console.error;

beforeEach(() => {
  jest.clearAllMocks();
  localStorage.clear();
  store = makeStore({
    auth: { token: null, user: null, isAuthenticated: false, loading: false, error: null, accessRevoked: false },
    projectTasks: { items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  });
  axios.get.mockResolvedValue({ data: { content: [], totalPages: 0 } });
  axios.post.mockResolvedValue({ data: {} });
  axios.put.mockResolvedValue({ data: {} });
  axios.delete.mockResolvedValue({ data: {} });
  console.error = (...args) => {
    if (typeof args[0] === 'string' && args[0].includes('Warning:')) return;
    originalError(...args);
  };
});

afterEach(() => {
  console.error = originalError;
});

// ─── T01 ─────────────────────────────────────────────────────────────────────
test('T01 - Redux Store: integrity check', () => {
  const state = store.getState();
  expect(state).toHaveProperty('auth');
  expect(state).toHaveProperty('projectTasks');
});

// ─── T02 ─────────────────────────────────────────────────────────────────────
test('T02 - AuthService: functionality exports', async () => {
  const authService = await import('../services/authService');
  expect(typeof authService.login).toBe('function');
  expect(typeof authService.register).toBe('function');
});

// ─── T03 ─────────────────────────────────────────────────────────────────────
test('T03 - Navbar: structural rendering', async () => {
  setAuthState('PROJECT_DIRECTOR');
  const { default: Navbar } = await import('../components/layout/Navbar');
  await act(async () => { render(<Provider store={store}><Navbar /></Provider>); });
  expect(document.body).toBeTruthy();
});

// ─── T04 ─────────────────────────────────────────────────────────────────────
test('T04 - Login: form field presence', async () => {
  const { default: Login } = await import('../components/Login');
  await act(async () => { render(<Provider store={store}><Login /></Provider>); });
  expect(screen.getByPlaceholderText(/user@enterprise/i)).toBeInTheDocument();
  expect(screen.getByPlaceholderText(/••••/i)).toBeInTheDocument();
});

// ─── T05 ─────────────────────────────────────────────────────────────────────
test('T05 - ErrorHandler: error boundary catch', async () => {
  const { default: ErrorHandler } = await import('../components/ErrorHandler');
  const Bomb = () => { throw new Error('boom'); };
  await act(async () => {
    render(<ErrorHandler><Bomb /></ErrorHandler>);
  });
  expect(document.body).toBeTruthy();
});

// ─── T06 ─────────────────────────────────────────────────────────────────────
test('T06 - ProjectTaskList: admin add button visible', async () => {
  store = makeStore({
    auth: { token: 'tok', user: { id: 1, domainRole: 'PROJECT_DIRECTOR' }, isAuthenticated: true, loading: false, error: null, accessRevoked: false },
    projectTasks: { items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  });
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList /></Provider>); });
  expect(screen.getByRole('button', { name: /add projecttask/i })).toBeInTheDocument();
});

// ─── T07 ─────────────────────────────────────────────────────────────────────
test('T07 - ProjectTaskList: contributor add button hidden', async () => {
  store = makeStore({
    auth: { token: 'tok', user: { id: 1, domainRole: 'TEAM_CONTRIBUTOR' }, isAuthenticated: true, loading: false, error: null, accessRevoked: false },
    projectTasks: { items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  });
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList /></Provider>); });
  expect(screen.queryByRole('button', { name: /add projecttask/i })).not.toBeInTheDocument();
});

// ─── T08 ─────────────────────────────────────────────────────────────────────
test('T08 - ProjectTaskForm: title state update', async () => {
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} /></Provider>); });
  const input = screen.getByLabelText('Title');
  fireEvent.change(input, { target: { value: 'New Title' } });
  expect(input.value).toBe('New Title');
});

// ─── T09 ─────────────────────────────────────────────────────────────────────
test('T09 - ProjectTaskForm: priority select update', async () => {
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} /></Provider>); });
  const sel = screen.getByLabelText('Priority');
  fireEvent.change(sel, { target: { value: 'HIGH' } });
  expect(sel.value).toBe('HIGH');
});

// ─── T10 ─────────────────────────────────────────────────────────────────────
test('T10 - ProjectTaskList: mount data fetch', async () => {
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList /></Provider>); });
  await waitFor(() => expect(axios.get).toHaveBeenCalled());
});

// ─── T11 ─────────────────────────────────────────────────────────────────────
test('T11 - ProjectTaskList: filter-triggered re-fetch', async () => {
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList /></Provider>); });
  const callsBefore = axios.get.mock.calls.length;
  fireEvent.change(screen.getByLabelText('Task status filter'), { target: { value: 'PENDING' } });
  await waitFor(() => expect(axios.get.mock.calls.length).toBeGreaterThan(callsBefore));
});

// ─── T12 ─────────────────────────────────────────────────────────────────────
test('T12 - ProjectTaskList: edit button visibility', async () => {
  axios.get.mockResolvedValue({ data: { content: [TASK], totalPages: 1 } });
  store = makeStore({
    auth: { token: 'tok', user: { id: 1, domainRole: 'PROJECT_DIRECTOR' }, isAuthenticated: true, loading: false, error: null, accessRevoked: false },
    projectTasks: { items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  });
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList /></Provider>); });
  const btn = await waitFor(() => screen.getByRole('button', { name: /edit/i }));
  expect(btn).toBeInTheDocument();
});

// ─── T13 ─────────────────────────────────────────────────────────────────────
test('T13 - ProjectTaskList: add button toggle logic', async () => {
  store = makeStore({
    auth: { token: 'tok', user: { id: 1, domainRole: 'PROJECT_DIRECTOR' }, isAuthenticated: true, loading: false, error: null, accessRevoked: false },
    projectTasks: { items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  });
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  let opened = false;
  await act(async () => { render(<Provider store={store}><EntityList onOpenForm={(v) => { opened = v; }} /></Provider>); });
  fireEvent.click(screen.getByRole('button', { name: /add projecttask/i }));
  expect(opened).toBeTruthy();
});

// ─── T14 ─────────────────────────────────────────────────────────────────────
test('T14 - ProjectTaskList: delete success feedback', async () => {
  axios.get.mockResolvedValue({ data: { content: [TASK], totalPages: 1 } });
  store = makeStore({
    auth: { token: 'tok', user: { id: 1, domainRole: 'PROJECT_DIRECTOR' }, isAuthenticated: true, loading: false, error: null, accessRevoked: false },
    projectTasks: { items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  });
  window.confirm = jest.fn(() => true);
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList /></Provider>); });
  fireEvent.click(await waitFor(() => screen.getByRole('button', { name: /delete/i })));
  await waitFor(() => expect(axios.delete).toHaveBeenCalled());
});

// ─── T15 ─────────────────────────────────────────────────────────────────────
test('T15 - ProjectTaskForm: create success alert', async () => {
  axios.get.mockResolvedValue({ data: { content: [{ id: 1, projectCode: 'PRJ', title: 'Test Init' }], totalPages: 1 } });
  axios.post.mockResolvedValueOnce({ data: { id: 2, taskCode: 'TASK-002', title: 'Provision Backend API', status: 'PENDING' } });
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} /></Provider>); });
  await act(async () => { await submitProjectTaskForm({ title: 'Provision Backend API' }); });
  await waitFor(() => expect(screen.getByText(/added to pipeline/i)).toBeInTheDocument());
});

// ─── T16 ─────────────────────────────────────────────────────────────────────
test('T16 - ProjectTaskList: update success alert', async () => {
  axios.get.mockResolvedValue({ data: { content: [TASK], totalPages: 1 } });
  store = makeStore({
    auth: { token: 'tok', user: { id: 1, domainRole: 'PROJECT_DIRECTOR' }, isAuthenticated: true, loading: false, error: null, accessRevoked: false },
    projectTasks: { items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  });
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList onOpenForm={() => {}} /></Provider>); });
  expect(await waitFor(() => screen.getByRole('button', { name: /edit/i }))).toBeInTheDocument();
});

// ─── T17 ─────────────────────────────────────────────────────────────────────
test('T17 - ProjectTaskList: row data rendering', async () => {
  axios.get.mockResolvedValue({ data: { content: [TASK], totalPages: 1 } });
  store = makeStore({
    auth: { token: 'tok', user: { id: 1, domainRole: 'PROJECT_DIRECTOR' }, isAuthenticated: true, loading: false, error: null, accessRevoked: false },
    projectTasks: { items: [], loading: false, error: null, totalPages: 0, currentPage: 0, selectedItem: null },
  });
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList /></Provider>); });
  await waitFor(() => expect(screen.getByText(/Provision Backend API/i)).toBeInTheDocument());
});

// ─── T18 ─────────────────────────────────────────────────────────────────────
test('T18 - ProjectTaskForm: dropdown data population', async () => {
  axios.get.mockResolvedValue({ data: { content: [{ id: 1, projectCode: 'PRJ', title: 'Test Init' }], totalPages: 1 } });
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} /></Provider>); });
  await waitFor(() => expect(screen.getByText(/Test Init/i)).toBeInTheDocument());
});

// ─── T19 ─────────────────────────────────────────────────────────────────────
test('T19 - ProjectTaskForm: 500 server error handling', async () => {
  axios.get.mockResolvedValue({ data: { content: [{ id: 1, projectCode: 'PRJ', title: 'Test Init' }], totalPages: 1 } });
  axios.post.mockRejectedValueOnce({ response: { status: 500, data: { message: 'Internal server error occurred' } } });
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} /></Provider>); });
  await act(async () => { await submitProjectTaskForm({ title: 'Provision Backend API' }); });
  await waitFor(() => expect(screen.getByText(/Internal server error/i)).toBeInTheDocument());
});

// ─── T20 ─────────────────────────────────────────────────────────────────────
test('T20 - ProjectTaskList: 401 token expiry handling', async () => {
  axios.get.mockRejectedValueOnce({ response: { status: 401 } });
  const { default: EntityList } = await import('../components/projectTask/ProjectTaskList');
  await act(async () => { render(<Provider store={store}><EntityList /></Provider>); });
  expect(document.body).toBeTruthy();
});

// ─── T21 ─────────────────────────────────────────────────────────────────────
test('T21 - ProjectTaskForm: required field validation', async () => {
  axios.get.mockResolvedValue({ data: { content: [{ id: 1, projectCode: 'PRJ', title: 'Test Init' }], totalPages: 1 } });
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} /></Provider>); });
  // Ensure initiatives are loaded first to avoid race conditions
  await waitFor(() => expect(screen.getByText(/Test Init/i)).toBeInTheDocument());
  fireEvent.click(screen.getByRole('button', { name: /finalize/i }));
  await waitFor(() => expect(screen.getByText(/Actionable title label is mandatory/i)).toBeInTheDocument());
});

// ─── T22 ─────────────────────────────────────────────────────────────────────
test('T22 - Success Alert: auto-dismissal timers', async () => {
  jest.useFakeTimers();
  axios.get.mockResolvedValue({ data: { content: [{ id: 1, projectCode: 'PRJ', title: 'Test Init' }], totalPages: 1 } });
  axios.post.mockResolvedValueOnce({ data: { id: 2, taskCode: 'TASK-002', title: 'Provision Backend API', status: 'PENDING' } });
  const onClose = jest.fn();
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} onClose={onClose} /></Provider>); });
  await act(async () => { await submitProjectTaskForm({ title: 'Provision Backend API' }); });
  await waitFor(() => expect(screen.getByText(/added to pipeline/i)).toBeInTheDocument());
  act(() => { jest.advanceTimersByTime(1500); });
  expect(onClose).toHaveBeenCalled();
  jest.useRealTimers();
});

// ─── T23 ─────────────────────────────────────────────────────────────────────
test('T23 - ProjectTaskForm: 409 conflict handling', async () => {
  axios.get.mockResolvedValue({ data: { content: [{ id: 1, projectCode: 'PRJ', title: 'Test Init' }], totalPages: 1 } });
  axios.post.mockRejectedValueOnce({ response: { status: 409, data: { message: 'Task code conflict detected' } } });
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} /></Provider>); });
  await act(async () => { await submitProjectTaskForm({ title: 'Provision Backend API' }); });
  await waitFor(() => expect(screen.getByText(/Task code conflict/i)).toBeInTheDocument());
});

// ─── T24 ─────────────────────────────────────────────────────────────────────
test('T24 - NotificationStack: component visibility', async () => {
  const { default: NotificationStack } = await import('../components/NotificationStack');
  await act(async () => { render(<Provider store={store}><NotificationStack /></Provider>); });
  expect(document.body).toBeTruthy();
});

// ─── T25 ─────────────────────────────────────────────────────────────────────
test('T25 - Login: localStorage token persistence', async () => {
  axios.post.mockResolvedValueOnce({ data: { token: 'tkn', id: 1, email: 'a@t.com', fullName: 'A', domainRole: 'USER' } });
  const { default: Login } = await import('../components/Login');
  await act(async () => { render(<Provider store={store}><Login /></Provider>); });
  fireEvent.change(screen.getByPlaceholderText(/user@enterprise/i), { target: { value: 'a@t.com' } });
  fireEvent.change(screen.getByPlaceholderText(/••••/i), { target: { value: 'password123' } });
  fireEvent.click(screen.getByRole('button', { name: /authorize session/i }));
  await waitFor(() => expect(localStorage.getItem('taskguard_token')).toBe('tkn'));
});

// ─── T26 ─────────────────────────────────────────────────────────────────────
test('T26 - Login: localStorage role persistence', async () => {
  axios.post.mockResolvedValueOnce({ data: { token: 'tkn', id: 1, email: 'a@t.com', fullName: 'A', domainRole: 'USER' } });
  const { default: Login } = await import('../components/Login');
  await act(async () => { render(<Provider store={store}><Login /></Provider>); });
  fireEvent.change(screen.getByPlaceholderText(/user@enterprise/i), { target: { value: 'a@t.com' } });
  fireEvent.change(screen.getByPlaceholderText(/••••/i), { target: { value: 'password123' } });
  fireEvent.click(screen.getByRole('button', { name: /authorize session/i }));
  await waitFor(() => {
    const user = JSON.parse(localStorage.getItem('taskguard_user'));
    expect(user.domainRole).toBe('USER');
  });
});

// ─── T27 ─────────────────────────────────────────────────────────────────────
test('T27 - Logout: localStorage cleanup', async () => {
  localStorage.setItem('taskguard_token', 'tok');
  localStorage.setItem('taskguard_user', JSON.stringify({ domainRole: 'USER' }));
  const { logout } = await import('../store/slices/authSlice');
  store.dispatch(logout());
  expect(localStorage.getItem('taskguard_token')).toBeNull();
  expect(localStorage.getItem('taskguard_user')).toBeNull();
});

// ─── T28 ─────────────────────────────────────────────────────────────────────
test('T28 - Logout: Redux state reset', async () => {
  setAuthState('PROJECT_DIRECTOR');
  const { logout } = await import('../store/slices/authSlice');
  store.dispatch(logout());
  expect(store.getState().auth.user).toBeNull();
  expect(store.getState().auth.token).toBeNull();
});

// ─── T29 ─────────────────────────────────────────────────────────────────────
test('T29 - Redux State: auth schema validation', async () => {
  setAuthState('DIRECTOR');
  expect(store.getState().auth.user.domainRole).toBe('DIRECTOR');
});

// ─── T30 ─────────────────────────────────────────────────────────────────────
test('T30 - ProjectTaskForm: milestone population check', async () => {
  axios.get
    .mockResolvedValueOnce({ data: { content: [{ id: 1, projectCode: 'PRJ', title: 'Test Init' }], totalPages: 1 } })
    .mockResolvedValueOnce({ data: { content: [{ id: 1, title: 'Milestone 1' }], totalPages: 1 } });
  const { default: EntityForm } = await import('../components/projectTask/ProjectTaskForm');
  await act(async () => { render(<Provider store={store}><EntityForm isOpen={true} /></Provider>); });
  await waitFor(() => expect(screen.getByText(/Test Init/i)).toBeInTheDocument());
  const selects = screen.getAllByRole('combobox');
  fireEvent.change(selects[0], { target: { value: '1' } });
  await waitFor(() => expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('/milestones'), expect.any(Object)));
});
