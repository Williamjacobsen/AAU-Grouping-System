import React, { useMemo, useState } from "react";


export default function useColumnSearching(columns) {

	const [searchString, setSearchString] = useState("");

	const searchedColumns = useMemo(() => {

		if (!columns) {
			return null;
		}

		const columnsClone = structuredClone(columns);

		if (!searchString || searchString === "") {
			return columnsClone;
		}

		// Work backwards to avoid index issues when removing rows
		for (let rowIndex = columnsClone[0].rows.length - 1; rowIndex >= 0; rowIndex--) {

			// Determine if row contains keyword
			let rowContainsKeyword = false;
			for (const column of columnsClone) {
				if (column.isHidden) {
					continue;
				}
				if (column.rows[rowIndex].toString().toLowerCase().includes(searchString)) {
					rowContainsKeyword = true;
					break;
				}
			}

			// Remove whole row if not containing the keyword
			if (!rowContainsKeyword) {
				for (let columnIndex = columnsClone.length - 1; columnIndex >= 0; columnIndex--) {
					columnsClone[columnIndex].rows.splice(rowIndex, 1);
				}
			}
		}

		return columnsClone;
	}, [columns, searchString]);

	return { searchedColumns, searchString, setSearchString };
}