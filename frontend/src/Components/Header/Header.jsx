import React from "react";
import { Outlet, Link } from "react-router-dom";
import { useGetSessionByUserOrParameter } from "../../hooks/useGetSession";
import { useAuth } from "../../ContextProviders/AuthProvider"

import "./Header.css";

export default function Header() {

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingSession, session } = useGetSessionByUserOrParameter(user);
	
	if (isLoadingUser) {
		return <>Loading user...</>
	}
	if (isLoadingSession) {
		return <>Loading session...</>
	}	

	return (
		<>
			<div className="sticky">
				<div className="header">
					<ul>
						<li>
							<Link to="/">About</Link>
						</li>
						{!user &&
							<li>
								<Link to="/sign-in">Sign in or up</Link>
							</li>
						}
						{user &&
							<li>
								<Link to="/profile">Profile</Link>
							</li>
						}
						{user?.role === "Coordinator" &&
							<li>
								<Link to="/sessions">Sessions</Link>
							</li>
						}
					</ul>
				</div>
				{session &&
					<div className="header">
						<ul>
							<li>
								<b>Session</b>: {session.name}
							</li>
							{user?.role !== "Supervisor" &&
								<li>
									<Link to={`/session/${session.id}/status`}>Status</Link>
								</li>
							}
							<li>
								<Link to={`/session/${session.id}/projects`}>Projects</Link>
							</li>
							{user?.role === "Coordinator" &&
								<li>
									<Link to={`/session/${session.id}/setup`}>Setup</Link>
									<Link to={`/session/${session.id}/supervisorsPage`}>Supervisors</Link>
								</li>
							}
							{user?.role === "Coordinator" &&
								<li>
									<Link to={`/session/${session.id}/groupManagement`}>Group Management</Link>
								</li>
							}
							{user?.role === "Student" &&
								<li>
									<Link to={`/session/${session.id}/studentQuestionnaire`}>Questionnaire</Link>
								</li>
							}
						</ul>
					</div>
				}
			</div>
			<Outlet />
		</>
	);
}