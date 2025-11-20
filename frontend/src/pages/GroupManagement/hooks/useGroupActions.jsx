import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

export default function useGroupActions(setError, sessionId, setGroups) {

	const moveStudent = async (fromGroupId, toGroupId, studentId) => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/groups/${fromGroupId}/move-student/${toGroupId}/${studentId}/${sessionId}`, {
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
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/groups/${fromGroupId}/move-members/${toGroupId}/${sessionId}`, {
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
			setError("Failed to assign supervisor: " + error);
		}
	};

	return { moveStudent, moveAllMembers, assignSupervisor };
}
