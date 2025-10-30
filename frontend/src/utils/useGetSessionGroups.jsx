import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

export async function requestSessionGroups(sessionId) {
	try {
		const response = await fetch(
			`http://localhost:8080/sessions/${sessionId}/getGroups`,
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

export default function useGetSessionGroups(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [groups, setGroups] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				setGroups(await requestSessionGroups(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		})();
	}, [sessionId]);

	return { isLoading, groups };
}

export function useGetSessionGroupsByParam() {
	const { sessionId } = useParams();
	return useGetSessionGroups(sessionId);
}

