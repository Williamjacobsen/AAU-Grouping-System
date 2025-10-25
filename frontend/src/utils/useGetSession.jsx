import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

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
				setSession(await requestSession(sessionId));
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
	const { isLoading, session } = useGetSession(sessionId);
	return { isLoading, session };
}

export function useGetSessionByUser(user) {
	const [ sessionId, setSessionId ] = useState(null);

	useEffect(() => {
		setSessionId(user?.sessionId);
	}, [user]);

	const { isLoading, session } = useGetSession(sessionId);
	return { isLoading, session };
}

/// Session from user is prioritized over session from URL parameter.
export function useGetSessionByUserOrParameter(user) {

	const { isLoading: IsLoadingSessionByParameter, session: sessionByParameter } = useGetSessionByParameter();
	const { isLoading: IsLoadingSessionByUser, session: sessionByUser } = useGetSessionByUser(user);

	const [session, setSession] = useState(null);
	const [isLoading, setIsLoading] = useState(null);

	useEffect(() => {
		setSession(sessionByUser ? sessionByUser : sessionByParameter);
		setIsLoading(IsLoadingSessionByParameter || IsLoadingSessionByUser);
	}, [sessionByUser, sessionByParameter, IsLoadingSessionByUser, IsLoadingSessionByParameter, ]);

	return { isLoading, session };
}


