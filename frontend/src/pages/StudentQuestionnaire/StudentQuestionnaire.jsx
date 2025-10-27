import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";
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
			const name = formData.get("name")
			
			await requestSaveQuestionnaireAnswers(name);
			
			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	async function requestSaveQuestionnaireAnswers(name) {
		try {
			const response = await fetch(
				`http://localhost:8080/student/saveQuestionnaireAnswers`,
				{
					method: "POST",
					credentials: "include", // Ensures cookies are sent with the request
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify({
						name,
					}),
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
					Name: 
					<input name="name" defaultValue={user.name} required maxLength={150} />
				</label>
				<input type="submit" value="Apply changes" />
			</form>
		</>
	);
}
