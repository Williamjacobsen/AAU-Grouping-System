import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";

import useGetSessionProjects from "../../hooks/useGetSessionProjects";
import ProjectsTable from "./ProjectsTable";
export default function Project() {

	const { sessionId } = useParams(); // Get session ID from URL
	const { isloading: isLoadingProjects, projects: allProjects, setProjects } = useGetSessionProjects(sessionId); // hook, fetch projects from backend

	if (isLoadingProjects) {
    return <>Fetching projects from database...</>;
  }
console.log("Projects in Projects.jsx: ", allProjects);
	return (
		<>
			<h1>List of projects:</h1>
			<ProjectsTable projects={allProjects} setProjects={setProjects} sessionId={sessionId}/>
		
		</>
	)

	

}