import React, { useState, useEffect } from "react";

/**
 * @returns An object {isLoading, session}.
 * - isLoading is a useState boolean.
 * - students is a useState Student array.
 */
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
				`http://localhost:8080/student/${sessionId}`,
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

	return { isLoading, students };
}

