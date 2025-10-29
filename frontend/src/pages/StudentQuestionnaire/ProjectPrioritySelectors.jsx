import React, { useState } from "react";

export default function ProjectPrioritySelectors({ projects, name, desiredProjectIds }) {

  const [priorities, setPriorities] = useState(desiredProjectIds);

  function setPriority(index, value) {
		setPriorities((previousArray) => {
			
      const newArray = [...previousArray]; // The spread operator "..." copies the array.
      newArray[index] = value;
      
      // Clear lower priorities when a higher priority changes
      for (let i = index + 1; i < newArray.length; i++) {
        newArray[i] = "";
      }
      
      return newArray;
		});
  }

	function PrioritySelector({ priorityIndex }) {
		
    return (
      <select
        value={priorities[priorityIndex]}
        onChange={(event) => setPriority(priorityIndex, event.target.value)}
        disabled={priorityIndex > 0 && !priorities[priorityIndex - 1]}
      >
        <option value="">
          None
				</option>
				
        {projects.map(project => {
          // Check if this project is already selected in a higher priority
          let isProjectAlreadyAPriority = false;
          for (let i = 0; i < priorityIndex; i++) {
            if (project.id === priorities[i]) {
              isProjectAlreadyAPriority = true;
              break;
            }
          }
          
          // Disabled the option if it's already selected in a higher priority
          return (
						<option key={project.id} value={project.id} disabled={isProjectAlreadyAPriority}>
							{project.name}
						</option>
					);
				})}
				
      </select>
    );
	}

  return (
    <>
      <br />
      1st priority:
      <PrioritySelector priorityIndex={0} />
      <br />
      2nd priority:
      <PrioritySelector priorityIndex={1} />
      <br />
      3rd priority:
      <PrioritySelector priorityIndex={2} />
      
      {/* Hidden input that FormData will capture */}
      <input 
        type="hidden" 
        name={name} 
        value={priorities} 
      />
    </>
  );
}