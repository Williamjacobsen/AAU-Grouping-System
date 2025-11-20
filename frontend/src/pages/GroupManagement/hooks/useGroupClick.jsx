
export default function useGroupClick({
	selectedGroup, setSelectedGroup,
	setPreviousGroups, setCanUndo, setLastAction,
	setGroups, moveAllMembers, session, groups, setError
}) {

	const handleGroupClick = async (groupId) => {
		if (!selectedGroup) {
			setSelectedGroup({ from: groupId });
			return;
		}

		if (selectedGroup.from === groupId) {
			setSelectedGroup(null);
			return;
		}

		try {
			setPreviousGroups(groups);
			setCanUndo(true);
			await moveAllMembers(selectedGroup.from, groupId);
			setLastAction({
				type: "group",
				from: selectedGroup.from,
				to: groupId,
			});
			setGroups(prevGroups => {
				const fromGroup = prevGroups.find(group => group.id === selectedGroup.from);
				const targetGroup = prevGroups.find(group => group.id === groupId);

				if (targetGroup.studentIds.length + fromGroup.studentIds.length > session?.maxGroupSize) {
					setError("Sorry, merging these groups would make the group too big");
					return prevGroups;
				}

				const newGroups = prevGroups.map(group => {
					if (group.id === selectedGroup.from)
						return { ...group, studentIds: [] };

					if (group.id === groupId)
						return { ...group, studentIds: [...group.studentIds, ...fromGroup.studentIds] };

					return group;
				});
				setPreviousGroups(newGroups);
				return newGroups;
			});
		} catch (error) {
			setError("Failed to merge groups: " + error.message);
		}
		setSelectedGroup(null);
	};

	return handleGroupClick;
}
