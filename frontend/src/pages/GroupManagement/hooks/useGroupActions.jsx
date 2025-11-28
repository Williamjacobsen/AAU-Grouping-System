import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

export default function useGroupActions(sessionId, setGroups) {

	const createGroupWithStudents = async (foundingStudentId, secondStudentId, groupName) => {
		try {
			const encodedGroupName = encodeURIComponent(groupName); // Encode groupName for URL safety
			const response = await fetch(
				`${process.env.REACT_APP_API_BASE_URL}/groups/${sessionId}/createGroupWithStudent/${foundingStudentId}/${secondStudentId}/${encodedGroupName}`,
				{
					method: "POST",
					credentials: "include"
				}
			);

			if (!response.ok) {
				const errorMessage = await response.text();
				alert(errorMessage);
				return;
			}

		} catch (error) {
			alert("Failed to create group: " + error);
			throw error;
		}
	};

	const moveStudent = async (fromGroupId, toGroupId, studentId) => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/groups/${fromGroupId}/move-student/${toGroupId}/${studentId}/${sessionId}`, {
				method: "POST",
				credentials: "include"
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				alert(errorMessage);
				return;
			}

		} catch (error) {
			alert("Error moving student");
		}
	};

	const moveAllMembers = async (fromGroupId, toGroupId) => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/groups/${fromGroupId}/move-members/${toGroupId}/${sessionId}`, {
				method: "POST",
				credentials: "include"
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				alert(errorMessage);
				return;
			}

		} catch (error) {
			alert("Error moving group members");
		}
	};

	const assignSupervisor = async (groupId, supervisorId) => {
		try {
			await fetchWithDefaultErrorHandling(
				`/groups/${sessionId}/modifyGroupSupervisor/${groupId}/${supervisorId}`,
				{
					method: "POST",
					credentials: "include",
				}
			);
			setGroups((prev) =>
				prev.map((g) =>
					g.id === groupId ? { ...g, supervisorId: supervisorId } : g
				)
			);
		} catch (error) {
			alert("Failed to assign supervisor: " + error);
		}
	};

	const assignProject = async (groupId, projectId) => {
		try {
			const response = await fetch(
				`${process.env.REACT_APP_API_BASE_URL}/groups/${sessionId}/modifyGroupAssignedProject/${groupId}/${projectId}`,
				{
					method: "POST",
					credentials: "include"
				}
			);

			if (!response.ok) {
				const errorMessage = await response.text();
				alert(errorMessage);
				return;
			}

			setGroups(prev =>
				prev.map(g =>
					g.id === groupId ? { ...g, assignedProjectId: projectId } : g
				)
			);
		} catch (error) {
			alert("Failed to assign project: " + error);
		}
	};

	return { moveStudent, moveAllMembers, assignSupervisor, assignProject, createGroupWithStudents };
}
