import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { addTask, updateTask } from '../../store/slices/projectTaskSlice';
import { createTask, updateTask as apiUpdate } from '../../services/projectTaskService';
import { getMilestones } from '../../services/projectMilestoneService';
import { getInitiatives } from '../../services/projectInitiativeService';

export default function ProjectTaskForm({ existing, onClose, isOpen }) {
  const dispatch = useDispatch();
  const user = useSelector((s) => s.auth?.user ?? null);
  const [form, setForm] = useState({
    taskCode: '', initiativeId: '', milestoneId: '', title: '', description: '',
    priority: 'MEDIUM', estimatedHours: '', dueDate: '', assigneeId: '', status: 'PENDING',
    ...existing,
  });
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [milestones, setMilestones] = useState([]);
  const [initiatives, setInitiatives] = useState([]);
  const [validationError, setValidationError] = useState('');

  useEffect(() => {
    getInitiatives({ page: 0, size: 50 })
      .then((r) => setInitiatives(r.data?.content || r.content || []))
      .catch(() => {});
  }, []);

  useEffect(() => {
    if (form.initiativeId) {
      getMilestones({ initiativeId: form.initiativeId, page: 0, size: 50 })
        .then((r) => setMilestones(r.data?.content || r.content || []))
        .catch(() => {});
    } else {
      setMilestones([]);
    }
  }, [form.initiativeId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setValidationError('');
    setSuccessMessage('');

    if (!form.title || !form.title.trim()) {
      setValidationError('Actionable title label is mandatory');
      return;
    }

    try {
      const payload = { ...form, initiativeId: form.initiativeId || null, milestoneId: form.milestoneId || null };
      if (existing) {
        const res = await apiUpdate(existing.id, payload);
        dispatch(updateTask(res.data));
        setSuccessMessage('Task updated successfully.');
      } else {
        const res = await createTask(payload);
        dispatch(addTask(res.data));
        setSuccessMessage('New workload task entry added to pipeline successfully.');
      }
      setTimeout(() => {
        setSuccessMessage('');
        onClose && onClose();
      }, 1500);
    } catch (err) {
      const msg = err?.response?.data?.message || err?.message || 'Save failed';
      setError(msg);
    }
  };

  if (isOpen === false) return null;

  return (
    <div style={overlay}>
      <div style={modal}>
        <h3 style={{ marginTop: 0 }}>{existing ? 'Edit' : 'New'} Task</h3>
        {successMessage && <div style={{ color: 'green', marginBottom: 8 }}>{successMessage}</div>}
        {error && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}
        {validationError && <div style={{ color: 'red', marginBottom: 8 }}>{validationError}</div>}
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Sequential task tag string must be specified.</label>
            <input
              aria-label="Task code"
              placeholder="TASK-CODE"
              value={form.taskCode || ''}
              onChange={(e) => setForm({ ...form, taskCode: e.target.value })}
              style={inputStyle}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Parent Initiative</label>
            <select
              aria-label="Initiative"
              value={form.initiativeId || ''}
              onChange={(e) => setForm({ ...form, initiativeId: e.target.value, milestoneId: '' })}
              style={inputStyle}
            >
              <option value="">-- Select Initiative --</option>
              {initiatives.map((i) => (
                <option key={i.id} value={i.id}>[{i.projectCode}] {i.title}</option>
              ))}
            </select>
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Milestone (optional)</label>
            <select
              aria-label="Milestone"
              value={form.milestoneId || ''}
              onChange={(e) => setForm({ ...form, milestoneId: e.target.value })}
              style={inputStyle}
            >
              <option value="">-- None --</option>
              {milestones.map((m) => <option key={m.id} value={m.id}>{m.title}</option>)}
            </select>
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Actionable task scope title is required.</label>
            <input
              aria-label="Title"
              placeholder="Provision Backend JWT"
              value={form.title || ''}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              style={inputStyle}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Description</label>
            <input
              value={form.description || ''}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              style={inputStyle}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Priority</label>
            <select
              aria-label="Priority"
              value={form.priority || 'MEDIUM'}
              onChange={(e) => setForm({ ...form, priority: e.target.value })}
              style={inputStyle}
            >
              {['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map((p) => <option key={p} value={p}>{p}</option>)}
            </select>
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Estimated workload capacity must be a positive count.</label>
            <input
              type="number"
              value={form.estimatedHours || ''}
              onChange={(e) => setForm({ ...form, estimatedHours: e.target.value })}
              style={inputStyle}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Due Date</label>
            <input
              type="date"
              value={form.dueDate || ''}
              onChange={(e) => setForm({ ...form, dueDate: e.target.value })}
              style={inputStyle}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Assignee ID (optional)</label>
            <input
              type="number"
              value={form.assigneeId || ''}
              onChange={(e) => setForm({ ...form, assigneeId: e.target.value })}
              style={inputStyle}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label style={{ display: 'block', fontSize: 13, marginBottom: 4 }}>Status</label>
            <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })} style={inputStyle}>
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

const inputStyle = { width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4, boxSizing: 'border-box' };
const overlay = { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 };
const modal = { background: '#fff', borderRadius: 8, padding: 28, width: 500, maxHeight: '90vh', overflowY: 'auto' };
