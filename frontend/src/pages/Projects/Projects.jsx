import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";

import useGetSessionProjects from "../../hooks/useGetSessionProjects";
import ProjectsTable from "./ProjectsTable";
import "./Projects.css";

export default function Project() {

	const { sessionId } = useParams(); // Get session ID from URL
	const { isLoading: isLoadingProjects, projects: allProjects, setProjects } = useGetSessionProjects(sessionId); // hook, fetch projects from backend

	if (isLoadingProjects) {
    return <div className="loading-message">Fetching projects from database...</div>;
  }

	return (
		<div className="projects-container">
			<h1 className="projects-title">List of Projects</h1>
			<ProjectsTable projects={allProjects} setProjects={setProjects} sessionId={sessionId}/>
		</div>
	)

}