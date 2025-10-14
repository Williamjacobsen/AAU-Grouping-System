import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthCheck } from "../User/UseAuthCheck";
import useSessionManager from "./useSessionManager";
import "./Sessions.css";

export default function Sessions() {
	const [newSessionName, setNewSessionName] = useState("");
	const navigate = useNavigate();
	
	const {
		sessions,
		loading,
		error,
		createSession,
		deleteSession
	} = useSessionManager();

	const { user } = useAuthCheck();
 	if (!user) return null; 
     

	const handleCreateSession = async (e) => {
		e.preventDefault();
		const success = await createSession(newSessionName);
		if (success) {
			setNewSessionName("");
		}
	};

	const handleDeleteSession = async (sessionId) => {
		if (!window.confirm("Are you sure you want to delete this session?")) {
			return;
		}
		await deleteSession(sessionId);
	};

	const openSession = (sessionId) => {
		navigate(`/status/${sessionId}`);
	};

	const editSetup = (sessionId) => {
		navigate(`/sessions/${sessionId}/setup`);
	};

	return (
		<div className="session-page">
			<div className="session-page-header">
				<h1>Session Management</h1>
				<p>Manage your sessions</p>
			</div>

			{error && <div className="error-message">{error}</div>}

			{/* Create New Session Form */}
			<div className="create-session-section">
				<h2>Create New Session</h2>
				<form onSubmit={handleCreateSession} className="create-session-form">
					<div className="form-group">
						<input
							type="text"
							placeholder="Enter session name"
							value={newSessionName}
							onChange={(e) => setNewSessionName(e.target.value)}
							maxLength={100}
							disabled={loading}
							className="session-input"
						/>
						<button type="submit" disabled={loading || !newSessionName.trim()} className="create-button">
							{loading ? "Creating..." : "Create Session"}
						</button>
					</div>
				</form>
			</div>

			{/* Sessions List */}
			<div className="sessions-section">
				<h2>Your Sessions</h2>
				{loading && sessions.length === 0 ? (
					<div className="loading">Loading sessions...</div>
				) : sessions.length === 0 ? (
					<div className="no-sessions">
						<p>No sessions found. Create your first session above.</p>
					</div>
				) : (
					<div className="sessions-grid">
						{sessions.map((session) => (
							<div key={session.id} className="session-card">
								<div className="session-header">
									<h3 className="session-name">
										{session.name || `Session ${session.id}`}
									</h3>
								</div>

								<div className="session-actions">
									<button
										onClick={() => openSession(session.id)}
										className="open-button"
										disabled={loading}
									>
										Open Session
									</button>
									<button
										onClick={() => editSetup(session.id)}
										className="edit-button"
										disabled={loading}
									>
										Edit Setup
									</button>
									<button
										onClick={() => handleDeleteSession(session.id)}
										className="delete-button"
										disabled={loading}
									>
										Delete
									</button>
								</div>
							</div>
						))}
					</div>
				)}
			</div>
		</div>
	);
}