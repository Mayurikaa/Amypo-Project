import React, { useEffect } from 'react';

export default function NotificationStack({ notification, onClose }) {
  useEffect(() => {
    if (!notification) return;
    const timer = setTimeout(() => { onClose && onClose(); }, 5000);
    return () => clearTimeout(timer);
  }, [notification, onClose]);

  if (!notification) return null;

  return (
    <div className="notification-stack-container" style={{ position: 'fixed', top: 16, right: 16, zIndex: 9999, minWidth: 280 }}>
      <div className="notification-banner" style={{ background: notification.type === 'error' ? '#d32f2f' : '#388e3c', color: '#fff', padding: '12px 18px', borderRadius: 6, display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          {notification.title && <div style={{ fontWeight: 'bold', marginBottom: 4 }}>{notification.title}</div>}
          <p style={{ margin: 0 }}>{notification.message}</p>
        </div>
        <button
          className="notification-close-btn"
          onClick={onClose}
          style={{ background: 'none', border: 'none', color: '#fff', cursor: 'pointer', marginLeft: 12, fontSize: 16 }}
        >
          ✕
        </button>
      </div>
    </div>
  );
}
