import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchInitiatives, deleteInitiative, selectInitiative } from '../../store/slices/projectInitiativeSlice';
import SearchFilterBar from '../common/SearchFilterBar';
import CapacityBar from '../common/CapacityBar';
import EmptyState from '../common/EmptyState';
import ProjectInitiativeForm from './ProjectInitiativeForm';

export default function ProjectInitiativeList() {
  const dispatch = useDispatch();
  const { items, loading, error, totalPages, currentPage } = useSelector((s) => s.projectInitiatives);
  const user = useSelector((s) => s.auth.user);
  const isAdmin = user?.domainRole === 'PROJECT_DIRECTOR';

  const [searchQuery, setSearchQuery] = useState('');
  const [filterByStatus, setFilterByStatus] = useState('ALL');
  const [page, setPage] = useState(0);
  const [formOpen, setFormOpen] = useState(false);

  useEffect(() => {
    dispatch(fetchInitiatives({ query: searchQuery || undefined, status: filterByStatus === 'ALL' ? undefined : filterByStatus, page, size: 10 }));
  }, [dispatch, searchQuery, filterByStatus, page]);

  const handleFilterChange = ({ text, status }) => {
    setSearchQuery(text);
    setFilterByStatus(status);
    setPage(0);
  };

  const handleEdit = (item) => {
    dispatch(selectInitiative(item));
    setFormOpen(true);
  };

  const handleDelete = (item) => {
    if (window.confirm(`Delete initiative "${item.title}"?`)) {
      dispatch(deleteInitiative(item.id)).then(() => {
        dispatch(fetchInitiatives({ query: searchQuery || undefined, status: filterByStatus === 'ALL' ? undefined : filterByStatus, page, size: 10 }));
      });
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>Project Initiatives</h2>
        {isAdmin && (
          <button onClick={() => { dispatch(selectInitiative(null)); setFormOpen(true); }} style={{ padding: '6px 16px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
            + Add ProjectInitiative
          </button>
        )}
      </div>
      <SearchFilterBar
        onFilterChange={handleFilterChange}
        placeholder="Search initiatives..."
        statusOptions={['ALL', 'PLANNING', 'ACTIVE', 'ON_HOLD', 'COMPLETED']}
      />
      {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
      {loading ? <div>Loading...</div> : items.length === 0 ? (
        <EmptyState />
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 14 }}>
          <thead>
            <tr style={{ background: '#f5f5f5' }}>
              <th style={th}>Code</th>
              <th style={th}>Title</th>
              <th style={th}>Start Date</th>
              <th style={th}>Target End</th>
              <th style={th}>Budget</th>
              <th style={th}>Status</th>
              {isAdmin && <th style={th}>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={td}>{item.projectCode}</td>
                <td style={td}>
                  <div style={{ fontWeight: 500 }}>{item.title}</div>
                  {item.description && <div style={{ fontSize: 12, color: '#777', marginTop: 2 }}>{item.description}</div>}
                </td>
                <td style={td}>{item.startDate}</td>
                <td style={td}>{item.targetEndDate}</td>
                <td style={td}>
                  <CapacityBar used={Number(item.budgetConsumed || 0)} total={Number(item.budgetAllocated || 0)} labelSuffix="USD" />
                </td>
                <td style={td}><span style={statusBadge}>{item.status}</span></td>
                {isAdmin && (
                  <td style={td}>
                    <button onClick={() => handleEdit(item)} style={actionBtn}>Edit</button>
                    <button onClick={() => handleDelete(item)} style={{ ...actionBtn, color: '#c62828' }}>Delete</button>
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
      <ProjectInitiativeForm isOpen={formOpen} onClose={() => setFormOpen(false)} />
    </div>
  );
}

const th = { padding: '10px 12px', textAlign: 'left', fontWeight: 600, fontSize: 13 };
const td = { padding: '10px 12px' };
const actionBtn = { marginRight: 6, padding: '4px 10px', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer', background: '#f5f5f5', fontSize: 12 };
const statusBadge = { background: '#e3f2fd', color: '#1565c0', padding: '2px 8px', borderRadius: 10, fontSize: 12 };
