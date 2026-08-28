import React from 'react';
import { useSelector } from 'react-redux';

export default function StatCards() {
  const { items: initiatives, analytics } = useSelector((s) => s.projectInitiatives);
  const { items: tasks } = useSelector((s) => s.projectTasks);
  const { items: submissions } = useSelector((s) => s.taskSubmissions);

  const totalInitiatives = analytics?.totalInitiatives ?? initiatives.length;
  const activeTasksCount = analytics?.activeTasksCount ?? tasks.filter((t) => t.status === 'IN_PROGRESS' || t.status === 'PENDING').length;
  const pendingAuditsCount = submissions.filter((s) => s.completionStatus === 'SUBMITTED').length;
  const totalLoggedHours = analytics?.totalHoursLogged ?? tasks.reduce((sum, t) => sum + (t.loggedHours || 0), 0);

  const cards = [
    { label: 'Total Initiatives', value: totalInitiatives, color: '#1976d2' },
    { label: 'Active Tasks', value: activeTasksCount, color: '#388e3c' },
    { label: 'Pending Audits', value: pendingAuditsCount, color: '#f57c00' },
    { label: 'Total Hours Logged', value: totalLoggedHours, color: '#7b1fa2' },
  ];

  return (
    <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap', marginBottom: 24 }}>
      {cards.map((c) => (
        <div
          key={c.label}
          className="stat-card"
          style={{ flex: '1 1 160px', background: '#fff', borderRadius: 8, padding: '20px 24px', borderLeft: `4px solid ${c.color}`, boxShadow: '0 1px 4px rgba(0,0,0,0.08)' }}
        >
          <div style={{ fontSize: 32, fontWeight: 'bold', color: c.color }}>{c.value ?? 0}</div>
          <div style={{ fontSize: 14, marginTop: 4, color: '#555' }}>{c.label}</div>
        </div>
      ))}
    </div>
  );
}
