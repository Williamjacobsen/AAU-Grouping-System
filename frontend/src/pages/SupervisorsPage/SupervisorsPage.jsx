import React, { useState, useEffect, useCallback } from "react";
import { useParams } from "react-router-dom";
import "./SupervisorsPage.css";

export default function SupervisorsPage() {
	const { sessionId } = useParams();
	const [supervisors, setSupervisors] = useState([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [isCoordinator, setIsCoordinator] = useState(false);
	const [showAddModal, setShowAddModal] = useState(false);
	const [showRemoveModal, setShowRemoveModal] = useState(false);
	const [email, setEmail] = useState("");
	const [supervisorToRemove, setSupervisorToRemove] = useState(null);
	const [addingSupervisor, setAddingSupervisor] = useState(false);
	const [removingSupervisor, setRemovingSupervisor] = useState(false);
	const [sendingPassword, setSendingPassword] = useState(null);

	const fetchUserRole = async () => {
		try {
			const response = await fetch("http://localhost:8080/auth/getUser", {
				method: "GET",
				credentials: "include",
			});
			
			if (response.ok) {
				const userData = await response.json();
				setIsCoordinator(userData.role === "Coordinator");
			}
		} catch (err) {
			console.error("Error fetching user role:", err);
		}
	};

	const fetchSupervisors = useCallback(async () => {
		setLoading(true);
		setError("");
		
		try {
			const response = await fetch(`http://localhost:8080/sessions/${sessionId}/supervisors`, {
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
		} finally {
			setLoading(false);
		}
	}, [sessionId]);

	useEffect(() => {
		fetchUserRole();
	}, []);

	useEffect(() => {
		if (sessionId && isCoordinator) {
			fetchSupervisors();
		}
	}, [sessionId, isCoordinator, fetchSupervisors]);

	const handleAddSupervisor = async (e) => {
		e.preventDefault();
		
		if (!email.trim()) {
			alert("Please enter an email address.");
			return;
		}

		setAddingSupervisor(true);
		
		try {
			const response = await fetch(`http://localhost:8080/sessions/${sessionId}/supervisors`, {
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
				alert("Supervisor added successfully!");
			} else if (response.status === 409) {
				alert("A supervisor with this email already exists in this session.");
			} else {
				const errorText = await response.text();
				alert("Failed to add supervisor: " + errorText);
			}
		} catch (err) {
			alert("Error adding supervisor: " + err.message);
		} finally {
			setAddingSupervisor(false);
		}
	};

	const handleRemoveSupervisor = async () => {
		if (!supervisorToRemove) return;

		setRemovingSupervisor(true);
		
		try {
			const response = await fetch(`http://localhost:8080/sessions/${sessionId}/supervisors/${supervisorToRemove.id}`, {
				method: "DELETE",
				credentials: "include",
			});
			
			if (response.ok) {
				setShowRemoveModal(false);
				setSupervisorToRemove(null);
				fetchSupervisors();
				alert("Supervisor removed successfully!");
			} else {
				const errorText = await response.text();
				alert("Failed to remove supervisor: " + errorText);
			}
		} catch (err) {
			alert("Error removing supervisor: " + err.message);
		} finally {
			setRemovingSupervisor(false);
		}
	};

	const handleSendNewPassword = async (supervisor) => {
		setSendingPassword(supervisor.id);
		
		try {
			const response = await fetch(`http://localhost:8080/sessions/${sessionId}/supervisors/${supervisor.id}/send-new-password`, {
				method: "POST",
				credentials: "include",
			});
			
			if (response.ok) {
				const successMessage = await response.text();
				alert(successMessage);
			} else {
				const errorText = await response.text();
				alert("Failed to send new password: " + errorText);
			}
		} catch (err) {
			alert("Error sending new password: " + err.message);
		} finally {
			setSendingPassword(null);
		}
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
	};

	if (!isCoordinator) {
		return (
			<div className="supervisors-page">
				<div className="error">
					Access denied. Only coordinators can manage supervisors.
				</div>
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
				<div className="error">{error}</div>
			</div>
		);
	}

	return (
		<div className="supervisors-page">
			<div className="supervisors-header">
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
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
							{supervisors.map((supervisor) => (
								<tr key={supervisor.id}>
									<td>{supervisor.name}</td>
									<td>{supervisor.email}</td>
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
		</div>
	);
}