import React, { memo } from "react";
import StudentGroupActions from './StudentGroupActions';
import { useNavigate } from "react-router-dom";

const StudentTable = (({ columns, students, sessionId, session, user }) => {

	const navigate = useNavigate();

	// Check that the inputted columns contain at least one unhidden column
	if (!columns || !columns?.some(column => column.isHidden === false)) {
		return <div className="empty-message"> No visible columns.</div>;
	}

	function navigateToStudentPage(rowIndex) {
		const studentId = getStudentByRowIndex(rowIndex).id;
		navigate(`/session/${sessionId}/student/${studentId}`);
	}

	function getStudentByRowIndex(rowIndex) {
		const idColumn = columns.find(column => column.label === "ID");
		const studentId = idColumn.rows[rowIndex];
		const student = students.find(theStudent => theStudent.id === studentId);
		return student;
	}

	return (
		<table className="student-table">
			<thead>
				<tr>
					{columns.map((column, columnIndex) => (
						<>
							{!column.isHidden &&
								<th key={columnIndex}>
									{column.label}
								</th>
							}
						</>
					))}
					<th>
						Actions
					</th>
				</tr>
			</thead>
			<tbody>
				{columns[0].rows.map((_, rowIndex) => (
					// Why "visibleColumns[0]"?: It gets an array of the first rows of the first column,
					// and since each column has the same amount of rows, this basically works
					// as a for loop on the amount of rows in a column.
					// ---
					// Why "underscore"?: "The underscore (_) is a placeholder for the array element.
					// It's used when you don't need the actual value, but only the index. 
					// This is useful in cases where the item itself is irrelevant to your logic."
					// ---
					// Why "key"?: "You should add a key to each child as well as each element inside children.
					// This way React can handle the minimal DOM change." And else you get a warning.
					<tr
						key={rowIndex}
					>
						{columns.map((column, columnIndex) => (
							<>
								{
									!column.isHidden &&
									<td key={columnIndex}>
										{column.rows[rowIndex]}
									</td>
								}
							</>
						))}
						<td key="actions">
							<input
								type="button"
								onClick={() => navigateToStudentPage(rowIndex)}
								value="Go to student page"
							/>
							{user.role === "Student" &&
								<StudentGroupActions
									groupId={getStudentByRowIndex(rowIndex).groupId}
									session={session}
									user={user}
								/>
							}
						</td>
					</tr>
				))}
			</tbody>
		</table>
	);
});

export default StudentTable;