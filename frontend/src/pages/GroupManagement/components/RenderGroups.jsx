import React, { memo } from "react";

const RenderGroups = memo(({
	groups, supervisors, assignSupervisor,
	handleGroupClick, selectedGroup,
	handleStudentClick, selectedStudent, students
}) => {

	return groups.map((group) => {
		return (
			<div className="group-box" key={group.id}>
				<div className="AssignSupervisor-button">
					<p>Current Supervisor: </p>
					<select defaultValue="" onChange={(e) => assignSupervisor(group.id, e.target.value)}>
						<option value="" disabled> {supervisors?.find(s => s.id === group.supervisorId)?.name || "None"} </option>
						{supervisors?.map((supervisor) => (
							<option key={supervisor.id} value={supervisor.id}>
								{supervisor.name}
							</option>
						))}
					</select>
				</div>
				<h4 onClick={() => handleGroupClick(group.id)}
					className={selectedGroup && selectedGroup.from === group.id ? "selected" : ""}>
					<span className="group-name">{group.name}</span> <br />
					<span className="group-detail">Size: </span> {group.studentIds.length} <br />
					<span className="group-detail">Preferred size: </span> {group.maxStudents}
				</h4>
				{group.project && (
					<p className="group-detail">
						Project: <span className="highlight">{group.project}</span>
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
											{member.questionnaire?.desiredProjectId1 || ""}
											{member.questionnaire?.desiredProjectId2
												? ", " + member.questionnaire.desiredProjectId2
												: ""}
											{member.questionnaire?.desiredProjectId3
												? ", " + member.questionnaire.desiredProjectId3
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