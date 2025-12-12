import useIsQuestionnaireDeadlineExceeded from "hooks/useIsQuestionnaireDeadlineExceeded";
import React, { useState, memo } from "react";

const ProjectsTable = memo(({ projects, supervisors, students, setProjects, session, user }) => { // "memo" component to prevent re-rendering

	// state for input fields when creating new project
	const [newProjectName, setNewProjectName] = useState("");
	const [newProjectDescription, setNewProjectDescription] = useState("");

	// state to track which project is expanded, and only allow one to be expanded
	const [expandedProjectId, setExpandedProjectId] = useState(null);
	const toggleExpanded = (projectId) =>
		setExpandedProjectId((prev) => (prev === projectId ? null : projectId));

	const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session); // hook that checks if questionnaire deadline has been exceeded

	function getDoesDeadlineBlockUser() {
		return user.role !== "Coordinator" && isDeadlineExceeded(); // blocks non-coordinators from making changes after questionnaire has been exceeded
	}

	function getIsStudentUserAllowedToMakeProjects() {
		return user.role !== "Student" || session?.allowStudentProjectProposals;
	}

	function getIsUserAllowedToDeleteProject(project) {
		return user.role === "Coordinator" || !isDeadlineExceeded() && project.creatorUserId === user.id;
	}

	function getHasStudentAlreadyCreatedProject() {
		if (user.role !== "Student") return false;
		return projects.some(project => project.creatorUserId === user.id);
	}

	function getCreatorNameAsText(project) {
		switch (project.creatorUserRole) {
			case "Coordinator":
				return "";
			case "Supervisor":
				return (supervisors.find(supervisor => supervisor.id === project.creatorUserId)?.name ?? "SUPERVISOR DOES NOT EXIST ANYMORE");
			case "Student":
				return (students.find(student => student.id === project.creatorUserId)?.name ?? "STUDENT DOES NOT EXIST ANYMORE");
			default:
				return "ERROR: Creator role is invalid";
		}
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

		// Check for same project name
		const existingProject = projects.find(project => 
			project.name.toLowerCase().trim() === newProjectName.toLowerCase().trim()
		);
		if (existingProject) {
			alert(`Project with the name "${newProjectName}" already exists in this session`);
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
				if (error.message.includes('already exists')) {
					alert(error.message);
				} else {
					alert('Failed to create project. Please try again.');
				}
			});

		window.location.reload(); // Refresh changes (else "getIsUserAllowedToDeleteProject()"" doesn't work properly, for some odd reason)
	};

	return (
		<table className="projects-table">
			<thead>
				<tr>
					<th>Name</th>
					<th>Creator</th>
					<th>Description</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
				{projects !== null && projects.map((project) => (
					<tr key={project.id || project._id || project.name}>
						<td className="project-name">{project.name}</td>
						<td className="project-creator">
							<b>{project.creatorUserRole}</b>
							<br />
							{getCreatorNameAsText(project)}
						</td>
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
							{getIsUserAllowedToDeleteProject(project) && (
								<button
									className="btn-delete"
									onClick={() => onDelete(project)}
								>
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
