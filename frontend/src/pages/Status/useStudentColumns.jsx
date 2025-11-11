import React, { useCallback, useMemo, useState } from "react";

// Hook that provides a set of selectable student columns and a small UI to toggle them
export default function useStudentColumns(visibleStudents, projects, groups) {

	const allColumns = useMemo(() => {

		if (!visibleStudents || !projects || !groups) {
			return null;
		}

		function createColumn(label, values) {
			return {
				label: label,
				rows: values
			};
		}

		return [
			createColumn("Name", visibleStudents.map(student => student.name)),
			createColumn("Group number", visibleStudents.map(student => {
				for (let i = 0; i < groups.length; i++) {
					if (groups[i].id === student.groupId) {
						return i + 1;
					}
				}
				return "";
			})),
			createColumn("Group project", visibleStudents.map(student => {
				return projects.find(
					project => project.id === groups.find(
						group => group.id === student.groupId))?.projectId;
			})),
			createColumn("1st project priority", visibleStudents.map(student => {
				return projects.find(
					project => project.id === student.questionnaire.desiredProjectId1)
					?? "";
			})),
			createColumn("2st project priority", visibleStudents.map(student => {
				return projects.find(
					project => project.id === student.questionnaire.desiredProjectId2)
					?? "";
			})),
			createColumn("3st project priority", visibleStudents.map(student => {
				return projects.find(
					project => project.id === student.questionnaire.desiredProjectId3)
					?? "";
			})),
			createColumn("Preferred group size", visibleStudents.map(student => {
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
			})),
			createColumn("Preferred work location", visibleStudents.map(student => student.questionnaire.desiredWorkLocation)),
			createColumn("Preferred work style", visibleStudents.map(student => student.questionnaire.desiredWorkStyle)),
			createColumn("Personal skills", visibleStudents.map(student => student.questionnaire.personalSkills)),
			createColumn("Special needs", visibleStudents.map(student => student.questionnaire.specialNeeds)),
			createColumn("Academic interests", visibleStudents.map(student => student.questionnaire.academicInterests)),
			createColumn("Other comments", visibleStudents.map(student => student.questionnaire.comments))
		];
	}, [visibleStudents, projects, groups]);

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

	function toggleLabel(label) {
		setEnabledLabels(previousValue => {
			if (previousValue.includes(label)) {
				return previousValue.filter(item => item !== label);
			}
			else {
				return [...previousValue, label];
			}
		});
	}

	const [dropdownIsOpen, setDropdownIsOpen] = useState(false);

	const ColumnSelector = useCallback(() => {

		return (
			<div style={{ position: 'relative', display: 'inline-block' }}>
				<button
					onClick={() => setDropdownIsOpen(!dropdownIsOpen)}
					style={{ padding: '8px 12px', border: '1px solid #ccc' }}
				>
					Select columns ▼
				</button>

				{dropdownIsOpen && (
					<div style={{
						position: 'absolute',
						top: '100%',
						left: 0,
						background: 'white',
						border: '1px solid #ccc',
						padding: '8px',
						zIndex: 1000,
						minWidth: '200px'
					}}>
						{allColumns.map(column => (
							column.label !== "Name" && (
								<label key={column.label} style={{ display: 'block', margin: '4px 0' }}>
									<input
										type="checkbox"
										checked={enabledLabels.includes(column.label)}
										onChange={() => toggleLabel(column.label)}
									/>
									<span style={{ marginLeft: '8px' }}>{column.label}</span>
								</label>
							)
						))}
					</div>
				)}
			</div>
		);
	}, [enabledLabels, allColumns, toggleLabel]);

	return { visibleColumns, ColumnSelector };
}
