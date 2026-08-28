import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import ProjectTaskForm from '../components/projectTask/ProjectTaskForm';
import projectTaskReducer from '../store/slices/projectTaskSlice';
import authReducer from '../store/slices/authSlice';
import axios from 'axios';

const makeStore = () =>
  configureStore({
    reducer: {
      projectTask: projectTaskReducer,
      auth: authReducer,
    },
    preloadedState: {
      auth: { user: { id: 1, domainRole: 'PROJECT_DIRECTOR' }, token: 'token', loading: false, error: null },
    },
  });

const renderForm = (existing = null) => {
  const onClose = jest.fn();
  const utils = render(
    <Provider store={makeStore()}>
      <ProjectTaskForm existing={existing} onClose={onClose} />
    </Provider>
  );
  return { ...utils, onClose };
};

describe('ProjectTaskForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    axios.get.mockResolvedValue({ data: { content: [] } });
  });

  test('renders New Task form with correct field labels', () => {
    renderForm();
    expect(screen.getByText('New Task')).toBeInTheDocument();
    expect(screen.getByText('Sequential task tag string must be specified.')).toBeInTheDocument();
    expect(screen.getByText('Actionable task scope title is required.')).toBeInTheDocument();
    expect(screen.getByText('Estimated workload capacity must be a positive count.')).toBeInTheDocument();
  });

  test('renders Edit Task form when existing prop is provided', () => {
    const existing = {
      id: 1, taskCode: 'TASK-001', initiativeId: 1, title: 'Existing Task',
      priority: 'HIGH', estimatedHours: 8, dueDate: '2025-12-31', status: 'PENDING',
    };
    renderForm(existing);
    expect(screen.getByText('Edit Task')).toBeInTheDocument();
  });

  test('Actionable title label is mandatory', () => {
    renderForm();
    expect(screen.getByText('Actionable task scope title is required.')).toBeInTheDocument();
  });

  test('on successful task creation shows success alert with exact message', async () => {
    axios.post.mockResolvedValueOnce({
      data: { id: 1, taskCode: 'TASK-NEW', title: 'New Task', status: 'PENDING' },
    });
    renderForm();
    fireEvent.change(screen.getAllByRole('textbox')[0], { target: { value: 'TASK-NEW' } });
    fireEvent.submit(screen.getByRole('button', { name: /save/i }).closest('form'));
    await waitFor(() => {
      expect(screen.getByText('New workload task entry added to pipeline successfully.')).toBeInTheDocument();
    });
  });

  test('auto-dismisses success notification using jest.useFakeTimers without throwing errors', async () => {
    jest.useFakeTimers();
    axios.post.mockResolvedValueOnce({
      data: { id: 1, taskCode: 'TASK-NEW', title: 'New Task', status: 'PENDING' },
    });
    const { onClose } = renderForm();
    fireEvent.submit(screen.getByRole('button', { name: /save/i }).closest('form'));
    await waitFor(() => {
      expect(axios.post).toHaveBeenCalled();
    });
    act(() => { jest.runAllTimers(); });
    expect(onClose).toHaveBeenCalled();
    jest.useRealTimers();
  });

  test('shows error message on create failure', async () => {
    axios.post.mockRejectedValueOnce({
      response: { data: { message: 'Task Code parameter matching collision: Code reference already maps to active pipeline entry.' } },
    });
    renderForm();
    fireEvent.submit(screen.getByRole('button', { name: /save/i }).closest('form'));
    await waitFor(() => {
      expect(screen.getByText(/Task Code parameter matching collision/)).toBeInTheDocument();
    });
  });

  test('calls axios.put on update when existing task provided', async () => {
    const existing = {
      id: 5, taskCode: 'TASK-005', initiativeId: 1, title: 'Old Title',
      priority: 'LOW', estimatedHours: 4, dueDate: '2025-12-31', status: 'PENDING',
    };
    axios.put.mockResolvedValueOnce({ data: { ...existing, title: 'Updated Title' } });
    renderForm(existing);
    fireEvent.submit(screen.getByRole('button', { name: /save/i }).closest('form'));
    await waitFor(() => {
      expect(axios.put).toHaveBeenCalledWith(
        expect.stringContaining('/tasks/5'),
        expect.any(Object),
        expect.any(Object)
      );
    });
  });

  test('cancel button calls onClose', () => {
    const { onClose } = renderForm();
    fireEvent.click(screen.getByRole('button', { name: /cancel/i }));
    expect(onClose).toHaveBeenCalled();
  });

  test('fetches milestones when initiativeId is set', async () => {
    axios.get.mockResolvedValueOnce({ data: { content: [{ id: 1, title: 'M1' }] } });
    renderForm();
    const inputs = screen.getAllByRole('spinbutton');
    fireEvent.change(inputs[0], { target: { value: '1' } });
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        expect.stringContaining('/milestones'),
        expect.any(Object)
      );
    });
  });
});
