import React from "react";
import { useParams } from "react-router-dom";

import ProjectsTable from "./ProjectsTable";
import "./Projects.css";

import { useAppState } from "ContextProviders/AppStateContext";
import { useAuth } from "ContextProviders/AuthProvider";

export default function Project() {

	const { isLoading: isLoadingUser, user } = useAuth(); // get auth state - returns user object and loading state
	const { isLoading, projects, setProjects, session } = useAppState(); // get application state incl. projects list, session info etc.

	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return <div className="access-denied-message">Access denied: Not logged in.</div>; // deny access if user is not logged in
	if (isLoading) {
		return (
			<div className="loading-message">Fetching projects from database...</div>
		);
	}
// render main page with title and table and pass down to projectsTable component
	return ( 
		<div className="projects-container">
			<h1 className="projects-title">List of Projects</h1>
			<ProjectsTable
				projects={projects}
				setProjects={setProjects}
				session={session}
				user={user}
			/>
		</div>
	);
}
