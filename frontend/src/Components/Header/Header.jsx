import React from "react";
import { Outlet, useLocation } from "react-router-dom";

import { useGetSessionByUserOrParameter } from "../../hooks/useGetSession";
import { useAuth } from "../../ContextProviders/AuthProvider";
import "./Header.css";
import StepByStepGuide from "./StepByStepGuide";
import NavigationMenu from "./NavigationMenu";

export default function Header() {

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingSession, session } = useGetSessionByUserOrParameter(user);

	const location = useLocation();

	if (isLoadingUser) {
		return <>Loading user...</>;
	}
	if (isLoadingSession) {
		return <>Loading session...</>;
	}

	return (
		<>
			<header className="sticky-header">
				<NavigationMenu
					user={user}
					session={session}
					location={location}
				/>
				<StepByStepGuide
					user={user}
					session={session}
					location={location}
					isLoadingSession={isLoadingSession}
				/>
			</header>
			<Outlet />
		</>
	);
}