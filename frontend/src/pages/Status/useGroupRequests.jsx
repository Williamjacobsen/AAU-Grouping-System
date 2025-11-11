const BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080';

async function requestToJoin(groupId, studentId) {
  try {
    const res = await fetch(`${BASE}/groups/${groupId}/request-join/${studentId}`, {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
    });

    const text = await res.text();
    if (!res.ok) return { ok: false, error: text || res.statusText };
    return { ok: true, message: text };
  } catch (error) {
    return { ok: false, error: error?.message || String(error) };
  }
}

async function acceptJoinRequest(groupId, studentId) {
  try {
    const res = await fetch(`${BASE}/groups/${groupId}/accept-request/${studentId}`, {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
    });

    const text = await res.text();
    if (!res.ok) return { ok: false, error: text || res.statusText };
    return { ok: true, message: text };
  } catch (error) {
    return { ok: false, error: error?.message || String(error) };
  }
}

async function getJoinRequests(groupId) {
  try {
    const res = await fetch(`${BASE}/groups/${groupId}/requests`, {
      method: 'GET',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
    });
    if (!res.ok) return { ok: false, error: res.statusText };
    const data = await res.json();
    return { ok: true, data };
  } catch (error) {
    return { ok: false, error: error?.message || String(error) };
  }
}

export { requestToJoin, acceptJoinRequest, getJoinRequests };

export default function useGroupRequests() {
  return { requestToJoin, acceptJoinRequest, getJoinRequests };
}
