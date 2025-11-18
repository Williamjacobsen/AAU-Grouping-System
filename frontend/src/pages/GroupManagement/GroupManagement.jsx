import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../../ContextProviders/AuthProvider";
import "./GroupM.css";

import NotifyButton from "Components/NotifyButton/NotifyButton";
import CsvDownloadButton from "./components/CsvDownloadButton";

import useGroupActions from "./hooks/useGroupActions";
import useSplitGroupsIntoSections from "./hooks/useSplitGroupsIntoSections";
import useStudentClick from "./hooks/useStudentClick";
import useGroupClick from "./hooks/useGroupClick";
import useUndoLogic from "./hooks/useUndoLogic";

import RenderGroups from "./components/RenderGroups";
import RenderStudentList from "./components/RenderStudentList";

import { useAppState } from "ContextProviders/AppStateContext";

export default function GroupManagement() {

	const { sessionId } = useParams();
	const navigate = useNavigate();

	const { isLoading: isLoadingUser, user } = useAuth();

	const [selectedStudent, setSelectedStudent] = useState(null);
	const [selectedGroup, setSelectedGroup] = useState(null);
	const [error, setError] = useState(null);
	const [previousGroups, setPreviousGroups] = useState([]);
	const [canUndo, setCanUndo] = useState(false);
	const [lastAction, setLastAction] = useState(null);
	const [localStudentsWithNoGroup, setLocalStudentsWithNoGroup] = useState([]);
	const [groups, setGroups] = useState([]);

	const { isLoading, session, students, supervisors, isDeadlineExceeded } = useAppState();

	const { moveStudent, moveAllMembers, assignSupervisor } = useGroupActions(setError, sessionId, setGroups);
	const { completedGroups, almostCompletedGroups, incompleteGroups, groupsWith1Member }
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
			const timer = setTimeout(() => setError(""), 10000);
			return () => clearTimeout(timer);
		}
	}, [error]);

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
		session,
		groups,
		setError
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
		session,
		setError
	});

	const handleUndo = useUndoLogic({ //undo button
		previousGroups,
		setGroups,
		lastAction,
		setLastAction,
		setCanUndo,
		moveStudent,
		moveAllMembers,
		setError
	});

	if (isLoadingUser || isLoading)
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
				<p className="info-text">Waiting for questionnaire deadline to pass...</p> //shows this if deadline isnt exceeded
			) : (
				<>
					<h1>Group Management</h1>
					<h3>How to move all group members from A to B?</h3>
					<p>Click on the name of group A and then click on the name of group B</p>
					<h3>How to move a student from A to B?</h3>
					<p>Click on the name of the student A and then click on the name of group B</p>
					<h3>Undo button</h3>
					<p>Once a move has been done, it can be undone by clicking on the "undo last change" button.
						This pops up at the top of the screen after a move.</p>
					<p>NOTE: When moving students without a group, this cant be undone</p>

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

					<p> 
						You can download the CSV file after all groups have been made, 
						and supervisors have been assigned to the groups <br></br>
						<CsvDownloadButton students={students} groups={groups} /> 
					</p>
				</>
			)}
		</div>
	);
}

