import React, { useState } from "react";

export default function ColumnsDropdown({ columns, enabledLabels, toggleLabel, sortColumns, alsoSortByGroupName, setAlsoSortByGroupName }) {

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
				Filter columns ▼
			</button>

			{dropdownIsOpen && (
				<div className="column-selector-dropdown">
					<h4>
						Columns are always sorted first by student name, then by your selected sorting method, then optionally also by group name.
					</h4>
					<label>
						Also lastly sort by group name?
						<input
							type="checkbox"
							checked={alsoSortByGroupName}
							onChange={() => setAlsoSortByGroupName(!alsoSortByGroupName)}
						/>
					</label>
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