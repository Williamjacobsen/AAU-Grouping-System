import React, { useMemo, memo } from "react";
import StudentGroupActions from './StudentGroupActions';


const StudentTable = memo(({ students, columnDefs, sessionId }) => {

  const columns = useMemo(() => {
    if (!students || students.length === 0) return null;

    // If columnDefs is provided (array of { label, accessor }), use it.
    if (columnDefs && Array.isArray(columnDefs) && columnDefs.length > 0) {
      return columnDefs.map(def => ({
        name: def.label ?? def.name,
        rows: students.map(def.accessor)
      }));
    }

    // Default columns
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
  }, [students, columnDefs]);

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
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
  {columns[0].rows.map((_, rowIndex) => (
          <tr
            key={rowIndex}
            style={{ cursor: 'pointer' }}
            onClick={() => {
              const studentId = students?.[rowIndex]?.id ?? rowIndex;
              const sid = sessionId ?? '';
              window.location.assign(`/session/${sid}/student/${studentId}`);
            }}
          >
            {columns.map((column, columnIndex) => (
              <td key={columnIndex}>
                {column.rows[rowIndex]}
              </td>
            ))}
            <td>
              <StudentGroupActions
                groupId={students?.[rowIndex]?.group?.id}
                studentId={students?.[rowIndex]?.id}
              />
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
});

export default StudentTable;