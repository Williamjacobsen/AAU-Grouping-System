import React, { useState, useCallback, useMemo } from "react";

/**
 * @returns An object {toSorted, SortingDropdown}.
 * - toSorted(students) is a useCallback function for creating a sorted clone of an array of students.
 * - SortingDropdown is a component.
 */
export default function useStudentSorting() {
	
	const sortingModeEnum = useMemo(() => Object.freeze({
		groupAscending: "By group (ascending)",
		groupDescending: "By group (descending)",
		nameAscending: "By name (ascending)",
		nameDescending: "By name (descending)",
	}), []);

	const [sortingMode, setSortingMode] = useState(sortingModeEnum.groupDescending);

	const toSorted = useCallback((students) => {

		if (!students || students.length === 0) return students;

		// Comparison functions
		function compareGroups(a, b) { return a.group?.number?.localeCompare(b.group?.number); }
		function compareNames(a, b) { return a.name?.localeCompare(b.name); }

		// Create a copy of the array to avoid mutation
		const sortedStudents = [...students];

		// Default sorting
		sortedStudents.sort((a, b) => compareNames(a, b));
		sortedStudents.sort((a, b) => compareGroups(b, a));

		// User-selected sorting mode
		sortedStudents.sort((a, b) => {
			switch (sortingMode) {
				case sortingModeEnum.groupAscending:
					return compareGroups(a, b);
				case sortingModeEnum.groupDescending:
					return compareGroups(b, a);
				case sortingModeEnum.nameAscending:
					return compareNames(a, b);
				case sortingModeEnum.nameDescending:
					return compareNames(b, a);
				default:
					return compareGroups(a, b);
			}
		});

		return sortedStudents;
	}, [sortingMode]);

	const SortingDropdown = useCallback(() => {
		return (
			<>
				<select value={sortingMode} onChange={(event) => setSortingMode(event.target.value)}>
					<option value={sortingModeEnum.groupAscending}>{sortingModeEnum.groupAscending}</option>
					<option value={sortingModeEnum.groupDescending}>{sortingModeEnum.groupDescending}</option>
					<option value={sortingModeEnum.nameAscending}>{sortingModeEnum.nameAscending}</option>
					<option value={sortingModeEnum.nameDescending}>{sortingModeEnum.nameDescending}</option>
				</select>
			</>
		);
	}, [sortingMode]);

	return { toSorted, SortingDropdown };

}