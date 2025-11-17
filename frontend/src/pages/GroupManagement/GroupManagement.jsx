
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../../ContextProviders/AuthProvider";
import "./GroupM.css";
import { useGetSessionStudentsByParam } from "hooks/useGetSessionStudents";
import { useGetSessionByParameter } from "hooks/useGetSession";
import NotifyButton from "Components/NotifyButton/NotifyButton";
import useIsQuestionnaireDeadlineExceeded from "hooks/useIsQuestionnaireDeadlineExceeded";
import useSplitGroupsIntoSections from "./useSplitGroupsIntoSections";
import useGetSessionSupervisors from "hooks/useGetSessionSupervisors";

export default function GroupManagement() {

	const { sessionId } = useParams();
	const navigate = useNavigate();

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingStudents, students } = useGetSessionStudentsByParam();
	const { isDeadlineExceeded } = useIsQuestionnaireDeadlineExceeded(session);
	const { isLoading: isLoadingSupervisors, supervisors } = useGetSessionSupervisors(sessionId);

	const [groups, setGroups] = useState([]);
	const [selectedStudent, setSelectedStudent] = useState(null);
	const [selectedGroup, setSelectedGroup] = useState(null);
	const [error, setError] = useState(null);
	const [previousGroups, setPreviousGroups] = useState([]);
	const [canUndo, setCanUndo] = useState(false);
	const [lastAction, setLastAction] = useState(null);
	const [localStudentsWithNoGroup, setLocalStudentsWithNoGroup] = useState([]);


	const [notifyButtonMessage, setNotifyButtonMessage] = useState();

	const { completedGroups, almostCompletedGroups, incompleteGroups, groupsWith1Member }
		= useSplitGroupsIntoSections(groups, students, session);

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
				console.log("Fetched groups:", groupArray);
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

	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return navigate("/sign-in");
	if (isLoadingSession) return <div className="loading-message">Loading session...</div>;
	if (isLoadingStudents) return <div className="loading-message">Loading students...</div>;
	if (isLoadingSupervisors) return <div className="loading-message">Loading supervisors...</div>;

	const moveStudent = async (fromGroupId, toGroupId, studentId) => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/groups/${fromGroupId}/move-student/${toGroupId}/${studentId}`, {
				method: "POST",
				credentials: "include"
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				setError(errorMessage);
				return;
			}
		} catch (error) {
			setError("Error moving student");
		}
	};

	const moveAllMembers = async (fromGroupId, toGroupId) => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/groups/${fromGroupId}/move-members/${toGroupId}`, {
				method: "POST",
				credentials: "include"
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				setError(errorMessage);
				return;
			}

		} catch (error) {
			setError("Error moving group members");
		}
	};

	const handleStudentClick = async (member, groupId) => {
		if (!selectedStudent) {
			setSelectedStudent({ member, from: groupId });
			return;
		}

		const from = selectedStudent.from;

		if (from === groupId) {
			setSelectedStudent(null);
			return;
		}

		try {
			setPreviousGroups(groups);
			setCanUndo(true);
			await moveStudent(selectedStudent.from, groupId, selectedStudent.member.id);
			selectedStudent.member.groupId = groupId;
			setLastAction({
				type: "student",
				from: selectedStudent.from,
				to: groupId,
				student: selectedStudent.member,
			});
			setGroups(prevGroups => {
				const targetGroup = prevGroups.find(group => group.id === groupId);

				// if the target group is full, we don’t add the student
				if (targetGroup.members.length >= session?.maxGroupSize) {
					setError("Sorry, adding this student would make the group too big");
					return prevGroups;
				}

				const newGroups = prevGroups.map(group => {
					if (group.id === from) {
						// Old group keeps all members, except the selected student
						return { ...group, members: group.members.filter(student => student.name !== selectedStudent.member.name) };
					}
					// Checks if the current group in the loop matches the target group
					if (group.id === groupId) {
						// Copy all the members, but add the selected student
						return { ...group, members: [...group.members, selectedStudent.member] };
					}
					return group;
				});

				return newGroups;
			});
			if (from == null) {
				setLocalStudentsWithNoGroup(prev =>
					prev.filter(s => s.id !== selectedStudent.member.id)
				);
			}
		} catch (error) {
			setError("Failed to move student: " + error.message);
		}
		setSelectedStudent(null);
	};


	const handleGroupClick = async (groupId) => {
		if (!selectedGroup) {
			setSelectedGroup({ from: groupId });
			return;
		}

		if (selectedGroup.from === groupId) {
			setSelectedGroup(null);
			return;
		}

		try {
			setPreviousGroups(groups);
			setCanUndo(true);
			await moveAllMembers(selectedGroup.from, groupId);
			setLastAction({
				type: "group",
				from: selectedGroup.from,
				to: groupId,
			});
			setGroups(prevGroups => {
				const fromGroup = prevGroups.find(group => group.id === selectedGroup.from);
				const targetGroup = prevGroups.find(group => group.id === groupId);

				// stops if combined size would exceed the maxGroupSize
				if (targetGroup.members.length + fromGroup.members.length > session?.maxGroupSize) {
					setError("Sorry, merging these groups would make the group too big");
					return prevGroups;
				}

				const newGroups = prevGroups.map(group => {
					if (group.id === selectedGroup.from) //
						return { ...group, members: [] };

					if (group.id === groupId)
						// Copy the group, but update the members
						// Copy all the members, but add the selected student
						return { ...group, members: [...group.members, ...fromGroup.members] };
					return group;
				});
				setPreviousGroups(newGroups);
				return newGroups;
			});
		} catch (error) {
			setError("Failed to merge groups: " + error.message);
		}
		setSelectedGroup(null);
	};

	const assignSupervisor = async (groupId, supervisorId) => {
		try {
			await fetch(`${process.env.REACT_APP_API_BASE_URL}/groups/${sessionId}/${groupId}/assign-supervisor/${supervisorId}`, {
				method: "POST",
				credentials: "include",
			});
			setGroups((prev) =>
				prev.map((g) =>
					g.id === groupId ? { ...g, supervisorId: supervisorId } : g
				)
			);
		} catch {
			setError("Failed to assign supervisor");
		}
	};

	function RenderStudentsWithNoGroup(localStudentsWithNoGroup, groupsWith1Member) {
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

	function RenderGroups(groups) {
		return groups.map((group) => {
			return (
				<div className="group-box" key={group.id}>
					<div className="AssignSupervisor-button">
						<p>Current Supervisor: </p>
						<select defaultValue="" onChange={(e) => assignSupervisor(group.id, e.target.value)}>
							<option value="" disabled> {supervisors?.find(s => s.id === group.supervisor)?.name || "None"} </option>
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
						<span className="group-detail">Size: </span> {group.members.length} <br />
						<span className="group-detail">Preferred size: </span> {group.maxStudents}
					</h4>
					{group.project && (
						<p className="group-detail">
							Project: <span className="highlight">{group.project}</span>
						</p>
					)}
					<ul>
						{group.members.map((member, index) => (
							<li key={index} onClick={() => handleStudentClick(member, group.id)}
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
				</div>
			);
		});
	}

	return (
		<div className="group-container">
			{!isDeadlineExceeded() ? (
				<p className="info-text">Waiting for questionnaire deadline to pass...</p>
			) : (
				<>
					<h1> Group Management</h1>

					{error && <div className="error-box">{error}</div>}

					{canUndo && (
						<div className="undo-box">
							<button
								onClick={async () => {
									try {
										setGroups(previousGroups);
										setError("");
										if (lastAction) {
											if (lastAction.type === "student") {
												await moveStudent(lastAction.to, lastAction.from, lastAction.student.id);
											} else if (lastAction.type === "group") {
												await moveAllMembers(lastAction.to, lastAction.from);
											}
										}
									} catch (err) {
										setError("Failed to undo: " + err.message);
									}
									setCanUndo(false);
									setLastAction(null);
								}}
							>
								Undo last change
							</button>
						</div>
					)}

					<h2 className="completed-groups" >Completed Groups</h2>
					<div className="group-row">{RenderGroups(completedGroups)}</div>

					<h2 className="almost-completed-groups">Almost Completed Groups</h2>
					<div className="group-row">{RenderGroups(almostCompletedGroups)}</div>

					<h2 className="incomplete-groups">Incomplete Groups</h2>
					<div className="group-row">{RenderGroups(incompleteGroups)}</div>

					<h2 className="students-no-group">Students Without a Group</h2>
					<div className="group-row">{RenderStudentsWithNoGroup(localStudentsWithNoGroup, groupsWith1Member)} </div>

					<NotifyButton sessionId={sessionId} setMessage={setNotifyButtonMessage} />
				</>
			)}
		</div>
	);
}

