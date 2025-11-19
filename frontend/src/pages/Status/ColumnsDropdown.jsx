import React, { useState } from "react";

export default function ColumnsDropdown({ columns, enabledLabels, toggleLabel, sortColumns }) {

	const [dropdownIsOpen, setDropdownIsOpen] = useState(false);

	if (!columns || !enabledLabels || !toggleLabel || !sortColumns) {
		return <></>;
	}

	return (
		<div className="column-selector">
			<button
				onClick={() => setDropdownIsOpen(!dropdownIsOpen)}
				className="column-selector-button"
			>
				Select columns ▼
			</button>

			{dropdownIsOpen && (
				<div className="column-selector-dropdown">
					{columns?.map(column => (
						<>
							{!column.isHidden &&
								<div key={column.label} className="column-selector-item">
									<input
										type="checkbox"
										checked={enabledLabels.includes(column.label)}
										onChange={() => toggleLabel(column.label)}
									/>
									<button
										disabled={!enabledLabels.includes(column.label)}
										onClick={() => sortColumns(column.label, true)}
									>
										↑
									</button>
									<button
										disabled={!enabledLabels.includes(column.label)}
										onClick={() => sortColumns(column.label, false)}
									>
										↓
									</button>
									<span>{column.label}</span>
								</div>
							}
						</>
					))}
				</div>
			)}
		</div>
	);
}