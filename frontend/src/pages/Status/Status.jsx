import React, { useMemo } from "react";
import { useParams } from "react-router-dom";
import { useGetUser } from "../../hooks/useGetUser";

import StudentTable from "./StudentTable";
import useGetSessionStudents from "../../hooks/useGetSessionStudents";
import { useGetSessionByParameter } from "../../hooks/useGetSession";
import useStudentFiltering from "./useStudentFiltering";
import useStudentColumns from "./useStudentColumns";
import CsvDownloadButton from "./CsvDownloadButton";
import useGetSessionProjects from "hooks/useGetSessionProjects";
import useGetSessionGroups from "hooks/useGetSessionGroups";

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

	if (isLoadingUser) return <>Checking authentication...</>;
	if (!user) return <>Access denied: Not logged in.</>;
	if (isLoadingSession) return <>Loading session information...</>;
	if (isLoadingStudents) return <>Loading session students...</>;
	if (isLoadingProjects) return <>Loading session projects...</>;
	if (isLoadingGroups) return <>Loading session groups...</>;

	return (
		<>
			<SearchFilterInput />
			<ColumnSelector />
			<StudentTable
				visibleColumns={visibleColumns}
				students={visibleStudents}
				sessionId={sessionId}
				session={session}
				user={user} />
			{user?.role === "Coordinator" &&
				<CsvDownloadButton allStudents={allStudents} sessionId={sessionId} />
			}
		</>
	);
}