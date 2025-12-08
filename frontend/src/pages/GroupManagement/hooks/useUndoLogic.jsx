import { fetchSessionGroups } from "hooks/useGetSessionGroups";

export default function useUndoLogic({
	previousGroups, setGroups,
	lastAction, setLastAction, setCanUndo,
	moveStudent, sessionId
}) {

	const handleUndo = async () => {
		try {
			setGroups(previousGroups);

			if (lastAction) {
				if (lastAction.type === "student") {
					await moveStudent(lastAction.to, lastAction.from, lastAction.student.id);

				} else if (lastAction.type === "group") {
					const originalFromGroup = previousGroups.find((g) => g.id === lastAction.from
					);

					if (originalFromGroup && originalFromGroup.studentIds?.length) {
						// Only move back the students that originally belonged to A
						for (const studentId of originalFromGroup.studentIds) {
							await moveStudent(lastAction.to, lastAction.from, studentId);
						}
					}

				}
				const updated = await fetchSessionGroups(sessionId);
				setGroups(updated);
			}
		} catch (err) {
			alert("Failed to undo: " + err.message);
		}

		setCanUndo(false);
		setLastAction(null);
	};

	return handleUndo;
}
