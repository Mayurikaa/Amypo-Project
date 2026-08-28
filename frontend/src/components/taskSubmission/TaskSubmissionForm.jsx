import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { addSubmission } from '../../store/slices/taskSubmissionSlice';
import { submitWork } from '../../services/taskSubmissionService';

export default function TaskSubmissionForm({ onClose }) {
  const dispatch = useDispatch();
  const user = useSelector((s) => s.auth.user);
  const [form, setForm] = useState({
    taskId: '',
    contributorId: user?.id || '',
    hoursSpent: '',
    submissionNotes: '',
    completionStatus: 'PENDING_REVIEW',
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    try {
      const res = await submitWork(form);
      dispatch(addSubmission(res.data));
      setSuccess('Work submission recorded successfully.');
      setTimeout(() => onClose(), 1500);
    } catch (err) {
      setError(err.response?.data?.message || 'Submission failed');
    }
  };

  const f = (label, key, type = 'text') => (
    <div style={{ marginBottom: 12 }}>
      <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>{label}</label>
      <input
        type={type}
        value={form[key] || ''}
        onChange={(e) => setForm({ ...form, [key]: e.target.value })}
        style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4, boxSizing: 'border-box' }}
      />
    </div>
  );

  return (
    <div style={overlay}>
      <div style={modal}>
        <h3 style={{ marginTop: 0 }}>Submit Work</h3>
        {error && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}
        {success && <div style={{ color: 'green', marginBottom: 8 }}>{success}</div>}
        <form onSubmit={handleSubmit}>
          {f('Task ID', 'taskId', 'number')}
          {f('Contributor ID', 'contributorId', 'number')}
          {f('Hours Spent', 'hoursSpent', 'number')}
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Submission Notes</label>
            <textarea
              value={form.submissionNotes}
              onChange={(e) => setForm({ ...form, submissionNotes: e.target.value })}
              style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4, boxSizing: 'border-box', minHeight: 80 }}
            />
          </div>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <button type="button" onClick={onClose} style={{ padding: '8px 16px', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer' }}>Cancel</button>
            <button type="submit" style={{ padding: '8px 16px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>Submit</button>
          </div>
        </form>
      </div>
    </div>
  );
}

const overlay = { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 };
const modal = { background: '#fff', borderRadius: 8, padding: 28, width: 460, maxHeight: '90vh', overflowY: 'auto' };
