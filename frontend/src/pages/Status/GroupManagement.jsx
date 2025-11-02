import React, { useEffect, useState } from "react";
import "../User/User.css";

export default function GroupManagement() {

	const [groups, setGroups] = useState([]);
	const [selectedStudent, setSelectedStudent] = useState(null);
	const [selectedGroup, setSelectedGroup] = useState(null);
	const [error, setError] = useState(null); //needs error handling


	const completedGroups = groups.filter(group => group.members.length === 7);
	const almostCompletedGroups = groups.filter(group => group.members.length >= 4 && group.members.length <= 6);
	const incompleteGroups = groups.filter(group => group.members.length >= 1 && group.members.length <= 3);

	useEffect(() => {
		const fetchGroups = async () => {
			setError(null);
			try {
				const response = await fetch("http://localhost:8080/groups");
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

	function handleStudentClick(name, groupId) {
		if (!selectedStudent) {
			setSelectedStudent({ name, from: groupId });
			return;
		}

		if (selectedStudent.from === groupId) {
			setSelectedStudent(null);
			return;
		}

		setGroups(prevGroups => {
			const targetGroup = prevGroups.find(group => group.id === groupId);

			if (targetGroup.members.length >= 7)
				// if the target group is full, we don’t add the student
				return prevGroups;

			return prevGroups.map(group => {
				if (group.id === selectedStudent.from) {
					// Old group keeps all members, except the selected student
					return { ...group, members: group.members.filter(student => student !== selectedStudent.name) }
				}
				// Checks if the current group in the loop matches the target group
				if (group.id === groupId) {
					// Copy all the members, but add the selected student
					return { ...group, members: [...group.members, selectedStudent.name] }
				}
				return group;
			})
		});
		setSelectedStudent(null);
	}


	function handleGroupClick(groupId) {
		if (!selectedGroup) {
			setSelectedGroup({ from: groupId });
			return;
		}

		if (selectedGroup.from === groupId) {
			setSelectedGroup(null);
			return;
		}

		setGroups(prevGroups => {
			const fromGroup = prevGroups.find(group => group.id === selectedGroup.from);
			const targetGroup = prevGroups.find(group => group.id === groupId);

			if (targetGroup.members.length + fromGroup.members.length > 7)
				// stops if combined size would exceed 7
				return prevGroups;

			return prevGroups.map(group => {
				if (group.id === selectedGroup.from) //
					return { ...group, members: [] };

				if (group.id === groupId)
					// Copy the group, but update the members
					// Copy all the members, but add the selected student
					return { ...group, members: [...group.members, ...fromGroup.members] }
				return group;
			});
		});
		setSelectedGroup(null);
	}


	function renderGroups(groups) {
		return groups.map((group) => {
			return (
				<div className="group-box" key={group.id}>
					<h4 onClick={() => handleGroupClick(group.id)}
						className={selectedGroup && selectedGroup.from === group.id ? "selected" : ""}>
						{group.name} - size: {group.members.length}
					</h4>
					<ul>
						{group.members.map((memberName, index) => (
							<li key={index} onClick={() => handleStudentClick(memberName, group.id)}
								className={selectedStudent && selectedStudent.name === memberName ? "selected" : ""}>
								{memberName} </li>
						))}
					</ul>
				</div>
			);
		});
	}

	return (
		<div className="group-container">
			<h1> Group Management</h1>
			<h2 className="completed-groups" >Completed Groups</h2>
			<div className="group-row">{renderGroups(completedGroups)}</div>

			<h2 className="almost-completed-groups">Almost Completed Groups</h2>
			<div className="group-row">{renderGroups(almostCompletedGroups)}</div>

			<h2 className="incomplete-groups">Incomplete Groups</h2>
			<div className="group-row">{renderGroups(incompleteGroups)}</div>
		</div>
	)
}