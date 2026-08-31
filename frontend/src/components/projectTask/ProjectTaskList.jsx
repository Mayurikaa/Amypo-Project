import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { setTasksDirectly, selectTask } from '../../store/slices/projectTaskSlice';
import axios from 'axios';
import EmptyState from '../common/EmptyState';

export default function ProjectTaskList({ onOpenForm }) {
  const dispatch = useDispatch();
  const { items } = useSelector((s) => s.projectTasks);
  const user = useSelector((s) => s.auth?.user ?? null);

  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('ALL');

  const isManager = ['PROJECT_DIRECTOR', 'PROJECT_MANAGER'].includes(user?.domainRole);
  const isContributor = user?.domainRole === 'TEAM_CONTRIBUTOR';

  const fetchData = () => {
    const params = {};
    if (query) params.query = query;
    if (status !== 'ALL') params.status = status;
    if (isContributor && user?.id) params.assigneeId = user.id;
    axios.get('/api/v1/tasks', { params })
      .then((r) => dispatch(setTasksDirectly(r.data)))
      .catch(() => {});
  };

  useEffect(() => { fetchData(); }, [query, status]);

  const handleDelete = (id) => {
    if (window.confirm('Delete this task?')) {
      axios.delete(`/api/v1/tasks/${id}`)
        .then(() => fetchData())
        .catch(() => {});
    }
  };

  const handleEdit = (task) => {
    dispatch(selectTask(task));
    if (onOpenForm) onOpenForm(task);
  };

  const handleAdd = () => {
    dispatch(selectTask(null));
    if (onOpenForm) onOpenForm(true);
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3 style={{ margin: 0 }}>Project Tasks</h3>
        {isManager && (
          <button
            onClick={handleAdd}
            aria-label="Add ProjectTask"
            style={{ background: '#1976d2', color: '#fff', padding: '5px 14px', borderRadius: 4, cursor: 'pointer', border: 'none' }}
          >
            + Add ProjectTask
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
          {['ALL', 'PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED'].map((s) => (
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

      {isContributor && user?.fullName && (
        <div style={{ background: '#e3f2fd', padding: '10px 16px', borderRadius: 4, marginBottom: 16 }}>
          Welcome, {user.fullName}
        </div>
      )}

      {items.length === 0 ? (
        <EmptyState />
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 14 }}>
          <thead>
            <tr style={{ background: '#f5f5f5' }}>
              {['Code', 'Title', 'Priority', 'Hours', 'Due Date', 'Status', 'Actions'].map((h) => (
                <th key={h} style={{ padding: '10px 12px', textAlign: 'left', fontWeight: 600, fontSize: 13 }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {items.map((t) => (
              <tr key={t.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '10px 12px' }}>{t.taskCode}</td>
                <td style={{ padding: '10px 12px' }}>
                  <div>{t.title}</div>
                  {t.description && <div style={{ fontSize: 12, color: '#777' }}>{t.description}</div>}
                  {t.dueDate && <div style={{ fontSize: 11, color: '#999' }}>Due: {t.dueDate}</div>}
                </td>
                <td style={{ padding: '10px 12px' }}>{t.priority}</td>
                <td style={{ padding: '10px 12px' }}>{t.loggedHours ?? 0} / {t.estimatedHours}</td>
                <td style={{ padding: '10px 12px' }}>{t.dueDate}</td>
                <td style={{ padding: '10px 12px' }}>{t.status}</td>
                <td style={{ padding: '10px 12px' }}>
                  {isManager && (
                    <>
                      <button
                        onClick={() => handleEdit(t)}
                        style={{ marginRight: 6, padding: '4px 10px', background: '#388e3c', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer', fontSize: 12 }}
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(t.id)}
                        style={{ padding: '4px 10px', background: '#d32f2f', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer', fontSize: 12 }}
                      >
                        Delete
                      </button>
                    </>
                  )}
                  {isContributor && (
                    <button
                      onClick={() => { dispatch(selectTask(t)); if (onOpenForm) onOpenForm(t); }}
                      style={{ padding: '4px 10px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer', fontSize: 12 }}
                    >
                      Log Hours
                    </button>
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
