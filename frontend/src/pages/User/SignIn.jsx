import React, { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import "./User.css";

export default function SignIn() {

	const navigate = useNavigate();

	const userRoleEnum = useMemo(() => Object.freeze({
		COORDINATOR: "COORDINATOR",
		SUPERVISOR: "SUPERVISOR",
		STUDENT: "STUDENT",
	}), []);

	const [password, setPassword] = useState("");
	const [emailOrId, setEmailOrId] = useState("");
	const [role, setRole] = useState(userRoleEnum.COORDINATOR);
	const [error, setError] = useState("");

	const handleSignIn = async () => {
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

	return (
		<div className="container">
			<div className="header-text">Sign In</div>
			{error && (
				<div className="error-box">
					{error}
				</div>
			)}
			<div className="text">
				<label className="label">
					Role:
					<select value={role} onChange={(event) => setRole(event.target.value)}>
						<option value={userRoleEnum.COORDINATOR}>Coordinator</option>
						<option value={userRoleEnum.SUPERVISOR}>Supervisor</option>
						<option value={userRoleEnum.STUDENT}>Student</option>
					</select>
				</label>
				<div className="input">
					<label className="label">
						{role === userRoleEnum.COORDINATOR &&
							<>
								Email
								<input type="emailOrId" onChange={(e) => setEmailOrId(e.target.value)} placeholder="john123@example.com" />
							</>
						}
						{role !== userRoleEnum.COORDINATOR &&
							<>
								User ID
								<input type="emailOrId" onChange={(e) => setEmailOrId(e.target.value)} placeholder="283fsdklajhfo23ljfd" />
							</>
						}
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
				<button className="sign-in" onClick={handleSignIn}>
					Sign In
				</button>
				<button className="sign-up" onClick={() => navigate("/sign-up")}>
					Sign Up
				</button>
				<div className="forgot-password" onClick={() => navigate("/forgotpassword")}>
					Forgot password?
				</div>
			</div>
		</div>
	);
}
