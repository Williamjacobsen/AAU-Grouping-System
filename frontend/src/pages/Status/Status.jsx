import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "../User/useAuth";

import StudentTable from "./StudentTable";
import useGetSessionStudents from "../../utils/useGetSessionStudents";
import useStudentSorting from "./useStudentSorting";
import useStudentFiltering from "./useStudentFiltering";


export default function Status() {

	const { sessionId } = useParams();
	const { user, loading: loadingAuth } = useAuth();

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

	if (loadingAuth) return <>Checking authentication...</>;
	if (!user) return null;

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