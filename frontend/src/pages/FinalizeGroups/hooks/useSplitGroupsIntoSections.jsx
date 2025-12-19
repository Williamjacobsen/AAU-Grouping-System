
import { useMemo } from "react";

export default function useSplitGroupsIntoSections(groups, session) {

	// Fraction of min group size required to call a group "almost completed" instead of "incomplete"
	const almostCompleteFraction = 0.5;

	const toLargeGroups = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length > session?.maxGroupSize
		);
	}, [groups, session]);

	const completedGroups = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length >= session?.minGroupSize && group.studentIds?.length <= session?.maxGroupSize
			&& group.studentIds?.length !== 0
		);
	}, [groups, session]);

	const almostCompletedGroups = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length >= session?.minGroupSize * almostCompleteFraction && group.studentIds?.length < session?.minGroupSize
			&& group.studentIds?.length !== 0
		);
	}, [groups, session]);

	const incompleteGroups = useMemo(() => {
		return groups.filter(group =>
			group.studentIds?.length < session?.minGroupSize * almostCompleteFraction
			&& group.studentIds?.length !== 0
		);
	}, [groups, session]);

	return { toLargeGroups, completedGroups, almostCompletedGroups, incompleteGroups };
}