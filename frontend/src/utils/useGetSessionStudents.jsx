import React, { useState, useEffect } from "react";

export default function useGetSessionStudents(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [students, setStudents] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				setStudents(await requestSessionStudents(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		})();
	}, [sessionId]);

	async function requestSessionStudents(sessionId) {
		try {
			const response = await fetch(
				`http://localhost:8080/sessions/${sessionId}/getStudents`,
				{
					method: "GET",
					credentials: "include",
				}
			);

			if (!response.ok) {
				return Promise.reject("Status code " + response.status + ": " + await response.text());
			}
			return await response.json();
		}
		catch (error) {
			return Promise.reject(error);
		}
	}

	return { isLoading, students };
}

