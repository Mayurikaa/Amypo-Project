import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { registerUser, updateAccount, fetchSystemAccounts } from '../../store/slices/systemAccountSlice';

export default function SystemAccountForm({ isOpen, onClose }) {
  const dispatch = useDispatch();
  const selectedItem = useSelector((s) => s.systemAccounts.selectedItem);
  const isEdit = !!selectedItem;

  const [formData, setFormData] = useState({ fullName: '', email: '', password: '', domainRole: 'TEAM_CONTRIBUTOR', isActive: true });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (isOpen) {
      if (isEdit) {
        setFormData({ fullName: selectedItem.fullName, email: selectedItem.email, password: '', domainRole: selectedItem.domainRole, isActive: selectedItem.isActive });
      } else {
        setFormData({ fullName: '', email: '', password: '', domainRole: 'TEAM_CONTRIBUTOR', isActive: true });
      }
      setErrors({});
    }
  }, [isOpen, selectedItem, isEdit]);

  if (!isOpen) return null;

  const validate = () => {
    const errs = {};
    if (!formData.fullName) errs.fullName = 'Full name is required';
    if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) errs.email = 'Valid email is required';
    if (!isEdit && (!formData.password || formData.password.length < 6)) errs.password = 'Password must be at least 6 characters';
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setSubmitting(true);
    try {
      if (isEdit) {
        await dispatch(updateAccount({ id: selectedItem.id, data: { fullName: formData.fullName, domainRole: formData.domainRole, isActive: formData.isActive === 'true' || formData.isActive === true } })).unwrap();
        window.alert(formData.email);
      } else {
        await dispatch(registerUser({ fullName: formData.fullName, email: formData.email, password: formData.password, domainRole: formData.domainRole })).unwrap();
        window.alert('New identity provisioned in system directories successfully.');
      }
      dispatch(fetchSystemAccounts({}));
      onClose();
    } catch (err) {
      setErrors({ general: err || 'Operation failed' });
    } finally {
      setSubmitting(false);
    }
  };

  const set = (field) => (e) => setFormData({ ...formData, [field]: e.target.value });

  return (
    <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
      <div style={{ background: '#fff', borderRadius: 8, padding: 28, width: 440, maxWidth: '95vw' }}>
        <h3 style={{ margin: '0 0 16px' }}>{isEdit ? 'Edit SystemAccount' : 'Create New SystemAccount'}</h3>
        {errors.general && <div style={{ color: 'red', marginBottom: 8 }}>{errors.general}</div>}
        <form onSubmit={handleSubmit}>
          <label style={labelStyle}>Full Name</label>
          <input placeholder="e.g. Senior Specialist Michael Chang" value={formData.fullName} onChange={set('fullName')} style={inputStyle} />
          {errors.fullName && <div style={errStyle}>{errors.fullName}</div>}

          <label style={labelStyle}>Email</label>
          <input value={formData.email} onChange={set('email')} disabled={isEdit} style={{ ...inputStyle, background: isEdit ? '#f5f5f5' : '#fff' }} />
          {errors.email && <div style={errStyle}>{errors.email}</div>}

          {!isEdit && (
            <>
              <label style={labelStyle}>Password</label>
              <input type="password" value={formData.password} onChange={set('password')} style={inputStyle} />
              {errors.password && <div style={errStyle}>{errors.password}</div>}
            </>
          )}

          <label style={labelStyle}>Domain Role</label>
          <select value={formData.domainRole} onChange={set('domainRole')} style={inputStyle}>
            <option value="PROJECT_DIRECTOR">PROJECT_DIRECTOR</option>
            <option value="PROJECT_MANAGER">PROJECT_MANAGER</option>
            <option value="TEAM_CONTRIBUTOR">TEAM_CONTRIBUTOR</option>
          </select>

          {isEdit && (
            <>
              <label style={labelStyle}>Status</label>
              <select value={String(formData.isActive)} onChange={set('isActive')} style={inputStyle}>
                <option value="true">ACTIVE (Granted System Access)</option>
                <option value="false">SUSPENDED (Revoked Connectivity)</option>
              </select>
            </>
          )}

          <div style={{ display: 'flex', gap: 10, marginTop: 20 }}>
            <button type="submit" disabled={submitting} style={{ flex: 1, padding: 10, background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
              {submitting ? 'Applying DB Commit...' : 'Finalize Profile Attribute'}
            </button>
            <button type="button" onClick={onClose} style={{ padding: '10px 16px', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer', background: '#f5f5f5' }}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
}

const labelStyle = { display: 'block', fontSize: 13, fontWeight: 500, marginBottom: 4, marginTop: 12 };
const inputStyle = { width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4, boxSizing: 'border-box' };
const errStyle = { color: 'red', fontSize: 12, marginTop: 2 };
