import { fetchSessionGroups } from "hooks/useGetSessionGroups";

export default function useStudentClick({
	selectedStudent, setSelectedStudent, setPreviousGroups,
	setCanUndo, setLastAction, setGroups, setLocalStudentsWithNoGroup,
	moveStudent, session, groups, setError, sessionId
}) {

	const handleStudentClick = async (member, groupId) => {
		if (!selectedStudent) {
			setSelectedStudent({ member, from: groupId });
			return;
		}

		const from = selectedStudent.from;

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
