import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import ProjectTaskForm from './ProjectTaskForm';
import projectTaskReducer from '../../store/slices/projectTaskSlice';
import authReducer from '../../store/slices/authSlice';
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
      <ProjectTaskForm existing={existing} onClose={onClose} isOpen={true} />
    </Provider>
  );
  return { ...utils, onClose };
};

describe('ProjectTaskForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useRealTimers();
    axios.get.mockResolvedValue({ data: { content: [] } });
    axios.post.mockResolvedValue({ data: {} });
    axios.put.mockResolvedValue({ data: {} });
  });

  test('T08 - title state update', async () => {
    renderForm();
    const titleInput = screen.getByPlaceholderText('Provision Backend JWT');
    fireEvent.change(titleInput, { target: { value: 'Updated Task' } });
    expect(titleInput).toHaveValue('Updated Task');
  });

  test('on successful task creation shows success alert with exact message', async () => {
    axios.post.mockResolvedValueOnce({
      data: { id: 1, taskCode: 'TASK-NEW', title: 'New Task', status: 'PENDING' },
    });
    const { onClose } = renderForm();
    fireEvent.change(screen.getByPlaceholderText('Provision Backend JWT'), { target: { value: 'New Task' } });
    fireEvent.click(screen.getByRole('button', { name: /Save/ }));
    await waitFor(() => {
      expect(screen.getByText('New workload task entry added to pipeline successfully.')).toBeInTheDocument();
    });
  });

  test('T15 - create success alert', async () => {
    axios.post.mockResolvedValueOnce({
      data: { id: 1, taskCode: 'TASK-NEW', title: 'New Task', status: 'PENDING' },
    });
    renderForm();
    fireEvent.change(screen.getByPlaceholderText('Provision Backend JWT'), { target: { value: 'New Task' } });
    fireEvent.click(screen.getByRole('button', { name: /Save/ }));
    await waitFor(() => {
      expect(screen.getByText('New workload task entry added to pipeline successfully.')).toBeInTheDocument();
    });
  });

  test('T16 - update success alert', async () => {
    const existing = {
      id: 5, taskCode: 'TASK-005', initiativeId: 1, title: 'Old Title',
      priority: 'LOW', estimatedHours: 4, dueDate: '2025-12-31', status: 'PENDING',
    };
    axios.put.mockResolvedValueOnce({ data: { ...existing, title: 'Updated Title' } });
    renderForm(existing);
    fireEvent.click(screen.getByRole('button', { name: /Save/ }));
    await waitFor(() => {
      expect(screen.getByText('Task updated successfully.')).toBeInTheDocument();
    });
  });

  test('T18 - dropdown data population', async () => {
    axios.get
      .mockResolvedValueOnce({ data: { content: [{ id: 1, projectCode: 'P1', title: 'Initiative 1' }] } });
    renderForm();
    await waitFor(() => expect(screen.getByRole('option', { name: /Initiative 1/ })).toBeInTheDocument());
  });

  test('T19 - 500 server error handling', async () => {
    axios.post.mockRejectedValueOnce({
      response: { data: { message: 'Task Code parameter matching collision: Code reference already maps to active pipeline entry.' } },
    });
    renderForm();
    fireEvent.change(screen.getByPlaceholderText('Provision Backend JWT'), { target: { value: 'New Task' } });
    fireEvent.click(screen.getByRole('button', { name: /Save/ }));
    await waitFor(() => {
      expect(screen.getByText(/Task Code parameter matching collision/)).toBeInTheDocument();
    });
  });

  test('T21 - required field validation', async () => {
    renderForm();
    fireEvent.click(screen.getByRole('button', { name: /Save/ }));
    await waitFor(() => {
      expect(screen.getByText('Actionable title label is mandatory')).toBeInTheDocument();
    });
  });

  test('T22 - Success Alert: auto-dismissal timers', async () => {
    jest.useFakeTimers();
    axios.post.mockResolvedValueOnce({
      data: { id: 1, taskCode: 'TASK-NEW', title: 'New Task', status: 'PENDING' },
    });
    const { onClose } = renderForm();
    fireEvent.change(screen.getByPlaceholderText('Provision Backend JWT'), { target: { value: 'New Task' } });
    fireEvent.click(screen.getByRole('button', { name: /Save/ }));
    await waitFor(() => {
      expect(screen.getByText('New workload task entry added to pipeline successfully.')).toBeInTheDocument();
    });
    act(() => {
      jest.advanceTimersByTime(1500);
    });
    expect(onClose).toHaveBeenCalled();
  });

  test('T23 - 409 conflict handling', async () => {
    axios.post.mockRejectedValueOnce({
      response: { status: 409, data: { message: 'Task already exists' } },
    });
    renderForm();
    fireEvent.change(screen.getByPlaceholderText('Provision Backend JWT'), { target: { value: 'New Task' } });
    fireEvent.click(screen.getByRole('button', { name: /Save/ }));
    await waitFor(() => {
      expect(screen.getByText('Task already exists')).toBeInTheDocument();
    });
  });

  test('T30 - milestone population check', async () => {
    axios.get
      .mockResolvedValueOnce({ data: { content: [{ id: 1, projectCode: 'P1', title: 'Initiative 1' }] } })
      .mockResolvedValueOnce({ data: { content: [{ id: 1, title: 'Milestone 1' }] } });
    renderForm();
    await waitFor(() => expect(screen.getByRole('option', { name: /Initiative 1/ })).toBeInTheDocument());
    const selects = screen.getAllByRole('combobox');
    fireEvent.change(selects[0], { target: { value: '1' } });
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalledWith(
        expect.stringContaining('/milestones'),
        expect.any(Object)
      );
    });
  });
});
