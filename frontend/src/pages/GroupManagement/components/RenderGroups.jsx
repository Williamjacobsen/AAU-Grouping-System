import React, { memo } from "react";

const RenderGroups = memo(({
	groups, allGroups, supervisors, assignSupervisor,
	assignProject, handleGroupClick, selectedGroup,
	handleStudentClick, selectedStudent, students, projects, 
}) => {

	function getProject(projectId) {
		return projects.find(project => project.id === projectId);
	}

	const supervisorLoad = {};

	supervisors?.forEach(supervisor => {
		supervisorLoad[supervisor.id] = allGroups.filter(g => g.supervisorId === supervisor.id).length;
	});

	function remainingSlots(supervisor) {
		const used = supervisorLoad[supervisor.id] || 0;
		return supervisor.maxGroups - used;
	}

	return groups.map((group) => {
		return (
			<div className="group-box" key={group.id}>
				<div className="assign-row">
				<div className="assign-button">
					<p>Current Supervisor: </p>
					<select defaultValue="" onChange={(e) => assignSupervisor(group.id, e.target.value)}>
						<option value="" disabled> {supervisors?.find(s => s.id === group.supervisorId)?.name || "None"} </option>
						{supervisors?.map((supervisor) => {
							const remaining = remainingSlots(supervisor);
							return (
								<option
									key={supervisor.id}
									value={supervisor.id}
									disabled={remaining <= 0}
								>
									{supervisor.name} ({remaining})
								</option>
							);
						})}
					</select>
				</div>
				<div className="assign-button">
					<p>Assigned project: </p>
					<select
						defaultValue={group.desiredProjectId1 || ""}
						onChange={(e) => assignProject(group.id, e.target.value)}>
						<option value="" disabled> {projects?.find(p => p.id === group.desiredProjectId1)?.name || "None"} </option>
						{projects?.map(project => (
							<option key={project.id} value={project.id}>
								{project.name}
							</option>
						))}
					</select>
				</div>
				</div>
				<h4 onClick={() => handleGroupClick(group.id)}
					className={selectedGroup && selectedGroup.from === group.id ? "selected" : ""}>
					<span className="group-name">{group.name}</span> <br />
					<span className="group-detail">Size: </span> {group.studentIds.length} <br />
					<span className="group-detail">Preferred size: </span> {group.maxStudents}
				</h4>
				{(group.desiredProjectId1 ||
					group.desiredProjectId2 ||
					group.desiredProjectId3) && (
						<p className="group-detail">
							Projects wishes:{" "}
							<span className="highlight">
								{group.desiredProjectId2 ?
									" 2. " + getProject(group.desiredProjectId2)?.name
									: ""}
								{group.desiredProjectId3 ?
									", 3. " + getProject(group.desiredProjectId3)?.name
									: ""}
							</span>
						</p>
					)}
				<ul>
					{group.studentIds.map((studentId, index) => {
						const member = students?.find(s => s.id === studentId);
						if (!member) return null;


						return (
							<li key={index} onClick={() => handleStudentClick(member, group.id)}
								className={selectedStudent && selectedStudent.member.name === member.name ? "selected" : ""
								}> <span className="student-name"> {member.name} </span>
								{(member.questionnaire?.desiredProjectId1 ||
									member.questionnaire?.desiredProjectId2 ||
									member.questionnaire?.desiredProjectId3) && (
										<span className="student-priorities">
											— [
											{member.questionnaire?.desiredProjectId1 ?
												"1. " + getProject(member.questionnaire.desiredProjectId1)?.name
												: ""}
											{member.questionnaire?.desiredProjectId2 ?
												", 2. " + getProject(member.questionnaire.desiredProjectId2)?.name
												: ""}
											{member.questionnaire?.desiredProjectId3 ?
												", 3. " + getProject(member.questionnaire.desiredProjectId3)?.name
												: ""}
											]
										</span>
									)}
								<hr></hr>
							</li>
						)
					})}
				</ul>
			</div>
		);
	});
});


export default RenderGroups;