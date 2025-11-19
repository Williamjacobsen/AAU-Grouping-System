

import React, { memo } from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

const SessionSetupForm = memo(({ sessionId, session, supervisors, students, setMessage }) => {

	async function saveSetup(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit
			setMessage("Saving session...");

			const formData = new FormData(event.currentTarget);
			const sessionSetupRecord = Object.fromEntries(formData);

			// <input type="datetime-local"/> gives an ISO string in the format "2007-12-03T10:15",
			// but the backend only takes a full ISO string in the format "2007-12-03T10:15:30.00Z",
			// so we must convert it.
			sessionSetupRecord.questionnaireDeadlineISODateString =
				convertToFullISODate(sessionSetupRecord.questionnaireDeadlineISODateString);

			// Convert <input type="checkbox"> value to a boolean value, since the checked value is "on".
			sessionSetupRecord.allowStudentProjectProposals =
				sessionSetupRecord.allowStudentProjectProposals == "on" ? true : false;

			await fetchWithDefaultErrorHandling(
				`/sessionSetup/${sessionId}/saveSetup`,
				{
					credentials: "include",
					method: "POST",
					headers: { "Content-Type": "application/json" },
					body: JSON.stringify(sessionSetupRecord),
				}
			);

			setMessage("Setup saved successfully!");

			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	function convertToFullISODate(datetimeLocalString) {
		return new Date(datetimeLocalString).toISOString();
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
				<h3 className="section-title">
					Basic Settings
				</h3>

				<div className="form-group">
					<label className="form-label">
						Session name
						<input
							className="form-input"
							name="name"
							defaultValue={session?.name}
							required
						/>
					</label>
				</div>

				<div className="form-group">
					<label className="form-label">
						Minimum group size ("-1" means no preference)
						<input
							className="form-input form-number"
							type="number"
							name="minGroupSize"
							defaultValue={session?.minGroupSize}
							min={-1}
							max={100000}
							step={1}
						/>
					</label>
				</div>

				<div className="form-group">
					<label className="form-label">
						Maximum group size ("-1" means no preference)
						<input
							className="form-input form-number"
							type="number"
							name="maxGroupSize"
							defaultValue={session?.maxGroupSize}
							required
							min={-1}
							max={100000}
							step={1}
						/>
					</label>
				</div>

				<div className="form-group">
					<label className="form-label">
						Deadline for students to submit their questionnaires
						<input
							className="form-input"
							type="datetime-local"
							name="questionnaireDeadlineISODateString"
							defaultValue={session?.questionnaireDeadline}
							required
						/>
					</label>
				</div>

				<div className="form-group">
					<input
						type="checkbox"
						name="allowStudentProjectProposals"
						defaultChecked={session?.allowStudentProjectProposals}
					/>
					<label className="checkbox-label">
						Allow students to create project proposals?
					</label>
				</div>

			</div>

			<div className="form-section">
				<h3 className="section-title">
					Participants
				</h3>

				<div className="form-group">
					<label className="form-label">
						Emails of supervisors
						<textarea
							className="form-textarea"
							name="supervisorEmails"
							defaultValue={getSupervisorEmails()}
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