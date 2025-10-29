import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "../User/User.css";

export default function GroupManagement() {
	const { sessionId } = useParams();
	const [groups, setGroups] = useState([]);

	useEffect(() => {
		// Temporary mock data – later, fetch from backend
		setGroups([
			{ id: 1, name: "Group 1", members: ["Student 1", "Student 2", "Student 3", "Student 4", "Student 5", "Student 6", "Student 7"] },
			{ id: 2, name: "Group 2", members: ["Student 8", "Student 9", "Student 10", "Student 11"] },
			{ id: 3, name: "Group 3", members: ["Student 12", "Student 13"] },
		]);
	}, [sessionId]);


	function getColor(size) {
		if (size === 7) return "completed-group";
		if (size >= 4) return "almost-completed-group";
		return "incomplete-group";
	}

	return (
		<div className="group-container">
			<h1> Group Management</h1>
			{groups.map((group) => (
				<div className="group-box" key={group.id}>
					<h3 className={getColor(group.members.length)}>
						{group.name} - size: {group.members.length}
					</h3>
					<ul>
						{group.members.map((memberName, index) => (
							<li key={index}> {memberName} </li>
						))}
					</ul>
				</div>
			))}
		</div>
	)
}