import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "../User/User.css";

export default function GroupManagement() {
	const { sessionId } = useParams();
	const [groups, setGroups] = useState([]);

	const completedGroups = groups.filter(group => group.members.length === 7);
	const almostCompletedGroups = groups.filter(group => group.members.length >= 4 && group.members.length <= 6);
	const incompleteGroups = groups.filter(group => group.members.length <= 3);

	useEffect(() => {
		setGroups([
			{ id: 1, name: "Group 1", members: ["Student 1", "Student 2", "Student 3", "Student 4", "Student 5", "Student 6", "Student 7"] },
			{ id: 2, name: "Group 2", members: ["Student 8", "Student 9", "Student 10", "Student 11"] },
			{ id: 3, name: "Group 3", members: ["Student 12", "Student 13"] },
			{ id: 4, name: "Group 4", members: ["Student 14", "Student 15"] },
		]);
	}, [sessionId]);


	function renderGroups(groups) {
		return groups.map((group) => (
			<div className="group-box" key={group.id}>
				<h4>
					{group.name} - size: {group.members.length}
				</h4>
				<ul>
					{group.members.map((memberName, index) => (
						<li key={index}> {memberName} </li>
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