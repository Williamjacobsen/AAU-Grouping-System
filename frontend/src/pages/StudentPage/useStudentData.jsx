import { useState, useEffect } from "react";

export default function useStudentData(sessionId, studentId) {
	const [student, setStudent] = useState(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [isCoordinator, setIsCoordinator] = useState(false);

	useEffect(() => {
		if (!sessionId || !studentId) {
			setLoading(false);
			setError("No session ID or student ID provided");
			return;
		}

		const fetchStudentData = async () => {
			setLoading(true);
			setError("");
			
			try {
				const roleResponse = await fetch("http://localhost:8080/auth/me", {
					method: "GET",
					credentials: "include",
				});
				
				let userIsCoordinator = false;
				if (roleResponse.ok) {
					const userData = await roleResponse.json();
					userIsCoordinator = userData.role === "COORDINATOR";
					setIsCoordinator(userIsCoordinator);
				}

				let url = `http://localhost:8080/session/${sessionId}/student/${studentId}`;
				let response = await fetch(url, {
					method: "GET",
					credentials: "include",
				});

				if (response.status === 401 || response.status === 403) {
					url = `http://localhost:8080/session/${sessionId}/student/${studentId}/public`;
					response = await fetch(url, {
						method: "GET",
					});
				}

				if (response.ok) {
					const data = await response.json();
					setStudent(data);
				} else if (response.status === 404) {
					setError("Student not found.");
				} else {
					setError("Failed to load student information.");
				}
			} catch (err) {
				setError("Network error. Please try again.");
				console.error("Error fetching student data:", err);
			} finally {
				setLoading(false);
			}
		};

		fetchStudentData();
	}, [sessionId, studentId]);

	return {
		student,
		loading,
		error,
		isCoordinator,
		refetch: () => {
			if (sessionId && studentId) {
				setLoading(true);
				setError("");
			}
		},
		removeStudent: async () => {
			try {
				const response = await fetch(`http://localhost:8080/session/${sessionId}/student/${studentId}`, {
					method: "DELETE",
					credentials: "include",
				});

				if (response.ok) {
					return { success: true, message: "Student removed successfully" };
				} else if (response.status === 401) {
					return { success: false, message: "Unauthorized: Please log in as a coordinator" };
				} else if (response.status === 403) {
					return { success: false, message: "Forbidden: You don't have permission to remove this student" };
				} else if (response.status === 404) {
					return { success: false, message: "Student not found" };
				} else {
					const errorText = await response.text();
					return { success: false, message: errorText || "Failed to remove student" };
				}
			} catch (err) {
				console.error("Error removing student:", err);
				return { success: false, message: "Network error. Please try again." };
			}
		}
	};
}