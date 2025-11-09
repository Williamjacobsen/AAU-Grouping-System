import React, { useState, memo } from "react";

const ProjectsTable = memo(({ projects, setProjects, sessionId }) => {
	const [newProjectName, setNewProjectName] = useState("");
	const [newProjectDescription, setNewProjectDescription] = useState("");

	const onDelete = (project) => {
		fetch(`http://localhost:8080/project/delete/${project.id}`, { method: 'DELETE' })
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				} else {
					setProjects(prevProjects => prevProjects.filter(p => p.id !== project.id));
				}
			});
	};

	const onAdd = () => {
		const projectData = {
			name: newProjectName,
			description: newProjectDescription,
		};

		fetch(`http://localhost:8080/project/create/${sessionId}/${newProjectName}/${newProjectDescription}`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(projectData),
		})
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				}
				return response.json();
			})
			.then(data => {
				// Update the projects state with the new project
				setProjects(prevProjects => [...prevProjects, { id: data.id, ...projectData }]);
				// Clear input fields
				setNewProjectName("");
				setNewProjectDescription("");
			})
			.catch(error => {
				console.error('Error adding project:', error);
			});
	};

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
							<button onClick={() => onDelete(project)}>Delete</button>
						</td>
					</tr>
				))}
			</tbody>
			<tfoot>
				<tr>
					<td>
						<input
							value={newProjectName}
							onChange={(e) => setNewProjectName(e.target.value)}
							placeholder="New Project Name"
						/>
					</td>
					<td>
						<textarea
							value={newProjectDescription}
							onChange={(e) => setNewProjectDescription(e.target.value)}
							placeholder="New Project Description"
						/>
					</td>
					<td>
						<button onClick={onAdd}>Add Project</button>
					</td>
				</tr>
			</tfoot>
		</table>
	);
});

export default ProjectsTable;