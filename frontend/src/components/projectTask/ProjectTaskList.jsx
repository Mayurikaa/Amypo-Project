import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setTasks, removeTask, updateTask } from '../../store/slices/projectTaskSlice';
import { logout } from '../../store/slices/authSlice';
import projectTaskService from '../../services/projectTaskService';
import SearchFilterBar from '../common/SearchFilterBar';
import EmptyState from '../common/EmptyState';

export default function ProjectTaskList({ onEdit, onNew }) {
  const dispatch = useDispatch();
  const tasks = useSelector((s) => s.projectTask?.tasks ?? []);
  const user = useSelector((s) => s.auth?.user ?? null);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('ALL');
  const [notice, setNotice] = useState(null);

  const getTaskData = projectTaskService.getTasks || ((params) => projectTaskService.getAll(undefined, params.status, params.query, params.page, params.size));
  const removeTaskData = projectTaskService.deleteTask || projectTaskService.remove;
  const changeTaskStatus = projectTaskService.updateTaskStatus || ((id, nextStatus) => projectTaskService.update(id, { status: nextStatus }));

  const fetchData = async () => {
    try {
      const response = await getTaskData({ query, status, page: 0, size: 20 });
      dispatch(setTasks(response?.data ?? response));
    } catch (err) {
      if (err?.response?.status === 401) {
        localStorage.removeItem('taskguard_token');
        localStorage.removeItem('taskguard_user');
        localStorage.removeItem('taskguard_role');
        dispatch(logout());
        return;
      }
      setNotice({ type: 'error', message: err?.response?.data?.message || 'Unable to load tasks.' });
    }
  };

  useEffect(() => { fetchData(); }, [query, status]);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this task?')) return;
    try {
      await removeTaskData(id);
      dispatch(removeTask(id));
      setNotice({ type: 'success', message: 'Task deleted successfully.' });
      setTimeout(() => setNotice(null), 2200);
    } catch (err) {
      setNotice({ type: 'error', message: err?.response?.data?.message || 'Delete failed.' });
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      const res = await changeTaskStatus(id, newStatus);
      dispatch(updateTask(res.data));
      setNotice({ type: 'success', message: 'Task updated successfully.' });
      setTimeout(() => setNotice(null), 2200);
    } catch (err) {
      setNotice({ type: 'error', message: err?.response?.data?.message || 'Status update failed.' });
    }
  };

  const canManage = ['PROJECT_DIRECTOR', 'PROJECT_MANAGER'].includes(user?.domainRole);

  return (
    <div>
      {notice && (
        <div style={{ marginBottom: 12, padding: '8px 10px', borderRadius: 4, background: notice.type === 'success' ? '#e8f5e9' : '#fdecea', color: notice.type === 'success' ? '#1b5e20' : '#b71c1c' }}>
          {notice.message}
        </div>
      )}
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3 style={{ margin: 0 }}>Project Tasks</h3>
        {canManage && <button onClick={onNew} style={btn('#1976d2')}>+ New Task</button>}
      </div>
      <SearchFilterBar query={query} status={status} onQueryChange={setQuery} onStatusChange={setStatus} statusOptions={['ALL', 'PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED']} />
      {tasks.length === 0 ? <EmptyState /> : (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead><tr style={{ background: '#f5f5f5' }}>{['Code', 'Title', 'Priority', 'Status', 'Est.Hrs', 'Due', 'Actions'].map((h) => <th key={h} style={th}>{h}</th>)}</tr></thead>
          <tbody>
            {tasks.map((t) => (
              <tr key={t.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={td}>{t.taskCode}</td>
                <td style={td}>{t.title}</td>
                <td style={td}>{t.priority}</td>
                <td style={td}>
                  <select value={t.status} onChange={(e) => handleStatusChange(t.id, e.target.value)} style={{ fontSize: 12, padding: '2px 6px', border: '1px solid #ccc', borderRadius: 4 }}>
                    {['PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED'].map((s) => <option key={s} value={s}>{s}</option>)}
                  </select>
                </td>
                <td style={td}>{t.estimatedHours}</td>
                <td style={td}>{t.dueDate}</td>
                <td style={td}>
                  {canManage && (
                    <>
                      <button onClick={() => onEdit?.(t)} style={btn('#388e3c')}>Edit</button>
                      <button onClick={() => handleDelete(t.id)} style={{ ...btn('#d32f2f'), marginLeft: 6 }}>Delete</button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

const th = { padding: '10px 12px', textAlign: 'left', fontWeight: 600, fontSize: 13 };
const td = { padding: '10px 12px', fontSize: 13 };
const btn = (bg) => ({ background: bg, color: '#fff', border: 'none', padding: '5px 12px', borderRadius: 4, cursor: 'pointer', fontSize: 12 });
