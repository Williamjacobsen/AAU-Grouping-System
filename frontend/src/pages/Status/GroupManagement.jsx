import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useGetUser } from "../../hooks/useGetUser";
import "../User/User.css";
import { useGetSessionByParameter } from "hooks/useGetSession";

export default function GroupManagement() {

	// TODO: Munasar, right now the re-rendering is fucked,
	// so the completedGroups, almostCompletedGroups, and incompleteGroups
	// don't make the component re-render when they are updated.
	// I'll let you fix it, but if you're stuck, just ask me.
	// - Jesp

	const { isLoading: isLoadingUser, user } = useGetUser();
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();

	const [groups, setGroups] = useState([]);
	const [selectedStudent, setSelectedStudent] = useState(null);
	const [selectedGroup, setSelectedGroup] = useState(null);
	const [error, setError] = useState(null);
	const [previousGroups, setPreviousGroups] = useState([]);
	const [canUndo, setCanUndo] = useState(false);
	const [lastAction, setLastAction] = useState(null);

	const navigate = useNavigate();

	const almostCompleteFraction = 0.5; // Fraction of min group size required to call a group "almost completed" instead of "incomplete"
	const completedGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length >= session?.minGroupSize && group.members.length <= session?.maxGroupSize
		);
	}, [groups, session]);
	const almostCompletedGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length >= session?.minGroupSize * almostCompleteFraction && group.members.length < session?.minGroupSize
		);
	}, [groups, session]);
	const incompleteGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length < session?.minGroupSize * almostCompleteFraction
		);
	}, [groups, session]);

	useEffect(() => {
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
	}, []);

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [error]);

	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return navigate("/sign-in");
	if (isLoadingSession) return <div className="loading-message">Loading session...</div>;

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

		if (selectedStudent.from === groupId) {
			setSelectedStudent(null);
			return;
		}

		try {
			setPreviousGroups(groups);
			setCanUndo(true);
			await moveStudent(selectedStudent.from, groupId, selectedStudent.member.id);
			setLastAction({
				type: "student",
				from: selectedStudent.from,
				to: groupId,
				student: selectedStudent.member,
			});
			setGroups(prevGroups => {
				const targetGroup = prevGroups.find(group => group.id === groupId);

				// if the target group is full, we don’t add the student
				if (targetGroup.members.length >= 7) {
					setError("Sorry, adding this student would make the group too big");
					return prevGroups;
				}

				const newGroups = prevGroups.map(group => {
					if (group.id === selectedStudent.from) {
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

				// stops if combined size would exceed 7
				if (targetGroup.members.length + fromGroup.members.length > 7) {
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


	function RenderGroups(groups) {
		return groups.map((group) => {
			return (
				<div className="group-box" key={group.id}>
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
								className={selectedStudent && selectedStudent.member?.name === member.name ? "selected" : ""
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
							</li>
						))}
					</ul>
				</div>
			);
		});
	}

	return (
		<div className="group-container">
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
		</div>
	);
}