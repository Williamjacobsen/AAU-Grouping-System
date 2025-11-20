import React from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";
import "./GroupMenu.css";

export default function GroupMenu({ session, user, groups, projects, students }) {

	if (user.role !== "Student") {
		return;
	}

	function getUserGroup() {
		if (user.groupId === null) {
			return null;
		}
		return groups.find(group => group.id === user.groupId);
	};

	function getIsUserGroupOwner() {
		if (getUserGroup() === null) {
			return false;
		}
		return getUserGroup().studentIds[0] === user.id;
	};

	function getUserActiveJoinRequestGroup() {
		if (user.activeJoinRequestGroupId === null) {
			return null;
		}
		return groups.find(group => group.id === user.activeJoinRequestGroupId);
	};

	async function createGroup(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit

			const formData = new FormData(event.currentTarget);
			const createGroupRecord = Object.fromEntries(formData);
			createGroupRecord.studentId = user.id;

			await fetchWithDefaultErrorHandling(
				`/groups/${session.id}/createGroup`,
				{
					method: "POST",
					credentials: "include",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify(
						createGroupRecord
					),
				}
			);

			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	async function modifyGroupName(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit

			const formData = new FormData(event.currentTarget);
			const formEntries = Object.fromEntries(formData);

			await fetchWithDefaultErrorHandling(
				`/groups/${session.id}/modifyGroupName/${getUserGroup().id}`,
				{
					method: "POST",
					credentials: "include",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify(
						formEntries
					),
				}
			);

			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	async function modifyGroupProject(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit

			const formData = new FormData(event.currentTarget);
			const formEntries = Object.fromEntries(formData);

			if (formEntries.newProjectId === "null") {
				formEntries.newProjectId = null;
			}

			await fetchWithDefaultErrorHandling(
				`/groups/${session.id}/modifyGroupProject/${getUserGroup().id}`,
				{
					method: "POST",
					credentials: "include",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify(
						formEntries
					),
				}
			);

			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	async function acceptJoinRequest(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit

			const formData = new FormData(event.currentTarget);
			const studentId = formData.get("studentId");

			await fetchWithDefaultErrorHandling(
				`/groups/${session.id}/${getUserGroup().id}/accept-request/${studentId}`,
				{
					method: 'POST',
					credentials: 'include',
					headers: { 'Content-Type': 'application/json' },
				});

			alert("Request succesfully accepted");
			window.location.reload(); // Reload the page (to refresh changes)

		} catch (error) {
			alert(error);
		}
	}

	async function cancelJoinRequest() {
		try {
			await fetchWithDefaultErrorHandling(
				`/groups/cancelJoinRequest`,
				{
					method: "POST",
					credentials: "include",
				}
			);

			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	async function leaveGroup() {
		try {
			await fetchWithDefaultErrorHandling(
				`/groups/${session.id}/${getUserGroup().id}/leave/${user.id}`,
				{
					method: "POST",
					credentials: "include",
				}
			);

			window.location.reload(); // Reload the page (to refresh changes)
		} catch (error) {
			alert(error);
		}
	}

	if (!students || students.length === 0 || !groups) {
		return <div>Loading group information...</div>;
	}

	return (
		<div className="group-menu-container">
			{getUserGroup() &&
				<div className="group-info-section">
					<h3 className="group-info-title">
						Group Information
					</h3>
					<div className="group-info-description">
						Only the owner can make changes to the group.
						The student who has been in the group for the longest time without leaving is regarded as the owner.
					</div>
					<div className="group-info-item">
						<b>Group owner:</b>
						<span>{getIsUserGroupOwner() ? "You" : students.find(student => student.id === getUserGroup().studentIds[0]).name}</span>
					</div>
					<div className="group-info-item">
						<b>Group size:</b>
						<span>{getUserGroup().studentIds.length} / {session.maxGroupSize}</span>
					</div>
					<div className="group-info-item">
						<b>Group members:</b>
					</div>
					<div className="group-members-list">
						{getUserGroup().studentIds.map((studentId, index) =>
							<div key={studentId} className="group-member-item">{index + 1}) {students.find(student => student.id === studentId).name}</div>
						)}
					</div>

					<form className="group-form" onSubmit={modifyGroupName}>
						<label>
							<b>Group name:</b>
							<input
								name="newName"
								type="text"
								defaultValue={getUserGroup().name}
								disabled={!getIsUserGroupOwner()}
								required
							/>
							{getIsUserGroupOwner() &&
								<input
									type="submit"
									value="Apply"
								/>
							}
						</label>
					</form>

					<form className="group-form" onSubmit={modifyGroupProject}>
						<label>
							<b>Group project:</b>
							<select
								name="newProjectId"
								defaultValue={projects.find(project => project.id === getUserGroup().projectId)?.id ?? null}
								required
							>
								<option key="null" value="null" disabled={!getIsUserGroupOwner()}>
									...
								</option>
								{projects.map(project =>
									<option key={project.id} value={project.id} disabled={!getIsUserGroupOwner()}>
										{project.name}
									</option>
								)}
							</select>
							{getIsUserGroupOwner() &&
								<input
									type="submit"
									value="Apply"
								/>
							}
						</label>
					</form>

					<form className="group-form" onSubmit={acceptJoinRequest}>
						<label>
							<b>Pending join requests:</b>
							<select
								name="studentId"
								defaultValue={getUserGroup().joinRequestStudentIds[0]}
								disabled={!getIsUserGroupOwner()}
								required
							>
								<option key="null" value="null" disabled={!getIsUserGroupOwner()}>
									...
								</option>
								{getUserGroup().joinRequestStudentIds.map(studentId =>
									<option key={studentId} value={studentId} disabled={!getIsUserGroupOwner()} >
										{students.find(student => student.id === studentId).name}
									</option>
								)}
							</select>
							{getIsUserGroupOwner() &&
								<input
									type="submit"
									value="Accept"
								/>
							}
						</label>
					</form>

					<button
						className="leave-group-button"
						onClick={() => leaveGroup()}
					>
						Leave Group
					</button>
				</div>
			}
			{!getUserGroup() &&
				<div className="create-group-section">
					<h3 className="group-info-title">Create New Group</h3>
					<form className="group-form" onSubmit={createGroup}>
						<label>
							<b>Group name:</b>
							<input
								name="name"
								type="text"
								placeholder="Enter group name..."
								required
							/>
							<input
								type="submit"
								value="Create New Group"
							/>
						</label>
					</form>
				</div>
			}
			{getUserActiveJoinRequestGroup() &&
				<div className="join-request-section">
					<span>You have an outgoing join request to group "{getUserActiveJoinRequestGroup().name}"</span>
					<button
						className="cancel-request-button"
						onClick={() => cancelJoinRequest()}
					>
						Cancel Join Request
					</button>
				</div>
			}
		</div>
	);
}