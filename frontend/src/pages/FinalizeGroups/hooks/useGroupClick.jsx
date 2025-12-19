import { fetchSessionGroups } from "hooks/fetching/useGetSessionGroups";

export default function useGroupClick({
	selectedGroup, setSelectedGroup,
	setPreviousGroups, setCanUndo, setLastAction,
	setGroups, moveAllMembers, groups, sessionId
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

			const updated = await fetchSessionGroups(sessionId);
			setGroups(updated);

		} catch (error) {
			alert("Failed to merge groups: " + error.message);
		}
		setSelectedGroup(null);
	};

	return handleGroupClick;
}
