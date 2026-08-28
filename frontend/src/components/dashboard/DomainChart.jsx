import React from 'react';
import { useSelector } from 'react-redux';

const STATUS_LABELS = {
  PENDING: 'Pending Assignment',
  IN_PROGRESS: 'Work In Progress',
  IN_REVIEW: 'Pending Audit Review',
  COMPLETED: 'Scope Achieved',
};

export default function DomainChart() {
  const tasks = useSelector((s) => s.projectTasks.items);

  const counts = { PENDING: 0, IN_PROGRESS: 0, IN_REVIEW: 0, COMPLETED: 0 };
  tasks.forEach((t) => { if (counts[t.status] !== undefined) counts[t.status]++; });

  const maxCount = Math.max(...Object.values(counts), 1);

  return (
    <div style={{ background: '#fff', borderRadius: 8, padding: 20, border: '1px solid #e0e0e0' }}>
      <div style={{ fontWeight: 'bold', marginBottom: 16 }}>Task Status Distribution</div>
      {Object.entries(counts).map(([status, count]) => {
        const widthPct = Math.max(5, Math.round((count / maxCount) * 100));
        return (
          <div key={status} style={{ marginBottom: 12 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, marginBottom: 4 }}>
              <span>{STATUS_LABELS[status]}</span>
              <span>{count}</span>
            </div>
            <div style={{ background: '#e0e0e0', borderRadius: 4, height: 10 }}>
              <div
                role="progressbar"
                aria-valuenow={count}
                aria-valuemin={0}
                aria-valuemax={maxCount}
                style={{ width: `${widthPct}%`, background: '#1976d2', height: '100%', borderRadius: 4, transition: 'width 0.3s' }}
              />
            </div>
          </div>
        );
      })}
    </div>
  );
}
