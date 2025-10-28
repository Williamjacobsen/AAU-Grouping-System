import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";
import { useGetUser } from "../../utils/useGetUser";

import StudentTable from "./StudentTable";
import useGetSessionStudents from "../../utils/useGetSessionStudents";
import useStudentSorting from "./useStudentSorting";
import useStudentFiltering from "./useStudentFiltering";


export default function Status() {

	const { sessionId } = useParams();
	const { user, isLoading: isLoadingUser } = useGetUser();

	const { isloading: isLoadingStudents, students: allStudents } = useGetSessionStudents(sessionId);
	const { toSorted, SortingDropdown } = useStudentSorting();
	const { toFiltered, SearchFilterInput } = useStudentFiltering();
	
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
			<StudentTable students={visibleStudents}/>
		</>
	) 
}