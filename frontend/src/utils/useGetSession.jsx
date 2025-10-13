import React, { useState, useEffect } from "react";

/**
 * @returns An object {isLoading, session}.
 * - isLoading is a useState boolean.
 * - session is a useState Session.
 */
export default function useGetSession(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [session, setSession] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				setSession(await requestSession(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		})();
	}, []);

	async function requestSession(sessionId) {
		try {
			const response = await fetch(
				`http://localhost:8080/session/${sessionId}`,
				{
					method: "GET",
					credentials: "include", // Ensures cookies are sent with the request
				}
			);

			const data = await response.json();
			if (!response.ok) {
				return Promise.reject(data.error);
			}
			
			return data.session;
		}
		catch (error) {
			return Promise.reject(error);
		}
	}

	return { isLoading, session };
}

