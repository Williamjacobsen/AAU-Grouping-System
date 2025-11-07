import { useState, useEffect, useCallback } from "react";

export default function useStudentData(sessionId, studentId) {
	const [student, setStudent] = useState(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [isCoordinator, setIsCoordinator] = useState(false);

	const fetchStudentData = useCallback(async () => {
		setLoading(true);
		setError("");
		
		try {
			const roleResponse = await fetch(`${process.env.REACT_APP_API_BASE_URL}/auth/getUser`, {
				method: "GET",
				credentials: "include",
			});
			
			let userIsCoordinator = false;
			if (roleResponse.ok) {
				const userData = await roleResponse.json();
				userIsCoordinator = userData.role === "Coordinator";
				setIsCoordinator(userIsCoordinator);
			}

			let url = `${process.env.REACT_APP_API_BASE_URL}/session/${sessionId}/student/${studentId}`;
			let response = await fetch(url, {
				method: "GET",
				credentials: "include",
			});

			if (response.status === 401 || response.status === 403) {
				url = `${process.env.REACT_APP_API_BASE_URL}/session/${sessionId}/student/${studentId}/public`;
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
			if (err.name === 'TypeError' && err.message.includes('fetch')) {
				setError("Cannot connect to server.");
			} else {
				setError("Network error. Please try again.");
			}
			console.error("Error fetching student data:", err);
		} finally {
			setLoading(false);
		}
	}, [sessionId, studentId]);

	useEffect(() => {
		if (!sessionId || !studentId) {
			setLoading(false);
			setError("No session ID or student ID provided");
			return;
		}

		fetchStudentData();
	}, [sessionId, studentId, fetchStudentData]);

	return {
		student,
		loading,
		error,
		isCoordinator,
		refetch: () => {
			if (sessionId && studentId) {
				fetchStudentData();
			}
		},
		removeStudent: async () => {
			try {
				const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/session/${sessionId}/student/${studentId}`, {
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
				if (err.name === 'TypeError' && err.message.includes('fetch')) {
					return { success: false, message: "Cannot connect to server." };
				}
				return { success: false, message: "Network error. Please try again." };
			}
		},
		resetPassword: async () => {
			try {
				const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/session/${sessionId}/student/${studentId}/reset-password`, {
					method: "POST",
					credentials: "include",
				});

				if (response.ok) {
					const message = await response.text();
					return { success: true, message: message || "New password generated and sent via email successfully" };
				} else if (response.status === 401) {
					return { success: false, message: "Unauthorized: Please log in as a coordinator" };
				} else if (response.status === 403) {
					return { success: false, message: "Forbidden: You don't have permission to reset this student's password" };
				} else if (response.status === 404) {
					return { success: false, message: "Student not found" };
				} else {
					const errorText = await response.text();
					return { success: false, message: errorText || "Failed to reset password" };
				}
			} catch (err) {
				console.error("Error resetting password:", err);
				if (err.name === 'TypeError' && err.message.includes('fetch')) {
					return { success: false, message: "Cannot connect to server." };
				}
				return { success: false, message: "Network error. Please try again." };
			}
		}
	};
}