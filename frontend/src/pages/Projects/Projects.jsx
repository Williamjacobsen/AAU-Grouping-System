import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";

import useGetSessionProjects from "./useGetSessionProjects";
import ProjectsTable from "./ProjectsTable";

export default function Project() {

	const { id: sessionId } = useParams(); // Get session ID from URL
	const { isloading: isLoadingProjects, projects: allProjects } = useGetSessionProjects(sessionId); // Get projects from backend

	if (isLoadingProjects) {
    return <>Fetching projects from database...</>;
  }

	return (
		<>
			<h1>List of projects:</h1>
			<ProjectsTable projects={allProjects}/>
		</>
	)

	

}