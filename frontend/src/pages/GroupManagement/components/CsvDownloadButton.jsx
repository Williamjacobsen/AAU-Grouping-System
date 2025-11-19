import React, { useState, useRef, useEffect, memo } from "react";
import { CSVLink } from "react-csv";

const CsvDownloadButton = memo(({ students, groups, supervisors }) => {

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

			if (!areAllStudentsAssignedAGroup(groups)) {
				alert("Warning: Not all students have been assigned a group!");
			}

			const newData = [];
			let number = 0;
			groups.forEach((group) => {

				const groupNumber = ++number;

				group.studentIds.forEach((studentId) => {

					const student = students.find((student) => student.id === studentId);

					if (student === null) {
						return Promise.reject("Error: The group's student id does not exist in the session's list of students. There must be an error in the database.");
					}

					const groupSupervisor = supervisors.find(supervisor => supervisor.id === group.supervisorId);

					newData.push({
						groupName: group.name,
						groupNumber: groupNumber,
						studentEmail: student.email,
						studentName: student.name,
						supervisorEmail: groupSupervisor.email ?? "Not specified",
						supervisorName: groupSupervisor.name ?? "Not specified"
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

	function areAllStudentsAssignedAGroup(groups) {

		let studentsAssignedAGroupAmount = 0;
		groups.forEach(group => {
			studentsAssignedAGroupAmount += group.studentIds.length;
		});

		return studentsAssignedAGroupAmount === students.length;
	}

	return (
		<>
			<button className="csv-download-button" onClick={startDownload}>
				Download CSV file of groups
			</button>

			{/* The CSVLink is not visible to the user. When clicked, it starts a download of a CSV file. */}
			<CSVLink
				filename={`Groups.csv`}
				headers={[
					{ label: "Group name", key: "groupName" },
					{ label: "Group number", key: "groupNumber" },
					{ label: "Student email", key: "studentEmail" },
					{ label: "Student name", key: "studentName" },
					{ label: "Supervisor email", key: "supervisorEmail" },
					{ label: "Supervisor name", key: "supervisorName" }
				]}
				data={csvData}
				ref={csvLinkRef}
			/>
		</>
	);
});

export default CsvDownloadButton;