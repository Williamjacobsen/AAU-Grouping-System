import React from "react";
import { useGetUser } from "../../hooks/useGetUser";
import { useGetSessionProjectsByParam } from "../../hooks/useGetSessionProjects";
import ProjectPrioritySelectors from "./ProjectPrioritySelectors";
import { useGetSessionByParameter } from "../../hooks/useGetSession";
import useIsQuestionnaireDeadlineExceeded from "../../hooks/useIsQuestionnaireDeadlineExceeded";

export default function StudentQuestionnaire() {

	const { isLoading: isLoadingUser, user } = useGetUser();
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingProjects, projects } = useGetSessionProjectsByParam();
	const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session);
	
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

			await requestSaveQuestionnaireAnswers(updatedQuestionnaire);
			
			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	async function requestSaveQuestionnaireAnswers(updatedQuestionnaire) {
		try {
			const response = await fetch(
				`${process.env.REACT_APP_API_BASE_URL}/student/saveQuestionnaireAnswers`,
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

			if (!response.ok) {
				return Promise.reject("Status code " + response.status + ": " + await response.text());
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
					<ProjectPrioritySelectors
						projects={projects}
						desiredProjectId1Name="desiredProjectId1"
						desiredProjectId2Name="desiredProjectId2"
						desiredProjectId3Name="desiredProjectId3"
						desiredProjectId1={user.questionnaire.desiredProjectId1}
						desiredProjectId2={user.questionnaire.desiredProjectId2}
						desiredProjectId3={user.questionnaire.desiredProjectId3}
					/>
				</label>
				<br />

				<label>
					Preferred minimum group size ("-1" means no preference): 
					<input name="desiredGroupSizeMin" defaultValue={user.questionnaire.desiredGroupSizeMin} type="number" min={-1} step="1"/>
				</label>
				<br />

				<label>
					Preferred maximum group size ("-1" means no preference): 
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

				{isDeadlineExceeded() &&
					<b>Submission deadline exceeded. Answers locked.</b>
				}
				{!isDeadlineExceeded() &&
					<input type="submit" value="Apply changes"/>
				}

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
