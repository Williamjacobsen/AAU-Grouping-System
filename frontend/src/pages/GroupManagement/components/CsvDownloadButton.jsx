import React, { useState, useRef, useEffect, memo } from "react";
import { CSVLink } from "react-csv";

const CsvDownloadButton = memo(({ students, groups, supervisors, projects, session }) => {

	console.log("reloading");

	const [csvData, setCsvData] = useState([]);
	const [clickCsvLink, setClickCsvLink] = useState(false);
	const csvLinkRef = useRef();

	useEffect(() => {
		if (clickCsvLink) {
			csvLinkRef.current.link.click();
			setClickCsvLink(false);
		}
	}, [clickCsvLink]);

	async function startDownload() {
		try {
			if (!groups || groups.length === 0) {
				alert("Error: No groups found to download.");
				return;
			}

			if (!students || students.length === 0) {
				alert("Error: No students found to download.");
				return;
			}


			// Check if groups have supervisor
			const groupsWithoutSupervisors = groups.filter(group =>
				!group.supervisorId ||
				(typeof group.supervisorId === 'string' && group.supervisorId.trim() === '')
			);

			// Check if groups have project

			const groupsWithoutProjects = groups.filter(group =>
				!group.assignedProjectId ||
				(typeof group.assignedProjectId === 'string' && group.assignedProjectId.trim() === '')
			);

			if (groupsWithoutProjects.length > 0 || groupsWithoutSupervisors.length > 0) {
				alert("Error: Groups must have supervisors and projects assigned.");
				return;
			}

			const newData = [];
			let number = 0;

			// Sort groups by supervisor
			const sortedGroups = [...groups].sort((a, b) => {
				const supervisorA = supervisors?.find(s => s.id === a.supervisorId);
				const supervisorB = supervisors?.find(s => s.id === b.supervisorId);

				const supervisorNameA = supervisorA?.name || "";
				const supervisorNameB = supervisorB?.name || "";

				console.log(`Sorting: ${a.name} (supervisor: ${supervisorNameA}) vs ${b.name} (supervisor: ${supervisorNameB})`);

				if (supervisorNameA !== supervisorNameB) {
					return supervisorNameA.localeCompare(supervisorNameB);
				}

				return a.name.localeCompare(b.name);
			});

			// Generate CSV data
			sortedGroups.forEach((group) => {

				const groupNumber = ++number;

				group.studentIds.forEach((studentId) => {

					const student = students.find((student) => student.id === studentId);

					if (student === null) {
						return Promise.reject("Error: The group's student id does not exist in the session's list of students. There must be an error in the database.");
					}

					const groupSupervisor = supervisors.find(supervisor => supervisor.id === group.supervisorId);
					const groupProject = projects.find(project => project.id === group.assignedProjectId);

					newData.push({
						groupName: group.name,
						groupNumber: groupNumber,
						groupSize: group.studentIds.length,
						studentEmail: student.email,
						studentName: student.name,
						supervisorEmail: groupSupervisor?.email ?? "Not specified",
						supervisorName: groupSupervisor?.name ?? "Not specified",
						projectName: groupProject?.name ?? "No project assigned"
					});
				});
			});

			// setCsvData is asynchronous, so it does not change the csvData immediately,
			// instead only updating once the component re-renders. So, since we need
			// the csvData to be updated before we click the CSVLink, we must also make
			// this clicking of the CSVLink trigger upon component re-rendering.
			setCsvData(newData);
			setClickCsvLink(true);

		} catch (error) {
			alert(error);
		}
	}

	return (
		<>
			<button className="csv-download-button" onClick={startDownload}>
				Download CSV file of groups
			</button>

			{/* The CSVLink is not visible to the user. When clicked, it starts a download of a CSV file. */}
			<CSVLink
				filename={`Groups ${session?.name || 'Session'}.csv`}
				headers={[
					{ label: "Group name", key: "groupName" },
					{ label: "Group number", key: "groupNumber" },
					{ label: "Group size", key: "groupSize" },
					{ label: "Student email", key: "studentEmail" },
					{ label: "Student name", key: "studentName" },
					{ label: "Supervisor email", key: "supervisorEmail" },
					{ label: "Supervisor name", key: "supervisorName" },
					{ label: "Project name", key: "projectName" }
				]}
				data={csvData}
				ref={csvLinkRef}
			/>
		</>
	);
});

export default CsvDownloadButton;