import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

export function useAuth() {

	const [user, setUser] = useState(null);
	const [loading, setLoading] = useState(true);

	const navigate = useNavigate();

	useEffect(() => {
		const fetchUser = async () => {
			try {
				const response = await fetch("http://localhost:8080/auth/getUser", {
					method: "GET",
					credentials: "include",
				})
				if (response.ok) {
					const data = await response.json();
					setUser(data);
				} else {
					navigate("/sign-in");
				}
			} catch (e) {
				navigate("/sign-in");
			} finally {
				setLoading(false);
			};

		}
		fetchUser();
	}, [navigate]);

	return { user, loading, setUser };
}

