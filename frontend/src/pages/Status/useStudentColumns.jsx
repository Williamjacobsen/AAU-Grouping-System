import React, { useCallback, useEffect, useMemo, useState } from "react";

export default function useStudentColumns(visibleStudents, projects, groups) {

	const [sortedStudents, setSortedStudents] = useState(visibleStudents);

	useEffect(() => {
		setSortedStudents(visibleStudents);
	}, [visibleStudents]);

	const allColumns = useMemo(() => {

		if (!sortedStudents || !projects || !groups) {
			return null;
		}

		// Add group numbers to groups
		const groupsWithNumbers = groups.map((group, index) => ({
			...group,
			number: index + 1
		}));

		function createColumn(label, getFunction) {

			const rows = sortedStudents.map(student => getFunction(student));

			function sortingFunction() {
				// Create a copy and sort it
				const newlySorted = [...visibleStudents].sort((a, b) => {
					const valueA = getFunction(a)?.toString() || "";
					const valueB = getFunction(b)?.toString() || "";
					return valueA.localeCompare(valueB);
				});
				setSortedStudents(newlySorted);
			}

			return {
				label: label,
				sortingFunction: sortingFunction,
				rows: rows
			};
		}

		return [
			createColumn(
				"Name",
				function (student) {
					return student.name;
				}
			),
			createColumn(
				"Group number",
				function (student) {
					return groupsWithNumbers.find(
						group => group.id === student.groupId)?.number
						?? "";
				}
			),
			createColumn(
				"Group project",
				function (student) {
					return projects.find(
						project => project.id === groupsWithNumbers.find(
							group => group.id === student.groupId))?.projectId
						?? "";
				}
			),
			createColumn(
				"1st project priority",
				function (student) {
					return projects.find(
						project => project.id === student.questionnaire.desiredProjectId1)
						?? "";
				}
			),
			createColumn(
				"2st project priority",
				function (student) {
					return projects.find(
						project => project.id === student.questionnaire.desiredProjectId2)
						?? "";
				}
			),
			createColumn(
				"3st project priority",
				function (student) {
					return projects.find(
						project => project.id === student.questionnaire.desiredProjectId3)
						?? "";
				}
			),
			createColumn(
				"Preferred group size",
				function (student) {
					let min = student.questionnaire.desiredGroupSizeMin;
					let max = student.questionnaire.desiredGroupSizeMax;
					if (min === -1 && max === -1) {
						return "No preference";
					}
					else if (min === -1) {
						return "Max: " + max;
					}
					else if (student.questionnaire.desiredGroupSizeMax === -1) {
						return "Min: " + min;
					}
					else {
						return min + " to " + max;
					}
				}
			),
			createColumn(
				"Preferred work location",
				function (student) {
					return student.questionnaire.desiredWorkLocation;
				}
			),
			createColumn(
				"Preferred work style",
				function (student) {
					return student.questionnaire.desiredWorkStyle;
				}
			),
			createColumn(
				"Personal skills",
				function (student) {
					return student.questionnaire.personalSkills;
				}
			),
			createColumn(
				"Special needs",
				function (student) {
					return student.questionnaire.specialNeeds;
				}
			),
			createColumn(
				"Academic interests",
				function (student) {
					return student.questionnaire.academicInterests;
				}
			),
			createColumn(
				"Other comments",
				function (student) {
					return student.questionnaire.comments;
				}
			)
		];
	}, [sortedStudents, visibleStudents, projects, groups]);

	const [enabledLabels, setEnabledLabels] = useState([
		"Name",
		"Group number",
		"1st project priority",
		"Special needs",
		"Other comments"
	]);

	const visibleColumns = useMemo(() => {
		return allColumns?.filter(column => enabledLabels.includes(column.label));
	}, [allColumns, enabledLabels]);

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

	const [dropdownIsOpen, setDropdownIsOpen] = useState(false);

	const ColumnSelector = useCallback(() => {

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
						{allColumns?.map(column => (
							<div key={column.label} className="column-selector-item">
								<input
									type="checkbox"
									checked={enabledLabels.includes(column.label)}
									onChange={() => toggleLabel(column.label)}
								/>
								<button
									disabled={!enabledLabels.includes(column.label)}
									onClick={column.sortingFunction}
								>
									Sort by
								</button>
								<span>{column.label}</span>
							</div>
						))}
					</div>
				)}
			</div>
		);
	}, [enabledLabels, allColumns, toggleLabel, dropdownIsOpen]);

	return { visibleColumns, ColumnSelector };
}
