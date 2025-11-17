import React, { useState } from 'react';
import useGroupRequests from './useGroupRequests';
import useIsQuestionnaireDeadlineExceeded from '../../hooks/useIsQuestionnaireDeadlineExceeded';

// Small UI for requesting to join or accepting a join request on a group.
// - session: session object (used for questionnaire deadline)
// - user: current logged in user (used to decide if the user is a student)
export default function StudentGroupActions({ groupId, studentId, session, user }) {
  const { requestToJoin, acceptJoinRequest } = useGroupRequests();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

  // Determine if the questionnaire submission deadline has passed for this session
  const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session);

  // If current user is a student and the deadline is exceeded, disable editing actions
  const editingLockedForStudent = user?.role === "Student" && isDeadlineExceeded && isDeadlineExceeded();

  // Don't show action buttons for Coordinators - they should only view student status
  if (user?.role === "Coordinator") {
    return (
      <div style={{ fontSize: '12px', color: '#666', fontStyle: 'italic' }}>
        Coordinator view
      </div>
    );
  }

  async function onRequest() {
    setLoading(true);
    setMessage(null);
    setError(null);
    const res = await requestToJoin(groupId, studentId);
    setLoading(false);
    if (res.ok) setMessage(res.message || 'Request sent');
    else setError(res.error || 'Failed to send request');
  }

  async function onAccept() {
    setLoading(true);
    setMessage(null);
    setError(null);
    const res = await acceptJoinRequest(groupId, studentId);
    setLoading(false);
    if (res.ok) setMessage(res.message || 'Accepted');
    else setError(res.error || 'Failed to accept request');
  }

  return (
    <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
      <button onClick={onRequest} disabled={loading || !groupId || !studentId || editingLockedForStudent}>
        {loading ? 'Please wait...' : 'Request to join'}
      </button>
      <button onClick={onAccept} disabled={loading || !groupId || !studentId || editingLockedForStudent}>
        {loading ? 'Please wait...' : 'Accept request'}
      </button>
      {editingLockedForStudent && (
        <span style={{ color: 'crimson', marginLeft: 8 }}>Deadline exceeded — students cannot change groups.</span>
      )}
      {message && <span style={{ color: 'green' }}>{message}</span>}
      {error && <span style={{ color: 'crimson' }}>{error}</span>}
    </div>
  );
}
