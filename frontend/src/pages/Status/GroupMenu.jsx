import ProjectPrioritySelectors from "Components/ProjectPrioritySelector/ProjectPrioritySelectors";
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

	async function modifyGroupPreferences(event) {
		try {
			event.preventDefault(); // Prevent page from refreshing on submit

			const formData = new FormData(event.currentTarget);
			const formEntries = Object.fromEntries(formData);

			await fetchWithDefaultErrorHandling(
				`/groups/${session.id}/modifyGroupPreferences/${getUserGroup().id}`,
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

					<form onSubmit={modifyGroupPreferences}>

						<label>
							<b>Group name:</b>
							<input
								name="name"
								type="text"
								defaultValue={getUserGroup().name}
								disabled={!getIsUserGroupOwner()}
								required
							/>
						</label>
						<br />

						<label>
							<b>Preferred minimum group size</b> (-1 means no preference):
							<input
								name="desiredGroupSizeMin"
								defaultValue={getUserGroup().desiredGroupSizeMin}
								type="number"
								min={-1}
								step="1"
								required
							/>
						</label>
						<br />

						<label>
							<b>Preferred maximum group size</b> (-1 means no preference):
							<input
								name="desiredGroupSizeMax"
								defaultValue={getUserGroup().desiredGroupSizeMax}
								type="number"
								min={-1}
								step="1"
								required
							/>
						</label>
						<br />

						<label>
							<b>Group project priorities:</b>
							<ProjectPrioritySelectors
								projects={projects}
								desiredProjectId1Name="desiredProjectId1"
								desiredProjectId2Name="desiredProjectId2"
								desiredProjectId3Name="desiredProjectId3"
								desiredProjectId1={getUserGroup().desiredProjectId1}
								desiredProjectId2={getUserGroup().desiredProjectId2}
								desiredProjectId3={getUserGroup().desiredProjectId3}
								isDisabled={!getIsUserGroupOwner()}
							/>
						</label>

						{getIsUserGroupOwner() &&
							<input
								type="submit"
								value="Apply"
							/>
						}
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