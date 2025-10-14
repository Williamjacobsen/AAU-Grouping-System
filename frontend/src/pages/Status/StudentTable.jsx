import React, { useMemo, memo } from "react";

const StudentTable = memo(({ students }) => {

	const columns = useMemo(() => {
		
		if (!students || students.length === 0) return null;
		
    return [
      createColumn("Name", students.map(student => student.name)),
      createColumn("Group", students.map(student => student.group?.number)),
      createColumn("Group project", students.map(student => student.group?.project))
		];
		
		function createColumn(columnName, values) {
			return {
				name: columnName,
				rows: values
			};
		}
  }, [students]);

  if (!columns || columns.length === 0) {
    return <div>List of students is empty.</div>;
  }

  return (
    <table>
      <thead>
        <tr>
          {columns.map((column, columnIndex) => (
            <th key={columnIndex}>
              {column.name}
            </th>
          ))}
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
            {columns.map((column, columnIndex) => (
              <td key={columnIndex}>
                {column.rows[rowIndex]}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
});

export default StudentTable;