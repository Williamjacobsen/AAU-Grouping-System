import useIsQuestionnaireDeadlineExceeded from "hooks/useIsQuestionnaireDeadlineExceeded";
import React, { useState, memo } from "react";

const ProjectsTable = memo(({ projects, setProjects, session, user }) => {
	const [newProjectName, setNewProjectName] = useState("");
	const [newProjectDescription, setNewProjectDescription] = useState("");

	const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session);

	function getDoesDeadlineBlockUser() {
		return user.role !== "Coordinator" && isDeadlineExceeded();
	}

	function getIsStudentUserAllowedToMakeProjects() {
		return user.role !== "Student" || session.allowStudentProjectProposals;
	}

	function getIsUserAllowedToChangeProject(project) {
		return user.role === "Coordinator" || !isDeadlineExceeded() && project.creatorUserId === user.id;
	}

	const onDelete = (project) => {
		fetch(`${process.env.REACT_APP_API_BASE_URL}/project/delete/${project.id}/${session.id}`,
			{
				method: 'DELETE',
				credentials: "include"
			})
			.then(response => {
				if (!response.ok) {
					throw new Error('Network response was not ok');
				} else {
					setProjects(prevProjects => prevProjects.filter(p => p.id !== project.id));
				}
			})
			.catch(error => {
				console.error('Error deleting project:', error);
			});
	};

	async function onAdd() {
		const projectData = {
			name: newProjectName,
			description: newProjectDescription,
		};

		await fetch(`${process.env.REACT_APP_API_BASE_URL}/project/create/${session.id}/${newProjectName}/${newProjectDescription}`, {
			method: 'POST',
			credentials: "include",
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
				setProjects(prevProjects => [...prevProjects, { id: data.id, ...projectData }]);
				setNewProjectName("");
				setNewProjectDescription("");
			})
			.catch(error => {
				console.error('Error adding project:', error);
			});

		window.location.reload(); // Refresh changes (else "getIsUserAllowedToChangeProject()"" doesn't work properly, for some odd reason)
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
				{projects !== null && projects.map((project) => (
					<tr key={project.id || project._id || project.name}>
						<td className="project-name">{project.name}</td>
						<td className="project-description">{project.description}</td>
						<td className="project-actions">
							<button
								className="btn-delete"
								onClick={() => onDelete(project)}
								disabled={!getIsUserAllowedToChangeProject(project)}>
								Delete
							</button>
						</td>
					</tr>
				))}
			</tbody>
			<tfoot style={{ borderTop: "3px solid #3498db" }}>
				{getDoesDeadlineBlockUser() &&
					<tr>
						Questionnaire deadline is exceeded. Only the coordinator can make changes to projects now.
					</tr>
				}
				{!getDoesDeadlineBlockUser() &&
					<>
						{!getIsStudentUserAllowedToMakeProjects() &&
							< tr >
								Your coordinator does not allow students to create their own proposals.
							</tr>
						}
						{getIsStudentUserAllowedToMakeProjects() &&
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
									<button
										className="btn-add"
										onClick={() => onAdd()}
									>
										Add Project
									</button>
								</td>
							</tr>
						}
					</>
				}
			</tfoot>
		</table>
	);
});

export default ProjectsTable;