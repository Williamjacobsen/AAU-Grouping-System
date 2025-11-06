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
				setSucces("An Email has been sent to you!");
				setError("");
			}

		} catch (e) {
			setError(e.message)
			setSucces("");
		}
	};


	return (
		<div className="container">
			<div className="header-text">Submit the email, which you have forgotten the password to</div>
			{error && (
				<div className="error-box">
					{error}
				</div>
			)}
				<div className="input">
					<label className="label">
						Email
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