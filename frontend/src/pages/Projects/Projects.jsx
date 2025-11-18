import React from "react";
import { useParams } from "react-router-dom";

import ProjectsTable from "./ProjectsTable";
import "./Projects.css";

import { useAppState } from "ContextProviders/AppStateContext";
import { useAuth } from "ContextProviders/AuthProvider";

export default function Project() {

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading, projects, setProjects, session } = useAppState();

	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return <div className="access-denied-message">Access denied: Not logged in.</div>;
	if (isLoading) {
		return (
			<div className="loading-message">Fetching projects from database...</div>
		);
	}

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
