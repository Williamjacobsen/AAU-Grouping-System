import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { useGetSessionByParameter } from "../../hooks/useGetSession";
import useGetSessionStudents from "../../hooks/useGetSessionStudents";
import useGetSessionSupervisors from "../../hooks/useGetSessionSupervisors";
import "./SessionSetup.css";

export default function SessionSetup() {

	const { sessionId } = useParams(); // Gets the session ID via the URL parameter "../:sessionId/setup"
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingStudents, students } = useGetSessionStudents(sessionId);
	const { isLoading: isLoadingSupervisors, supervisors } = useGetSessionSupervisors(sessionId);
	const [message, setMessage] = useState("");

	if (isLoadingSession) {
		return <div className="loading-message">Loading session...</div>;
	}
	if (isLoadingStudents) {
		return <div className="loading-message">Loading students...</div>;
	}
	if (isLoadingSupervisors) {
		return <div className="loading-message">Loading supervisors...</div>;
	}

	async function saveSetup(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit
			setMessage("Saving session...");

			const formData = new FormData(event.currentTarget);

			const res = await fetch(`${process.env.REACT_APP_API_BASE_URL}/sessions/${sessionId}/saveSetup`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				credentials: "include",
				body: JSON.stringify(formData),
			});

			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			setMessage("Setup saved successfully!");

			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	function getSupervisorEmails() {
		let text = "";
		supervisors.map((supervisor) => {
			text += supervisor.email + "\n";
		});
		return text;
	}

	function getStudentEmails() {
		let text = "";
		students.map((student) => {
			text += student.email + "\n";
		});
		return text;
	}

	function SetupForm() {
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
	}

	async function sendLoginCodeToStudents(event) {

		try {
			event.preventDefault(); // Prevent page from refreshing on submit
			setMessage("Sending login codes to students...");

			const formData = new FormData(event.currentTarget);
			const sendOnlyNew = formData.get("sendOnlyNew") === "on";

			const res = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions/${sessionId}/sendLoginCodeToStudents`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				credentials: "include",
				body: JSON.stringify({ sendOnlyNew })
			});

			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			setMessage("Login codes sent to students successfully!");
		} catch (error) {
			setMessage(`Error: ${error.message}`);
		}
	}

	async function sendLoginCodeToSupervisors(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit
			setMessage("Sending login codes to supervisors...");

			const formData = new FormData(event.currentTarget);
			const sendOnlyNew = formData.get("sendOnlyNew") === "on";

			const res = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions/${sessionId}/sendLoginCodeToSupervisors`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				credentials: "include",
				body: JSON.stringify({ sendOnlyNew })
			});

			if (!res.ok) throw new Error(`HTTP ${res.status}`);
			setMessage("Login codes sent to supervisors successfully!");
		} catch (error) {
			setMessage(`Error: ${error.message}`);
		}
	}

	return (
		<div className="session-setup-container">
			<h1 className="session-setup-title">Session Setup</h1>
			<SetupForm />

			<div className="form-section">
				<h3 className="section-title">Actions</h3>

				<div className="button-group">
					<button
						className="button-secondary"
						type="button"
						onClick={() => window.location.reload()}
					>
						Reset Form
					</button>
				</div>
			</div>

			<div className="form-section">
				<h3 className="section-title">Send Login Codes</h3>

				<form className="checkbox-form" onSubmit={sendLoginCodeToStudents}>
					<div className="checkbox-group">
						<input
							type="checkbox"
							name="sendOnlyNew"
							defaultChecked={true}
							id="students-checkbox"
						/>
						<label className="checkbox-label" htmlFor="students-checkbox">
							Send only to students who have not received a login code yet
						</label>
					</div>
					<input
						className="button-primary"
						type="submit"
						value="Send Login Codes to Students"
					/>
				</form>

				<form className="checkbox-form" onSubmit={sendLoginCodeToSupervisors}>
					<div className="checkbox-group">
						<input
							type="checkbox"
							name="sendOnlyNew"
							defaultChecked={true}
							id="supervisors-checkbox"
						/>
						<label className="checkbox-label" htmlFor="supervisors-checkbox">
							Send only to supervisors who have not received a login code yet
						</label>
					</div>
					<input
						className="button-primary"
						type="submit"
						value="Send Login Codes to Supervisors"
					/>
				</form>
			</div>

			{message && <div className="message">{message}</div>}
		</div>
	);
}
