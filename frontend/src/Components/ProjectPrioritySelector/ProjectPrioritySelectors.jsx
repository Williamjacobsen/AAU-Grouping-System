import React, { useState, memo } from "react";

const ProjectPrioritySelectors = memo(({
	projects,
	desiredProjectId1Name,
	desiredProjectId2Name,
	desiredProjectId3Name,
	desiredProjectId1,
	desiredProjectId2,
	desiredProjectId3,
	isDisabled = false }) => {

	const [priorities, setPriorities] = useState([
		desiredProjectId1,
		desiredProjectId2,
		desiredProjectId3
	]);

	function setPriority(index, value) {
		setPriorities((previousArray) => {

			const newArray = [...previousArray]; // The spread operator "..." shallow-copies the array.
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
						<option key={project.id} value={project.id} disabled={isProjectAlreadyAPriority || isDisabled}>
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

			{/* Hidden inputs that FormData will capture */}
			<input
				type="hidden"
				name={desiredProjectId1Name}
				value={priorities[0]}
			/>
			<input
				type="hidden"
				name={desiredProjectId2Name}
				value={priorities[1]}
			/>
			<input
				type="hidden"
				name={desiredProjectId3Name}
				value={priorities[2]}
			/>
		</>
	);
});

export default ProjectPrioritySelectors;