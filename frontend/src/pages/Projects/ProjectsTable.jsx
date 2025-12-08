import useIsQuestionnaireDeadlineExceeded from "hooks/useIsQuestionnaireDeadlineExceeded";
import React, { useState, memo } from "react";

const ProjectsTable = memo(({ projects, setProjects, session, user }) => { // component to prevent re-rendering
	// state for input fields when creating new project
	const [newProjectName, setNewProjectName] = useState("");
	const [newProjectDescription, setNewProjectDescription] = useState("");
	// state to track which project is expanded, and only allow one to be expanded
	const [expandedProjectId, setExpandedProjectId] = useState(null);
	const toggleExpanded = (projectId) =>
		setExpandedProjectId((prev) => (prev === projectId ? null : projectId));

	const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session); // hook that checks if Q deadline has been exceeded

	function getDoesDeadlineBlockUser() {
		return user.role !== "Coordinator" && isDeadlineExceeded(); // blocks non-coordinators from making changes after Q has been exceeded
	}

	function getIsStudentUserAllowedToMakeProjects() {
		return user.role !== "Student" || session.allowStudentProjectProposals;
	}

	function getHasStudentAlreadyCreatedProject() {
		if (user.role !== "Student") return false;
		return projects.some(project => project.creatorUserId === user.id);
	}

	function getIsUserAllowedToChangeProject(project) {
		return user.role === "Coordinator" || (!isDeadlineExceeded() && project.creatorUserId === user.id);
	}

	const onDelete = (project) => { // handles project deletion via API call
		fetch(`${process.env.REACT_APP_API_BASE_URL}/api/project/delete/${project.id}/${session.id}`,
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

	async function onAdd() { // handles adding projects via API call
		// Check if student already has a project
		if (user.role === "Student" && getHasStudentAlreadyCreatedProject()) {
			alert("You have already created a project proposal. Students are only allowed to create one project proposal per session.");
			return;
		}

		const projectData = {
			name: newProjectName,
			description: newProjectDescription,
		};

		await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/project/create/${session.id}/${newProjectName}/${newProjectDescription}`, {
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
			.then(data => { // project added to list of projects
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
						<td className="project-description">
							<div
								className={`description-content ${expandedProjectId === project.id ? "expanded" : "collapsed"
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
							{user.role !== "Student" && (
								<button
									className="btn-delete"
									onClick={() => onDelete(project)}
									disabled={!getIsUserAllowedToChangeProject(project)}>
									Delete
								</button>
							)}
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
									<textarea
										value={newProjectDescription}
										onChange={(e) => setNewProjectDescription(e.target.value)}
										placeholder="New Project Description"
										className="input-project-description"
										style={{ width: "100%", resize: 'vertical' }}
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
