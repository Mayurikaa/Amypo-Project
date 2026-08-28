import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchSystemAccounts, toggleAccountStatus, selectAccount } from '../../store/slices/systemAccountSlice';
import EmptyState from '../common/EmptyState';
import SystemAccountForm from './SystemAccountForm';

export default function SystemAccountList() {
  const dispatch = useDispatch();
  const { items, loading, error, totalPages, currentPage } = useSelector((s) => s.systemAccounts);
  const user = useSelector((s) => s.auth.user);
  const isAdmin = user?.domainRole === 'PROJECT_DIRECTOR';

  const [roleFilter, setRoleFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [formOpen, setFormOpen] = useState(false);

  useEffect(() => {
    dispatch(fetchSystemAccounts({ role: roleFilter === 'ALL' ? undefined : roleFilter, page, size: 10 }));
  }, [dispatch, roleFilter, page]);

  const handleEdit = (account) => {
    dispatch(selectAccount(account));
    setFormOpen(true);
  };

  const handleToggle = (account) => {
    if (window.confirm(`Are you sure you want to ${account.isActive ? 'revoke' : 're-grant'} access for ${account.email}?`)) {
      dispatch(toggleAccountStatus(account.id)).then(() => {
        dispatch(fetchSystemAccounts({ role: roleFilter === 'ALL' ? undefined : roleFilter, page, size: 10 }));
      });
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>System Accounts</h2>
        <div style={{ display: 'flex', gap: 10 }}>
          <select value={roleFilter} onChange={(e) => { setRoleFilter(e.target.value); setPage(0); }} style={{ padding: '6px 12px', border: '1px solid #ccc', borderRadius: 4 }}>
            {['ALL', 'PROJECT_DIRECTOR', 'PROJECT_MANAGER', 'TEAM_CONTRIBUTOR'].map((r) => (
              <option key={r} value={r}>{r}</option>
            ))}
          </select>
          {isAdmin && (
            <button onClick={() => { dispatch(selectAccount(null)); setFormOpen(true); }} style={{ padding: '6px 16px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
              Provision Access Account
            </button>
          )}
        </div>
      </div>
      {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
      {loading ? <div>Loading...</div> : items.length === 0 ? (
        <EmptyState />
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 14 }}>
          <thead>
            <tr style={{ background: '#f5f5f5' }}>
              <th style={th}>Identity Reference ID</th>
              <th style={th}>Display Full Name</th>
              <th style={th}>Enterprise Email</th>
              <th style={th}>Domain Role Attribute</th>
              <th style={th}>Status State</th>
              {isAdmin && <th style={th}>Management Actions</th>}
            </tr>
          </thead>
          <tbody>
            {items.map((acc) => (
              <tr key={acc.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={td}>ACC-{acc.id}</td>
                <td style={td}>{acc.fullName}</td>
                <td style={td}>{acc.email}</td>
                <td style={td}><span className="role-pill" style={{ background: '#e3f2fd', color: '#1976d2', padding: '2px 8px', borderRadius: 10, fontSize: 12 }}>{acc.domainRole}</span></td>
                <td style={td}>{acc.isActive ? 'ACTIVE' : 'SUSPENDED'}</td>
                {isAdmin && (
                  <td style={td}>
                    <button onClick={() => handleEdit(acc)} style={actionBtn}>Edit Scope</button>
                    <button onClick={() => handleToggle(acc)} style={{ ...actionBtn, background: acc.isActive ? '#ffebee' : '#e8f5e9', color: acc.isActive ? '#c62828' : '#2e7d32' }}>
                      {acc.isActive ? 'Revoke' : 'Re-grant Access'}
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {totalPages > 1 && (
        <div style={{ display: 'flex', gap: 8, marginTop: 16, justifyContent: 'center' }}>
          {Array.from({ length: totalPages }, (_, i) => (
            <button key={i} onClick={() => setPage(i)} style={{ padding: '4px 10px', background: i === currentPage ? '#1976d2' : '#f5f5f5', color: i === currentPage ? '#fff' : '#333', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer' }}>{i + 1}</button>
          ))}
        </div>
      )}
      <SystemAccountForm isOpen={formOpen} onClose={() => setFormOpen(false)} />
    </div>
  );
}

const th = { padding: '10px 12px', textAlign: 'left', fontWeight: 600, fontSize: 13 };
const td = { padding: '10px 12px' };
const actionBtn = { marginRight: 6, padding: '4px 10px', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer', background: '#f5f5f5', fontSize: 12 };
