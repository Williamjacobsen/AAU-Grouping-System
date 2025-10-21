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

export const handleSignIn = async (password, emailOrId, role, setError, navigate) => {
	try {
		const response = await fetch("http://localhost:8080/auth/signIn", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ emailOrId, password, role }),
			credentials: "include"
		})
		if (response.ok) {
			navigate("/profile");
		} else {
			const error = await response.text();
			setError(error);
		}
	} catch (e) {
		setError(e.message);
	}
}

export const handleSignUp = async (password, email, name, setError, navigate) => {
	try {
		const response = await fetch("http://localhost:8080/coordinator/signUp", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ email, password, name }),
			credentials: "include"
		})
		if (response.ok) {
			navigate("/profile");
		} else {
			const errorMessage = await response.text();
			setError(errorMessage);
		}
	} catch (e) {
		setError(e.message);
	}
}

