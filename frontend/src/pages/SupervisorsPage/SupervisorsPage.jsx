import { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./SupervisorsPage.css";
import { useAuth } from "../../ContextProviders/AuthProvider";
import { useAppState } from "ContextProviders/AppStateContext";

export default function SupervisorsPage() {
	const { sessionId } = useParams();
	const navigate = useNavigate();
	const { supervisors, setSupervisors, isLoading: loading } = useAppState();
	const [error, setError] = useState("");
	const [showAddModal, setShowAddModal] = useState(false);
	const [showRemoveModal, setShowRemoveModal] = useState(false);
	const [email, setEmail] = useState("");
	const [supervisorToRemove, setSupervisorToRemove] = useState(null);
	const [addingSupervisor, setAddingSupervisor] = useState(false);
	const [removingSupervisor, setRemovingSupervisor] = useState(false);
	const [sendingPassword, setSendingPassword] = useState(null);
	const [editingMaxGroups, setEditingMaxGroups] = useState(null);
	const [tempMaxGroups, setTempMaxGroups] = useState({});
	
	const [message, setMessage] = useState({ text: "", type: "" }); // 'success' or 'error'

	const { user } = useAuth();
	const [isCoordinator, setIsCoordinator] = useState(false);
	useEffect(() => {
		if (user) {
			setIsCoordinator(user.role === "Coordinator");
		}
	}, [user]);

	const goBack = () => {
		navigate(-1);
	};

	useEffect(() => {
		if (message.text && message.type === "success") {
			const timer = setTimeout(() => setMessage({ text: "", type: "" }), 5000);
			return () => clearTimeout(timer);
		}
	}, [message]);

	const fetchSupervisors = useCallback(async () => {
		setError("");
		
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions/${sessionId}/supervisors`, {
				method: "GET",
				credentials: "include",
			});
			
			if (response.ok) {
				const supervisorsData = await response.json();
				setSupervisors(supervisorsData);
			} else if (response.status === 401) {
				setError("Unauthorized. Please log in.");
			} else if (response.status === 403) {
				setError("Access denied. Only coordinators can view supervisors.");
			} else {
				setError("Failed to load supervisors.");
			}
		} catch (err) {
			setError("Error loading supervisors: " + err.message);
		} 
	}, [sessionId]);

	const handleAddSupervisor = async (e) => {
		e.preventDefault();
		
		if (!email.trim()) {
			setMessage({ text: "Please enter an email address.", type: "error" });
			return;
		}

		setAddingSupervisor(true);
		setMessage({ text: "", type: "" });
		
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions/${sessionId}/supervisors`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				credentials: "include",
				body: JSON.stringify({ email: email.trim() }),
			});
			
			if (response.ok) {
				setEmail("");
				setShowAddModal(false);
				fetchSupervisors();
				setMessage({ text: "Supervisor added successfully!", type: "success" });
			} else if (response.status === 409) {
				setMessage({ text: "A supervisor with this email already exists in this session.", type: "error" });
			} else {
				const errorText = await response.text();
				setMessage({ text: "Failed to add supervisor: " + errorText, type: "error" });
			}
		} catch (err) {
			setMessage({ text: "Error adding supervisor: " + err.message, type: "error" });
		} finally {
			setAddingSupervisor(false);
		}
	};

	const handleRemoveSupervisor = async () => {
		if (!supervisorToRemove) return;

		setRemovingSupervisor(true);
		setMessage({ text: "", type: "" });
		
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions/${sessionId}/supervisors/${supervisorToRemove.id}`, {
				method: "DELETE",
				credentials: "include",
			});
			
			if (response.ok) {
				setShowRemoveModal(false);
				setSupervisorToRemove(null);
				fetchSupervisors();
				setMessage({ text: "Supervisor removed successfully!", type: "success" });
			} else {
				const errorText = await response.text();
				setMessage({ text: "Failed to remove supervisor: " + errorText, type: "error" });
			}
		} catch (err) {
			setMessage({ text: "Error removing supervisor: " + err.message, type: "error" });
		} finally {
			setRemovingSupervisor(false);
		}
	};

	const handleSendNewPassword = async (supervisor) => {
		setSendingPassword(supervisor.id);
		setMessage({ text: "", type: "" });
		
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions/${sessionId}/supervisors/${supervisor.id}/send-new-password`, {
				method: "POST",
				credentials: "include",
			});
			
			if (response.ok) {
				const successText = await response.text();
				setMessage({ text: successText, type: "success" });
			} else {
				const errorText = await response.text();
				setMessage({ text: "Failed to send new password: " + errorText, type: "error" });
			}
		} catch (err) {
			setMessage({ text: "Error sending new password: " + err.message, type: "error" });
		} finally {
			setSendingPassword(null);
		}
	};

	const handleMaxGroupsClick = (supervisor) => {
		setEditingMaxGroups(supervisor.id);
		setTempMaxGroups({ ...tempMaxGroups, [supervisor.id]: supervisor.maxGroups || 1 });
	};

	const handleMaxGroupsChange = (supervisorId, value) => {
		const numValue = parseInt(value) || 1;
		setTempMaxGroups({ ...tempMaxGroups, [supervisorId]: numValue });
	};

	const handleMaxGroupsSave = async (supervisorId) => {
		const maxGroups = tempMaxGroups[supervisorId];
		
		if (!maxGroups || maxGroups < 1 || maxGroups > 100) {
			setMessage({ text: "Max groups must be between 1 and 100", type: "error" });
			return;
		}

		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions/${sessionId}/supervisors/${supervisorId}/max-groups`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				credentials: "include",
				body: JSON.stringify({ maxGroups }),
			});
			
			if (response.ok) {
				setSupervisors(prev => prev.map(s => 
					s.id === supervisorId ? { ...s, maxGroups } : s
				));
				setEditingMaxGroups(null);
				setMessage({ text: "Max groups updated successfully!", type: "success" });
			} else {
				const errorText = await response.text();
				setMessage({ text: "Failed to update max groups: " + errorText, type: "error" });
			}
		} catch (err) {
			setMessage({ text: "Error updating max groups: " + err.message, type: "error" });
		}
	};

	const handleMaxGroupsCancel = () => {
		setEditingMaxGroups(null);
		setTempMaxGroups({});
	};

	const openRemoveModal = (supervisor) => {
		setSupervisorToRemove(supervisor);
		setShowRemoveModal(true);
	};

	const closeModals = () => {
		setShowAddModal(false);
		setShowRemoveModal(false);
		setSupervisorToRemove(null);
		setEmail("");
		setMessage({ text: "", type: "" });
	};

	if (!isCoordinator) {
		return (
			<div className="supervisors-page">
				<div className="error-message">
					Access denied. Only coordinators can manage supervisors.
				</div>
				<button onClick={goBack} className="back-button">
					← Back
				</button>
			</div>
		);
	}

	if (loading) {
		return (
			<div className="supervisors-page">
				<div className="loading">Loading supervisors...</div>
			</div>
		);
	}

	if (error) {
		return (
			<div className="supervisors-page">
				<div className="error-message">{error}</div>
				<button onClick={goBack} className="back-button">
					← Back
				</button>
			</div>
		);
	}

	return (
		<div className="supervisors-page">
			<div className="supervisors-header">
				<button onClick={goBack} className="back-button">
					← Back
				</button>
				<h2>Session Supervisors</h2>
				<button 
					className="add-supervisor-button"
					onClick={() => setShowAddModal(true)}
				>
					Add Supervisor
				</button>
			</div>

			<div className="supervisors-list">
				{supervisors.length === 0 ? (
					<div className="no-supervisors">
						No supervisors assigned to this session yet.
					</div>
				) : (
					<table className="supervisors-table">
						<thead>
							<tr>
								<th>Name</th>
								<th>Email</th>
								<th>Max Groups</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							{supervisors.map((supervisor) => (
								<tr key={supervisor.id}>
									<td>{supervisor.name}</td>
									<td>{supervisor.email}</td>
									<td>
										{editingMaxGroups === supervisor.id ? (
											<div className="max-groups-edit">
												<input
													type="number"
													min="1"
													max="100"
													value={tempMaxGroups[supervisor.id] || 1}
													onChange={(e) => handleMaxGroupsChange(supervisor.id, e.target.value)}
													className="max-groups-input"
												/>
												<button
													className="save-max-groups-button"
													onClick={() => handleMaxGroupsSave(supervisor.id)}
												>
													✓
												</button>
												<button
													className="cancel-max-groups-button"
													onClick={handleMaxGroupsCancel}
												>
													✗
												</button>
											</div>
										) : (
											<span
												className="max-groups-display"
												onClick={() => handleMaxGroupsClick(supervisor)}
											>
												{supervisor.maxGroups || 1}
											</span>
										)}
									</td>
									<td>
										<button
											className="send-password-button"
											onClick={() => handleSendNewPassword(supervisor)}
											disabled={sendingPassword === supervisor.id}
										>
											{sendingPassword === supervisor.id ? "Sending..." : "Send New Password"}
										</button>
										<button
											className="remove-button"
											onClick={() => openRemoveModal(supervisor)}
										>
											Remove
										</button>
									</td>
								</tr>
							))}
						</tbody>
					</table>
				)}
			</div>

			{/* Add Supervisor Modal */}
			{showAddModal && (
				<div className="modal-overlay" onClick={closeModals}>
					<div className="modal" onClick={(e) => e.stopPropagation()}>
						<div className="modal-header">
							<h3>Add Supervisor</h3>
							<button className="close-button" onClick={closeModals}>×</button>
						</div>
						<form onSubmit={handleAddSupervisor}>
							<div className="modal-body">
								<label htmlFor="email">Email Address:</label>
								<input
									type="email"
									id="email"
									value={email}
									onChange={(e) => setEmail(e.target.value)}
									placeholder="Enter supervisor's email"
									required
									disabled={addingSupervisor}
								/>
							</div>
							<div className="modal-footer">
								<button 
									type="button" 
									className="cancel-button" 
									onClick={closeModals}
									disabled={addingSupervisor}
								>
									Cancel
								</button>
								<button 
									type="submit" 
									className="confirm-button"
									disabled={addingSupervisor}
								>
									{addingSupervisor ? "Adding..." : "Add Supervisor"}
								</button>
							</div>
						</form>
					</div>
				</div>
			)}

			{/* Remove Supervisor Modal */}
			{showRemoveModal && supervisorToRemove && (
				<div className="modal-overlay" onClick={closeModals}>
					<div className="modal" onClick={(e) => e.stopPropagation()}>
						<div className="modal-header">
							<h3>Remove Supervisor</h3>
							<button className="close-button" onClick={closeModals}>×</button>
						</div>
						<div className="modal-body">
							<p>
								Are you sure you want to remove <strong>{supervisorToRemove.name}</strong> 
								({supervisorToRemove.email}) from this session?
							</p>
							<p className="warning">This action cannot be undone.</p>
						</div>
						<div className="modal-footer">
							<button 
								type="button" 
								className="cancel-button" 
								onClick={closeModals}
								disabled={removingSupervisor}
							>
								Cancel
							</button>
							<button 
								type="button" 
								className="danger-button"
								onClick={handleRemoveSupervisor}
								disabled={removingSupervisor}
							>
								{removingSupervisor ? "Removing..." : "Remove"}
							</button>
						</div>
					</div>
				</div>
			)}

			{/* Success and Error Messages */}
			{message.text && (
				<div className={message.type === "success" ? "success-message" : "error-message"} style={{ marginTop: '20px' }}>
					{message.text}
				</div>
			)}
		</div>
	);
}