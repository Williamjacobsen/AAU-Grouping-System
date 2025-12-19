import React, { useCallback, useMemo, useState } from "react";

export default function useColumnSelection(columns) {

	const [enabledLabels, setEnabledLabels] = useState([
		"Name",
		"Group: Name",
		"Group: 1st project priority",
		"1st project priority",
		"Special needs",
		"Other comments"
	]);

	const enabledColumns = useMemo(() => {

		if (!columns) return null;

		const columnsClone = structuredClone(columns);

		const result = columnsClone.filter(column => column.isHidden || enabledLabels.includes(column.label));

		return result;
	}, [columns, enabledLabels]);

	const toggleLabel = useCallback((label) => {
		setEnabledLabels(previousValues => {
			if (previousValues.includes(label)) {
				return previousValues.filter(item => item !== label);
			}
			else {
				return [...previousValues, label];
			}
		});
	}, []);

	return { enabledColumns, enabledLabels, toggleLabel };

}