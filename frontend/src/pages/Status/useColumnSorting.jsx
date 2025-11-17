import React, { useEffect, useMemo, useState } from "react";

export default function useColumnSorting(unsortedColumns) {

	const [labelToSortBy, setLabelToSortBy] = useState(null);
	const [inAscendingOrder, setInAscendingOrder] = useState(true);

	// Set default column to sort by
	useEffect(() => {
		if (unsortedColumns && !labelToSortBy) {
			setLabelToSortBy("Name");
		}
	}, [unsortedColumns]);

	function sortByLabel(columns, labelToSortBy, inAscendingOrder) {

		const columnsClone = structuredClone(columns);
		const columnToSortBy = columnsClone.find(column => column.label === labelToSortBy);

		// Get the sort order
		const rowIndeces = columnToSortBy.rows.map((_, index) => index);
		rowIndeces.sort((a, b) => {
			let valueA = columnToSortBy.rows[a]?.toString().toLowerCase() || "";
			let valueB = columnToSortBy.rows[b]?.toString().toLowerCase() || "";
			if (inAscendingOrder) {
				return valueA.localeCompare(valueB);
			} else {
				return valueB.localeCompare(valueA);
			}
		});

		// Apply the same sort order to all columns
		const result = columnsClone.map(column => {
			const newColumn = {
				...column,
				rows: rowIndeces.map(sortedIndex => column.rows[sortedIndex])
			};

			return newColumn;
		});

		return result;
	}

	const sortedColumns = useMemo(() => {

		if (!unsortedColumns || !labelToSortBy) {
			return null;
		}

		// Always sort by name first
		const columnsSortedByName = sortByLabel(unsortedColumns, "Name", inAscendingOrder);
		const columnsSortedBySelected = sortByLabel(columnsSortedByName, labelToSortBy, inAscendingOrder);
		return columnsSortedBySelected;

	}, [unsortedColumns, labelToSortBy, inAscendingOrder]);

	function sortColumns(labelToSortBy, inAscendingOrder) {
		setInAscendingOrder(inAscendingOrder);
		setLabelToSortBy(labelToSortBy);
	}

	return { sortedColumns, sortColumns };

}