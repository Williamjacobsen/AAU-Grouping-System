

import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function StudentTable({ students }) {

	// hooks

	const [ columns, setColumns ] = useState(null);

	useEffect(() => {
		if (students !== null) {
			const newColumns = createColumns(students)
			setColumns(newColumns);
		}
	}, [students]);
	
	// methods

	function createColumns() {
		const columns = [];
		// TODO: Add column showning/hiding
		columns.push(createColumn("Name", students.map(student => student.name)));
		columns.push(createColumn("1st priority", students.map(student => student.questionnaire.projectPriority1)));
		columns.push(createColumn("Group number", students.map(student => student.group.number)));
		columns.push(createColumn("Group project", students.map(student => student.group.project)));
		return columns;
	}

	function createColumn(columnName, values) {
		const column = {
			name: columnName,
			rows: []
		};
		values.forEach(value => {
			column.rows.push(value);
		});
		return column;
	}

	// return

	return (
		<>
			{columns != null &&
				<table>
					<thead>
						<tr>
							{columns.map((column, columnIndex) => {
								return (
									<th key={columnIndex}>
										{column.name}
									</th>
								);
							})}
						</tr>
					</thead>
					<tbody>
						{columns[0].rows.map((_, rowIndex) => (
							// Why "columns[0]"?: It gets an array of the first rows of the first column,
							// and since each column has the same amount of rows, this basically works
							// as a for loop on the amount of rows in a column.
							// ---
							// Why "underscore"?: "The underscore (_) is a placeholder for the array element.
							// It's used when you don't need the actual value, but only the index. 
							// This is useful in cases where the item itself is irrelevant to your logic."
							// ---
							// Why "key"?: "You should add a key to each child as well as each element inside children.
							// This way React can handle the minimal DOM change." And else you get a warning.
							<tr key={rowIndex}>
								{columns?.map((column, columnIndex) => (
									<td key={columnIndex}>
										{column.rows[rowIndex]}
									</td>
								))}
							</tr>
						))}
					</tbody>
				</table>
			}
		</>
	);
}