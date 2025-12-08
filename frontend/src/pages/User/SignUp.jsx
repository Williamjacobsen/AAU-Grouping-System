import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import "./User.css";

export default function SignUp() {

	const navigate = useNavigate();

	const [password, setPassword] = useState("");
	const [email, setEmail] = useState("");
	const [name, setName] = useState("");
	const [error, setError] = useState("");
	const [success, setSuccess] = useState("");
	const navTimer = useRef(null);

	const handleSignUp = async (email, password, name, setError, navigate) => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/coordinator/signUp`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ email, password, name }),
				credentials: "include"
			})

			if (!response.ok) {
				const errorMessage = await response.text();
				setError(errorMessage);
				return Promise.resolve();
			}
			// loads success message and navigates to sign in after 3 seconds
			setSuccess("Account created successfully! You will be redirected to Sign In");
			navTimer.current = setTimeout(() => navigate("/sign-in"), 3000);

		} catch (e) {
			setError(e.message);
		}
	}

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(""), 10000);
			return () => clearTimeout(timer);
		}
	}, [error])

	useEffect(() => {
		return () => {
			if (navTimer.current) {
				clearTimeout(navTimer.current);
			}
		};
	}, []);

	return (
		<div className="container">
			<div className="header-text">Sign Up</div>

			{error && (<div className="error-box">{error}</div>)}
			{success && (<div className="success-box">{success}</div>)}

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
						<input type="password" onChange={(e) => setPassword(e.target.value)} 
						onKeyDown={(e) => {
								if (e.key === "Enter") {
									handleSignUp(email, password, name, setError, navigate);
								}
							}}
						placeholder="******" />
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

