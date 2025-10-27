import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useStudentData from "./useStudentData";
import "./StudentPage.css";

export default function StudentPage() {
	const { sessionId, studentId } = useParams();
	const navigate = useNavigate();
	const { student, loading, error, isCoordinator, removeStudent } = useStudentData(sessionId, studentId);
	const [showConfirmDialog, setShowConfirmDialog] = useState(false);
	const [isRemoving, setIsRemoving] = useState(false);
	const [removeError, setRemoveError] = useState("");

	const goBack = () => {
		navigate(-1);
	};

	const handleRemoveClick = () => {
		setShowConfirmDialog(true);
		setRemoveError("");
	};

	const handleConfirmRemove = async () => {
		setIsRemoving(true);
		setRemoveError("");
		
		const result = await removeStudent();
		
		if (result.success) {
			navigate(`/session/${sessionId}`);
		} else {
			setRemoveError(result.message);
			setIsRemoving(false);
		}
		
		setShowConfirmDialog(false);
	};

	const handleCancelRemove = () => {
		setShowConfirmDialog(false);
		setRemoveError("");
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
				<h1>Student Details</h1>
				{isCoordinator && (
					<button onClick={handleRemoveClick} className="remove-button">
						Remove Student
					</button>
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
								<label>Group ID:</label>
								<span>{student.group.id}</span>
							</div>
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
								<span>{student.questionnaire.projectPriority1}</span>
							</div>
							<div className="info-item">
								<label>Project Priority 2:</label>
								<span>{student.questionnaire.projectPriority2}</span>
							</div>
							{student.questionnaire.previousSessionTeammates !== undefined && (
								<div className="info-item">
									<label>Previous Session Teammates:</label>
									<span>{student.questionnaire.previousSessionTeammates}</span>
								</div>
							)}
							{student.questionnaire.desiredGroupMembers !== undefined && (
								<div className="info-item">
									<label>Desired Group Members:</label>
									<span>{student.questionnaire.desiredGroupMembers}</span>
								</div>
							)}
							<div className="info-item">
								<label>Desired Group Size:</label>
								<span>{student.questionnaire.desiredGroupSize} members</span>
							</div>
							<div className="info-item">
								<label>Working Environment:</label>
								<span>{student.questionnaire.workingEnvironment}</span>
							</div>
							{student.questionnaire.personalSkills && (
								<div className="info-item full-width">
									<label>Personal Skills:</label>
									<span>
										{Array.isArray(student.questionnaire.personalSkills) 
											? student.questionnaire.personalSkills.join(", ")
											: student.questionnaire.personalSkills
										}
									</span>
								</div>
							)}
							{student.questionnaire.academicInterests && (
								<div className="info-item full-width">
									<label>Academic Interests:</label>
									<span>
										{Array.isArray(student.questionnaire.academicInterests) 
											? student.questionnaire.academicInterests.join(", ")
											: student.questionnaire.academicInterests
										}
									</span>
								</div>
							)}
							{isCoordinator && student.questionnaire.specialNeeds && (
								<div className="info-item full-width">
									<label>Special Needs:</label>
									<span>{student.questionnaire.specialNeeds}</span>
								</div>
							)}
							{student.questionnaire.otherComments && (
								<div className="info-item full-width">
									<label>Additional Comments:</label>
									<span>{student.questionnaire.otherComments}</span>
								</div>
							)}
						</div>
					</div>
				)}
			</div>

			{/* Error message for remove operation */}
			{removeError && (
				<div className="error-message" style={{ marginTop: '20px' }}>
					{removeError}
				</div>
			)}

			{/* Confirmation Dialog */}
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
								disabled={isRemoving}
							>
								Cancel
							</button>
							<button 
								onClick={handleConfirmRemove} 
								className="confirm-remove-button"
								disabled={isRemoving}
							>
								{isRemoving ? "Removing..." : "Remove Student"}
							</button>
						</div>
					</div>
				</div>
			)}
		</div>
	);
}
