import React, { useState, useEffect } from "react";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

async function fetchSessionStudents(sessionId) {
	const response = await fetchWithDefaultErrorHandling(
		`/sessions/${sessionId}/getStudents`,
		{
			credentials: "include",
			method: "GET"
		}
	);
	return await response.json();
}

export default function useGetSessionStudents(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [students, setStudents] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				setStudents(await fetchSessionStudents(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		})();
	}, [sessionId]);

	return { isLoading, students };
}

