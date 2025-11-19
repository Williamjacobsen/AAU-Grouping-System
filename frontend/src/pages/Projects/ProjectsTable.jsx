import React, { useState, useMemo, memo } from "react";

const ProjectsTable = memo(({ projects, setProjects, sessionId }) => {
  const [newProjectName, setNewProjectName] = useState("");
  const [newProjectDescription, setNewProjectDescription] = useState("");

	const [expandedProjectId, setExpandedProjectId] = useState(null);
  const toggleExpanded = (projectId) =>
    setExpandedProjectId((prev) => (prev === projectId ? null : projectId));

  const onDelete = (project) => {
    fetch(`http://localhost:8080/project/delete/${project.id}`, {
      method: "DELETE",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        } else {
          setProjects((prevProjects) =>
            prevProjects.filter((p) => p.id !== project.id)
          );
        }
      })
      .catch((error) => {
        console.error("Error deleting project:", error);
      });
  };

  const onAdd = () => {
    const projectData = {
      name: newProjectName,
      description: newProjectDescription,
    };

    fetch(
      `http://localhost:8080/project/create/${sessionId}/${newProjectName}/${newProjectDescription}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(projectData),
      }
    )
      .then((response) => {
        if (!response.ok) {
          throw new Error("Network response was not ok");
        }
        return response.json();
      })
      .then((data) => {
        setProjects((prevProjects) => [
          ...prevProjects,
          { id: data.id, ...projectData },
        ]);
        setNewProjectName("");
        setNewProjectDescription("");
      })
      .catch((error) => {
        console.error("Error adding project:", error);
      });
  };

  return (
    <table className="projects-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Description</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {projects !== null &&
          projects.map((project) => (
            <tr key={project.id || project._id || project.name}>
              <td className="project-name">{project.name}</td>
              <td className="project-description">
              <div
                className={`description-content ${
                  expandedProjectId === project.id ? "expanded" : "collapsed"
                }`}
              >
                {project.description}
              </div>
              <button
                className="btn-expand"
                onClick={() => toggleExpanded(project.id)}
              >
                {expandedProjectId === project.id ? "Show Less" : "Show More"}
              </button>
							</td>
              <td className="project-actions">
                <button
                  className="btn-delete"
                  onClick={() => onDelete(project)}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
      </tbody>
      <tfoot style={{ borderTop: "3px solid #3498db" }}>
        <tr className="add-project-row">
          <td>
            <input
              value={newProjectName}
              onChange={(e) => setNewProjectName(e.target.value)}
              placeholder="New Project Name"
              className="input-project-name"
            />
          </td>
          <td>
            <input
              value={newProjectDescription}
              onChange={(e) => setNewProjectDescription(e.target.value)}
              placeholder="New Project Description"
              className="input-project-description"
            />
          </td>
          <td>
            <button className="btn-add" onClick={onAdd}>
              Add Project
            </button>
          </td>
        </tr>
      </tfoot>
    </table>
  );
});

export default ProjectsTable;
