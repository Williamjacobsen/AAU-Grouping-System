
export default function RenderStudentList({ 
	localStudentsWithNoGroup, groupsWith1Member, 
	selectedStudent, handleStudentClick}) {

	const hasStudents = localStudentsWithNoGroup && localStudentsWithNoGroup.length > 0;
	const hasSingleGroups = groupsWith1Member && groupsWith1Member.length > 0;

	if (!hasStudents && !hasSingleGroups) {
		return <p>No students without a group</p>;
	}

	return (
		<div className="group-box no-group-box">
			<h4>Students without a group</h4>

			{hasStudents && (
				<ul>
					{localStudentsWithNoGroup.map((member) => (
						<li key={member.id} onClick={() => handleStudentClick(member, null)}
							className={selectedStudent && selectedStudent.member.name === member.name ? "selected" : ""
							}> <span className="student-name"> {member.name} </span>
							{member.priority1 || member.priority2 || member.priority3 ? (
								<span className="student-priorities">
									— [
									{member.priority1 ? member.priority1 : ""}
									{member.priority2 ? ", " + member.priority2 : ""}
									{member.priority3 ? ", " + member.priority3 : ""}
									]
								</span>
							) : null}
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
							group.members.map((member) => (
								<li key={member.id} onClick={() => handleStudentClick(member, group.id)}
									className={selectedStudent && selectedStudent.member.name === member.name ? "selected" : ""
									}> <span className="student-name"> {member.name} ({group.name})</span>
									{member.priority1 || member.priority2 || member.priority3 ? (
										<span className="student-priorities">
											— [
											{member.priority1 ? member.priority1 : ""}
											{member.priority2 ? ", " + member.priority2 : ""}
											{member.priority3 ? ", " + member.priority3 : ""}
											]
										</span>
									) : null}
									<hr></hr>
								</li>
							))
						)}
					</ul>
				</>
			)}
		</div>
	);
}
