import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { loginStart, loginSuccess, loginFailure } from '../store/slices/authSlice';
import { login, register } from '../services/authService';

export default function Login() {
  const dispatch = useDispatch();
  const { loading, error } = useSelector((s) => s.auth);
  const [isRegistering, setIsRegistering] = useState(false);
  const [formData, setFormData] = useState({ email: '', password: '', fullName: '', domainRole: 'TEAM_CONTRIBUTOR' });
  const [validationErrors, setValidationErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState('');

  const validate = () => {
    const errs = {};
    if (!formData.email) errs.email = 'Email is required';
    if (!formData.password) errs.password = 'Password is required';
    if (isRegistering && !formData.fullName) errs.fullName = 'Full name is required';
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setValidationErrors(errs); return; }
    setValidationErrors({});
    dispatch(loginStart());
    try {
      if (isRegistering) {
        await register({ email: formData.email, password: formData.password, fullName: formData.fullName, domainRole: formData.domainRole });
        setSuccessMessage('Account registered. Please log in.');
        setIsRegistering(false);
        dispatch(loginFailure(null));
      } else {
        const data = await login({ email: formData.email, password: formData.password });
        localStorage.setItem('taskguard_token', data.token);
        localStorage.setItem('taskguard_user', JSON.stringify(data));
        dispatch(loginSuccess(data));
      }
    } catch (err) {
      dispatch(loginFailure(err.response?.data?.message || 'Authentication failed'));
    }
  };

  return (
    <div style={{ maxWidth: 420, margin: '80px auto', padding: 28, border: '1px solid #ddd', borderRadius: 8 }}>
      <h2>Login - TaskGuard</h2>
      {successMessage && <div style={{ color: 'green', marginBottom: 8 }}>{successMessage}</div>}
      {error && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}
      <form onSubmit={handleSubmit}>
        <div>
          <input
            placeholder="user@enterprise.domain"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            style={{ width: '100%', marginBottom: 4, padding: 8, boxSizing: 'border-box' }}
          />
          {validationErrors.email && <div style={{ color: 'red', fontSize: 12 }}>{validationErrors.email}</div>}
        </div>
        <div style={{ marginTop: 8 }}>
          <input
            type="password"
            placeholder="••••••••••••"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            style={{ width: '100%', marginBottom: 4, padding: 8, boxSizing: 'border-box' }}
          />
          {validationErrors.password && <div style={{ color: 'red', fontSize: 12 }}>{validationErrors.password}</div>}
        </div>
        {isRegistering && (
          <>
            <div style={{ marginTop: 8 }}>
              <input
                placeholder="Full Name"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                style={{ width: '100%', marginBottom: 4, padding: 8, boxSizing: 'border-box' }}
              />
              {validationErrors.fullName && <div style={{ color: 'red', fontSize: 12 }}>{validationErrors.fullName}</div>}
            </div>
            <select
              value={formData.domainRole}
              onChange={(e) => setFormData({ ...formData, domainRole: e.target.value })}
              style={{ width: '100%', marginTop: 8, marginBottom: 4, padding: 8 }}
            >
              <option value="TEAM_CONTRIBUTOR">Team Contributor</option>
              <option value="PROJECT_MANAGER">Project Manager</option>
              <option value="PROJECT_DIRECTOR">Project Director</option>
            </select>
          </>
        )}
        <button
          type="submit"
          disabled={loading}
          style={{ width: '100%', padding: 10, marginTop: 16, background: '#1976d2', color: '#fff', borderRadius: 4, cursor: 'pointer' }}
        >
          {loading ? 'Please wait...' : isRegistering ? 'Register Account' : 'Authorize Session'}
        </button>
      </form>
      <button
        onClick={() => { setIsRegistering(!isRegistering); setValidationErrors({}); setSuccessMessage(''); }}
        style={{ marginTop: 12, background: 'none', color: '#1976d2', cursor: 'pointer' }}
      >
        {isRegistering ? 'Back to Login' : 'Create an account'}
      </button>
    </div>
  );
}
