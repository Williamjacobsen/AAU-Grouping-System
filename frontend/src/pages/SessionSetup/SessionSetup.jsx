import React, { useState } from "react";
import { useParams } from "react-router-dom";

import { useGetSessionByParameter } from "../../hooks/useGetSession";
import useGetSessionStudents from "../../hooks/useGetSessionStudents";
import useGetSessionSupervisors from "../../hooks/useGetSessionSupervisors";
import "./SessionSetup.css";
import SessionSetupForm from "./SessionSetupForm";
import SendLoginCodesForm from "./SendLoginCodesForm";
import ResetFormButton from "./ResetFormButton";

export default function SessionSetup() {

	const { sessionId } = useParams(); // Gets the session ID via the URL parameter "../:sessionId/setup"
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingStudents, students } = useGetSessionStudents(sessionId);
	const { isLoading: isLoadingSupervisors, supervisors } = useGetSessionSupervisors(sessionId);
	const [message, setMessage] = useState("");

	if (isLoadingSession) return <div className="loading-message">Loading session...</div>;
	if (isLoadingStudents) return <div className="loading-message">Loading students...</div>;
	if (isLoadingSupervisors) return <div className="loading-message">Loading supervisors...</div>;

	return (
		<div className="session-setup-container">

			<h1 className="session-setup-title">
				Session Setup
			</h1>

			<SessionSetupForm
				sessionId={sessionId}
				session={session}
				supervisors={supervisors}
				students={students}
				setMessage={setMessage}
			/>

			<div className="form-section">
				<h3 className="section-title">
					Actions
				</h3>

				<ResetFormButton />

				<SendLoginCodesForm
					sessionId={sessionId}
					setMessage={setMessage}
					targetUsers="supervisors"
				/>

				<SendLoginCodesForm
					sessionId={sessionId}
					setMessage={setMessage}
					targetUsers="students"
				/>

			</div>

			{message && <div className="message">{message}</div>}
		</div>
	);
}
