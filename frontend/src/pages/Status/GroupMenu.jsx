import React from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

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

	return (
		<div>
			{getUserGroup() &&
				<div>
					<h3>
						Group information:
					</h3>
					<div>
						Only the owner can make changes to the group.
						The student who has been in the group for the longest time without leaving is regarded as the owner.
					</div>
					<div>
						<b>Group owner: </b>
						{getIsUserGroupOwner() ? "You" : students.find(student => student.id === getUserGroup().studentIds[0]).name}
					</div>
					<div>
						<b>Group size: </b>
						{getUserGroup().studentIds.length} / {session.maxGroupSize}
					</div>
					<div>
						<b>Group members: </b>
						{getUserGroup().studentIds.map((studentId, index) =>
							<div>{index + 1}) {students.find(student => student.id === studentId).name}</div>
						)}
					</div>

					<form onSubmit={modifyGroupName}>
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

					<form onSubmit={modifyGroupProject}>
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

					<form onSubmit={acceptJoinRequest}>
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

					<input
						type="button"
						onClick={() => leaveGroup()}
						value="Leave group"
					/>
				</div>
			}
			{!getUserGroup() &&
				<div>
					<form onSubmit={createGroup}>
						<label>
							Name:
							<input
								name="name"
								type="text"
								placeholder="Group name..."
								required
							/>
							<input
								type="submit"
								value="Create new group"
							/>
						</label>
					</form>
				</div>
			}
			{getUserActiveJoinRequestGroup() &&
				<div>
					You have an outgoing join request to group "{getUserActiveJoinRequestGroup().name}"
					<input
						type="button"
						onClick={() => cancelJoinRequest()}
						value="Cancel outgoin join request"
					/>
				</div>
			}
		</div>
	);
}