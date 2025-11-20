import React from "react";
import { useParams } from "react-router-dom";
import "./Status.css";

import { useAuth } from "../../ContextProviders/AuthProvider";
import { useAppState } from "ContextProviders/AppStateContext";

import useColumns from "./useColumns";
import useColumnSorting from "./useColumnSorting";
import useColumnSelection from "./useColumnSelection";
import useColumnSearching from "./useColumnSearching";

import SearchFilter from "./SearchFilter";
import ColumnsDropdown from "./ColumnsDropdown";
import StudentTable from "./StudentTable";
import GroupMenu from "./GroupMenu";

export default function Status() {

	const { sessionId } = useParams();

	// Get hooks
	const { isLoading: isLoadingUser, user } = useAuth();
	const { isLoading: isLoadingApp, session, students, projects, groups } = useAppState();

	// Getting the columns for the student table
	const { originalColumns } = useColumns(students, projects, groups);
	const { sortedColumns, sortColumns, alsoSortByGroupName, setAlsoSortByGroupName } = useColumnSorting(originalColumns);
	const { enabledColumns, enabledLabels, toggleLabel } = useColumnSelection(sortedColumns);
	const { searchedColumns, searchString, setSearchString } = useColumnSearching(enabledColumns);

	// Loading
	if (isLoadingUser) return <div className="loading-message">Checking authentication...</div>;
	if (!user) return <div className="access-denied-message">Access denied: Not logged in.</div>;
	if (isLoadingApp) return <div className="loading-message">Loading information...</div>;

	return (
		<div className="status-container">
			<h1 className="status-title">
				Student Status
			</h1>
			<div>
				<b>Students are allowed to make changes until this deadline: </b>
				{session?.questionnaireDeadline?.replace("T", " ") ?? "No deadline set"}
			</div>
			<div>
				<GroupMenu
					session={session}
					user={user}
					groups={groups}
					projects={projects}
					students={students}
				/>
			</div>
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
					alsoSortByGroupName={alsoSortByGroupName}
					setAlsoSortByGroupName={setAlsoSortByGroupName}
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
		</div>
	);
}