import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

export async function fetchSession(sessionId) {
	const response = await fetchWithDefaultErrorHandling(
		`/sessions/${sessionId}`,
		{
			credentials: "include",
			method: "GET"
		}
	);
	return await response.json();
}

function useGetSession(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [session, setSession] = useState(null);

	useEffect(() => {
		(async () => {
			if (!sessionId) {
				setIsLoading(false);
				return;
			}

			try {
				setSession(await fetchSession(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		})();
	}, [sessionId]);

	return { isLoading, session };
}

export function useGetSessionByParameter() {
	const { sessionId } = useParams();
	return useGetSession(sessionId);
}

export function useGetSessionByUser(user) {
	const [sessionId, setSessionId] = useState(null);

	useEffect(() => {
		setSessionId(user?.sessionId);
	}, [user]);

	return useGetSession(sessionId);;
}

/** Session from user is prioritized over session from URL parameter. */
export function useGetSessionByUserOrParameter(user) {

	const { isLoading: IsLoadingSessionByParameter, session: sessionByParameter } = useGetSessionByParameter();
	const { isLoading: IsLoadingSessionByUser, session: sessionByUser } = useGetSessionByUser(user);

	const [session, setSession] = useState(null);
	const [isLoading, setIsLoading] = useState(null);

	useEffect(() => {
		setSession(sessionByUser ? sessionByUser : sessionByParameter);
		setIsLoading(IsLoadingSessionByParameter || IsLoadingSessionByUser);
	}, [sessionByUser, sessionByParameter, IsLoadingSessionByUser, IsLoadingSessionByParameter,]);

	return { isLoading, session };
}


