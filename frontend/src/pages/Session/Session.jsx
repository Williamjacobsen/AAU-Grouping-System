import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";

import StudentTable from "./StudentTable";
import useGetSession from "./useGetSession";
import useStudentSorting from "./useStudentSorting";
import useStudentFiltering from "./useStudentFiltering";

export default function Session() {

	const { id: sessionId } = useParams();

	// TODO: Add a proper hook instead of this mock data
	// DO NOT REMOVE THIS!: const {isLoadingSession, session} = useGetSession(sessionId);
	const session = {};
	session.students = [];
	session.students.push({ id: 0, email: "boris@gmail.com", name: "Boris", questionnaire: { projectPriority1: "Project B", projectPriority2: "..." }, group: { number: "...", project: "..." } });
	session.students.push({ id: 1, email: "caroline@gmail.com", name: "Caroline", questionnaire: { projectPriority1: "Project A", projectPriority2: "Project B" }, group: { number: "1", project: "Project A" } });
	session.students.push({ id: 2, email: "darryl@gmail.com", name: "Darryl", questionnaire: { projectPriority1: "Project B", projectPriority2: "Project C" }, group: { number: "2", project: "Project B" } });
	session.students.push({ id: 3, email: "abe@gmail.com", name: "Abe", questionnaire: { projectPriority1: "Project C", projectPriority2: "Project A" }, group: { number: "1", project: "Project A" } });

	const [allStudents, setAllStudents] = useState(null);
	useEffect(() => {
		setAllStudents(session.students);
	}, []);

	const { toSorted, SortingDropdown } = useStudentSorting();
	const { toFiltered, SearchFilterInput } = useStudentFiltering();
	
	const visibleStudents = useMemo(() => {

		if (allStudents === null) return null;
		
		let result = allStudents;
		result = toFiltered(result);
		result = toSorted(result);

		return result;
	}, [allStudents, toSorted, toFiltered]);

	// DO NOT REMOVE THIS!: if (isLoadingSession) {
  //   return <>Loading session information...</>;
  // }

	return (
		<>
			<SearchFilterInput/>
			<SortingDropdown />
			<StudentTable students={visibleStudents}/>
		</>
	) 

	// 

}