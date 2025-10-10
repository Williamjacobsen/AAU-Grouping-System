import React from "react";
import "./User.css";

export default function User() {
	return (

		<div className="container"> 
			<div className="header-text">Sign-in</div>
			<div className="text"> 
				<div className="input">
					<label className="label"> Email
						<input type="email" placeholder = "John123@email.com"/>
					</label>
				</div>
				<div className="input">
					<label className="label"> Password:
						<input type="password" placeholder = "******" />
					</label>
				</div>
			</div>
			<div className="submit-container">
				<div className="log-in"> Log In
				</div>
				<div className="sign-up"> Sign Up
				</div>
				<div className="forgot-password"> Forgot password?
				</div>
			</div>
		</div>


	);
}