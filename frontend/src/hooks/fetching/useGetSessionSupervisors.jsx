import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";

import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

async function fetchSessionSupervisors(sessionId) {
	const response = await fetchWithDefaultErrorHandling(
		`/api/sessions/${sessionId}/getSupervisors`,
		{
			credentials: "include",
			method: "GET",
		}
	);
	return await response.json();
}

export default function useGetSessionSupervisors(sessionId, pollingInterval) {
	const [isLoading, setIsLoading] = useState(true);
	const [supervisors, setSupervisors] = useState(null);
	const intervalRef = useRef(null);

	useEffect(() => {
		const fetchData = async () => {
			try {
				setSupervisors(await fetchSessionSupervisors(sessionId));
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

	return { isLoading, supervisors };
}

export function useGetSessionSupervisorsByParam(pollingInterval) {
	const { sessionId } = useParams();
	return useGetSessionSupervisors(sessionId, pollingInterval);
}
