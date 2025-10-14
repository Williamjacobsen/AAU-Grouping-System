import React from "react";
import { Outlet, Link } from "react-router-dom";

import "./Header.css";

export default function Header() {

	// TODO: Add proper hooks
	const latestSession = {};
	latestSession.name = "testSessionName"; 
	latestSession.id = "0"; 

	return (
		<>
			<div className="header sticky">
				<ul>
					<li>
						<Link to="/">About</Link>
					</li>
					<li>
						<Link to="/sign-in">Profile</Link>
					</li>
					<li>
						<Link to="/sessions">Sessions</Link>
					</li>
				</ul>
				<ul>
					<li>
						<Link to={`/session/${latestSession.id}`}>Status</Link>
					</li>
					<li>
						<b>Session</b>: {latestSession.name}
					</li>
				</ul>
			</div>
			<Outlet />
		</>
	);
}