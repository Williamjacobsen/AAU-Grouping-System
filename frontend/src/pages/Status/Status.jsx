import React, { useMemo } from "react";
import { useParams } from "react-router-dom";
import { useGetUser } from "../../utils/useGetUser";

import StudentTable from "./StudentTable";
import useGetSessionStudents from "../../utils/useGetSessionStudents";
import useStudentSorting from "./useStudentSorting";
import useStudentFiltering from "./useStudentFiltering";
import useStudentColumns from "./useStudentColumns";


export default function Status() {

	const { sessionId } = useParams();
	const { user, isLoading: isLoadingUser } = useGetUser();

	const { isLoading: isLoadingStudents, students: allStudents } = useGetSessionStudents(sessionId);
	const { toSorted, SortingDropdown } = useStudentSorting();
	const { toFiltered, SearchFilterInput } = useStudentFiltering();
	const { selectedColumns, ColumnsSelector } = useStudentColumns();
	const visibleStudents = useMemo(() => {

		if (!allStudents) return null;

		let result = allStudents;
		result = toFiltered(result);
		result = toSorted(result);

		return result;
	}, [allStudents, toSorted, toFiltered]);

	if (isLoadingUser) return <>Checking authentication...</>;
	if (!user) return <>Access denied: Not logged in.</>;

	if (isLoadingStudents) {
    return <>Loading session information...</>;
  }
 
	return (
		<>
			<SearchFilterInput/>
			<SortingDropdown />
			<ColumnsSelector />
				<StudentTable students={visibleStudents} columnDefs={selectedColumns} sessionId={sessionId} />
		</>
	);
}