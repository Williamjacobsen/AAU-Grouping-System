import React from 'react';

import useIsQuestionnaireDeadlineExceeded from 'hooks/useIsQuestionnaireDeadlineExceeded';
import { fetchWithDefaultErrorHandling } from 'utils/fetchHelpers';

// Small UI for requesting to join or accepting a join request on a group.
// - session: session object (used for questionnaire deadline)
// - user: current logged in user (used to decide if the user is a student)
export default function StudentGroupActions({ groupId, session, user }) {

	// Determine if the questionnaire submission deadline has passed for this session
	const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session);

	function getIsUserAlreadyInTheGroup() {
		return user.groupId === groupId;
	}

	function getIsUserAlreadyInAnyGroup() {
		return user.groupId !== null;
	}

	function getIsUserHasPendingJoinRequest() {
		return user.activeJoinRequestGroupId !== null;
	}

	function getCanUserSendJoinRequest() {
		return !getIsUserAlreadyInAnyGroup() && !getIsUserHasPendingJoinRequest();
	}

	async function sendJoinRequest() {
		try {
			// Additional check to prevent sending request if user is already in a group or has pending request
			if (!getCanUserSendJoinRequest()) {
				alert("Cannot send join request: You are already in a group or have a pending request.");
				return;
			}

			await fetchWithDefaultErrorHandling(
				`/api/groups/${session.id}/${groupId}/request-to-join`,
				{
					method: 'POST',
					credentials: 'include',
					headers: { 'Content-Type': 'application/json' },
				});
			alert("Request succesfully sent");
			window.location.reload(); // Reload page to refresh changes
		} catch (error) {
			alert(error);
		}
	}

	return (
		<div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>

			{!isDeadlineExceeded() && (
				<>
					<button onClick={sendJoinRequest} disabled={!groupId || !getCanUserSendJoinRequest()}>
						Request to join group
					</button>
				</>
			)}

			{isDeadlineExceeded() && (
				<>
					<span style={{ color: 'crimson', marginLeft: 8 }}>Deadline exceeded: Groups locked.</span>
				</>
			)}
		</div>
	);
}
