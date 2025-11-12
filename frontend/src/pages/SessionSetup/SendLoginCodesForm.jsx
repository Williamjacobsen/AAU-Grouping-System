import React, { memo, useState } from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

const SendLoginCodesForm = memo(({ sessionId, targetUsers, setMessage }) => {

	const [sendOnlyNew, setSendOnlyNew] = useState(true);

	if (targetUsers !== "supervisors" && targetUsers !== "students") {
		return <div>targetUsers is not one of the allowed values</div>;
	}

	async function sendLoginCodes() {
		try {
			setMessage(`Sending login codes to ${targetUsers}...`);

			await fetchWithDefaultErrorHandling(
				`/sessionSetup/${sessionId}/sendLoginCodeTo/${targetUsers}`,
				{
					credentials: "include",
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify({ sendOnlyNew })
				}
			);

			setMessage(`Login codes sent to ${targetUsers} successfully!`);
		} catch (error) {
			setMessage(`Error: ${error}`);
		}
	}

	return (
		<div className="checkbox-form">
			<div className="checkbox-group">
				<input
					type="checkbox"
					defaultChecked={sendOnlyNew}
					onClick={() => setSendOnlyNew(!sendOnlyNew)}
				/>
				<label className="checkbox-label">
					Send only to {targetUsers} who have not received a login code yet
				</label>
			</div>
			<input
				className="button-primary"
				value={`Email login codes to ${targetUsers}`}
				onClick={sendLoginCodes}
			/>
		</div>
	);
});

export default SendLoginCodesForm;