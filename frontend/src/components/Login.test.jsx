import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import Login from '../components/Login';
import authReducer from '../store/slices/authSlice';
import axios from 'axios';

const makeStore = () =>
  configureStore({ reducer: { auth: authReducer } });

const renderLogin = () =>
  render(
    <Provider store={makeStore()}>
      <Login />
    </Provider>
  );

describe('Login Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('renders login form by default', () => {
    renderLogin();
    expect(screen.getByText(/Login - TaskGuard/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Email')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Password')).toBeInTheDocument();
  });

  test('shows validation errors when submitting empty form', async () => {
    renderLogin();
    fireEvent.click(screen.getByRole('button', { name: /login/i }));
    await waitFor(() => {
      expect(screen.getByText('Email is required')).toBeInTheDocument();
      expect(screen.getByText('Password is required')).toBeInTheDocument();
    });
  });

  test('toggles to registration form', () => {
    renderLogin();
    fireEvent.click(screen.getByText('Create an account'));
    expect(screen.getByText(/Register - TaskGuard/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Full Name')).toBeInTheDocument();
  });

  test('shows full name validation error on register with empty name', async () => {
    renderLogin();
    fireEvent.click(screen.getByText('Create an account'));
    fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'test@example.com' } });
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'pass123' } });
    fireEvent.click(screen.getByRole('button', { name: /register/i }));
    await waitFor(() => {
      expect(screen.getByText('Full name is required')).toBeInTheDocument();
    });
  });

  test('calls login API on valid login submit', async () => {
    axios.post.mockResolvedValueOnce({
      data: { token: 'jwt-token', id: 1, email: 'test@example.com', fullName: 'Test', domainRole: 'PROJECT_DIRECTOR' },
    });
    renderLogin();
    fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'test@example.com' } });
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'password' } });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));
    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        expect.stringContaining('/auth/login'),
        expect.objectContaining({ email: 'test@example.com' })
      );
    });
  });

  test('shows error message on login failure', async () => {
    axios.post.mockRejectedValueOnce({ response: { data: { message: 'Invalid credentials.' } } });
    renderLogin();
    fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'bad@example.com' } });
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'wrong' } });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));
    await waitFor(() => {
      expect(screen.getByText('Invalid credentials.')).toBeInTheDocument();
    });
  });

  test('calls register API and shows success message', async () => {
    axios.post.mockResolvedValueOnce({ data: { id: 2, email: 'new@example.com' } });
    renderLogin();
    fireEvent.click(screen.getByText('Create an account'));
    fireEvent.change(screen.getByPlaceholderText('Email'), { target: { value: 'new@example.com' } });
    fireEvent.change(screen.getByPlaceholderText('Password'), { target: { value: 'pass123' } });
    fireEvent.change(screen.getByPlaceholderText('Full Name'), { target: { value: 'New User' } });
    fireEvent.click(screen.getByRole('button', { name: /register/i }));
    await waitFor(() => {
      expect(axios.post).toHaveBeenCalledWith(
        expect.stringContaining('/auth/register'),
        expect.objectContaining({ email: 'new@example.com' })
      );
    });
    await waitFor(() => {
      expect(screen.getByText('Account registered. Please log in.')).toBeInTheDocument();
    });
  });

  test('back to login button switches back from register', () => {
    renderLogin();
    fireEvent.click(screen.getByText('Create an account'));
    expect(screen.getByText(/Register - TaskGuard/i)).toBeInTheDocument();
    fireEvent.click(screen.getByText('Back to Login'));
    expect(screen.getByText(/Login - TaskGuard/i)).toBeInTheDocument();
  });
});
