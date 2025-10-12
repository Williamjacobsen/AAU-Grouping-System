import { useState, useEffect } from "react";

export default function useSessionManager() {
	const [sessions, setSessions] = useState([]);
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState("");

	const fetchSessions = async () => {
		setLoading(true);
		setError("");
		try {
			const response = await fetch("http://localhost:8080/sessions", {
				method: "GET",
				credentials: "include", 
			});

			if (response.ok) {
				const data = await response.json();
				const sessionArray = Object.entries(data).map(([id, session]) => ({
					id: parseInt(id),
					...session,
				}));
				setSessions(sessionArray);
			} else if (response.status === 401) {
				setError("You must be logged in to view sessions.");
			} else {
				setError("Failed to fetch sessions.");
			}
		} catch (err) {
			setError("Network error. Please try again.");
			console.error("Error fetching sessions:", err);
		} finally {
			setLoading(false);
		}
	};

	const createSession = async (sessionName) => {
		if (!sessionName.trim()) return false;

		setLoading(true);
		setError("");
		try {
			const response = await fetch("http://localhost:8080/sessions", {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				credentials: "include",
				body: JSON.stringify({ name: sessionName.trim() }),
			});

			if (response.ok) {
				await fetchSessions();
				return true;
			} else if (response.status === 401) {
				setError("You must be logged in to create sessions.");
			} else {
				setError("Failed to create session.");
			}
		} catch (err) {
			setError("Network error. Please try again.");
			console.error("Error creating session:", err);
		} finally {
			setLoading(false);
		}
		return false;
	};

	const deleteSession = async (sessionId) => {
		setLoading(true);
		setError("");
		try {
			const response = await fetch(`http://localhost:8080/sessions/${sessionId}`, {
				method: "DELETE",
				credentials: "include",
			});

			if (response.ok) {
				await fetchSessions();
				return true;
			} else if (response.status === 401) {
				setError("You must be logged in to delete sessions.");
			} else if (response.status === 403) {
				setError("You don't have permission to delete this session.");
			} else {
				setError("Failed to delete session.");
			}
		} catch (err) {
			setError("Network error. Please try again.");
			console.error("Error deleting session:", err);
		} finally {
			setLoading(false);
		}
		return false;
	};

	useEffect(() => {
		fetchSessions();
	}, []);

	return {
		sessions,
		loading,
		error,
		createSession,
		deleteSession,
		refreshSessions: fetchSessions
	};
}