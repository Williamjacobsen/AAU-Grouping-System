package com.aau.grouping_system.StudentPage;

import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record StudentDetailsRecord(
		@NoDangerousCharacters @NoWhitespace @NotNull String id,
		@NoDangerousCharacters @NotNull String name,
		@Email String email,
		@Valid StudentQuestionnaireRecord questionnaire,
		@Valid @NotNull StudentGroupRecord group) {
}