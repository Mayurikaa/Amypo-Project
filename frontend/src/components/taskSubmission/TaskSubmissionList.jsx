import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchSubmissions, reviewSubmission } from '../../store/slices/taskSubmissionSlice';
import EmptyState from '../common/EmptyState';

export default function TaskSubmissionList({ onNew }) {
  const dispatch = useDispatch();
  const { submissions } = useSelector((s) => s.taskSubmission);
  const user = useSelector((s) => s.auth.user);
  const [reviewId, setReviewId] = useState(null);
  const [reviewForm, setReviewForm] = useState({ reviewerFeedback: '', completionStatus: 'APPROVED' });

  useEffect(() => {
    dispatch(fetchSubmissions({ page: 0, size: 20 }));
  }, [dispatch]);

  const handleReview = async (id) => {
    await dispatch(reviewSubmission({ id, data: reviewForm })).unwrap();
    setReviewId(null);
  };

  const canReview = ['PROJECT_DIRECTOR', 'PROJECT_MANAGER'].includes(user?.domainRole);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <h3 style={{ margin: 0 }}>Task Submissions</h3>
        {user?.domainRole === 'TEAM_CONTRIBUTOR' && (
          <button onClick={onNew} style={btn('#1976d2')}>+ Submit Work</button>
        )}
      </div>
      {submissions.length === 0 ? <EmptyState /> : (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead><tr style={{ background: '#f5f5f5' }}>{['Task', 'Contributor', 'Hours', 'Notes', 'Status', 'Actions'].map((h) => <th key={h} style={th}>{h}</th>)}</tr></thead>
          <tbody>
            {submissions.map((s) => (
              <tr key={s.id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={td}>{s.taskId}</td>
                <td style={td}>{s.contributorId}</td>
                <td style={td}>{s.hoursSpent}</td>
                <td style={td}>{s.submissionNotes}</td>
                <td style={td}>{s.completionStatus}</td>
                <td style={td}>
                  {canReview && s.completionStatus === 'PENDING_REVIEW' && (
                    <button onClick={() => setReviewId(s.id)} style={btn('#1976d2')}>Review</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {reviewId && (
        <div style={overlay}>
          <div style={modal}>
            <h3 style={{ marginTop: 0 }}>Review Submission</h3>
            <textarea placeholder="Feedback" value={reviewForm.reviewerFeedback} onChange={(e) => setReviewForm({ ...reviewForm, reviewerFeedback: e.target.value })} style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4, marginBottom: 12, boxSizing: 'border-box', minHeight: 80 }} />
            <select value={reviewForm.completionStatus} onChange={(e) => setReviewForm({ ...reviewForm, completionStatus: e.target.value })} style={{ width: '100%', padding: 8, border: '1px solid #ccc', borderRadius: 4, marginBottom: 16 }}>
              <option value="APPROVED">APPROVED</option>
              <option value="REJECTED">REJECTED</option>
            </select>
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
              <button onClick={() => setReviewId(null)} style={{ padding: '8px 16px', border: '1px solid #ccc', borderRadius: 4, cursor: 'pointer' }}>Cancel</button>
              <button onClick={() => handleReview(reviewId)} style={{ padding: '8px 16px', background: '#1976d2', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' }}>Submit Review</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

const th = { padding: '10px 12px', textAlign: 'left', fontWeight: 600, fontSize: 13 };
const td = { padding: '10px 12px', fontSize: 13 };
const btn = (bg) => ({ background: bg, color: '#fff', border: 'none', padding: '5px 12px', borderRadius: 4, cursor: 'pointer', fontSize: 12 });
const overlay = { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 };
const modal = { background: '#fff', borderRadius: 8, padding: 28, width: 420 };
