import React, { useState } from 'react';

export default function SearchFilterBar({
  onFilterChange,
  onQueryChange,
  onStatusChange,
  query,
  status,
  placeholder = 'Search...',
  statusOptions = ['ALL', 'PENDING', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED'],
  assigneeOptions = [],
}) {
  const [searchText, setSearchText] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('ALL');
  const [selectedAssignee, setSelectedAssignee] = useState('');

  const handleTextChange = (e) => {
    setSearchText(e.target.value);
    if (onFilterChange) onFilterChange({ text: e.target.value, status: selectedStatus, assignee: selectedAssignee });
    if (onQueryChange) onQueryChange(e.target.value);
  };

  const handleStatusChange = (e) => {
    setSelectedStatus(e.target.value);
    if (onFilterChange) onFilterChange({ text: searchText, status: e.target.value, assignee: selectedAssignee });
    if (onStatusChange) onStatusChange(e.target.value);
  };

  const handleAssigneeChange = (e) => {
    setSelectedAssignee(e.target.value);
    if (onFilterChange) onFilterChange({ text: searchText, status: selectedStatus, assignee: e.target.value });
  };

  const handleClear = () => {
    setSearchText('');
    setSelectedStatus('ALL');
    setSelectedAssignee('');
    if (onFilterChange) onFilterChange({ text: '', status: 'ALL', assignee: '' });
    if (onQueryChange) onQueryChange('');
    if (onStatusChange) onStatusChange('ALL');
  };

  return (
    <div style={{ display: 'flex', gap: 10, marginBottom: 16, flexWrap: 'wrap', alignItems: 'center' }}>
      <input
        aria-label="Search tasks"
        placeholder={placeholder}
        value={query ?? searchText}
        onChange={handleTextChange}
        style={{ flex: 1, minWidth: 160, padding: '8px 12px', border: '1px solid #ccc', borderRadius: 4 }}
      />
      <select aria-label="Task status filter" value={status ?? selectedStatus} onChange={handleStatusChange} style={{ padding: '8px 12px', border: '1px solid #ccc', borderRadius: 4 }}>
        {statusOptions.map((s) => <option key={s} value={s}>{s}</option>)}
      </select>
      {assigneeOptions.length > 0 && (
        <select value={selectedAssignee} onChange={handleAssigneeChange} style={{ padding: '8px 12px', border: '1px solid #ccc', borderRadius: 4 }}>
          <option value="">All Assignees</option>
          {assigneeOptions.map((a) => (
            <option key={a.id} value={a.id}>{a.fullName} ({a.domainRole})</option>
          ))}
        </select>
      )}
      <button onClick={handleClear} style={{ padding: '8px 14px', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer', background: '#f5f5f5' }}>
        Clear Dynamic Filters
      </button>
    </div>
  );
}
