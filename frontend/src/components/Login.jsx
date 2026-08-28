import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { loginUser } from '../store/slices/authSlice';
import authService from '../services/authService';

export default function Login() {
  const dispatch = useDispatch();
  const { loading, error, accessRevoked } = useSelector((s) => s.auth);
  const [isRegistering, setIsRegistering] = useState(false);
  const [formData, setFormData] = useState({ email: '', password: '', fullName: '', domainRole: 'TEAM_CONTRIBUTOR' });
  const [validationErrors, setValidationErrors] = useState({});
  const [successMessage, setSuccessMessage] = useState('');

  const validate = () => {
    const errs = {};
    if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) errs.email = 'Valid email is required';
    if (!formData.password || formData.password.length < 6) errs.password = 'Password must be at least 6 characters';
    if (isRegistering) {
      if (!formData.fullName) errs.fullName = 'Full name is required';
      if (!formData.domainRole) errs.domainRole = 'Domain role is required';
    }
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setValidationErrors(errs); return; }
    setValidationErrors({});
    if (isRegistering) {
      try {
        await authService.register({ email: formData.email, password: formData.password, fullName: formData.fullName, domainRole: formData.domainRole });
        setSuccessMessage('Account registered. Please log in.');
        setIsRegistering(false);
      } catch (err) {
        setValidationErrors({ general: err.response?.data?.message || 'Registration failed' });
      }
    } else {
      dispatch(loginUser({ email: formData.email, password: formData.password }));
    }
  };

  const set = (field) => (e) => setFormData({ ...formData, [field]: e.target.value });

  return (
    <div style={{ maxWidth: 420, margin: '80px auto', padding: 28, border: '1px solid #ddd', borderRadius: 8 }}>
      <h2>{isRegistering ? 'Provision Access Account' : 'TaskGuard Security Gateway'}</h2>
      {successMessage && <div style={{ color: 'green', marginBottom: 8 }}>{successMessage}</div>}
      {accessRevoked && (
        <div style={{ background: '#ffebee', border: '1px solid #ef9a9a', color: '#c62828', padding: '10px 14px', borderRadius: 4, marginBottom: 12 }}>
          Your account has been suspended. Access has been revoked.
        </div>
      )}
      {error && !accessRevoked && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}
      {validationErrors.general && <div style={{ color: 'red', marginBottom: 8 }}>{validationErrors.general}</div>}
      <form onSubmit={handleSubmit}>
        <div>
          <input
            placeholder="user@enterprise.domain"
            value={formData.email}
            onChange={set('email')}
            style={{ width: '100%', marginBottom: 4, padding: 8, boxSizing: 'border-box' }}
          />
          {validationErrors.email && <span className="field-error-text" style={{ color: 'red', fontSize: 12 }}>{validationErrors.email}</span>}
        </div>
        <div style={{ marginTop: 8 }}>
          <input
            type="password"
            placeholder="........"
            value={formData.password}
            onChange={set('password')}
            style={{ width: '100%', marginBottom: 4, padding: 8, boxSizing: 'border-box' }}
          />
          {validationErrors.password && <span className="field-error-text" style={{ color: 'red', fontSize: 12 }}>{validationErrors.password}</span>}
        </div>
        {isRegistering && (
          <>
            <div style={{ marginTop: 8 }}>
              <input
                placeholder="Full Name"
                value={formData.fullName}
                onChange={set('fullName')}
                style={{ width: '100%', marginBottom: 4, padding: 8, boxSizing: 'border-box' }}
              />
              {validationErrors.fullName && <span className="field-error-text" style={{ color: 'red', fontSize: 12 }}>{validationErrors.fullName}</span>}
            </div>
            <div style={{ marginTop: 8 }}>
              <select value={formData.domainRole} onChange={set('domainRole')} style={{ width: '100%', padding: 8, boxSizing: 'border-box' }}>
                <option value="TEAM_CONTRIBUTOR">TEAM_CONTRIBUTOR</option>
                <option value="PROJECT_MANAGER">PROJECT_MANAGER</option>
                <option value="PROJECT_DIRECTOR">PROJECT_DIRECTOR</option>
              </select>
              {validationErrors.domainRole && <span className="field-error-text" style={{ color: 'red', fontSize: 12 }}>{validationErrors.domainRole}</span>}
            </div>
          </>
        )}
        <button
          type="submit"
          disabled={loading}
          style={{ width: '100%', padding: 10, marginTop: 16, background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}
        >
          {loading ? 'Please wait...' : isRegistering ? 'Register Credentials' : 'Authorize Session'}
        </button>
      </form>
      <button
        onClick={() => { setIsRegistering(!isRegistering); setValidationErrors({}); setSuccessMessage(''); }}
        style={{ marginTop: 12, background: 'none', border: 'none', color: '#1976d2', cursor: 'pointer' }}
      >
        {isRegistering ? 'Back to Login' : 'Create an account'}
      </button>
    </div>
  );
}
