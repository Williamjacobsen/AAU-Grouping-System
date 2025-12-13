import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAppState } from "ContextProviders/AppStateContext";
import { useAuth } from "../../ContextProviders/AuthProvider";
import "./GroupM.css";

import useGetSessionGroups from "hooks/useGetSessionGroups";
import useGroupActions from "./hooks/useGroupActions";
import useSplitGroupsIntoSections from "./hooks/useSplitGroupsIntoSections";
import useStudentClick from "./hooks/useStudentClick";
import useGroupClick from "./hooks/useGroupClick";
import useUndoLogic from "./hooks/useUndoLogic";

import CsvDownloadButton from "./components/CsvDownloadButton";
import RenderGroups from "./components/RenderGroups";
import RenderStudentList from "./components/RenderStudentList";
import NotifyButton from "Components/NotifyButton/NotifyButton";


export default function GroupManagement() {

	const { sessionId } = useParams();
	const navigate = useNavigate();

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingGroups, groups: fetchedGroups } = useGetSessionGroups(sessionId);
	const { isLoading: isLoadingSessionData, session, students, supervisors, isDeadlineExceeded, projects } = useAppState();

	const [selectedStudent, setSelectedStudent] = useState(null);
	const [selectedGroup, setSelectedGroup] = useState(null);
	const [previousGroups, setPreviousGroups] = useState([]);
	const [canUndo, setCanUndo] = useState(false);
	const [lastAction, setLastAction] = useState(null);
	const [localStudentsWithNoGroup, setLocalStudentsWithNoGroup] = useState([]);
	const [groups, setGroups] = useState([]);


	const { moveStudent, moveAllMembers, assignSupervisor, assignProject } = useGroupActions(sessionId, setGroups);
	const { toLargeGroups, completedGroups, almostCompletedGroups, incompleteGroups, groupsWith1Member }
		= useSplitGroupsIntoSections(groups, session);

	useEffect(() => {
		if (fetchedGroups) {
			setGroups(fetchedGroups);
		}
	}, [fetchedGroups]);


	useEffect(() => {
		if (students != null) {
			setLocalStudentsWithNoGroup(students.filter(s => !s.groupId));
		}
	}, [students]);

	const handleStudentClick = useStudentClick({ // function for moving students between groups
		selectedStudent,
		setSelectedStudent,
		setPreviousGroups,
		setCanUndo,
		setLastAction,
		setGroups,
		setLocalStudentsWithNoGroup,
		moveStudent,
		groups,
		sessionId
	});

	const handleGroupClick = useGroupClick({ // function for moving group members from one group to another
		groups,
		setGroups,
		selectedGroup,
		setSelectedGroup,
		setPreviousGroups,
		setCanUndo,
		setLastAction,
		moveAllMembers,
		sessionId
	});

	const handleUndo = useUndoLogic({ //undo button
		previousGroups,
		setGroups,
		lastAction,
		setLastAction,
		setCanUndo,
		moveStudent,
		sessionId
	});

	if (isLoadingUser || isLoadingGroups || isLoadingSessionData)
		return <div className="loading-message">Loading...</div>;
	if (!user)
		return navigate("/sign-in");
	if (user.role !== "Coordinator") {
		return (
			<div className="error-message">
				Access denied. Only coordinators can manage groups.
			</div>
		);
	}

	return (
		<div className="group-container">
			{!isDeadlineExceeded() ? (
				<>
					<p className="info-text">Waiting for questionnaire deadline to pass...</p> {/* shows this if deadline isnt exceeded */}
					{session?.questionnaireDeadline?.replace("T", " ") ?? "No deadline set"}
				</>
			) : (
				<>
					<h1>Group Management</h1>
					<h3>How to move all group members from A to B?</h3>
					<p>Click on the name of group A and then click on the name of group B</p>
					<h3>How to move a student from group A to group B?</h3>
					<p>Click on the name of the student in group A and then click on the list of studentnames in group B</p>
					<h3>How to create a new group?</h3>
					<p>Click on the name of a student without a group, then click on the name of the same student (single student group) or another student without a group.
						You will then be prompted to enter a name for the new group.</p>
					<h3>Undo button</h3>
					<p>Once a move has been done, it can be undone by clicking on the "undo last change" button.
						This pops up at the top of the screen after a move.</p>
					<p>NOTE: When moving students without a group, this cant be undone</p>

					{canUndo && (
						<div className="undo-box">
							<button onClick={handleUndo}>Undo last change</button>
						</div>
					)}

					{toLargeGroups.length > 0 && (
						<>
							<h2 className="toLarge-groups">Too Large Groups ({toLargeGroups.length} / {incompleteGroups.length + almostCompletedGroups.length + completedGroups.length + toLargeGroups.length})</h2>
							<div className="group-row">
								<RenderGroups
									groups={toLargeGroups}
									allGroups={groups}
									assignSupervisor={assignSupervisor}
									assignProject={assignProject}
									supervisors={supervisors}
									selectedGroup={selectedGroup}
									handleGroupClick={handleGroupClick}
									handleStudentClick={handleStudentClick}
									selectedStudent={selectedStudent}
									students={students}
									projects={projects}
								/>
							</div>
						</>
					)}

					<h2 className="completed-groups">Completed Groups ({completedGroups.length} / {incompleteGroups.length + almostCompletedGroups.length + completedGroups.length + toLargeGroups.length})</h2>
					<div className="group-row">
						<RenderGroups
							groups={completedGroups}
							allGroups={groups}
							assignSupervisor={assignSupervisor}
							assignProject={assignProject}
							supervisors={supervisors}
							selectedGroup={selectedGroup}
							handleGroupClick={handleGroupClick}
							handleStudentClick={handleStudentClick}
							selectedStudent={selectedStudent}
							students={students}
							projects={projects}
						/>
					</div>

					<h2 className="almost-completed-groups">Almost Completed Groups ({almostCompletedGroups.length} / {incompleteGroups.length + almostCompletedGroups.length + completedGroups.length + toLargeGroups.length})</h2>
					<div className="group-row">
						<RenderGroups
							groups={almostCompletedGroups}
							allGroups={groups}
							assignSupervisor={assignSupervisor}
							assignProject={assignProject}
							supervisors={supervisors}
							selectedGroup={selectedGroup}
							handleGroupClick={handleGroupClick}
							handleStudentClick={handleStudentClick}
							selectedStudent={selectedStudent}
							students={students}
							projects={projects}
						/>
					</div>

					{incompleteGroups.length > 0 && (
						<>
							<h2 className="incomplete-groups">Incomplete Groups ({incompleteGroups.length} / {incompleteGroups.length + almostCompletedGroups.length + completedGroups.length + toLargeGroups.length})</h2>
							<div className="group-row">
								<RenderGroups
									groups={incompleteGroups}
									allGroups={groups}
									assignSupervisor={assignSupervisor}
									assignProject={assignProject}
									supervisors={supervisors}
									selectedGroup={selectedGroup}
									handleGroupClick={handleGroupClick}
									handleStudentClick={handleStudentClick}
									selectedStudent={selectedStudent}
									students={students}
									projects={projects}
								/>
							</div>
						</>
					)}

					<h2 className="students-no-group">Students Without a Group</h2>
					<div className="group-row">
						<RenderStudentList
							localStudentsWithNoGroup={localStudentsWithNoGroup}
							groupsWith1Member={groupsWith1Member}
							selectedStudent={selectedStudent}
							handleStudentClick={handleStudentClick}
							students={students}
							projects={projects}
						/>
					</div>

					<NotifyButton sessionId={sessionId} />

					<p>
						You can download the CSV file after all groups have been made,
						and supervisors have been assigned to the groups: <br></br>
						<CsvDownloadButton
							students={students}
							groups={groups}
							supervisors={supervisors}
							projects={projects}
							session={session}
						/>
					<hr></hr>
					</p>
				</>
			)}
		</div>
	);
}

