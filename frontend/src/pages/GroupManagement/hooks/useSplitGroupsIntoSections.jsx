
import { useMemo } from "react";

export default function useSplitGroupsIntoSections(groups, session) {

	// Fraction of min group size required to call a group "almost completed" instead of "incomplete"
	const almostCompleteFraction = 0.5;

	const toLargeGroups = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length > session?.minGroupSize && group.studentIds?.length > session?.maxGroupSize
		);
	}, [groups, session]);

	const completedGroups = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length >= session?.minGroupSize && group.studentIds?.length <= session?.maxGroupSize
		);
	}, [groups, session]);

	const almostCompletedGroups = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length >= session?.minGroupSize * almostCompleteFraction && group.studentIds?.length < session?.minGroupSize
		);
	}, [groups, session]);

	const incompleteGroups = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length > 1 && group.studentIds?.length < session?.minGroupSize * almostCompleteFraction
		);
	}, [groups, session]);

	const groupsWith1Member = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length === 1
		);
	}, [groups]);

	return { toLargeGroups, completedGroups, almostCompletedGroups, incompleteGroups, groupsWith1Member };
}