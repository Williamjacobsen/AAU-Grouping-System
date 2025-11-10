import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";
import { useGetUser } from "../../hooks/useGetUser";

import StudentTable from "./StudentTable";
import useGetSessionStudents from "../../hooks/useGetSessionStudents";
import useStudentSorting from "./useStudentSorting";
import useStudentFiltering from "./useStudentFiltering";
import CsvDownloadButton from "./CsvDownloadButton";
import "./Status.css";


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

	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return <div className="access-denied-message">Access denied: Not logged in.</div>;
	if (isLoadingStudents) {
    return <div className="loading-message">Loading session information...</div>;
	}
	
	return (
		<div className="status-container">
			<h1 className="status-title">Student Status</h1>
			<div className="status-controls">
				<SearchFilterInput />
				<SortingDropdown />
			</div>
			<StudentTable students={visibleStudents} />
			{user?.role === "Coordinator" &&
				<CsvDownloadButton allStudents={allStudents} sessionId={sessionId} />
			}
		</div>
	) 
}