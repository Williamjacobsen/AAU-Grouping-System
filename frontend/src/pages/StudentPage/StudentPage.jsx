import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./StudentPage.css";

import { useGetStudent } from "../../hooks/useGetStudent";
import { useAuth } from "../../ContextProviders/AuthProvider";
import { useAppState } from "../../ContextProviders/AppStateContext";
import { fetchWithDefaultErrorHandling } from "../../utils/fetchHelpers";

export default function StudentPage() {

	const { studentId, sessionId } = useParams();
	const navigate = useNavigate();

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingApp, projects, groups, session, setStudents } = useAppState();
	const { isLoading: isLoadingStudent, student } = useGetStudent(studentId);

	// Confirmation pop up
	const [isConfirmationPopUpVisible, setIsConfirmationPopUpVisible] = useState(false);
	const [isConfirmationPopUpOperationLoading, setIsConfirmationPopUpOperationLoading] = useState(false);
	const [confirmationPopUpOperation, setConfirmationPopUpOperation] = useState(null);
	const [confirmationPopUpTitle, setConfirmationPopUpTitle] = useState("");
	const [confirmationPopUpDescription, setConfirmationPopUpDescription] = useState("");

	// Loading
	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return <div className="access-denied-message">Access denied: Not logged in.</div>;
	if (isLoadingApp) return <div className="loading-message">Loading information...</div>;
	if (isLoadingStudent) return <div className="loading-message">Loading student...</div>;

	function getProject(projectId) {
		return projects.find(project => project.id === projectId);
	};

	function getStudentGroup() {
		return groups.find(group => group.id === student.groupId);
	};

	function goBack() {
		navigate(-1);
	};

	function ConfirmationPopUp() {
		return (
			<div className="modal-overlay">
				<div className="modal-content">
					<h3>{confirmationPopUpTitle}</h3>
					<p>
						{confirmationPopUpDescription}
					</p>
					<p className="warning-text">
						This action cannot be undone.
					</p>
					<div className="modal-buttons">
						<button
							onClick={() => setIsConfirmationPopUpVisible(false)}
							disabled={isConfirmationPopUpOperationLoading}
							className="cancel-button"
						>
							Cancel
						</button>
						<button
							onClick={() => confirmationPopUpOperation()}
							disabled={isConfirmationPopUpOperationLoading}
							className="confirm-remove-button"
						>
							Confirm
						</button>
					</div>
				</div>
			</div>
		);
	}

	function showConfirmationPopUp(operation, title, description) {
		setConfirmationPopUpOperation(() => operation);
		setConfirmationPopUpTitle(title);
		setConfirmationPopUpDescription(description);
		setIsConfirmationPopUpVisible(true);
	}

	async function removeStudent() {
		try {
			setIsConfirmationPopUpOperationLoading(true);

			await fetchWithDefaultErrorHandling(
				`/api/student/${studentId}`,
				{
					method: "DELETE",
					credentials: "include",
				}
			);

			alert("Successfully deleted student");

			// Update frontend state
			setStudents(prev =>
				prev.filter(student => student.id !== studentId)
			);

			goBack(); // Navigate away from the student page
		} catch (error) {
			alert(error);
		} finally {
			setIsConfirmationPopUpOperationLoading(false);
			setIsConfirmationPopUpVisible(false);
		}
	}

	async function resetPassword() {
		try {
			setIsConfirmationPopUpOperationLoading(true);

			await fetchWithDefaultErrorHandling(
				`/api/student/${studentId}/reset-password`,
				{
					method: "POST",
					credentials: "include",
				}
			);

			alert("Successfully reset student password and emailed them a new one");
		} catch (error) {
			alert(error);
		} finally {
			setIsConfirmationPopUpOperationLoading(false);
			setIsConfirmationPopUpVisible(false);
		}
	}

	return (

		<div className="student-page">
			{isConfirmationPopUpVisible &&
				<ConfirmationPopUp />
			}
			<div className="student-page-header">
				<button onClick={goBack} className="back-button">
					← Back
				</button>
				<h1>Student</h1>

				{user.role === "Coordinator" && (
					<div className="header-buttons">
						<button
							onClick={() => showConfirmationPopUp(resetPassword, "Reset Student Password", "Reset and email a new password to the student.")}
							className="reset-password-button"
						>
							Send new password
						</button>
						<button
							onClick={() => showConfirmationPopUp(removeStudent, "Remove Student", "Delete the student from the session.")}
							className="remove-button">
							Delete Student
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
						<div className="info-item">
							<label>Email:</label>
							<span>{student.email}</span>
						</div>
					</div>
				</div>

				{/* Group Information */}
				<div className="student-section">
					<h2>Group Status</h2>
					{getStudentGroup()
						? (
							<div className="group-info">
								<div className="info-item">
									<label>Group name:</label>
									<span>{getStudentGroup()?.name ?? ""}</span>
								</div>
								<div className="info-item">
									<label>Group project priority 1:</label>
									<span>{getProject(getStudentGroup().desiredProjectId1)?.name ?? ""}</span>
								</div>
								<div className="info-item">
									<label>Group project priority 2:</label>
									<span>{getProject(getStudentGroup().desiredProjectId2)?.name ?? ""}</span>
								</div>
								<div className="info-item">
									<label>Group project priority 3:</label>
									<span>{getProject(getStudentGroup().desiredProjectId3)?.name ?? ""}</span>
								</div>
								<div className="info-item">
									<label>Group size:</label>
									<span>{getStudentGroup().studentIds.length}/{session.maxGroupSize}</span>
								</div>
							</div>
						)
						: (
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
								<label>Project priority 1:</label>
								<span>{getProject(student.questionnaire.desiredProjectId1)?.name ?? ""}</span>
							</div>
							<div className="info-item">
								<label>Project priority 2:</label>
								<span>{getProject(student.questionnaire.desiredProjectId2)?.name ?? ""}</span>
							</div>
							<div className="info-item">
								<label>Project priority 3:</label>
								<span>{getProject(student.questionnaire.desiredProjectId3)?.name ?? ""}</span>
							</div>
							<div className="info-item">
								<label>Desired minimum group size:</label>
								<span>
									{
										(student.questionnaire.desiredGroupSizeMin === -1 && <>No preference</>)
										|| (<>{student.questionnaire.desiredGroupSizeMin}</>)
									}
								</span>
							</div>
							<div className="info-item">
								<label>Desired maximum group size:</label>
								<span>
									{
										(student.questionnaire.desiredGroupSizeMax === -1 && <>No preference</>)
										|| (<>{student.questionnaire.desiredGroupSizeMax}</>)
									}
								</span>
							</div>
							<div className="info-item">
								<label>Desired work location:</label>
								<span>
									{
										(student.questionnaire.desiredWorkLocation === 'NoPreference' && <>No preference</>)
										|| (student.questionnaire.desiredWorkLocation === 'Located' && <>Located</>)
										|| (student.questionnaire.desiredWorkLocation === 'Remote' && <>Remote</>)
									}
								</span>
							</div>
							<div className="info-item">
								<label>Desired work style:</label>
								<span>
									{
										(student.questionnaire.desiredWorkStyle === 'NoPreference' && <>No preference</>)
										|| (student.questionnaire.desiredWorkStyle === 'Solo' && <>Solo</>)
										|| (student.questionnaire.desiredWorkStyle === 'Together' && <>Together</>)
									}
								</span>
							</div>
							<div className="info-item full-width">
								<label>Personal skills:</label>
								<span>{student.questionnaire.personalSkills}</span>
							</div>
							<div className="info-item full-width">
								<label>Academic interests:</label>
								<span>{student.questionnaire.academicInterests}	</span>
							</div>
							<div className="info-item full-width">
								<label>Special needs:</label>
								<span>{student.questionnaire.specialNeeds}</span>
							</div>
							<div className="info-item full-width">
								<label>Additional comments:</label>
								<span>{student.questionnaire.comments}</span>
							</div>
						</div>
					</div>
				)}
			</div>
		</div>
	);
}
