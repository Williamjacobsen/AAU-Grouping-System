import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import StudentTable from "./StudentTable";
import useGetSession from "./useGetSession";

export default function Session() {

	// enums

	const sortingModeEnum = Object.freeze({
		groupAscending: "groupAscending",
		groupDescending: "groupDescending",
		nameAscending: "nameAscending",
		nameDescending: "nameDescending",
	});
	
	// hooks

	const { id: sessionId } = useParams();

	// TODO: Add a proper hook instead of this mock data
	// DO NOT REMOVE THIS! const [isLoadingSession, session] = useGetSession(sessionId);
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

	const [visibleStudents, setVisibleStudents] = useState(null);
	const [sortingMode, setSortingMode] = useState(sortingModeEnum.groupDescending);
	const [searchFilter, setSearchFilter] = useState(""); 

	useEffect(() => {
		if (allStudents !== null) {
			let newArray = allStudents;
			newArray = toFilter(newArray);
			sort(newArray);
			setVisibleStudents(newArray);
		}
	}, [allStudents, sortingMode, searchFilter]);

	// methods

	function toFilter(students) {
		if (searchFilter !== "") {
			return students.filter(item => {
				// TODO: Add more student properties to include in the search for
				if (item.name.includes(searchFilter)
					|| item.email.includes(searchFilter)
					|| item.questionnaire.projectPriority1.includes(searchFilter)
					|| item.questionnaire.projectPriority2.includes(searchFilter)
					|| item.group.number.includes(searchFilter)
					|| item.group.project.includes(searchFilter)) {
					return true;
				}
				else {
					return false;
				}
			})
		}
		return students;
	}

	function sort(students) {
		// Comparison functions
		function compareGroups(a, b) { return a.group.number.localeCompare(b.group.number); }
		function compareNames(a, b) { return a.name.localeCompare(b.name); }

		// Default sorting
		students.sort((a, b) => compareNames(a, b))
		students.sort((a, b) => compareGroups(b, a))

		// User-selected sorting mode
		students.sort((a, b) => {
			switch (sortingMode) {
				case sortingModeEnum.groupAscending:
					return compareGroups(a, b);
				case sortingModeEnum.groupDescending:
					return compareGroups(b, a);
				case sortingModeEnum.nameAscending:
					return compareNames(a, b);
				case sortingModeEnum.nameDescending:
					return compareNames(b, a);
				default: 
					return compareGroups(a, b);
			}
		});
	}
	
	// return

	// DO NOT REMOVE THIS! if (isLoadingSession) {
  //   return <>Loading session information...</>;
  // }

	return (
		<>
			<StudentTable students={visibleStudents}/>
		</>
	) 

}