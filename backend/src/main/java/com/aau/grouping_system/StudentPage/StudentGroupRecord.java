package com.aau.grouping_system.StudentPage;

import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;

import jakarta.validation.constraints.NotNull;

public record StudentGroupRecord(
		@NoDangerousCharacters @NoWhitespace String id,
		@NotNull Boolean hasGroup,
		@NoDangerousCharacters String project,
		Integer groupSize,
		Integer maxSize) {
}