import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "../User/User.css";

export default function GroupManagement() {
	const { sessionId } = useParams();
	const [groups, setGroups] = useState([]);
	const [selectedStudent, setSelectedStudent] = useState(null);
	const [selectedGroup, setSelectedGroup] = useState(null);

	const completedGroups = groups.filter(group => group.members.length === 7);
	const almostCompletedGroups = groups.filter(group => group.members.length >= 4 && group.members.length <= 6);
	const incompleteGroups = groups.filter(group => group.members.length >= 1 && group.members.length <= 3);

	useEffect(() => {
		setGroups([
			{ id: 1, name: "Group 1", members: ["Student 1", "Student 2", "Student 3", "Student 4", "Student 5", "Student 6", "Student 7"] },
			{ id: 2, name: "Group 2", members: ["Student 8", "Student 9", "Student 10", "Student 11"] },
			{ id: 3, name: "Group 3", members: ["Student 12", "Student 13"] },
			{ id: 4, name: "Group 4", members: ["Student 14", "Student 15"] },
		]);
	}, [sessionId]);

	function handleStudentClick(name, groupId) {
		if (!selectedStudent) {
			setSelectedStudent({ name, from: groupId })
			return;
		} else {
			if (selectedStudent.from === groupId) {
				setSelectedStudent(null);
				return;
			}

			setGroups(groups =>
				groups.map(group => {
					if (group.id === selectedStudent.from) {
						const targetGroup = groups.find(group => group.id === groupId);
						if (targetGroup.members.length >= 7)
							// if the target group is full, we don’t remove the student
							return group;
						// Copy the group, but update the members
						// Old group keeps all members, except the selected student
						return { ...group, members: group.members.filter(student => student !== selectedStudent.name) }
					}
					// Checks if the current group in the loop matches the target group
					if (group.id === groupId) {
						if (group.members.length >= 7) return group;
						// Copy the group, but update the members
						// Copy all the members, but add the selected student
						return { ...group, members: [...group.members, selectedStudent.name] }
					}
					return group;
				})
			);
			setSelectedStudent(null);
		}
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
		setGroups(groups => {
			const fromGroup = groups.find(g => g.id === selectedGroup.from);
			const targetGroup = groups.find(group => group.id === groupId);
			// nothing happens if the groups would exceed 7 in total
			if (targetGroup.members.length + fromGroup.members.length > 7) return groups;

			// Checks if the current group in the loop matches the target group
			return groups.map(group => {
				if (group.id === selectedGroup.from) 
					return {...group, members: [] };
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
	return groups.map((group) => (
		<div className="group-box" key={group.id}>
			<h4 onClick={() => handleGroupClick(group.id)}
				className={selectedGroup?.from === group.id ? "selected" : ""}>
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
	));
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