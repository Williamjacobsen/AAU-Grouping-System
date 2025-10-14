import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./User.css";

export default function Profile() {

	const navigate = useNavigate();

	const [name, setName] = useState("");
	const [email, setEmail] = useState("");
	const [role, setRole] = useState("Coordinator"); //todo: implementere setRole, så det ikke er hard coded

	const [newEmail, setNewEmail] = useState("");
	const [newPassword, setNewPassword] = useState("");
	const [error, setError] = useState("");

	const handleEmailChange = () => {
		fetch("http://localhost:8080/coordinator/modifyEmail", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ newEmail }),
			credentials: "include"
		})
			.then(async (response) => {
				if (response.ok) {
					navigate("/profile");
				} else {
					const errorMessage = await response.text();
					setError(errorMessage);
				}
			})
			.catch((e) => {
				setError(e.message);
			})
	};

	const handlePasswordChange = () => {
		fetch("http://localhost:8080/coordinator/modifyPassword", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ newPassword }),
			credentials: "include"
		})
			.then(async (response) => {
				if (response.ok) {
					navigate("/profile");
				} else {
					const errorMessage = await response.text();
					setError(errorMessage);
				}
			})
			.catch((e) => {
				setError(e.message);
			})
	};

	const handleLogout = () => {
		fetch("http://localhost:8080/auth/logout", {
			method: "POST",
			credentials: "include"
		})
			.then(async (response) => {
				if (response.ok) {
					navigate("/sign-in");
				} else {
					const errorMessage = await response.text();
					setError(errorMessage);
				}
			})
			.catch((e) => {
				setError(e.message);
			})
	};

	return (
		<div className="container">
			<div className="header-text">Profile</div>
			{error && (
				<div className="error-box">
					{error}
				</div>
			)}
			<div className="text">
				<p><b>Name:</b> {name}</p>
				<p><b>Email:</b> {email}</p>
				<p><b>Role:</b> {role}</p>
			</div>

			<hr />

			<div className="text">
				<label className="label">
					Change Email
					<input
						type="email"
						placeholder="New email"
						onChange={(e) => setNewEmail(e.target.value)}
					/>
				</label>
				<button className="sign-in" onClick={handleEmailChange}>
					Update Email
				</button>
			</div>

			<div className="text">
				<label className="label">
					Change Password
					<input
						type="password"
						placeholder="New password"
						onChange={(e) => setNewPassword(e.target.value)}
					/>
				</label>
				<button className="sign-in" onClick={handlePasswordChange}>
					Update Password
				</button>
			</div>

			<hr />

			<button className="sign-up" onClick={handleLogout}>
				Log Out
			</button>
		</div>
	);
}




