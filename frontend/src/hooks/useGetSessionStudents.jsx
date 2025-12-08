import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

async function fetchSessionStudents(sessionId) {
	const response = await fetchWithDefaultErrorHandling(
		`/api/sessions/${sessionId}/getStudents`,
		{
			credentials: "include",
			method: "GET"
		}
	);
	return await response.json();
}

export default function useGetSessionStudents(sessionId, pollingInterval) {

	const [isLoading, setIsLoading] = useState(true);
	const [students, setStudents] = useState(null);
	const intervalRef = useRef(null);

	useEffect(() => {
		const fetchData = async () => {
			try {
				setStudents(await fetchSessionStudents(sessionId));
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		}

		fetchData();

		if (pollingInterval) {
			intervalRef.current = setInterval(fetchData, pollingInterval)
		}

		return () => {
			if (intervalRef.current) {
				clearInterval(intervalRef.current)
			}
		}
	}, [sessionId]);

	return { isLoading, students };
}

export function useGetSessionStudentsByParam(pollingInterval) {
	const { sessionId } = useParams();
	return useGetSessionStudents(sessionId, pollingInterval);
}

