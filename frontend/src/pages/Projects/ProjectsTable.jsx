import React, { useMemo, memo } from "react";

const ProjectsTable = memo(({ projects }) => {
  return (
    <table>
      <tr>
        <th>Name:</th>
        <th>Description:</th>
      </tr>
      {projects!==null&&projects.map((project) => (
        <tr>
          <th>{project.name}</th>
          <th>{project.description}</th>
        </tr>
      ))}
      TODO: Lav en tabel over "projects"-variablen.
    </table>
  );
});

export default ProjectsTable;
