package com.aau.grouping_system.Session;

import com.aau.grouping_system.InputValidation.IsEmailAndNamePairList;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.ValidISODate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SessionSetupRecord(
		@NoDangerousCharacters @NotNull String name,
		@NotNull @Min(0) @Max(100000) Integer minGroupSize,
		@NotNull @Min(0) @Max(100000) Integer maxGroupSize,
		Boolean allowStudentProjectProposals,
		@NoDangerousCharacters @NotNull @ValidISODate String questionnaireDeadlineISODateString,
		@NoDangerousCharacters @NotNull @IsEmailAndNamePairList String supervisorEmailAndNamePairs,
		@NoDangerousCharacters @NotNull @IsEmailAndNamePairList String studentEmailAndNamePairs) {
}
