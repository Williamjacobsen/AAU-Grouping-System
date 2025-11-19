import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useStudentData from "./useStudentData";
import useGetSessionProjects from "../../hooks/useGetSessionProjects";
import "./StudentPage.css";

export default function StudentPage() {
	const { sessionId, studentId } = useParams();
	const navigate = useNavigate();
	const { student, loading, error, isCoordinator, removeStudent, resetPassword } = useStudentData(sessionId, studentId);
	const { projects } = useGetSessionProjects(sessionId);
	const [showConfirmDialog, setShowConfirmDialog] = useState(false);
	const [operationState, setOperationState] = useState({ type: "", loading: false, message: "", messageType: "" });

	const goBack = () => {
		navigate(-1);
	};

	const handleRemoveClick = () => {
		setShowConfirmDialog(true);
		setOperationState({ type: "remove", loading: false, message: "", messageType: "" });
	};

	const handleConfirmRemove = async () => {
		setOperationState({ type: "remove", loading: true, message: "", messageType: "" });
		
		const result = await removeStudent();
		
		if (result.success) {
			navigate(`/session/${sessionId}`);
		} else {
			setOperationState({ type: "remove", loading: false, message: result.message, messageType: "error" });
		}
		
		setShowConfirmDialog(false);
	};

	const handleCancelRemove = () => {
		setShowConfirmDialog(false);
		setOperationState({ type: "", loading: false, message: "", messageType: "" });
	};

	const handleResetPassword = async () => {
		setOperationState({ type: "reset", loading: true, message: "", messageType: "" });
		
		const result = await resetPassword();
		
		if (result.success) {
			setOperationState({ type: "reset", loading: false, message: result.message, messageType: "success" });
		} else {
			setOperationState({ type: "reset", loading: false, message: result.message, messageType: "error" });
		}
	};

	const getProjectName = (projectId) => {
		if (!projects || !projectId) return "";
		const project = projects.find(p => p.id === projectId);
		return project ? project.name : projectId;
	};

	if (loading) {
		return (
			<div className="student-page">
				<div className="loading">Loading student information...</div>
			</div>
		);
	}

	if (error) {
		return (
			<div className="student-page">
				<div className="error-message">{error}</div>
				<button onClick={goBack} className="back-button">
					Go Back
				</button>
			</div>
		);
	}

	if (!student) {
		return (
			<div className="student-page">
				<div className="error-message">Student not found.</div>
				<button onClick={goBack} className="back-button">
					Go Back
				</button>
			</div>
		);
	}

	return (
		<div className="student-page">
			<div className="student-page-header">
				<button onClick={goBack} className="back-button">
					← Back
				</button>
				<h1>Student</h1>
				{isCoordinator && (
					<div className="header-buttons">
						<button 
							onClick={handleResetPassword} 
							className="reset-password-button"
							disabled={operationState.type === "reset" && operationState.loading}
						>
							{operationState.type === "reset" && operationState.loading ? "Sending..." : "Send New Password"}
						</button>
						<button onClick={handleRemoveClick} className="remove-button">
							Remove Student
						</button>
					</div>
				)}
			</div>

			<div className="student-details-container">
				{/* Basic Information */}
				<div className="student-section">
					<h2>Basic Information</h2>
					<div className="info-grid">
						<div className="info-item">
							<label>Name:</label>
							<span>{student.name}</span>
						</div>
						{isCoordinator && student.email && (
							<div className="info-item">
								<label>Email:</label>
								<span>{student.email}</span>
							</div>
						)}
					</div>
				</div>

				{/* Group Information */}
				<div className="student-section">
					<h2>Group Status</h2>
					{student.group && student.group.hasGroup ? (
						<div className="group-info">
							<div className="info-item">
								<label>Project:</label>
								<span>{student.group.project}</span>
							</div>
							<div className="info-item">
								<label>Group Size:</label>
								<span>{student.group.groupSize}/{student.group.maxSize}</span>
							</div>
						</div>
					) : (
						<div className="group-info">
							<p>This student is not in a group.</p>
						</div>
					)}
				</div>

				{/* Questionnaire Responses */}
				{student.questionnaire && (
					<div className="student-section">
						<h2>Questionnaire Responses</h2>
						<div className="questionnaire-grid">
							<div className="info-item">
								<label>Project Priority 1:</label>
								<span>{getProjectName(student.questionnaire.desiredProjectId1)}</span>
							</div>
							<div className="info-item">
								<label>Project Priority 2:</label>
								<span>{getProjectName(student.questionnaire.desiredProjectId2)}</span>
							</div>
							<div className="info-item">
								<label>Project Priority 3:</label>
								<span>{getProjectName(student.questionnaire.desiredProjectId3)}</span>
							</div>
							<div className="info-item">
								<label>Desired Group Size:</label>
								<span>
									{student.questionnaire.desiredGroupSizeMin === -1 && student.questionnaire.desiredGroupSizeMax === -1
										? "No preference"
										: student.questionnaire.desiredGroupSizeMin === student.questionnaire.desiredGroupSizeMax && student.questionnaire.desiredGroupSizeMin !== -1
										? `${student.questionnaire.desiredGroupSizeMin} members`
										: `${student.questionnaire.desiredGroupSizeMin === -1 ? 'No min' : student.questionnaire.desiredGroupSizeMin} - ${student.questionnaire.desiredGroupSizeMax === -1 ? 'No max' : student.questionnaire.desiredGroupSizeMax} members`
									}
								</span>
							</div>
							<div className="info-item">
								<label>Working Environment:</label>
								<span>
									{student.questionnaire.desiredWorkLocation === 'NoPreference' && student.questionnaire.desiredWorkStyle === 'NoPreference'
										? "No preference"
										: `${student.questionnaire.desiredWorkLocation === 'Located' ? 'Located together' : student.questionnaire.desiredWorkLocation === 'Remote' ? 'Remote' : 'No preference'}, ${student.questionnaire.desiredWorkStyle === 'Solo' ? 'Work independently' : student.questionnaire.desiredWorkStyle === 'Together' ? 'Work together' : 'No preference'}`
									}
								</span>
							</div>
							<div className="info-item full-width">
								<label>Personal Skills:</label>
								<span>
									{student.questionnaire.personalSkills && Array.isArray(student.questionnaire.personalSkills) 
										? student.questionnaire.personalSkills.join(", ")
										: student.questionnaire.personalSkills || "Not specified"
									}
								</span>
							</div>
							<div className="info-item full-width">
								<label>Academic Interests:</label>
								<span>
									{student.questionnaire.academicInterests && Array.isArray(student.questionnaire.academicInterests) 
										? student.questionnaire.academicInterests.join(", ")
										: student.questionnaire.academicInterests || "Not specified"
									}
								</span>
							</div>
							{isCoordinator && student.questionnaire.specialNeeds && (
								<div className="info-item full-width">
									<label>Special Needs:</label>
									<span>{student.questionnaire.specialNeeds}</span>
								</div>
							)}
							{student.questionnaire.comments && (
								<div className="info-item full-width">
									<label>Additional Comments:</label>
									<span>{student.questionnaire.comments}</span>
								</div>
							)}
						</div>
					</div>
				)}
			</div>

			{operationState.message && (
				<div className={operationState.messageType === "success" ? "success-message" : "error-message"} style={{ marginTop: '20px' }}>
					{operationState.message}
				</div>
			)}

			{showConfirmDialog && (
				<div className="modal-overlay">
					<div className="modal-content">
						<h3>Remove Student</h3>
						<p>
							Are you sure you want to remove <strong>{student.name}</strong> from this session?
						</p>
						<p className="warning-text">
							This action cannot be undone.
						</p>
						<div className="modal-buttons">
							<button 
								onClick={handleCancelRemove} 
								className="cancel-button"
								disabled={operationState.type === "remove" && operationState.loading}
							>
								Cancel
							</button>
							<button 
								onClick={handleConfirmRemove} 
								className="confirm-remove-button"
								disabled={operationState.type === "remove" && operationState.loading}
							>
								{operationState.type === "remove" && operationState.loading ? "Removing..." : "Remove Student"}
							</button>
						</div>
					</div>
				</div>
			)}
		</div>
	);
}
