import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchMilestones, deleteMilestone } from '../../store/slices/projectMilestoneSlice';
import EmptyState from '../common/EmptyState';

export default function ProjectMilestoneList({ onEdit, onNew }) {
  const dispatch = useDispatch();
  const { milestones } = useSelector((s) => s.projectMilestone);
  const user = useSelector((s) => s.auth.user);
  const [status, setStatus] = useState('ALL');

  useEffect(() => {
    dispatch(fetchMilestones({ status: status === 'ALL' ? undefined : status, page: 0, size: 20 }));
  }, [dispatch, status]);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this milestone?')) return;
    dispatch(deleteMilestone(id));
  };

  const canManage = ['PROJECT_DIRECTOR', 'PROJECT_MANAGER'].includes(user?.domainRole);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3 style={{ margin: 0 }}>Project Milestones</h3>
        {canManage && <button onClick={onNew} style={btn('#1976d2')}>+ New Milestone</button>}
      </div>
      <select value={status} onChange={(e) => setStatus(e.target.value)} style={{ marginBottom: 16, padding: '8px 12px', border: '1px solid #ccc', borderRadius: 4 }}>
        <option value="ALL">All Statuses</option>
        {['PENDING', 'IN_PROGRESS', 'COMPLETED'].map((s) => <option key={s}>{s}</option>)}
      </select>
      {milestones.length === 0 ? <EmptyState /> : (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead><tr style={{ background: '#f5f5f5' }}>{['Title', 'Initiative', 'Target Date', 'Hours', 'Status', 'Actions'].map((h) => <th key={h} style={th}>{h}</th>)}</tr></thead>
          <tbody>
            {milestones.map((m) => (
              <tr key={m.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={td}>{m.title}</td>
                <td style={td}>{m.initiativeId}</td>
                <td style={td}>{m.targetDate}</td>
                <td style={td}>{m.allocatedHours}</td>
                <td style={td}>{m.status}</td>
                <td style={td}>
                  {canManage && (
                    <>
                      <button onClick={() => onEdit(m)} style={btn('#388e3c')}>Edit</button>
                      <button onClick={() => handleDelete(m.id)} style={{ ...btn('#d32f2f'), marginLeft: 6 }}>Delete</button>
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
