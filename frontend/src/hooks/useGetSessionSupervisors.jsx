import React, { useState, useEffect } from "react";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

async function fetchSessionSupervisors(sessionId) {
	const response = await fetchWithDefaultErrorHandling(
		`/sessions/${sessionId}/getSupervisors`,
		{
			credentials: "include",
			method: "GET",
		}
	);
	return await response.json();
}

export default function useGetSessionSupervisors(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [supervisors, setSupervisors] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				setSupervisors(await fetchSessionSupervisors(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		})();
	}, [sessionId]);

	return { isLoading, supervisors };
}

