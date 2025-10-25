import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

export function useGetUser() {

	const [user, setUser] = useState(null);
	const [isLoading, setIsLoading] = useState(true);

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
					setUser(null)
				}
			} catch (error) {
				alert(error);
			} finally {
				setIsLoading(false);
			};
		}
		fetchUser();
	}, [navigate]);

	return { user, isLoading, setUser };
}



