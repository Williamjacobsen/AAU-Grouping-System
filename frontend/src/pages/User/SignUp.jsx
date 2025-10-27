import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./User.css";

export default function SignUp() {

	const navigate = useNavigate();

	const [password, setPassword] = useState("");
	const [email, setEmail] = useState("");
	const [name, setName] = useState("");
	const [error, setError] = useState("");

	const handleSignUp = async (password, email, name, setError, navigate) => {
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

	return (

		<div className="container">
			<div className="header-text">Sign Up</div>
			{error && (
				<div className="error-box">
					{error}
				</div>
			)}
			<div className="text">
				<div className="input">
					<label className="label">
						Name
						<input type="text" onChange={(e) => setName(e.target.value)} placeholder="John Doe" />
					</label>
				</div>
				<div className="input">
					<label className="label">
						Email
						<input type="email" onChange={(e) => setEmail(e.target.value)} placeholder="John123@email.com" />
					</label>
				</div>
				<div className="input">
					<label className="label">
						Password
						<input type="password" onChange={(e) => setPassword(e.target.value)} placeholder="******" />
					</label>
				</div>
			</div>
			<div className="submit-container">
				<button className="sign-in" onClick={() => handleSignUp(email, password, name, setError, navigate)}>
					Sign Up
				</button>
			</div>
		</div>
	);
	


}

