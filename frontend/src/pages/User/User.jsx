import React from "react";
import { usestate } from "react";
import "./User.css";

export default function User() {
	const [password, setPassword] = usestate("");
	const [email, setEmail] = usestate("");
	const [name, setName] = usestate(""); // later

	const handleLogin = () => {
		console.log(email);
		console.log(password);
	}

	return (

		<div className="container">
			<div className="header-text">Sign-in</div>
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
				<button className="log-in" onClick={handleLogin}>
					Log In
				</button>
				<button className="sign-up">
					Sign Up
				</button>
				<div className="forgot-password">
					Forgot password?
				</div>
			</div>
		</div>


	);
}