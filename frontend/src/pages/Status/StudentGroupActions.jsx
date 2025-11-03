import React, { useState } from 'react';
import useGroupRequests from './useGroupRequests';

// Small UI for requesting to join or accepting a join request on a group.
// - groupId: id of the group to act on
// - studentId: id of the student performing the action (or being accepted)
export default function StudentGroupActions({ groupId, studentId }) {
  const { requestToJoin, acceptJoinRequest } = useGroupRequests();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);

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
      <button onClick={onRequest} disabled={loading || !groupId || !studentId}>
        {loading ? 'Please wait...' : 'Request to join'}
      </button>
      <button onClick={onAccept} disabled={loading || !groupId || !studentId}>
        {loading ? 'Please wait...' : 'Accept request'}
      </button>
      {message && <span style={{ color: 'green' }}>{message}</span>}
      {error && <span style={{ color: 'crimson' }}>{error}</span>}
    </div>
  );
}
