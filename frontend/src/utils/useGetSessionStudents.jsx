import { useState, useEffect } from "react";

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
				// Report the error and clear the loading state so the UI can render an error state
				console.error("Failed to load session students:", error);
				
				if (process.env.NODE_ENV === 'development') {
					setStudents([
						{ name: "Alice Example", group: { number: "1", project: "Proj A" }, desiredWorkingEnvironment: "Remote", personalSkills: ["JS", "React"] },
						{ name: "Bob Example", group: { number: "2", project: "Proj B" }, desiredWorkingEnvironment: "On-site", personalSkills: ["Java", "Spring"] },
						{ name: "Carol Example", group: { number: "1", project: "Proj A" }, desiredWorkingEnvironment: "Hybrid", personalSkills: ["Python"] }
					]);
				} else {
					setStudents(null);
				}
				setIsLoading(false);
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

