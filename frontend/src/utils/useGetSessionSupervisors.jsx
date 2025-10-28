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

			const data = await response.json();
			if (!response.ok) {
				return Promise.reject(data.error);
			}
			
			return data;
		}
		catch (error) {
			return Promise.reject(error);
		}
	}

	return { isLoading, supervisors };
}

