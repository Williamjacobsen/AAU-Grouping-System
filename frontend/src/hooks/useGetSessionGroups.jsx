import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

export async function fetchSessionGroups(sessionId) {
	const response = await fetchWithDefaultErrorHandling(
		`/groups/${sessionId}/getGroups`,
		{
			credentials: "include",
			method: "GET"
		}
	);
	return await response.json();
}

export default function useGetSessionGroups(sessionId, pollingInterval) {

	const [isLoading, setIsLoading] = useState(true);
	const [groups, setGroups] = useState(null);
	const intervalRef = useRef(null);

	useEffect(() => {
		const fetchData = async () => {
			try {
				setGroups(await fetchSessionGroups(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		};

		fetchData();

		if (pollingInterval) {
			intervalRef.current = setInterval(fetchData, pollingInterval);
		}

		return () => {
			if (intervalRef.current) {
				clearInterval(intervalRef.current);
			}
		};
	}, [sessionId]);

	return { isLoading, groups };
}

export function useGetSessionGroupsByParam(pollingInterval) {
	const { sessionId } = useParams();
	return useGetSessionGroups(sessionId, pollingInterval);
}

