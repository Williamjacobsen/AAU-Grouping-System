import React, { useState } from "react";
import { Outlet, useLocation, Link } from "react-router-dom";

import { useGetSessionByUserOrParameter } from "../../hooks/useGetSession";
import { useAuth } from "../../ContextProviders/AuthProvider";
import "./Header.css";

export default function Header() {

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingSession, session } = useGetSessionByUserOrParameter(user);

	const location = useLocation();

	const [pageHelpText, setPageHelpText] = useState("");

	function Tab({ path, label, helpText }) {
		const isActivePage = location.pathname.includes(path);

		if (isActivePage) {
			setPageHelpText(helpText);
		}

		return (
			<li>
				<Link
					to={path}
					className={isActivePage ? "active" : ""}
				>
					{label}
				</Link>
			</li>
		);
	}

	if (isLoadingUser) {
		return <>Checking user...</>;
	}

	return (
		<>
			<header className="sticky-header">
				<div className="menu">
					<ul className="items">
						{!user &&
							<>
								<Tab path="/about" label="About" helpText="Go to the 'Sign In' page." />
								<Tab path="/sign-in" label="Sign In" helpText="Sign in, or create a coordinator account." />
							</>
						}
						{user?.role === "Coordinator" &&
							<>
								<Tab path="/about" label="About" helpText="Go to the 'My Sessions' page." />
								<Tab path="/profile" label="Profile" helpText="Go to the 'My Sessions' page." />
								<Tab path="/sessions" label="My Sessions" helpText="Step 1) Create a new session. Step 2) Click on 'Edit Setup'." />
								{session &&
									<>
										<li className="session-name"> <b>Selected session</b>: {session?.name} </li>
										<Tab path={`/session/${session.id}/setup`} label="Session Setup" helpText="Step 1) Fill out the form and apply your changes. Step 2) Send login codes to students and supervisors. Step 3) Go to the 'Supervisors' page." />
										<Tab path={`/session/${session.id}/supervisorsPage`} label="Supervisors" helpText="Step 1) Configure max amount of groups per supervisor. Step 2) Go to the 'Projects' page." />
										<Tab path={`/session/${session.id}/projects`} label="Projects" helpText="Step 1) Add some project proposals, or wait for your supervisors to add some. Step 2) Go to the 'Finalize Groups' page." />
										<Tab path={`/session/${session.id}/groupManagement`} label="Finalize Groups" helpText="Step 1) Wait for the deadline to pass. Step 2) Read the tutorial. Step 3) Adjust the groups. Step 4) Download a CSV file of the groups." />
										<Tab path={`/session/${session.id}/students`} label="Students" helpText="On this page, you can see student wishes and how the students have grouped up so far." />
									</>
								}
							</>
						}
						{user?.role === "Supervisor" &&
							<>
								<Tab path="/about" label="About" helpText="Go to the 'Projects' page." />
								<Tab path="/profile" label="Profile" helpText="Go to the 'Projects' page." />
								{session &&
									<>
										<Tab path={`/session/${session.id}/projects`} label="Projects" helpText="Add some project proposals." />
									</>
								}
							</>
						}
						{user?.role === "Student" &&
							<>
								<Tab path="/about" label="About" helpText="Go to the 'Projects' page." />
								<Tab path="/profile" label="Profile" helpText="Go to the 'Projects' page." />
								{session &&
									<>
										<Tab path={`/session/${session.id}/projects`} label="Projects" helpText="Step 1) View the available projects. Step 2) If your coordinator has allowed student project proposals, you may create a single project proposal. Step 3) Go to the 'My Wishes' page." />
										<Tab path={`/session/${session.id}/studentQuestionnaire`} label="My Wishes" helpText="Step 1) Fill out the form. Step 2) Go to the 'Students' page." />
										<Tab path={`/session/${session.id}/students`} label="Students" helpText="On this page, you can view student wishes, view which groups have already been formed, and send a join request to a group. You can also chat with students via the chat box in the lower right corner. Make sure to also take a look at the 'My Group' page." />
										<Tab path={`/session/${session.id}/my-group`} label="My Group" helpText="On this page, you can either cancel your group join request, make a new group, or view or leave your current group. If you are the owner of your group, you can also modify your group's preferences. Make sure to also take a look at the 'Students' page." />
									</>
								}
							</>
						}
					</ul>
				</div >
				<div className="menu help">
					<ul className="items">
						<li>
							<b> Help: </b> {pageHelpText}
						</li>
					</ul>
				</div>
			</header>

			<Outlet />
		</>
	);
}