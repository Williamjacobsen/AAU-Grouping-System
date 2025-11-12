package com.aau.grouping_system.Session;

import com.aau.grouping_system.InputValidation.IsListOfEmails;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.ValidISODate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SessionSetupRecord(
		@NoDangerousCharacters @NotNull String name,
		@NotNull @Min(-1) @Max(100000) Integer minGroupSize,
		@NotNull @Min(-1) @Max(100000) Integer maxGroupSize,
		@NoDangerousCharacters @NotNull @ValidISODate String questionnaireDeadlineISODateString,
		@NoDangerousCharacters @NotNull @IsListOfEmails String supervisorEmails,
		@NoDangerousCharacters @NotNull @IsListOfEmails String studentEmails) {
}
