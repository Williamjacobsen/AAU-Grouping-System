import { useEffect, useState } from "react";
import "./User.css";

export default function ResetPassword() {

	const [newPassword, setNewPassword] = useState("");
	const [confirmPassword, setConfirmPassword] = useState("");
	const [error, setError] = useState("");
	const [success, setSucces] = useState("");

	const token = new URLSearchParams(window.location.search).get("token"); // get token from URL

	const handlePasswordSubmit = async () => {
		if (newPassword !== confirmPassword) {
			setError("Passwords do not match.");
			setSucces("");
			return;
		}

		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/auth/resetPassword`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ token, newPassword }),
			});

			if (response.ok) {
				setSucces("Your password has been reset successfully!");
				setError("");
			} else {
				setError("Failed to reset password.");
				setSucces("");
			}
		} catch (e) {
			setError(e.message);
			setSucces("");
		}
	};

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [error])

	useEffect(() => {
		if (success) {
			const timer = setTimeout(() => setSucces(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [success])

	return (
		<div className="container">
			<h2>Reset your password</h2>
			<p>
				Enter your new password below. Make sure it’s something secure that you can remember.
			</p>

			<div className="header-text">
				New password
			</div>

			{error && <div className="error-box">{error}</div>}
			{success && <div className="success-box">{success}</div>}

			<div className="input">
				<label className="label">
					<input
						type="password"
						placeholder="Enter new password"
						value={newPassword}
						onChange={(e) => setNewPassword(e.target.value)}
					/>
				</label>
			</div>

			<div className="input">
				<label className="label">
					<input
						type="password"
						placeholder="Confirm new password"
						value={confirmPassword}
						onChange={(e) => setConfirmPassword(e.target.value)}
						onKeyDown={(e) => {
								if (e.key === "Enter") {
									handlePasswordSubmit();
								}
							}}
					/>
				</label>
			</div>

			<div className="submit-container">
				<button className="submit" onClick={handlePasswordSubmit}>
					Reset Password
				</button>
			</div>
		</div>
	);
}
