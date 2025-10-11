import { useState, useEffect, useCallback } from "react";

/**
 * @returns An object {toFiltered, SearchFilterInput}.
 * - toFiltered(students) is a useCallback function for creating a filtered clone of an array of students.
 * - SearchFilterInput is a component.
 */
export default function useStudentFiltering() {

	const [searchFilter, setSearchFilter] = useState(""); 

	const toFiltered = useCallback((students) => {
		if (!students || students.length === 0) return students;
		if (!searchFilter || searchFilter === "") return students;

		const caseInsensitiveSearchFilter = searchFilter?.toLowerCase();

		return students.filter(item => {
			// TODO: Add more student properties to include in the search for
			if (item.name?.toLowerCase().includes(caseInsensitiveSearchFilter)
				|| item.email?.toLowerCase().includes(caseInsensitiveSearchFilter)
				|| item.questionnaire.projectPriority1?.toLowerCase().includes(caseInsensitiveSearchFilter)
				|| item.questionnaire.projectPriority2?.toLowerCase().includes(caseInsensitiveSearchFilter)
				|| item.group.number?.toLowerCase().includes(caseInsensitiveSearchFilter)
				|| item.group.project?.toLowerCase().includes(caseInsensitiveSearchFilter)) {
				return true;
			} else { return false; }
		});
	}, [searchFilter]);

	const SearchFilterInput = useCallback(() => {
		return (
			<>
				<input
					defaultValue={searchFilter}
					placeholder={"Search..."}
					onChange={(event) => setSearchFilter(event.target.value)}
				/>
			</>
		);
	}, []);

	return { toFiltered, SearchFilterInput };

}