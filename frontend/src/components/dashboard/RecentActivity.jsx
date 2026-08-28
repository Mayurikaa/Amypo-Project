import React from 'react';
import { useSelector } from 'react-redux';

export default function RecentActivity() {
  const submissions = useSelector((s) => s.taskSubmissions.items);
  const tasks = useSelector((s) => s.projectTasks.items);

  const submissionEvents = submissions.map((s) => ({
    id: `sub-${s.id}`,
    title: `Contributor work log submitted (${s.hoursSpent} hrs)`,
    statusText: s.completionStatus,
    timestamp: s.submittedAt ? new Date(s.submittedAt) : new Date(0),
  }));

  const taskEvents = tasks.map((t) => ({
    id: `task-${t.id}`,
    title: `Task code [${t.taskCode}] status set to ${t.status}`,
    statusText: t.priority,
    timestamp: t.updatedAt ? new Date(t.updatedAt) : (t.createdAt ? new Date(t.createdAt) : new Date(0)),
  }));

  const combined = [...submissionEvents, ...taskEvents]
    .sort((a, b) => b.timestamp - a.timestamp)
    .slice(0, 6);

  return (
    <div style={{ background: '#fff', borderRadius: 8, padding: 20, border: '1px solid #e0e0e0' }}>
      <div style={{ fontWeight: 'bold', marginBottom: 12 }}>Recent Activity</div>
      {combined.length === 0 ? (
        <div style={{ textAlign: 'center', color: '#9e9e9e' }}>No activity signals logged yet.</div>
      ) : (
        combined.map((event) => (
          <div key={event.id} className="activity-item" style={{ display: 'flex', alignItems: 'flex-start', gap: 10, padding: '8px 0', borderBottom: '1px solid #f5f5f5' }}>
            <span style={{ width: 8, height: 8, borderRadius: '50%', background: '#1976d2', marginTop: 5, flexShrink: 0 }} />
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 13 }}>{event.title}</div>
              <div style={{ fontSize: 11, color: '#9e9e9e', marginTop: 2 }}>
                {event.timestamp.getTime() > 0 ? event.timestamp.toLocaleDateString() : '—'}
                {event.statusText && <span style={{ marginLeft: 8, background: '#e3f2fd', color: '#1976d2', padding: '1px 6px', borderRadius: 10, fontSize: 10 }}>{event.statusText}</span>}
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
}
