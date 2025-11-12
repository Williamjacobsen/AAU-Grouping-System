import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useGetUser } from "../../hooks/useGetUser";
import "../User/User.css";

export default function GroupManagement() {

	const [groups, setGroups] = useState([]);
	const [selectedStudent, setSelectedStudent] = useState(null);
	const [selectedGroup, setSelectedGroup] = useState(null);
	const [error, setError] = useState(null);
	const [previousGroups, setPreviousGroups] = useState([]);
	const [canUndo, setCanUndo] = useState(false);
	const [lastAction, setLastAction] = useState(null);
	const navigate = useNavigate();

	// Default is max = 7, needs to change so that it gets the number from the max students session setup page
	const completedGroups = groups.filter(group => group.members.length === 7);
	const almostCompletedGroups = groups.filter(group => group.members.length >= 4 && group.members.length <= 6);
	const incompleteGroups = groups.filter(group => group.members.length >= 1 && group.members.length <= 3);


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
		}
		fetchGroups();
	}, []);

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [error])

	const { user, isLoading: isLoadingUser } = useGetUser();
	if (isLoadingUser) return <>Checking authentication...</>;
	if (!user) return navigate("/sign-in");


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
	}

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
	}

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
						return { ...group, members: group.members.filter(student => student.name !== selectedStudent.member.name) }
					}
					// Checks if the current group in the loop matches the target group
					if (group.id === groupId) {
						// Copy all the members, but add the selected student
						return { ...group, members: [...group.members, selectedStudent.member] }
					}
					return group;
				});

				return newGroups;
			});
		} catch (error) {
			setError("Failed to move student: " + error.message);
		}
		setSelectedStudent(null);
	}


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
						return { ...group, members: [...group.members, ...fromGroup.members] }
					return group;
				});
				setPreviousGroups(newGroups);
				return newGroups;
			});
		} catch (error) {
			setError("Failed to merge groups: " + error.message)
		}
		setSelectedGroup(null);
	}


	function RenderGroups(groups) {
		return groups.map((group) => {
			return (
				<div className="group-box" key={group.id}>
					<h4 onClick={() => handleGroupClick(group.id)}
						className={selectedGroup && selectedGroup.from === group.id ? "selected" : ""}>
						<span className="group-name">{group.name}</span> <br />
						<span className="group-detail">Size: </span> {group.members.length} <br/> 
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
	)
}