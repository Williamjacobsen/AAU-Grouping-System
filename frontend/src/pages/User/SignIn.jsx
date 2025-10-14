import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./User.css";

export default function SignIn() {

	const navigate = useNavigate();

	const [password, setPassword] = useState("");
	const [email, setEmail] = useState("");
	const [error, setError] = useState("");

	const handleSignIn = () => {
		fetch("http://localhost:8080/auth/login", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ email, password }),
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