import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { createInitiative, updateInitiative } from '../../store/slices/projectInitiativeSlice';

export default function ProjectInitiativeForm({ existing, onClose }) {
  const dispatch = useDispatch();
  const user = useSelector((s) => s.auth.user);
  const [form, setForm] = useState({
    projectCode: '', title: '', description: '', budgetAllocated: '', startDate: '', targetEndDate: '', status: 'ACTIVE', directorId: user?.id || '',
    ...existing,
  });
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (existing) {
        await dispatch(updateInitiative({ id: existing.id, data: form })).unwrap();
      } else {
        await dispatch(createInitiative(form)).unwrap();
      }
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || 'Save failed');
    }
  };

  const f = (label, key, type = 'text') => (
    <div style={{ marginBottom: 12 }}>
      <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>{label}</label>
      <input type={type} value={form[key] || ''} onChange={(e) => setForm({ ...form, [key]: e.target.value })} style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4, boxSizing: 'border-box' }} />
    </div>
  );

  return (
    <div style={overlay}>
      <div style={modal}>
        <h3 style={{ marginTop: 0 }}>{existing ? 'Edit' : 'New'} Initiative</h3>
        {error && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}
        <form onSubmit={handleSubmit}>
          {f('Project Code', 'projectCode')}
          {f('Title', 'title')}
          {f('Description', 'description')}
          {f('Budget Allocated', 'budgetAllocated', 'number')}
          {f('Start Date', 'startDate', 'date')}
          {f('Target End Date', 'targetEndDate', 'date')}
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Status</label>
            <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })} style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4 }}>
              {['ACTIVE', 'ON_HOLD', 'COMPLETED'].map((s) => <option key={s}>{s}</option>)}
            </select>
          </div>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <button type="button" onClick={onClose} style={{ padding: '8px 16px', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer' }}>Cancel</button>
            <button type="submit" style={{ padding: '8px 16px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>Save</button>
          </div>
        </form>
      </div>
    </div>
  );
}

const overlay = { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 };
const modal = { background: '#fff', borderRadius: 8, padding: 28, width: 480, maxHeight: '90vh', overflowY: 'auto' };
