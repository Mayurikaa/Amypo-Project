import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../../store/slices/authSlice';

export default function Navbar({ onNavigate, activeTab }) {
  const dispatch = useDispatch();
  const user = useSelector((s) => s.auth.user);

  const navItems = [
    { key: 'home', label: 'Home' },
    { key: 'initiatives', label: 'Initiatives' },
    { key: 'milestones', label: 'Milestones' },
    { key: 'tasks', label: 'Tasks' },
    { key: 'submissions', label: 'Submissions' },
    ...(user?.domainRole === 'PROJECT_DIRECTOR' ? [{ key: 'accounts', label: 'System Accounts' }] : []),
  ];

  const handleLogout = () => {
    localStorage.removeItem('taskguard_token');
    dispatch(logout());
  };

  return (
    <nav style={{ background: '#1976d2', color: '#fff', padding: '12px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
      <span style={{ fontWeight: 'bold', fontSize: 18, cursor: 'pointer' }} onClick={() => onNavigate && onNavigate('home')}>TaskGuard</span>
      <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
        {navItems.map((item) => (
          <button
            key={item.key}
            className={activeTab === item.key ? 'active' : ''}
            onClick={() => onNavigate && onNavigate(item.key)}
            style={{ background: activeTab === item.key ? 'rgba(255,255,255,0.3)' : 'rgba(255,255,255,0.15)', border: 'none', color: '#fff', padding: '6px 12px', borderRadius: 4, cursor: 'pointer' }}
          >
            {item.label}
          </button>
        ))}
        {user && (
          <span style={{ fontSize: 13, marginLeft: 8 }}>
            {user.fullName || user.email}
          </span>
        )}
        <button
          onClick={handleLogout}
          style={{ background: '#d32f2f', border: 'none', color: '#fff', padding: '6px 12px', borderRadius: 4, cursor: 'pointer' }}
        >
          Logout
        </button>
      </div>
    </nav>
  );
}
