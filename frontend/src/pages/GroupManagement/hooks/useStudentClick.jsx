import { fetchSessionGroups } from "hooks/useGetSessionGroups";
import useGroupActions from "./useGroupActions";

export default function useStudentClick({
	selectedStudent, setSelectedStudent, setPreviousGroups,
	setCanUndo, setLastAction, setGroups, setLocalStudentsWithNoGroup,
	moveStudent, session, groups, setError, sessionId
}) {

	const { createGroupWithStudents } = useGroupActions(setError, sessionId, setGroups);

	const handleStudentClick = async (member, groupId) => {
		if (!selectedStudent) {
			setSelectedStudent({ member, from: groupId });
			return;
		}

		const from = selectedStudent.from;

		// If both selected students are not in a group
		if (from == null && groupId == null) {
			// Prompt for group name
			const groupName = window.prompt("Enter a name for the new group:");
			if (!groupName) {
				setSelectedStudent(null);
				return;
			}
			try {
				await createGroupWithStudents(selectedStudent.member.id, member.id, groupName);

				setLocalStudentsWithNoGroup(prev =>
					prev.filter(s => s.id !== selectedStudent.member.id && s.id !== member.id)
				);
				const updated = await fetchSessionGroups(sessionId);
				setGroups(updated);

			} catch (error) {
				setError("Failed to create group: " + error.message);

				setSelectedStudent(null);
				return;
			}
			setSelectedStudent(null);
			return;
		}

		if (from === groupId) {
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

			const updated = await fetchSessionGroups(sessionId);
			setGroups(updated);

			if (from == null) {
				setLocalStudentsWithNoGroup(prev =>
					prev.filter(s => s.id !== selectedStudent.member.id)
				);
			}

		} catch (error) {
			setError("Failed to move student: " + error.message);
		}
		setSelectedStudent(null);
	};

	return handleStudentClick;
}
