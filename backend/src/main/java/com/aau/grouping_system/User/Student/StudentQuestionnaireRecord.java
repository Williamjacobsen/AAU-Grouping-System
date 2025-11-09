package com.aau.grouping_system.User.Student;

import com.aau.grouping_system.InputValidation.NoDangerousCharacters;

import jakarta.validation.constraints.NotNull;

public record StudentQuestionnaireRecord(
		@NoDangerousCharacters @NotNull String desiredProjectId1,
		@NoDangerousCharacters @NotNull String desiredProjectId2,
		@NoDangerousCharacters @NotNull String desiredProjectId3,
		@NotNull Integer desiredGroupSizeMin,
		@NotNull Integer desiredGroupSizeMax,
		// "@NotNull" is enough validation for enums, because if the input isn't exactly
		// one of the valid enum types, it is considered as null.
		@NotNull StudentQuestionnaire.WorkLocation desiredWorkLocation,
		@NotNull StudentQuestionnaire.WorkStyle desiredWorkStyle,
		@NoDangerousCharacters @NotNull String personalSkills,
		@NoDangerousCharacters @NotNull String specialNeeds,
		@NoDangerousCharacters @NotNull String academicInterests,
		@NoDangerousCharacters @NotNull String comments) {

	public StudentQuestionnaire toQuestionnaire() {
		return new StudentQuestionnaire(
				desiredProjectId1,
				desiredProjectId2,
				desiredProjectId3,
				desiredGroupSizeMin,
				desiredGroupSizeMax,
				desiredWorkLocation,
				desiredWorkStyle,
				personalSkills,
				specialNeeds,
				academicInterests,
				comments);
	}
}
