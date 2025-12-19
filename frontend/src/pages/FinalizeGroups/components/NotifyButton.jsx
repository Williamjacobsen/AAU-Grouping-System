import useGetSessionGroups from "hooks/fetching/useGetSessionGroups";
import useGetSessionStudents from "hooks/fetching/useGetSessionStudents";
import React, { useState, memo } from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";


const NotifyButton = memo(({ sessionId }) => {
	const { groups } = useGetSessionGroups(sessionId);
	const { students } = useGetSessionStudents(sessionId);
	const [isSending, setIsSending] = useState(false);
	const [hasSent, setHasSent] = useState(false);

	if (!sessionId) return null;

	async function sendNotifications() {
		try {
			if (!groups || groups.length === 0) {
				alert("Error: No groups found to download.");
				return;
			}

			if (!students || students.length === 0) {
				alert("Error: No students found to download.");
				return;
			}

			// Check if groups have students
			const groupsWithStudents = groups.filter(group =>
				group.students && group.students.length > 0
			);

			// Check if groups have supervisor
			const groupsWithoutSupervisors = groupsWithStudents.filter(group =>
				!group.supervisorId ||
				(typeof group.supervisorId === 'string' && group.supervisorId.trim() === '')
			);

			// Check if groups have project
			const groupsWithoutProjects = groupsWithStudents.filter(group =>
				!group.assignedProjectId ||
				(typeof group.assignedProjectId === 'string' && group.assignedProjectId.trim() === '')
			);

			if (groupsWithoutProjects.length > 0 || groupsWithoutSupervisors.length > 0) {
				alert("Error: Groups must have supervisors and projects assigned.");
				return;
			}

			setIsSending(true);
			await fetchWithDefaultErrorHandling(`/api/${sessionId}/notify`, {
				credentials: "include",
				method: "POST",
			});
			setHasSent(true);

			alert("Notifications sent successfully!");
		} catch (error) {
			alert(`Error sending notifications: ${error}`);
		} finally {
			setIsSending(false);
		}
	}

	return (
		<div>
			<div
				className="checkbox-group"
				style={{ display: "inline", gap: "1rem" }}
			>
				<p className="checkbox-label">
					When all groups have been made, send emails to students and
					supervisors:
				</p>
				<br></br>
				<button
					className="button-primary"
					onClick={sendNotifications}
					disabled={isSending || hasSent}
					style={{ backgroundColor: hasSent ? "green" : undefined }}
				>
					{isSending
						? "Sending notifications..."
						: hasSent
							? "Notifications sent"
							: "Notify participants"}
				</button>
				<hr></hr>
				<br></br>
			</div>
		</div>
	);
});

export default NotifyButton;
