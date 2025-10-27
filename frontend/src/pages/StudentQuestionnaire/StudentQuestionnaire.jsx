import React from "react";
import { useGetUser } from "../../utils/useGetUser";

export default function StudentQuestionnaire() {

	const { isLoading: isLoadingUser, user: user } = useGetUser();
	
	if (isLoadingUser) return <>Checking authentication...</>;
	if (!user) return <>Access denied: Not logged in.</>;
	if (user.role !== "Student") return <>Access denied: Not a student.</>
	
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
			<form onSubmit={saveQuestionnaireAnswers}>
				<label>
					Personal skills: 
					<textarea name="personalSkills" defaultValue={user.questionnaire.personalSkills} required maxLength={200} />
				</label>
				<label>
					Special needs: 
					<textarea name="specialNeeds" defaultValue={user.questionnaire.specialNeeds} required maxLength={200} />
				</label>
				<label>
					Academic interests: 
					<textarea name="academicInterests" defaultValue={user.questionnaire.academicInterests} required maxLength={200} />
				</label>
				<label>
					Other commens: 
					<textarea name="comments" defaultValue={user.questionnaire.comments} required maxLength={200} />
				</label>
				<input type="submit" value="Apply changes" />
			</form>
		</>
	);
}
