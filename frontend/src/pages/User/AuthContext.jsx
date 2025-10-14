import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

export function AuthProvider({ children }) {

	const [user, setUser] = useState(null);

	const navigate = useNavigate();

	useEffect(() => {
		fetch("http://localhost:8080/auth/me", {
			method: "GET",
			credentials: "include"
		})
			.then(async (response) => {
				if (response.ok) {
					const data = await response.json();
					setUser(data);
				}
				else {
					setUser(null)
				}
			})
			.catch(() => {
				setUser(null);
			});
	}, []);

	const logout = () => {
		fetch("http://localhost:8080/auth/logout", {
			method: "POST",
			credentials: "include",
		}).finally(() => {
			setUser(null);
			navigate("/sign-in");
		});
	};

	return (
		0
	)
}