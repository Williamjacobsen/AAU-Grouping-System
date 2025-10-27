import React, { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { handleSignIn } from "../../utils/useGetUser";
import "./User.css";

export default function SignIn() {

	const navigate = useNavigate();

	// obejct.freeze: makes the object immutable
	// useMemo: used to remember the userRole, so it dosnt have to re-render (unless its dependecies change)
	const userRoleEnum = useMemo(() => Object.freeze({
		Coordinator: "Coordinator",
		Supervisor: "Supervisor",
		Student: "Student",
	}), []);

	const [password, setPassword] = useState("");
	const [emailOrId, setEmailOrId] = useState("");
	const [role, setRole] = useState(userRoleEnum.Coordinator);
	const [error, setError] = useState("");

	const handleSignIn = async (password, emailOrId, role, setError, navigate) => {
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
						<option value={userRoleEnum.Coordinator}>Coordinator</option>
						<option value={userRoleEnum.Supervisor}>Supervisor</option>
						<option value={userRoleEnum.Student}>Student</option>
					</select>
				</label>
				<div className="input">
					<label className="label">
						{role === userRoleEnum.Coordinator &&
							<>
								Email
								<input type="emailOrId" onChange={(e) => setEmailOrId(e.target.value)} placeholder="john123@example.com" />
							</>
						}
						{role !== userRoleEnum.Coordinator &&
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
				<button className="sign-in" onClick={() => handleSignIn(password, emailOrId, role, setError, navigate)}>
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
