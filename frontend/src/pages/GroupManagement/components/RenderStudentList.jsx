
export default function RenderStudentList({
	localStudentsWithNoGroup, groupsWith1Member,
	selectedStudent, handleStudentClick, students, projects
}) {

	const hasStudents = localStudentsWithNoGroup && localStudentsWithNoGroup.length > 0;
	const hasSingleGroups = groupsWith1Member && groupsWith1Member.length > 0;

	if (!hasStudents && !hasSingleGroups) {
		return <p>No students without a group</p>;
	}

	function getProject(projectId) {
		return projects?.find(p => p.id === projectId);
	}

	return (
		<div className="group-box no-group-box">
			<h4>Students without a group</h4>

			{hasStudents && (
				<ul>
					{localStudentsWithNoGroup.map((member) => (
						<li key={member.id} onClick={() => handleStudentClick(member, null)}
							className={selectedStudent && selectedStudent.member.id === member.id ? "selected" : ""
							}> <span className="student-name"> {member.name} </span>
							{(member.questionnaire?.desiredProjectId1 ||
								member.questionnaire?.desiredProjectId2 ||
								member.questionnaire?.desiredProjectId3) && (
									<span className="student-priorities">
										— [
										{getProject(member.desiredProjectId1)?.name || ""}
										{member.questionnaire?.desiredProjectId2
											? ", " + getProject(member.desiredProjectId2)?.name
											: ""}
										{member.questionnaire?.desiredProjectId3
											? ", " + getProject(member.desiredProjectId3)?.name
											: ""}
										]
									</span>
								)}
							<hr></hr>
						</li>
					))}
				</ul>
			)}


			{hasSingleGroups && (
				<>
					<h4>Students from 1-member groups</h4>
					<ul>
						{groupsWith1Member.map((group) =>
							group.studentIds.map((studentId) => {
								const member = students?.find(s => s.id === studentId);
								if (!member) return null;
								return (
									<li key={member.id} onClick={() => handleStudentClick(member, group.id)}
										className={selectedStudent && selectedStudent.member.name === member.name ? "selected" : ""
										}> <span className="student-name"> {member.name} ({group.name})</span>
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
								);
							})
						)}
					</ul>
				</>
			)}
		</div>
	);
}
