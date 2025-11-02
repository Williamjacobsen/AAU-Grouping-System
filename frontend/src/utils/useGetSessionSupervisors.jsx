import React, { useState, useEffect } from "react";

export default function useGetSessionSupervisors(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [supervisors, setSupervisors] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				setSupervisors(await requestSessionSupervisors(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		})();
	}, [sessionId]);

	async function requestSessionSupervisors(sessionId) {
		try {
			const response = await fetch(
				`http://localhost:8080/sessions/${sessionId}/getSupervisors`,
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

	return { isLoading, supervisors };
}

