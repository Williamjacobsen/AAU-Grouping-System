import React, { useMemo, memo } from "react";

// ...existing code...
{ /* replaced component to add keys and action buttons */ }
const ProjectsTable = memo(({ projects }) => {
 const onDelete = (project)	=> {
	fetch(`http://localhost:8080/project/${project.id}`, { method: 'DELETE' })
 };
 const onEdit = () => {};


	return (
    <table>
      <thead>
        <tr>
          <th>Name</th>
          <th>Description</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {projects !== null && projects.map((project) => (
          <tr key={project.id || project._id || project.name}>
            <td>{project.name}</td>
            <td>{project.description}</td>
            <td>
              <button onClick={() => onEdit(project)}>Edit</button>
              <button onClick={() => onDelete(project)}>Delete</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
});

export default ProjectsTable;