import React, { useState, useEffect } from "react";

/**
 * @returns An object {isLoading, session}.
 * - isLoading is a useState boolean.
 * - projects is a useState Project array.
 */
export default function useGetSessionProjects(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [projects, setProjects] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				// TODO: Opdater "projects"-variablen via "requestSessionProjects()"-funktionen
				// TODO: Opdater "isLoading"-variablen
			} catch (error) {
				alert(error);
			}
		})();
	}, [sessionId]);

	async function requestSessionProjects(sessionId) {
		try {
			// TODO: Return en liste af Projects, som du har fået via en fetch request til backenden.
		}
		catch (error) {
			return Promise.reject(error);
		}
	}

	return { isLoading, projects };
}

