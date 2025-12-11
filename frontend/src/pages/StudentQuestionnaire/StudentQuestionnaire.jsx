import React from "react";
import { useAuth } from "../../ContextProviders/AuthProvider";
import { useGetSessionProjectsByParam } from "../../hooks/useGetSessionProjects";
import ProjectPrioritySelectors from "../../Components/ProjectPrioritySelector/ProjectPrioritySelectors";
import { useGetSessionByParameter } from "../../hooks/useGetSession";
import useIsQuestionnaireDeadlineExceeded from "../../hooks/useIsQuestionnaireDeadlineExceeded";
import "./StudentQuestionnaire.css";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

export default function StudentQuestionnaire() {

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingProjects, projects } = useGetSessionProjectsByParam();
	const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session);

	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return <div className="access-denied-message">Access denied: Not logged in.</div>;
	if (user.role !== "Student") return <div className="access-denied-message">Access denied: Not a student.</div>;
	if (isLoadingSession) return <div className="loading-message">Loading session...</div>;
	if (isLoadingProjects) return <div className="loading-message">Loading projects...</div>;

	async function saveQuestionnaireAnswers(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit

			const formData = new FormData(event.currentTarget);
			const updatedQuestionnaire = Object.fromEntries(formData);

			await fetchWithDefaultErrorHandling(
				`/api/student/saveQuestionnaireAnswers`,
				{
					method: "POST",
					credentials: "include", // Ensures cookies are sent with the request
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify(
						updatedQuestionnaire
					),
				}
			);

			alert("Successfully saved answers!");
			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	return (
		<div className="student-questionnaire-container">
			<h1 className="questionnaire-title">My Wishes</h1>

			<div className="deadline-info">
				<strong>Submission deadline:</strong> {session.questionnaireDeadline
					? session.questionnaireDeadline
					: "Not set."
				}
			</div>

			<form className="questionnaire-form" onSubmit={saveQuestionnaireAnswers}>
				<div className="form-section">
					<h2>Project Preferences</h2>
					<div className="form-group">
						<label>Project priorities:</label>
						<ProjectPrioritySelectors
							projects={projects}
							desiredProjectId1Name="desiredProjectId1"
							desiredProjectId2Name="desiredProjectId2"
							desiredProjectId3Name="desiredProjectId3"
							desiredProjectId1={user.questionnaire.desiredProjectId1}
							desiredProjectId2={user.questionnaire.desiredProjectId2}
							desiredProjectId3={user.questionnaire.desiredProjectId3}
						/>
					</div>
				</div>

				<div className="form-section">
					<h2>Group Preferences</h2>
					<div className="form-group">
						<label htmlFor="desiredGroupSizeMin">
							Preferred minimum group size (-1 means no preference):
						</label>
						<input
							id="desiredGroupSizeMin"
							name="desiredGroupSizeMin"
							defaultValue={user.questionnaire.desiredGroupSizeMin}
							type="number"
							min={-1}
							step="1"
						/>
					</div>

					<div className="form-group">
						<label htmlFor="desiredGroupSizeMax">
							Preferred maximum group size (-1 means no preference):
						</label>
						<input
							id="desiredGroupSizeMax"
							name="desiredGroupSizeMax"
							defaultValue={user.questionnaire.desiredGroupSizeMax}
							type="number"
							min={-1}
							step="1"
						/>
					</div>
				</div>

				<div className="form-section">
					<h2>Working Preferences</h2>
					<div className="form-group">
						<label htmlFor="desiredWorkLocation">
							Preferred working location:
						</label>
						<select
							id="desiredWorkLocation"
							name="desiredWorkLocation"
							defaultValue={user.questionnaire.desiredWorkLocation}
						>
							<option value="NoPreference">No preference</option>
							<option value="Located">Located</option>
							<option value="Remote">Remote</option>
						</select>
					</div>

					<div className="form-group">
						<label htmlFor="desiredWorkStyle">
							Preferred working style:
						</label>
						<select
							id="desiredWorkStyle"
							name="desiredWorkStyle"
							defaultValue={user.questionnaire.desiredWorkStyle}
						>
							<option value="NoPreference">No preference</option>
							<option value="Solo">Solo</option>
							<option value="Together">Together</option>
						</select>
					</div>
				</div>

				<div className="form-section">
					<h2>Additional Information</h2>
					<div className="form-group">
						<label htmlFor="personalSkills">
							Personal skills:
						</label>
						<textarea
							id="personalSkills"
							name="personalSkills"
							defaultValue={user.questionnaire.personalSkills}
							maxLength={200}
							placeholder="Describe your technical skills, programming languages, tools, etc."
						/>
					</div>

					<div className="form-group">
						<label htmlFor="specialNeeds">
							Special needs:
						</label>
						<textarea
							id="specialNeeds"
							name="specialNeeds"
							defaultValue={user.questionnaire.specialNeeds}
							maxLength={200}
							placeholder="Any accessibility requirements, scheduling constraints, etc."
						/>
					</div>

					<div className="form-group">
						<label htmlFor="academicInterests">
							Academic interests:
						</label>
						<textarea
							id="academicInterests"
							name="academicInterests"
							defaultValue={user.questionnaire.academicInterests}
							maxLength={200}
							placeholder="Research areas, topics you're passionate about, etc."
						/>
					</div>

					<div className="form-group">
						<label htmlFor="comments">
							Other comments:
						</label>
						<textarea
							id="comments"
							name="comments"
							defaultValue={user.questionnaire.comments}
							maxLength={200}
							placeholder="Any additional information you'd like to share..."
						/>
					</div>
				</div>

				{isDeadlineExceeded() && (
					<div className="deadline-exceeded-message">
						Submission deadline exceeded. Answers locked.
					</div>
				)}

				<div className="form-buttons">
					{!isDeadlineExceeded() && (
						<input
							type="submit"
							value="Apply Changes"
							className="submit-button"
						/>
					)}

					<button
						type="button"
						onClick={() => window.location.reload()}
						className="reset-button"
					>
						Reset
					</button>
				</div>
			</form>
		</div>
	);
}
