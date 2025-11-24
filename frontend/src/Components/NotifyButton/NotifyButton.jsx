import React, { useState, memo } from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

const NotifyButton = memo(({ sessionId }) => {
	const [isSending, setIsSending] = useState(false);
	const [hasSent, setHasSent] = useState(false);

	if (!sessionId) return null;

	async function sendNotifications() {
		try {
			setIsSending(true);
			await fetchWithDefaultErrorHandling(`/${sessionId}/notify`, {
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
