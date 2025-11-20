
export default function useStudentClick({
	selectedStudent, setSelectedStudent, setPreviousGroups,
	setCanUndo, setLastAction, setGroups, setLocalStudentsWithNoGroup,
	moveStudent, session, groups, setError
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
			selectedStudent.member.groupId = groupId;
			setLastAction({
				type: "student",
				from: selectedStudent.from,
				to: groupId,
				student: selectedStudent.member,
			});
			setGroups(prevGroups => {
				const targetGroup = prevGroups.find(group => group.id === groupId);

				if (!targetGroup) {
					setError("You cannot move a student into the - Students without a group - list");
					return prevGroups;
				}

				if (targetGroup.studentIds.length >= session?.maxGroupSize) {
					setError("Sorry, adding this student would make the group too big");
					return prevGroups;
				}

				const newGroups = prevGroups.map(group => {
					if (group.id === from) {
						return { ...group, studentIds: group.studentIds.filter(id => id !== selectedStudent.member.id) };
					}
					if (group.id === groupId) {
						return { ...group, studentIds: [...group.studentIds, selectedStudent.member.id] };
					}
					return group;
				});

				return newGroups;
			});
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
