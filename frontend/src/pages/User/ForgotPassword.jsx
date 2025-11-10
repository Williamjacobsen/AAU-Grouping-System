import { useState } from "react";
import "./User.css";

export default function ForgotPassword() {

	const [email, setEmail] = useState("");
	const [error, setError] = useState("");
	const [succes, setSucces] = useState("");

	const handleEmailSubmit = async () => {
		try {
			const response = await fetch("http://localhost:8080/auth/forgotPassword", {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ email }),
			})
			if (response.ok) {
				setSucces("An reset-link has been sent to your email!");
				setError("");
			}
			else {
			setError("The email is not valid");
			setSucces("");
			}
		} catch (e) {
			setError(e.message)
			setSucces("");
		}
	};


	return (
		<div className="container">
			<h2>Forgot your password?</h2>
			<p>
				No problem! Enter your email address and we’ll send you a password reset link.
			</p>

			<div className="header-text"> Email</div>
			{error && (
				<div className="error-box">
					{error}
				</div>
			)}
			{succes && (
				<div className="succes-box">
					{succes}
				</div>
			)}
			<div className="input">
				<label className="label">
					<input type="emailOrId" onChange={(e) => setEmail(e.target.value)} placeholder="john123@example.com" />
				</label>
			</div>
			<div className="submit-container">
				<button className="submit" onClick={() => handleEmailSubmit(email)}>
					Submit
				</button>
			</div>
		</div>
	);


}