import { useEffect, useState } from "react";
import "./User.css";

export default function ForgotPassword() {

	const [email, setEmail] = useState("");
	const [error, setError] = useState("");
	const [success, setSuccess] = useState("");

	const handleEmailSubmit = async () => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/auth/forgotPassword`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ email }),
			});
			if (response.ok) {
				setSuccess("An reset-link has been sent to your email!");
				setError("");
			}
			else {
				setError("The email is not valid");
				setSuccess("");
			}
		} catch (e) {
			setError(e.message);
			setSuccess("");
		}
	};

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [error]);

	useEffect(() => {
		if (success) {
			const timer = setTimeout(() => setSuccess(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [success]);


	return (
		<div className="container">
			<h2>Forgot your password?</h2>
			<p>
				If you are a <b>coordinator</b>,
				enter your email address,
				and we’ll send you a password reset link.
			</p>
			<p>
				If you are <i>not</i> a <b>coordinator</b>,
				you will have to find the email containing login credentials
				that we have previously sent you.
				If you have lost this email,
				you may contact your coordinator
				and ask them to reset your password.
			</p>

			<div className="header-text"> Email</div>

			{error && (<div className="error-box">{error}</div>)}
			{success && (<div className="success-box">{success}</div>)}

			<div className="input">
				<label className="label">
					<input type="emailOrId" onChange={(e) => setEmail(e.target.value)} 
					onKeyDown={(e) => {
								if (e.key === "Enter") {
									handleEmailSubmit(email);
								}
							}}
					placeholder="john123@example.com" />
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