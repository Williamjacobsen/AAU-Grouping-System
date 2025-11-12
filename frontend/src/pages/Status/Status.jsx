import React, { useMemo } from "react";
import { useParams } from "react-router-dom";
import { useGetUser } from "../../hooks/useGetUser";

import StudentTable from "./StudentTable";
import useGetSessionStudents from "../../hooks/useGetSessionStudents";
import { useGetSessionByParameter } from "../../hooks/useGetSession";
import useStudentFiltering from "./useStudentFiltering";
import CsvDownloadButton from "./CsvDownloadButton";
import useGetSessionProjects from "hooks/useGetSessionProjects";
import useGetSessionGroups from "hooks/useGetSessionGroups";

import "./Status.css";
import useStudentColumns from "./useStudentColumns";

export default function Status() {

	const { sessionId } = useParams();

	const { isLoading: isLoadingUser, user } = useGetUser();
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingStudents, students: allStudents } = useGetSessionStudents(sessionId);
	const { isLoading: isLoadingProjects, projects } = useGetSessionProjects(sessionId);
	const { isLoading: isLoadingGroups, groups } = useGetSessionGroups(sessionId);

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
	if (isLoadingSession) return <div className="loading-message">Loading session information...</div>;
	if (isLoadingStudents) return <div className="loading-message">Loading session information...</div>;
	if (isLoadingProjects) return <div className="loading-message">Loading session projects...</div>;
	if (isLoadingGroups) return <div className="loading-message">Loading session groups...</div>;

	return (
		<div className="status-container">
			<h1 className="status-title">Student Status</h1>
			<div className="status-controls">
				<SearchFilterInput />
				<ColumnSelector />
			</div>
			<StudentTable
				visibleColumns={visibleColumns}
				visibleStudents={visibleStudents}
				sessionId={sessionId}
				session={session}
				user={user} />
			{user?.role === "Coordinator" &&
				<CsvDownloadButton allStudents={allStudents} sessionId={sessionId} />
			}
		</div>
	);
}