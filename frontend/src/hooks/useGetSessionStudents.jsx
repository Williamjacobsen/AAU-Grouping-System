import React, { useState, useEffect } from "react";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers"

async function fetchSessionStudents(sessionId) {
	return await fetchWithDefaultErrorHandling(
		`/sessions/${sessionId}/getStudents`,
		{
			method: "GET"
		}
	);
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

