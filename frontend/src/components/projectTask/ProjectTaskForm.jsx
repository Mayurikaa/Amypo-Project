import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { createTask, updateTask } from '../../store/slices/projectTaskSlice';
import projectMilestoneService from '../../services/projectMilestoneService';

export default function ProjectTaskForm({ existing, onClose }) {
  const dispatch = useDispatch();
  const user = useSelector((s) => s.auth.user);
  const [form, setForm] = useState({
    taskCode: '', initiativeId: '', milestoneId: '', title: '', description: '',
    priority: 'MEDIUM', estimatedHours: '', dueDate: '', assigneeId: '', status: 'PENDING',
    ...existing,
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [milestones, setMilestones] = useState([]);

  useEffect(() => {
    if (form.initiativeId) {
      projectMilestoneService.getAll(form.initiativeId, undefined, 0, 50)
        .then((data) => setMilestones(data?.content || []));
    }
  }, [form.initiativeId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    try {
      if (existing) {
        await dispatch(updateTask({ id: existing.id, data: form })).unwrap();
        setSuccess('Task updated successfully.');
      } else {
        await dispatch(createTask(form)).unwrap();
        setSuccess('New workload task entry added to pipeline successfully.');
      }
      setTimeout(() => onClose(), 1500);
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
        <h3 style={{ marginTop: 0 }}>{existing ? 'Edit' : 'New'} Task</h3>
        {error && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}
        {success && <div style={{ color: 'green', marginBottom: 8 }}>{success}</div>}
        <form onSubmit={handleSubmit}>
          {f('Sequential task tag string must be specified.', 'taskCode')}
          {f('Parent Initiative ID', 'initiativeId', 'number')}
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Milestone (optional)</label>
            <select value={form.milestoneId || ''} onChange={(e) => setForm({ ...form, milestoneId: e.target.value })} style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4 }}>
              <option value="">-- None --</option>
              {milestones.map((m) => <option key={m.id} value={m.id}>{m.title}</option>)}
            </select>
          </div>
          {f('Actionable task scope title is required.', 'title')}
          {f('Description', 'description')}
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Priority</label>
            <select value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })} style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4 }}>
              {['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((p) => <option key={p}>{p}</option>)}
            </select>
          </div>
          {f('Estimated workload capacity must be a positive count.', 'estimatedHours', 'number')}
          {f('Due Date', 'dueDate', 'date')}
          {f('Assignee ID (optional)', 'assigneeId', 'number')}
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Status</label>
            <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })} style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4 }}>
              {['PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED'].map((s) => <option key={s}>{s}</option>)}
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
const modal = { background: '#fff', borderRadius: 8, padding: 28, width: 500, maxHeight: '90vh', overflowY: 'auto' };
