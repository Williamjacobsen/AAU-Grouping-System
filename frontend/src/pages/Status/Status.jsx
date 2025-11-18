import React, { useMemo } from "react";
import { useParams } from "react-router-dom";
import { useAuth } from "../../ContextProviders/AuthProvider";

import StudentTable from "./StudentTable";
import useStudentFiltering from "./useStudentFiltering";
import CsvDownloadButton from "./CsvDownloadButton";

import "./Status.css";
import useStudentColumns from "./useStudentColumns";

import { useAppState } from "ContextProviders/AppStateContext";

export default function Status() {

	const { sessionId } = useParams();

	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingApp, session, students: allStudents, projects, groups } = useAppState();

	const { toFiltered, SearchFilterInput } = useStudentFiltering();

	const visibleStudents = useMemo(() => {

		if (!allStudents) return null;

		let result = allStudents;
		result = toFiltered(result);

		return result;
	}, [allStudents, toFiltered]);

	const { visibleColumns, ColumnSelector } = useStudentColumns(visibleStudents, projects, groups);

	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return <div className="access-denied-message">Access denied: Not logged in.</div>;
	if (isLoadingApp) return <div className="loading-message">Loading information...</div>;

	return (
		<div className="status-container">
			<h1 className="status-title">Student Status</h1>
			<div className="status-controls">
				<SearchFilterInput />
				<ColumnSelector />
			</div>
			<div className="table-wrapper">
				<StudentTable
					visibleColumns={visibleColumns}
					visibleStudents={visibleStudents}
					sessionId={sessionId}
					session={session}
					user={user} />
			</div>
			{user?.role === "Coordinator" &&
				<CsvDownloadButton allStudents={allStudents} sessionId={sessionId} />
			}
		</div>
	);
}