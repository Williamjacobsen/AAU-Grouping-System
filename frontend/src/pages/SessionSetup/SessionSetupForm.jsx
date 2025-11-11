

import React, { memo } from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

const SessionSetupForm = memo(({ sessionId, session, supervisors, students, setMessage }) => {

	async function saveSetup(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit
			setMessage("Saving session...");

			const formData = new FormData(event.currentTarget);

			const response = await fetchWithDefaultErrorHandling(
				`/sessions/${sessionId}/saveSetup`,
				{
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify(formData),
				}
			);

			setMessage("Setup saved successfully!");

			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	function getSupervisorEmails() {
		let text = "";
		supervisors.forEach((supervisor) => {
			text += supervisor.email + "\n";
		});
		return text;
	}

	function getStudentEmails() {
		let text = "";
		students.forEach((student) => {
			text += student.email + "\n";
		});
		return text;
	}

	return (
		<form className="setup-form" onSubmit={saveSetup}>
			<div className="form-section">
				<h3 className="section-title">Basic Settings</h3>

				<div className="form-group">
					<label className="form-label">
						Session name
						<input
							className="form-input"
							name="name"
							defaultValue={session.name}
							required
						/>
					</label>
				</div>

				<div className="form-group">
					<label className="form-label">
						Maximum group size
						<input
							className="form-input form-number"
							type="number"
							name="maxGroupSize"
							defaultValue={session.maxGroupSize}
							required
						/>
					</label>
				</div>

				<div className="form-group">
					<label className="form-label">
						Minimum group size
						<input
							className="form-input form-number"
							type="number"
							name="minGroupSize"
							defaultValue={session.minGroupSize}
						/>
					</label>
				</div>

				<div className="form-group">
					<label className="form-label">
						Group rounding method
						<select
							className="form-select"
							name="groupRounding"
							defaultValue={session.groupRounding}
						>
							<option value="none">No rounding</option>
							<option value="round_up">Round up</option>
							<option value="round_down">Round down</option>
						</select>
					</label>
				</div>

				<div className="form-group">
					<label className="form-label">
						Deadline for students to submit their questionnaires
						<input
							className="form-input"
							type="datetime-local"
							name="questionnaireDeadline"
							defaultValue={session.questionnaireDeadline}
						/>
					</label>
				</div>
			</div>

			<div className="form-section">
				<h3 className="section-title">Participants</h3>

				<div className="form-group">
					<label className="form-label">
						Emails of supervisors
						<textarea
							className="form-textarea"
							name="supervisorEmails"
							defaultValue={getSupervisorEmails()}
							required
						/>
					</label>
				</div>

				<div className="form-group">
					<label className="form-label">
						Emails of students
						<textarea
							className="form-textarea"
							name="studentEmails"
							defaultValue={getStudentEmails()}
							required
						/>
					</label>
				</div>
			</div>

			<div className="button-group">
				<input className="button-primary" type="submit" value="Apply Changes" />
			</div>
		</form>
	);
});

export default SessionSetupForm;