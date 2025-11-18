
import { useMemo } from "react";

export default function useSplitGroupsIntoSections(groups, session) {

	// Fraction of min group size required to call a group "almost completed" instead of "incomplete"
	const almostCompleteFraction = 0.5;

const completedGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length >= (session?.minGroupSize || 7) && group.members.length <= (session?.maxGroupSize || 7)
		);
	}, [groups, session]);

	const almostCompletedGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length >= (session?.minGroupSize || 7) * almostCompleteFraction && group.members.length < (session?.minGroupSize || 7)
		);
	}, [groups, session]);

	const incompleteGroups = useMemo(() => {
		return groups.filter(group =>
			group.members.length > 1 && group.members.length < (session?.minGroupSize || 7) * almostCompleteFraction
		);
	}, [groups, session]);

	const groupsWith1Member = useMemo(() => {
		return groups.filter(group =>
			group.members.length === 1 
		);
	}, [groups])

	return { completedGroups, almostCompletedGroups, incompleteGroups, groupsWith1Member };
}