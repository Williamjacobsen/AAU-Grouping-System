

import React from "react";
import { Outlet, Link, useParams } from "react-router-dom";

import "./Header.css";
import useGetSession from "../../utils/useGetSession";

export default function SessionHeader() {

	const { sessionId } = useParams();
	const { isLoading: sessionIsLoading, session } = useGetSession();

	if (sessionIsLoading) {
		return <>Loading session...</>
	}

	return (
		<>
			<div className="header sticky">
				<ul>
					<li>
						<Link to={`/session/${sessionId}/status`}>Status</Link>
					</li>
					<li>
						<Link to={`/session/${sessionId}/projects`}>Projects</Link>
					</li>
					<li>
						<b>Session</b>: {session?.name}
					</li>
				</ul>
			</div>
			<Outlet />
		</>
	);
}

