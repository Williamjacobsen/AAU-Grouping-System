import React from "react";
import { Link } from "react-router-dom";

export default function NavigationMenu({ user, session, location }) {

	function getIsActivePage(pathName) {
		if (location.pathname.includes(pathName)) {
			return "active";
		}
		else {
			return "";
		}
	}

	return (
		<>
			<div className="menu navigation-menu">
				<ul className="items">
					<li className="explanation">
						<b>
							Navigate the webpages:
						</b>
					</li>
					<li>
						<Link
							to="/about"
							className={getIsActivePage("/about")}
						>
							About
						</Link>
					</li>
					{!user &&
						<li>
							<Link
								to="/sign-in"
								className={getIsActivePage("/sign-in")}>
								Sign in or up
							</Link>
						</li>
					}
					{user &&
						<li>
							<Link
								to="/profile"
								className={getIsActivePage("/profile")}>
								My Profile
							</Link>
						</li>
					}
					{user?.role === "Coordinator" &&
						<li>
							<Link
								to="/sessions"
								className={getIsActivePage("/sessions")}
							>
								My Sessions
							</Link>
						</li>
					}
					{session &&
						<>
							<li className="session-name">
								<b>Selected session</b>: {session?.name}
							</li>
							{user?.role !== "Supervisor" &&
								<li>
									<Link
										to={`/session/${session.id}/students`}
										className={getIsActivePage(`/session/${session.id}/students`)}
									>
										Students
									</Link>
								</li>
							}
							<li>
								<Link
									to={`/session/${session.id}/projects`}
									className={getIsActivePage(`/session/${session.id}/projects`)}
								>
									Projects
								</Link>
							</li>
							{user?.role === "Coordinator" &&
								<>
									<li>
										<Link
											to={`/session/${session.id}/supervisorsPage`}
											className={getIsActivePage(`/session/${session.id}/supervisorsPage`)}
										>
											Supervisors
										</Link>
									</li>
									<li>
										<Link
											to={`/session/${session.id}/setup`}
											className={getIsActivePage(`/session/${session.id}/setup`)}
										>
											Session Setup
										</Link>
									</li>
									<li>
										<Link
											to={`/session/${session.id}/groupManagement`}
											className={getIsActivePage(`/session/${session.id}/groupManagement`)}
										>
											Finalize Groups
										</Link>
									</li>
								</>
							}
							{user?.role === "Student" &&
								<>
									<li>
										<Link
											to={`/session/${session.id}/my-group`}
											className={getIsActivePage(`/session/${session.id}/my-group`)}
										>
											My Group
										</Link>
									</li>
									<li>
										<Link
											to={`/session/${session.id}/studentQuestionnaire`}
											className={getIsActivePage(`/session/${session.id}/studentQuestionnaire`)}
										>
											My Preferences
										</Link>
									</li>
								</>
							}
						</>
					}
				</ul>
			</div>
		</>
	);
}