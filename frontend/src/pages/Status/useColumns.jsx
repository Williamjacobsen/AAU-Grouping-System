import React, { useMemo } from "react";

export default function useColumns(students, projects, groups) {

	const originalColumns = useMemo(() => {

		if (!students || !projects || !groups) {
			return null;
		}

		function createColumn(label, getFunction, isHidden = false) {
			const rows = students.map(student => getFunction(student));
			return {
				label: label,
				isHidden: isHidden,
				rows: rows
			};
		}

		const result = [
			createColumn(
				"ID",
				function (student) {
					return student.id;
				},
				true
			),
			createColumn(
				"Name",
				function (student) {
					return student.name;
				}
			),
			createColumn(
				"Email",
				function (student) {
					return student.email;
				}
			),
			createColumn(
				"Group name",
				function (student) {
					return groups.find(
						group => group.id === student.groupId)?.name
						?? "";
				}
			),
			createColumn(
				"Group project",
				function (student) {
					return projects.find(
						project => project.id === groups.find(
							group => group.id === student.groupId)?.projectId)?.name
						?? "";
				}
			),
			createColumn(
				"1st project priority",
				function (student) {
					return projects.find(
						project => project.id === student.questionnaire.desiredProjectId1)?.name
						?? "";
				}
			),
			createColumn(
				"2st project priority",
				function (student) {
					return projects.find(
						project => project.id === student.questionnaire.desiredProjectId2)?.name
						?? "";
				}
			),
			createColumn(
				"3st project priority",
				function (student) {
					return projects.find(
						project => project.id === student.questionnaire.desiredProjectId3)?.name
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

		return result;
	}, [students, projects, groups]);

	return { originalColumns };
}