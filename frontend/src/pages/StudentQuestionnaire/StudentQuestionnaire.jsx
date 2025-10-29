import React from "react";
import { useGetUser } from "../../utils/useGetUser";
import { useGetSessionProjectsByParam } from "../../utils/useGetSessionProjects";
import ProjectPrioritiesSelector from "./ProjectPrioritiesSelector";
import { useGetSessionByParameter } from "../../utils/useGetSession";

export default function StudentQuestionnaire() {

	const { isLoading: isLoadingUser, user } = useGetUser();
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingProjects, projects } = useGetSessionProjectsByParam();
	
	if (isLoadingUser) return <>Checking authentication...</>;
	if (!user) return <>Access denied: Not logged in.</>;
	if (user.role !== "Student") return <>Access denied: Not a student.</>
	if (isLoadingSession) return <>Loading session...</>;
	if (isLoadingProjects) return <>Loading projects...</>;

	async function saveQuestionnaireAnswers(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit
			
			const formData = new FormData(event.currentTarget);
			const updatedQuestionnaire = Object.fromEntries(formData);
			// Because Object.fromEntries(formData) flattens the form data to strings,
			// arrays become a string of values seperated by commas, so we need
			// to re-convert it into an actual array
			updatedQuestionnaire.desiredProjectIds = updatedQuestionnaire.desiredProjectIds.split(",")

			await requestSaveQuestionnaireAnswers(updatedQuestionnaire);
			
			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	async function requestSaveQuestionnaireAnswers(updatedQuestionnaire) {
		try {
			const response = await fetch(
				`http://localhost:8080/student/saveQuestionnaireAnswers`,
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
			
			const data = await response;
			if (!response.ok) {
				return Promise.reject(data.error);
			}
		} catch (error) {
			return Promise.reject(error);
		}
	}

	return (
		<>
			<h1>Submission deadline: {session.questionnaireDeadline
				? session.questionnaireDeadline
				: "Not set."
			}</h1>
			<br />

			<form onSubmit={saveQuestionnaireAnswers}>
				<label>
					Project priorities: 
					<ProjectPrioritiesSelector name="desiredProjectIds" projects={projects} desiredProjectIds={user.questionnaire.desiredProjectIds}/>
				</label>
				<br />

				<label>
					Preferred minimum group size: 
					<input name="desiredGroupSizeMin" defaultValue={user.questionnaire.desiredGroupSizeMin} type="number" min={-1} step="1"/>
				</label>
				<br />

				<label>
					Preferred maximum group size: 
					<input name="desiredGroupSizeMax" defaultValue={user.questionnaire.desiredGroupSizeMax} type="number" min={-1} step="1"/>
				</label>
				<br />

				<label>
					Preferred working location:
					<select name="desiredWorkLocation" defaultValue={user.questionnaire.desiredWorkLocation}>
						<option value="NoPreference">No preference</option>
						<option value="Located">Located</option>
						<option value="Remote">Remote</option>
					</select>
				</label>
				<br />

				<label>
					Preferred working style: 
					<select name="desiredWorkStyle" defaultValue={user.questionnaire.desiredWorkStyle}>
						<option value="NoPreference">No preference</option>
						<option value="Solo">Solo</option>
						<option value="Together">Together</option>
					</select>
				</label>
				<br />

				<label>
					Personal skills: 
					<textarea name="personalSkills" defaultValue={user.questionnaire.personalSkills} maxLength={200} />
				</label>
				<br />

				<label>
					Special needs: 
					<textarea name="specialNeeds" defaultValue={user.questionnaire.specialNeeds} maxLength={200} />
				</label>
				<br />

				<label>
					Academic interests: 
					<textarea name="academicInterests" defaultValue={user.questionnaire.academicInterests} maxLength={200} />
				</label>
				<br />

				<label>
					Other commens: 
					<textarea name="comments" defaultValue={user.questionnaire.comments} maxLength={200} />
				</label>
				<br />

				<input type="submit" value="Apply changes" />

			</form>
				
			<button
				type="button"
				onClick={() => window.location.reload()}
				style={{ marginLeft: "10px" }}
			>
				Reset
			</button>
		</>
	);
}
