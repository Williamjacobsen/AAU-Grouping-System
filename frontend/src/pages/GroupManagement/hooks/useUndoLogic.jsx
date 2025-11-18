
export default function useUndoLogic({
	previousGroups, setGroups, 
	lastAction, setLastAction, setCanUndo, 
	moveStudent, moveAllMembers, setError
}) {

	const handleUndo = async () => {
		try {
			setGroups(previousGroups);
			setError("");
			if (lastAction) {
				if (lastAction.type === "student") {
					await moveStudent(lastAction.to, lastAction.from, lastAction.student.id);
				} else if (lastAction.type === "group") {
					await moveAllMembers(lastAction.to, lastAction.from);
				}
			}
		} catch (err) {
			setError("Failed to undo: " + err.message);
		}
		setCanUndo(false);
		setLastAction(null);
	};

	return handleUndo;
}
