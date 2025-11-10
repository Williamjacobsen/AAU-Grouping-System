package com.aau.grouping_system.StudentPage;

import java.util.List;

import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;

public record StudentQuestionnaireRecord(
		@NoDangerousCharacters @NoWhitespace String projectPriority1,
		@NoDangerousCharacters @NoWhitespace String projectPriority2,
		@NoDangerousCharacters @NoWhitespace String projectPriority3,
		Object desiredGroupMembers,
		Object desiredGroupSize,
		@NoDangerousCharacters String workingEnvironment,
		@NoDangerousCharacters String specialNeeds,
		@NoDangerousCharacters String otherComments,
		List<String> personalSkills,
		List<String> academicInterests) {
}