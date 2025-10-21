import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

/**
 * @returns An object {isLoading, session}.
 * - isLoading is a useState boolean.
 * - students is a useState Student array.
 */
export default function useGetSession() {

	const { sessionId } = useParams();
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
	}, [sessionId]);

	async function requestSession(sessionId) {
		try {
			const response = await fetch(
				`http://localhost:8080/sessions/${sessionId}`,
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

	return { isLoading, session };
}

