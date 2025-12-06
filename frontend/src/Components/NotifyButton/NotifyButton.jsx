import useGetSessionGroups from "hooks/useGetSessionGroups";
import useGetSessionStudents  from "hooks/useGetSessionStudents";
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

			console.log("groups", groups);
			console.log("students", students);

			// Check if groups have supervisor
			const groupsWithoutSupervisors = groups.filter(group =>
				!group.supervisorId ||
				(typeof group.supervisorId === 'string' && group.supervisorId.trim() === '')
			);

			// Check if groups have project
			const groupsWithoutProjects = groups.filter(group =>
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
				<label className="checkbox-label">
					When all groups have been made, send emails to students and
					supervisors:
				</label>
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
			</div>
		</div>
	);
});

export default NotifyButton;
