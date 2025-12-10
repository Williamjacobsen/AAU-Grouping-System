import { useState, useEffect } from "react";

export default function useSessionManager() {
	const [sessions, setSessions] = useState([]);
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState("");

	const fetchSessions = async () => {
		setLoading(true);
		setError("");
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions`, {
				method: "GET",
				credentials: "include",
			});

			if (!response.ok) {
				setError(await response.text());
				return Promise.reject("Status code " + response.status + ": " + await response.text());
			}

			setSessions(await response.json());

		} catch (error) {
			alert(error);
		} finally {
			setLoading(false);
		}
	};

	const createSession = async (sessionName) => {
		if (!sessionName.trim()) return false;

		setLoading(true);
		setError("");
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions`, {
				method: "POST",
				headers: {
					"Content-Type": "application/json",
				},
				credentials: "include",
				body: JSON.stringify({ name: sessionName.trim() }),
			});

			if (!response.ok) {
				setError(await response.text());
				return Promise.reject("Status code " + response.status + ": " + await response.text());
			}

			await fetchSessions();
			alert("Successfully created session!");
			return true;

		} catch (error) {
			alert(error);
		} finally {
			setLoading(false);
		}
		return false;
	};

	const deleteSession = async (sessionId) => {
		setLoading(true);
		setError("");
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/sessions/${sessionId}`, {
				method: "DELETE",
				credentials: "include",
			});

			if (!response.ok) {
				setError(await response.text());
				return Promise.reject("Status code " + response.status + ": " + await response.text());
			}

			await fetchSessions();
			alert("Successfully deleted session!");
			return true;

		} catch (error) {
			alert(error);
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