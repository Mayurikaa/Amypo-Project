import React from 'react';

export default function CapacityBar({ used = 0, total = 0, labelSuffix = '' }) {
  const percent = Math.min(100, Math.round((used / Math.max(total, 1)) * 100));
  const colorClass = percent >= 90 ? 'capacity-red' : percent >= 70 ? 'capacity-amber' : 'capacity-green';
  const bgColor = percent >= 90 ? '#d32f2f' : percent >= 70 ? '#f57c00' : '#388e3c';

  return (
    <div style={{ minWidth: 120 }}>
      <div style={{ background: '#e0e0e0', borderRadius: 4, height: 8, width: '100%' }}>
        <div
          className={colorClass}
          style={{ width: `${percent}%`, background: bgColor, height: '100%', borderRadius: 4, transition: 'width 0.3s' }}
        />
      </div>
      <div style={{ fontSize: 11, color: '#555', marginTop: 2 }}>
        {used} / {total} {labelSuffix}
      </div>
      <div style={{ fontSize: 11, color: '#555' }}>{percent}% Consumed</div>
    </div>
  );
}
