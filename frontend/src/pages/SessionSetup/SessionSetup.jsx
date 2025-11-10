import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { useGetSessionByParameter } from "../../hooks/useGetSession";
import useGetSessionStudents from "../../hooks/useGetSessionStudents";
import useGetSessionSupervisors from "../../hooks/useGetSessionSupervisors";

export default function SessionSetup() {

	const { sessionId } = useParams(); // Gets the session ID via the URL parameter "../:sessionId/setup"
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingStudents, students } = useGetSessionStudents(sessionId);
	const { isLoading: isLoadingSupervisors, supervisors } = useGetSessionSupervisors(sessionId);
	const [message, setMessage] = useState("");
	
	if (isLoadingSession) {
		return <>Loading session...</>
	}
	if (isLoadingStudents) {
		return <>Loading students...</>
	}
	if (isLoadingSupervisors) {
		return <>Loading supervisors...</>
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
		})
		return text;
	}

	function getStudentEmails() {
		let text = "";
		students.map((student) => {
			text += student.email + "\n";
		})
		return text;
	}

	function SetupForm() {
		return (
			<form onSubmit={saveSetup}>
        <label>
          Session name<br />
          <input
            name="name"
            defaultValue={session.name}
            required
          />
        </label>
        <br/>

        <label>
          Maximum group size<br />
          <input
            type="number"
            name="maxGroupSize"
            defaultValue={session.maxGroupSize}
            required
          />
        </label>
        <br/>

        <label>
          Minimum group size<br />
          <input
            type="number"
            name="minGroupSize"
            defaultValue={session.minGroupSize}
          />
        </label>
        <br/>

        <label>
          Group rounding method<br />
          <select
            name="groupRounding"
            defaultValue={session.groupRounding}
          >
            <option value="none">No rounding</option>
            <option value="round_up">Round up</option>
            <option value="round_down">Round down</option>
          </select>
        </label>
				<br/>
				
				<label>
          Deadline for students' to submit their questionnaires<br />
          <input
            type="datetime-local"
            name="questionnaireDeadline"
            defaultValue={session.questionnaireDeadline}
          />
        </label>
				<br/>
				
				<label>
          Emails of supervisors<br />
          <textarea
            name="supervisorEmails"
            defaultValue={getSupervisorEmails()}
            required
          />
        </label>
        <br/>

        <label>
          Emails of students<br />
          <textarea
            name="studentEmails"
            defaultValue={getStudentEmails()}
            required
          />
        </label>
        <br/>

				<br/>
				<input type="submit" value="Apply changes" />
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
    <div style={{ maxWidth: 600, margin: "2rem auto", padding: "1rem" }}>
			<h2>Session Setup</h2>
			<SetupForm />

			<button
				type="button"
				onClick={() => window.location.reload()}
				style={{ marginLeft: "10px" }}
			>
				Reset
			</button>
		
			<form onSubmit={sendLoginCodeToStudents}>
				<label>
					<input
						type="checkbox"
						name="sendOnlyNew"
						defaultChecked={true}
					/>
					Send only to students who have not received a login code yet
				</label>
				<input
					type="submit"
					value="Send login codes to students"
					style={{ marginLeft: "10px" }}
				/>
			</form>

			<form onSubmit={sendLoginCodeToSupervisors}>
				<label>
					<input
						type="checkbox"
						name="sendOnlyNew"
						defaultChecked={true}
					/>
					Send only to supervisors who have not received a login code yet
				</label>
				<input
					type="submit"
					value="Send login codes to supervisors"
					style={{ marginLeft: "10px" }}
				/>
			</form>

			{message && <p style={{ marginTop: "1rem" }}>{message}</p>}
    </div>
  );
}
