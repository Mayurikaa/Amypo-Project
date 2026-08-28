import React from 'react';

export default function EmptyState({ message = 'No task assignment records found in this operational view.', actionLabel, onAction }) {
  return (
    <div style={{ textAlign: 'center', padding: '40px 0', color: '#9e9e9e' }}>
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#bdbdbd" strokeWidth="1.5" style={{ marginBottom: 12 }}>
        <rect x="3" y="3" width="18" height="18" rx="2" />
        <path d="M9 9h6M9 12h6M9 15h4" />
      </svg>
      <p style={{ margin: '0 0 12px' }}>{message}</p>
      {actionLabel && onAction && (
        <button
          onClick={onAction}
          style={{ padding: '8px 20px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
}
