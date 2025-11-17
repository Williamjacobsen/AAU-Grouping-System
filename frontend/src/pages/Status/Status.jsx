import React from "react";
import { useParams } from "react-router-dom";
import { useGetUser } from "../../hooks/useGetUser";
import "./Status.css";

import useGetSessionStudents from "../../hooks/useGetSessionStudents";
import { useGetSessionByParameter } from "../../hooks/useGetSession";
import useGetSessionProjects from "hooks/useGetSessionProjects";
import useGetSessionGroups from "hooks/useGetSessionGroups";

import useColumns from "./useColumns";
import useColumnSorting from "./useColumnSorting";
import useColumnSelecting from "./useColumnSelecting";
import useColumnSearching from "./useColumnSearching";

import SearchFilter from "./SearchFilter";
import ColumnsDropdown from "./ColumnsDropdown";
import StudentTable from "./StudentTable";
import CsvDownloadButton from "./CsvDownloadButton";

export default function Status() {

	const { sessionId } = useParams();

	// Get hooks
	const { isLoading: isLoadingUser, user } = useGetUser();
	const { isLoading: isLoadingSession, session } = useGetSessionByParameter();
	const { isLoading: isLoadingStudents, students } = useGetSessionStudents(sessionId);
	const { isLoading: isLoadingProjects, projects } = useGetSessionProjects(sessionId);
	const { isLoading: isLoadingGroups, groups } = useGetSessionGroups(sessionId);

	// Getting the columns for the student table
	const { originalColumns } = useColumns(students, projects, groups);
	const { sortedColumns, sortColumns } = useColumnSorting(originalColumns);
	const { enabledColumns, enabledLabels, toggleLabel } = useColumnSelecting(sortedColumns);
	const { searchedColumns, searchString, setSearchString } = useColumnSearching(enabledColumns);

	// Loading
	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return <div className="access-denied-message">Access denied: Not logged in.</div>;
	if (isLoadingSession) return <div className="loading-message">Loading session information...</div>;
	if (isLoadingStudents) return <div className="loading-message">Loading session information...</div>;
	if (isLoadingProjects) return <div className="loading-message">Loading session projects...</div>;
	if (isLoadingGroups) return <div className="loading-message">Loading session groups...</div>;

	return (
		<div className="status-container">
			<h1 className="status-title">
				Student Status
			</h1>
			<h3>
				Students are allowed to make changes until this deadline: {session.questionnaireDeadline?.replace("T", " ") ?? "No deadline set"}
			</h3>
			<div className="status-controls">
				<SearchFilter
					searchString={searchString}
					setSearchString={setSearchString}
				/>
				<ColumnsDropdown
					originalColumns={originalColumns}
					columns={sortedColumns}
					enabledLabels={enabledLabels}
					toggleLabel={toggleLabel}
					sortColumns={sortColumns}
				/>
			</div>
			<div className="table-wrapper">
				<StudentTable
					columns={searchedColumns}
					students={students}
					sessionId={sessionId}
					session={session}
					user={user} />
			</div>
			{user?.role === "Coordinator" &&
				<CsvDownloadButton students={students} sessionId={sessionId} />
			}
		</div>
	);
}