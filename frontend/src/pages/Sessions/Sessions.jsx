import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthProvider";
import useSessionManager from "./hooks/useSessionManager";
import "./Sessions.css";

export default function Sessions() {
	const [newSessionName, setNewSessionName] = useState("");
	const [showDeleteModal, setShowDeleteModal] = useState(false);
	const [sessionToDelete, setSessionToDelete] = useState(null);
	const [deletingSession, setDeletingSession] = useState(false);
	const navigate = useNavigate();

	const {
		sessions,
		loading,
		error,
		createSession,
		deleteSession
	} = useSessionManager();

	const { user, isLoading: isLoadingUser } = useAuth();

	if (isLoadingUser) return <>Checking authentication...</>;
	if (!user) return navigate("/sign-in");

	const handleCreateSession = async (e) => {
		e.preventDefault();
		const success = await createSession(newSessionName);
		if (success) {
			setNewSessionName("");
		}
	};

	const handleDeleteSession = async (sessionId) => {
		setDeletingSession(true);

		try {
			await deleteSession(sessionId);
			setShowDeleteModal(false);
			setSessionToDelete(null);
		} catch (err) {
			console.error("Error deleting session:", err);
		} finally {
			setDeletingSession(false);
		}
	};

	const openDeleteModal = (session) => {
		setSessionToDelete(session);
		setShowDeleteModal(true);
	};

	const closeDeleteModal = () => {
		setShowDeleteModal(false);
		setSessionToDelete(null);
	};

	const openSession = (sessionId) => {
		navigate(`/session/${sessionId}/students`);
	};

	const editSetup = (sessionId) => {
		navigate(`/session/${sessionId}/setup`);
	};

	const manageSupervisors = (sessionId) => {
		navigate(`/session/${sessionId}/supervisors`);
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
						{sessions
							.filter(session => session != null)
							.map((session) => (
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
											onClick={() => manageSupervisors(session.id)}
											className="supervisors-button"
											disabled={loading}
										>
											Manage Supervisors
										</button>
										<button
											onClick={() => openDeleteModal(session)}
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

			{/* Delete Session Modal */}
			{showDeleteModal && sessionToDelete && (
				<div className="modal-overlay" onClick={closeDeleteModal}>
					<div className="modal" onClick={(e) => e.stopPropagation()}>
						<div className="modal-header">
							<h3>Delete Session</h3>
							<button className="close-button" onClick={closeDeleteModal}>×</button>
						</div>
						<div className="modal-body">
							<p>
								Are you sure you want to delete <strong>{sessionToDelete.name || `Session ${sessionToDelete.id}`}</strong>?
							</p>
							<p className="warning">This action cannot be undone.</p>
						</div>
						<div className="modal-footer">
							<button
								type="button"
								className="cancel-button"
								onClick={closeDeleteModal}
								disabled={deletingSession}
							>
								Cancel
							</button>
							<button
								type="button"
								className="danger-button"
								onClick={() => handleDeleteSession(sessionToDelete.id)}
								disabled={deletingSession}
							>
								{deletingSession ? "Deleting..." : "Delete"}
							</button>
						</div>
					</div>
				</div>
			)}
		</div>
	);
}