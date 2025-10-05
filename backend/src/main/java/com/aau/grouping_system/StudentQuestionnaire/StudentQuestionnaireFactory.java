package com.aau.grouping_system.StudentQuestionnaire;

import java.util.List;

public class StudentQuestionnaireFactory {

    public static StudentQuestionnaire create(
            String studentName,
            int previousSessionTeammates,
            int projectPriorities,
            int desiredGroupMembers,
            int desiredGroupSize,
            String workingEnvironment,
            String specialNeeds,
            List<String> personalSkills,
            List<String> academicInterests,
            String otherComments
    ) {
        StudentQuestionnaire q = new StudentQuestionnaire(studentName);
        q.setPreviousSessionTeammates(previousSessionTeammates);
        q.setProjectPriorities(projectPriorities);
        q.setDesiredGroupMembers(desiredGroupMembers);
        q.setDesiredGroupSize(desiredGroupSize);
        q.setWorkingEnvironment(workingEnvironment);
        q.setSpecialNeeds(specialNeeds);
        q.setPersonalSkills(personalSkills);
        q.setAcademicInterests(academicInterests);
        q.setOtherComments(otherComments);
        return q;
    }
}
