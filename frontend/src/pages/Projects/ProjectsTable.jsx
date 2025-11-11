import React, { useMemo, memo } from "react";

const ProjectsTable = memo(({ projects }) => { //only re-render if data changes (memo)
  
  if (!projects || projects.length === 0) {
    return <div className="empty-message">No projects found.</div>;
  }

  return (
    <table className="projects-table">
      <thead>
        <tr>
          <th>Name</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        {projects.map((project, index) => ( //check if we have projects to show. .map means do this for every project
          <tr key={project.id || index}>
            <td className="project-name" data-label="Name">{project.name}</td>
            <td className="project-description" data-label="Description">{project.description}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
});

export default ProjectsTable;
