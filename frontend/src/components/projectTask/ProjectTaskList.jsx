import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setTasks, removeTask, updateTask } from '../../store/slices/projectTaskSlice';
import { getTasks, deleteTask, updateTaskStatus } from '../../services/projectTaskService';

export default function ProjectTaskList({ onEdit, onNew, isAdmin, onOpenForm }) {
  const dispatch = useDispatch();
  const projectTaskState = useSelector((s) => s.projectTasks ?? s.projectTask ?? {});
  const tasks = projectTaskState.tasks ?? projectTaskState.items ?? [];
  const user = useSelector((s) => s.auth?.user ?? null);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('ALL');

  const fetchData = () => {
    getTasks({ query, status, page: 0, size: 20 })
      .then((r) => dispatch(setTasks(r.data)))
      .catch(() => {});
  };

  useEffect(() => { fetchData(); }, [query, status]);

  const handleDelete = async (id) => {
    await deleteTask(id);
    dispatch(removeTask(id));
  };

  const handleStatusChange = async (id, newStatus) => {
    const res = await updateTaskStatus(id, newStatus);
    dispatch(updateTask(res.data));
  };

  const canManage = isAdmin ||
    ['PROJECT_DIRECTOR', 'PROJECT_MANAGER'].includes(user?.domainRole);

  const handleNew = () => {
    if (onOpenForm) onOpenForm(true);
    if (onNew) onNew();
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3 style={{ margin: 0 }}>Project Tasks</h3>
        {canManage && (
          <button
            aria-label="Add ProjectTask"
            onClick={handleNew}
            style={{ background: '#1976d2', color: '#fff', padding: '5px 12px', borderRadius: 4, cursor: 'pointer', fontSize: 12 }}
          >
            + New Task
          </button>
        )}
      </div>
      <div style={{ display: 'flex', gap: 10, marginBottom: 16, flexWrap: 'wrap', alignItems: 'center' }}>
        <input
          aria-label="Search tasks"
          placeholder="Search..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          style={{ flex: 1, minWidth: 160, padding: '8px 12px', border: '1px solid #ccc', borderRadius: 4 }}
        />
        <select
          aria-label="Task status filter"
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          style={{ padding: '8px 12px', border: '1px solid #ccc', borderRadius: 4 }}
        >
          <option value="ALL">ALL</option>
          {['PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED'].map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
        <button
          onClick={() => { setQuery(''); setStatus('ALL'); }}
          style={{ padding: '8px 14px', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer', background: '#f5f5f5' }}
        >
          Clear Dynamic Filters
        </button>
      </div>
      {tasks.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px 0', color: '#9e9e9e' }}>
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#bdbdbd" strokeWidth="1.5" style={{ marginBottom: 12 }}>
            <rect x="3" y="3" width="18" height="18" rx="2" />
            <path d="M9 9h6M9 12h6M9 15h4" />
          </svg>
          <p style={{ margin: '0 0 12px' }}>No task assignment records found in this operational view.</p>
        </div>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ background: '#f5f5f5' }}>
              {['Code', 'Title', 'Priority', 'Status', 'Est.Hrs', 'Due', 'Actions'].map((h) => (
                <th key={h} style={{ padding: '10px 12px', textAlign: 'left', fontWeight: 600, fontSize: 13 }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {tasks.map((t) => (
              <tr key={t.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '10px 12px', fontSize: 13 }}>{t.taskCode}</td>
                <td style={{ padding: '10px 12px', fontSize: 13 }}>{t.title}</td>
                <td style={{ padding: '10px 12px', fontSize: 13 }}>{t.priority}</td>
                <td style={{ padding: '10px 12px', fontSize: 13 }}>
                  <select
                    value={t.status}
                    onChange={(e) => handleStatusChange(t.id, e.target.value)}
                    style={{ fontSize: 12, padding: '2px 6px', border: '1px solid #ccc', borderRadius: 4 }}
                  >
                    {['PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED'].map((s) => (
                      <option key={s}>{s}</option>
                    ))}
                  </select>
                </td>
                <td style={{ padding: '10px 12px', fontSize: 13 }}>{t.estimatedHours}</td>
                <td style={{ padding: '10px 12px', fontSize: 13 }}>{t.dueDate}</td>
                <td style={{ padding: '10px 12px', fontSize: 13 }}>
                  {canManage && (
                    <>
                      <button
                        aria-label={`Edit ${t.title || t.id}`}
                        onClick={() => onEdit && onEdit(t)}
                        style={{ background: '#388e3c', color: '#fff', border: 'none', padding: '5px 12px', borderRadius: 4, cursor: 'pointer', fontSize: 12 }}
                      >
                        Edit
                      </button>
                      <button
                        aria-label={`Delete ${t.title || t.id}`}
                        onClick={() => handleDelete(t.id)}
                        style={{ background: '#d32f2f', color: '#fff', border: 'none', padding: '5px 12px', borderRadius: 4, cursor: 'pointer', fontSize: 12, marginLeft: 6 }}
                      >
                        Delete
                      </button>
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
