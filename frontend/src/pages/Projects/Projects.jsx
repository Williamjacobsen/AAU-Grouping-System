import React from "react";
import { useParams } from "react-router-dom";

import ProjectsTable from "./ProjectsTable";
import "./Projects.css";

import { useAppState } from "ContextProviders/AppStateContext";

export default function Project() {
  const { sessionId } = useParams();

  const { isLoading, projects, setProjects } = useAppState();

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
        sessionId={sessionId}
      />
    </div>
  );
}
