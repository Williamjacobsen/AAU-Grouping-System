import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers"

export async function fetchSessionGroups(sessionId) {
	return await fetchWithDefaultErrorHandling(
		`/sessions/${sessionId}/getGroups`,
		{
			method: "GET"
		}
	);
}

export default function useGetSessionGroups(sessionId) {

	const [isLoading, setIsLoading] = useState(true);
	const [groups, setGroups] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				setGroups(await fetchSessionGroups(sessionId));
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

