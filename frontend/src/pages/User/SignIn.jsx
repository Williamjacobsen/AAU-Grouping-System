import React, { useEffect, useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../ContextProviders/AuthProvider";
import "./User.css";

export default function SignIn() {

	const navigate = useNavigate();

	const [password, setPassword] = useState("");
	const [emailOrId, setEmailOrId] = useState("");
	const [error, setError] = useState("");
	const { setUser } = useAuth();

	const handleSignIn = async (password, emailOrId, setError, navigate) => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/auth/signIn`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ emailOrId, password }),
				credentials: "include"
			});

			if (!response.ok) {
				const error = await response.text();
				setError(error);
				return Promise.resolve();
			}

			const user = await response.json();
			setUser(user);
			console.log(user)

			if (user && user.role === "Coordinator") {
				navigate("/sessions");
			} else  if (user && user.role === "Supervisor") {
				navigate(`/session/${user.sessionId}/projects`);
			} else if (user && user.role === "Student") {
				navigate(`/session/${user.sessionId}/studentQuestionnaire`);
			}
			window.location.reload();
		} catch (e) {
			setError(e.message);
		}
	};

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [error]);

	return (
		<div className="container">
			<div className="header-text">Sign In</div>
			{error && (
				<div className="error-box">
					{error}
				</div>
			)}
			<div className="text">
				<div className="input">
					<label className="label">
						<>
							Email or ID
							<input type="emailOrId" onChange={(e) => setEmailOrId(e.target.value)} placeholder="john123@example.com" />
						</>
					</label>
				</div>
				<div className="input">
					<label className="label">
						Password
						<input type="password" onChange={(e) => setPassword(e.target.value)}
							onKeyDown={(e) => {
								if (e.key === "Enter") {
									handleSignIn(password, emailOrId, setError, navigate);
								}
							}}
							placeholder="******" />
					</label>
				</div>
			</div>
			<div className="submit-container">
				<button className="sign-in" onClick={() => handleSignIn(password, emailOrId, setError, navigate)}>
					Sign In
				</button>
				<button className="sign-up" onClick={() => navigate("/sign-up")}>
					Sign Up
				</button>
				<br />
				<div className="forgot-password" onClick={() => navigate("/forgotpassword")}>
					Forgot your password?
				</div>
			</div>
		</div>
	);
}
