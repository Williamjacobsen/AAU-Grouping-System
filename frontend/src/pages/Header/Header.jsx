import React from "react";
import { Outlet, Link } from "react-router-dom";

import "./Header.css";

export default function Header() {
	return (
		<div>
			<div className="header sticky">
				<ul>
					<li>
						<Link to="/">About</Link>
					</li>
					<li>
						<Link to="/user">Profile</Link>
					</li>
					<li>
						<Link to="/user/sessions">*NOT IMPLEMENTED*</Link>
					</li>
				</ul>
			</div>
			<Outlet />
		</div>
	);
}