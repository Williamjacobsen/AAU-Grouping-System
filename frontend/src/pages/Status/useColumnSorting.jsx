import React, { useEffect, useMemo, useState } from "react";

export default function useColumnSorting(unsortedColumns) {

	const [labelToSortBy, setLabelToSortBy] = useState(null);
	const [isInAscendingOrder, setIsInAscendingOrder] = useState(true);
	const [alsoSortByGroupName, setAlsoSortByGroupName] = useState(true);

	// Set default column to sort by
	useEffect(() => {
		if (unsortedColumns && !labelToSortBy) {
			setLabelToSortBy("Name");
		}
	}, [unsortedColumns]);

	function sortByLabel(columns, labelToSortBy, inIsAscendingOrder) {

		const columnsClone = structuredClone(columns);
		const columnToSortBy = columnsClone.find(column => column.label === labelToSortBy);

		// Get the sort order
		const rowIndeces = columnToSortBy.rows.map((_, index) => index);
		rowIndeces.sort((a, b) => {
			let valueA = columnToSortBy.rows[a]?.toString().toLowerCase() || "";
			let valueB = columnToSortBy.rows[b]?.toString().toLowerCase() || "";

			if (!inIsAscendingOrder) {
				// Switch value A and B around
				const previousValueA = valueA;
				valueA = valueB;
				valueB = previousValueA;
			}

			return valueA.localeCompare(valueB,
				"en", // Using the English alphabet
				{
					numeric: true // Ensure that e.g. "10, 1, 2," is sorted as "1, 2, 10", not "1, 10, 2".
				}
			);
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

		// First, sort by name, then by user selected label, then by group name.
		const columnsSortedByName = sortByLabel(unsortedColumns, "Name", isInAscendingOrder);
		const columnsSortedBySelected = sortByLabel(columnsSortedByName, labelToSortBy, isInAscendingOrder);
		if (alsoSortByGroupName) {
			const columnsSortedByGroupName = sortByLabel(columnsSortedBySelected, "Group: Name", isInAscendingOrder);
			return columnsSortedByGroupName;
		}
		else {
			return columnsSortedBySelected;
		}

	}, [unsortedColumns, labelToSortBy, isInAscendingOrder, alsoSortByGroupName]);

	function sortColumns(labelToSortBy, inAscendingOrder) {
		setIsInAscendingOrder(inAscendingOrder);
		setLabelToSortBy(labelToSortBy);
	}

	return { sortedColumns, sortColumns, alsoSortByGroupName, setAlsoSortByGroupName };

}