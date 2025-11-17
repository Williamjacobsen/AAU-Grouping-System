import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../../ContextProviders/AuthProvider";
import "./GroupM.css";

import { useGetSessionStudentsByParam } from "hooks/useGetSessionStudents";
import { useGetSessionByParameter } from "hooks/useGetSession";
import NotifyButton from "Components/NotifyButton/NotifyButton";
import useIsQuestionnaireDeadlineExceeded from "hooks/useIsQuestionnaireDeadlineExceeded";
import useGetSessionSupervisors from "hooks/useGetSessionSupervisors";

import useGroupActions from "./hooks/useGroupActions";
import useSplitGroupsIntoSections from "./hooks/useSplitGroupsIntoSections";
import useStudentClick from "./hooks/useStudentClick";
import useGroupClick from "./hooks/useGroupClick";
import useUndoLogic from "./hooks/useUndoLogic";

import RenderGroups from "./components/RenderGroups";
import RenderStudentList from "./components/RenderStudentList";

export default function GroupManagement() {

	const { sessionId } = useParams();
	const navigate = useNavigate();

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingStudents, students } = useGetSessionStudentsByParam();
	const { isLoading: isLoadingSupervisors, supervisors } = useGetSessionSupervisors(sessionId);
	const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session);

	const [groups, setGroups] = useState([]);
	const [selectedStudent, setSelectedStudent] = useState(null);
	const [selectedGroup, setSelectedGroup] = useState(null);
	const [error, setError] = useState(null);
	const [previousGroups, setPreviousGroups] = useState([]);
	const [canUndo, setCanUndo] = useState(false);
	const [lastAction, setLastAction] = useState(null);
	const [localStudentsWithNoGroup, setLocalStudentsWithNoGroup] = useState([]);
	const [notifyButtonMessage, setNotifyButtonMessage] = useState();

	const { moveStudent, moveAllMembers, assignSupervisor } = useGroupActions(setError, sessionId, setGroups);
	const { completedGroups, almostCompletedGroups,
		incompleteGroups, groupsWith1Member }
		= useSplitGroupsIntoSections(groups, session);


	useEffect(() => {
		if (!session) return;
		const fetchGroups = async () => {
			try {
				const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/groups`);
				if (!response.ok) {
					const errorMessage = await response.text();
					setError(errorMessage);
					return;
				}
				const data = await response.json();
				const groupArray = Object.values(data); //convert object into an array
				setGroups(groupArray);
			} catch (error) {
				setError("Failed to fetch data");
			}
		};
		fetchGroups();
	}, [session, students]);

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [error]);

	useEffect(() => {
		if (students) {
			setLocalStudentsWithNoGroup(students.filter(s => !s.groupId));
		}
	}, [students]);

	const handleStudentClick = useStudentClick({
		selectedStudent,
		setSelectedStudent,
		setPreviousGroups,
		setCanUndo,
		setLastAction,
		setGroups,
		setLocalStudentsWithNoGroup,
		moveStudent,
		session,
		groups,
		setError
	});

	const handleGroupClick = useGroupClick({
		groups,
		setGroups,
		selectedGroup,
		setSelectedGroup,
		setPreviousGroups,
		setCanUndo,
		setLastAction,
		moveAllMembers,
		session,
		setError
	});

	const handleUndo = useUndoLogic({
		previousGroups,
		setGroups,
		lastAction,
		setLastAction,
		setCanUndo,
		moveStudent,
		moveAllMembers,
		setError
	});

	if (isLoadingUser || isLoadingSession || isLoadingStudents || isLoadingSupervisors)
		return <div className="loading-message">Loading...</div>;
	if (!user) return navigate("/sign-in");

	return (
		<div className="group-container">
			{!isDeadlineExceeded() ? (
				<p className="info-text">Waiting for questionnaire deadline to pass...</p>
			) : (
				<>
					<h1>Group Management</h1>
					
					{error && <div className="error-box">{error}</div>}

					{canUndo && (
						<div className="undo-box">
							<button onClick={handleUndo}>Undo last change</button>
						</div>
					)}

					<h2 className="completed-groups">Completed Groups</h2>
					<div className="group-row">
						<RenderGroups
							groups={completedGroups}
							assignSupervisor={assignSupervisor}
							supervisors={supervisors}
							selectedGroup={selectedGroup}
							handleGroupClick={handleGroupClick}
							handleStudentClick={handleStudentClick}
							selectedStudent={selectedStudent}
						/>
					</div>

					<h2 className="almost-completed-groups">Almost Completed Groups</h2>
					<div className="group-row">
						<RenderGroups
							groups={almostCompletedGroups}
							assignSupervisor={assignSupervisor}
							supervisors={supervisors}
							selectedGroup={selectedGroup}
							handleGroupClick={handleGroupClick}
							handleStudentClick={handleStudentClick}
							selectedStudent={selectedStudent}
						/>
					</div>

					<h2 className="incomplete-groups">Incomplete Groups</h2>
					<div className="group-row">
						<RenderGroups
							groups={incompleteGroups}
							assignSupervisor={assignSupervisor}
							supervisors={supervisors}
							selectedGroup={selectedGroup}
							handleGroupClick={handleGroupClick}
							handleStudentClick={handleStudentClick}
							selectedStudent={selectedStudent}
						/>
					</div>

					<h2 className="students-no-group">Students Without a Group</h2>
					<div className="group-row">
						<RenderStudentList
							localStudentsWithNoGroup={localStudentsWithNoGroup}
							groupsWith1Member={groupsWith1Member}
							selectedStudent={selectedStudent}
							handleStudentClick={handleStudentClick}
						/>
					</div>

					<NotifyButton sessionId={sessionId} setMessage={setNotifyButtonMessage} />
				</>
			)}
		</div>
	);
}

