import React, { useMemo, memo } from "react";

const ProjectsTable = memo(({ projects }) => { //only re-render if data changes (memo)
  return (
    <table>
      <tr>
        <th>Name:</th>
        <th>Description:</th>
      </tr>
      {projects!==null&&projects.map((project) => ( //check if we have projects to show. ()=> means do this for every project
        <tr>
          <th>{project.name}</th>
          <th>{project.description}</th>
        </tr>
      ))}
    </table>
  );
});

export default ProjectsTable;
