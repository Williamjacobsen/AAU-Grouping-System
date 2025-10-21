import React from "react";
import { Outlet, Link } from "react-router-dom";

import "./Header.css";

export default function Header() {

	return (
		<>
			<div className="header sticky">
				<ul>
					<li>
						<Link to="/">About</Link>
					</li>
					<li>
						<Link to="/profile">Profile</Link>
					</li>
					<li>
						<Link to="/sessions">Sessions</Link>
					</li>
				</ul>
			</div>
			<Outlet />
		</>
	);
}