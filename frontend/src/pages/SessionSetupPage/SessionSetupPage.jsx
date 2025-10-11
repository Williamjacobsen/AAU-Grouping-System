import React, { useState } from 'react';

// Code does not call any backend.
export default function SessionSetupPage() {
	// Session information
	const [sessionName, setSessionName] = useState('');
	const [description, setDescription] = useState('');

		// Questions / mandatory session setup fields
		// Replace the single 'mandatoryQuestion' with explicit required fields
		const [coordinatorName, setCoordinatorName] = useState('');
		const [numberOfParticipants, setNumberOfParticipants] = useState('');
		const [maxGroupSize, setMaxGroupSize] = useState('');
		const [minGroupSize, setMinGroupSize] = useState('');
		const [groupRounding, setGroupRounding] = useState('none');
		// Comma- or newline-separated emails for students
		const [studentEmails, setStudentEmails] = useState('');
		// Supervisors: expected simple structured text per-line (email,maxGroups)
		const [supervisorDetails, setSupervisorDetails] = useState('');
		const [questionnaireDeadline, setQuestionnaireDeadline] = useState('');
		const [initialProjects, setInitialProjects] = useState('');
		// Keep optional question field
		const [optionalQuestion, setOptionalQuestion] = useState('');

	// Checkbox for sending login codes only to newly added participants
	const [sendOnlyNew, setSendOnlyNew] = useState(true);

	// Handle saving the session setup
	const handleSave = (e) => {
		e && e.preventDefault();
		// Build payload representing current setup
			const payload = {
				sessionName: sessionName.trim(),
				description: description.trim(),
				mandatory: {
					coordinatorName: coordinatorName.trim(),
					numberOfParticipants: numberOfParticipants ? Number(numberOfParticipants) : null,
					maxGroupSize: maxGroupSize ? Number(maxGroupSize) : null,
					minGroupSize: minGroupSize ? Number(minGroupSize) : null,
					groupRounding: groupRounding,
					studentEmails: studentEmails.split(/[,\n]+/).map(s => s.trim()).filter(Boolean),
					supervisorDetails: supervisorDetails
						.split(/[,\n]+/)
						.map(s => s.trim())
						.filter(Boolean),
					questionnaireDeadline: questionnaireDeadline || null,
					initialProjects: initialProjects
						.split(/[,\n]+/)
						.map(s => s.trim())
						.filter(Boolean),
				},
				optional: optionalQuestion.trim() || null,
				meta: {
					createdAt: new Date().toISOString(),
				},
			};

		console.log('Saving session setup:', payload);
		// Basic confirmation to the user
		alert('Session saved');
	};

	// Handle sending login codes. Behavior depends on the checkbox.
	const handleSendCodes = () => {
		if (!sessionName.trim()) {
			alert('Please enter a session name before sending codes.');
			return;
		}

		if (sendOnlyNew) {
			console.log(`Sending login codes only to newly added participants for session '${sessionName}'.`);
			alert('Login codes sent to newly added participants');
		} else {
			console.log(`Sending login codes to ALL participants for session '${sessionName}'.`);
			alert('Login codes sent to all participants (simulated)');
		}
	};

	// Reset all form fields
	const handleReset = () => {
		setSessionName('');
		setDescription('');
		setCoordinatorName('');
		setNumberOfParticipants('');
		setMaxGroupSize('');
		setMinGroupSize('');
		setGroupRounding('none');
		setStudentEmails('');
		setSupervisorDetails('');
		setQuestionnaireDeadline('');
		setInitialProjects('');
		setOptionalQuestion('');
		setSendOnlyNew(true);
	};

	return (
		<div>
			{/* Session Information Section */}
			<h2>Session setup</h2>
			<form onSubmit={handleSave}>
				<div>
					<label>
						Session Name (required)
						<br />
						<input
							type="text"
							value={sessionName}
							onChange={(e) => setSessionName(e.target.value)}
							required
							placeholder="e.g. Fall 2025 P3"
						/>
					</label>
				</div>
				<div>
								<label>
									Coordinator name (required)
									<br />
									<input
										type="text"
										value={coordinatorName}
										onChange={(e) => setCoordinatorName(e.target.value)}
										required
										placeholder="Name of the coordinator"
									/>
								</label>
							</div>

						{/* Mandatory session fields (replaces the single generic mandatory question) */}
						<fieldset>
							<legend>Mandatory questions</legend>

							<div>
								<label>
									Number of students (required)
									<br />
									<input
										type="number"
										min={1}
										value={numberOfParticipants}
										onChange={(e) => setNumberOfParticipants(e.target.value)}
										required
										placeholder="e.g. 120"
									/>
								</label>
							</div>

							<div>
								<label>
									Max group size (required)
									<br />
									<input
										type="number"
										min={1}
										value={maxGroupSize}
										onChange={(e) => setMaxGroupSize(e.target.value)}
										required
										placeholder="e.g. 4"
									/>
								</label>
							</div>

							<div>
								<label>
									Min group size (optional)
									<br />
									<input
										type="number"
										min={1}
										value={minGroupSize}
										onChange={(e) => setMinGroupSize(e.target.value)}
										placeholder="e.g. 2"
									/>
								</label>
							</div>

							<div>
								<label>
									Group size rounding
									<br />
									<select value={groupRounding} onChange={(e) => setGroupRounding(e.target.value)}>
										<option value="none">No rounding</option>
										<option value="round_up">Round up</option>
										<option value="round_down">Round down</option>
									</select>
								</label>
							</div>

							<div>
								<label>
									Emails of students
									<br />
									<textarea
										value={studentEmails}
										onChange={(e) => setStudentEmails(e.target.value)}
										rows={5}
										required
										placeholder="student1@student.aau.dk, student2@student.aau.dk"
									/>
								</label>
							</div>

							<div>
								<label>
									Supervisor details (email, maxGroups)
									<br />
									<textarea
										value={supervisorDetails}
										onChange={(e) => setSupervisorDetails(e.target.value)}
										rows={4}
										required
										placeholder={"super1@example.com,2\nsuper2@example.com,1"}
									/>
								</label>
							</div>

							<div>
								<label>
									Student questionnaire submission deadline (optional)
									<br />
									<input
										type="datetime-local"
										value={questionnaireDeadline}
										onChange={(e) => setQuestionnaireDeadline(e.target.value)}
									/>
								</label>
							</div>

							<div>
								<label>
									Initial projects (optional)
									<br />
									<textarea
										value={initialProjects}
										onChange={(e) => setInitialProjects(e.target.value)}
										rows={3}
										placeholder="Project A, Project B"
									/>
								</label>
							</div>

						</fieldset>

						{/* Optional question still available */}
						<div>
							<label>
								Optional Question
								<br />
								<input
									type="text"
									value={optionalQuestion}
									onChange={(e) => setOptionalQuestion(e.target.value)}
									placeholder="Anything else?"
								/>
							</label>
						</div>

				{/* Save Changes */}
				<div style={{ marginTop: 12 }}>
					<button type="submit">Save Changes</button>
				</div>
			</form>

			{/* Send Login Codes */}
			<div style={{ marginTop: 18 }}>
				<label>
					<input
						type="checkbox"
						checked={sendOnlyNew}
						onChange={(e) => setSendOnlyNew(e.target.checked)}
					/>
					{' '}Send only to newly added participants
				</label>

				<div style={{ marginTop: 8 }}>
					<button type="button" onClick={handleSendCodes}>Send Login Codes</button>
				</div>
			</div>

			{/* Reset button */}
			<div style={{ marginTop: 18 }}>
				<button type="button" onClick={handleReset}>Reset</button>
			</div>
		</div>
	);
}

