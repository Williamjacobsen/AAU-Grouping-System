
import { useMemo } from "react";

export default function useSplitGroupsIntoSections(groups, session) {

	// Fraction of min group size required to call a group "almost completed" instead of "incomplete"
	const almostCompleteFraction = 0.5;

	
	const completedGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length >= session?.minGroupSize && group.members.length <= session?.maxGroupSize
		);
	}, [groups, session]);

	const almostCompletedGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length >= session?.minGroupSize * almostCompleteFraction && group.members.length < session?.minGroupSize
		);
	}, [groups, session]);

	const incompleteGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length > 0 && group.members.length < session?.minGroupSize * almostCompleteFraction 
		);
	}, [groups, session]);

	return { completedGroups, almostCompletedGroups, incompleteGroups };
}