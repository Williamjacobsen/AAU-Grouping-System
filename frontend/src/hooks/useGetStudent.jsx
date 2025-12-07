import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

export function useGetStudent(studentId) {

	const [isLoading, setIsLoading] = useState(true);
	const [student, setStudent] = useState(null);

	useEffect(() => {
		(async () => {
			try {
				const response = await fetchWithDefaultErrorHandling(
					`/api/student/${studentId}`,
					{
						credentials: "include",
						method: "GET"
					}
				);
				const student = await response.json();
				setStudent(student);
				setIsLoading(false);
			} catch (error) {
				alert(error);
			}
		})();
	}, [studentId]);

	return { isLoading, student };
}

export function useGetStudentByParam() {
	const { studentId } = useParams();
	return useGetStudent(studentId);
}

