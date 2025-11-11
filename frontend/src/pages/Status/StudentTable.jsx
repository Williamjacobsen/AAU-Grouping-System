import React, { memo } from "react";
import StudentGroupActions from './StudentGroupActions';
import { useNavigate } from "react-router-dom";

const StudentTable = memo(({ visibleColumns, visibleStudents, sessionId, session, user }) => {

	const navigate = useNavigate();

	function navigateToStudentPage(studentIndex) {
		const student = visibleStudents[studentIndex];
		if (student && student.id) {
			navigate(`/session/${sessionId}/student/${student.id}`);
		}
	}

	if (!visibleColumns || visibleColumns.length === 0) {
		return <div>No visible columns.</div>;
	}

	return (
		<table>
			<thead>
				<tr>
					{visibleColumns.map((column, columnIndex) => (
						<th key={columnIndex}>
							{column.label}
						</th>
					))}
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
				{visibleColumns[0].rows.map((_, rowIndex) => (
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
						onClick={() => navigateToStudentPage(rowIndex)}
						style={{ cursor: 'pointer' }}
					>
						{visibleColumns.map((column, columnIndex) => (
							<td key={columnIndex}>
								{column.rows[rowIndex]}
							</td>
						))}
						<td>
							<StudentGroupActions
								groupId={visibleStudents?.[rowIndex]?.group?.id}
								studentId={visibleStudents?.[rowIndex]?.id}
								session={session}
								user={user}
							/>
						</td>
					</tr>
				))}
			</tbody>
		</table>
	);
});

export default StudentTable;