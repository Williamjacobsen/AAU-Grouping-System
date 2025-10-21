package com.aau.grouping_system.StudentQuestionnaire;

import java.util.concurrent.CopyOnWriteArrayList;

public class StudentQuestionnaireFactory {

	public static StudentQuestionnaire create(
			String studentName,
			int previousSessionTeammates,
			int projectPriorities,
			int desiredGroupMembers,
			int desiredGroupSize,
			String workingEnvironment,
			String specialNeeds,
			CopyOnWriteArrayList<String> personalSkills,
			CopyOnWriteArrayList<String> academicInterests,
			String otherComments) {
		StudentQuestionnaire questionnaire = new StudentQuestionnaire(studentName);
		questionnaire.setPreviousSessionTeammates(previousSessionTeammates);
		questionnaire.setProjectPriorities(projectPriorities);
		questionnaire.setDesiredGroupMembers(desiredGroupMembers);
		questionnaire.setDesiredGroupSize(desiredGroupSize);
		questionnaire.setWorkingEnvironment(workingEnvironment);
		questionnaire.setSpecialNeeds(specialNeeds);
		questionnaire.setPersonalSkills(personalSkills);
		questionnaire.setAcademicInterests(academicInterests);
		questionnaire.setOtherComments(otherComments);
		return questionnaire;
	}
}
