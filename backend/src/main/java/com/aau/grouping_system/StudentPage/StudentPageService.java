package com.aau.grouping_system.StudentPage;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Group.GroupService;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Student.StudentQuestionnaire;
import com.aau.grouping_system.User.Student.StudentQuestionnaireRecord;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.Utils.RequestRequirementService;

@Service
public class StudentPageService {

    private final Database db;
    private final RequestRequirementService requestRequirementService;
    private final EmailService emailService;
    private final UserService userService;
    private final GroupService groupService;

    private static final String NOT_SPECIFIED = "Not specified";
    private static final String NO_PROJECT_ASSIGNED = "No project assigned";
    private static final String NOT_IN_GROUP = "Not in a group";
    private static final String QUESTIONNAIRE_NOT_FILLED = "Student has not filled out questionnaire yet.";

    public StudentPageService(Database db,
            RequestRequirementService requestRequirementService,
            EmailService emailService,
            UserService userService,
            GroupService groupService) {
        this.db = db;
        this.requestRequirementService = requestRequirementService;
        this.emailService = emailService;
        this.userService = userService;
        this.groupService = groupService;
    }

    public static class StudentSessionData {
        public final Student student;
        public final Session session;

        public StudentSessionData(Student student, Session session) {
            this.student = student;
            this.session = session;
        }
    }

    public StudentSessionData validateStudentAndSession(String sessionId, String studentId) {
        
        Student student = requestRequirementService.requireStudentExists(studentId);
        Session session = requestRequirementService.requireSessionExists(sessionId);
        
        if (!student.getSessionId().equals(sessionId)) {
            throw new RequestException(HttpStatus.BAD_REQUEST, 
                "Student does not belong to the specified session");
        }

        return new StudentSessionData(student, session);
    }

    // Student details for display
    public StudentDetailsRecord getStudentDetails(StudentSessionData validation, boolean includeEmail) {
        StudentQuestionnaireRecord questionnaireRecord = buildQuestionnaireRecord(validation.student);
        StudentGroupRecord groupRecord = buildGroupRecord(validation.student);

        return new StudentDetailsRecord(
                validation.student.getId(),
                validation.student.getName(),
                includeEmail ? validation.student.getEmail() : null,
                questionnaireRecord,
                groupRecord);
    }

    // Questionnaire record from student data
    public StudentQuestionnaireRecord buildQuestionnaireRecord(Student student) {
        if (student.getQuestionnaire() == null) {
            return new StudentQuestionnaireRecord(
                    NOT_SPECIFIED,
                    NOT_SPECIFIED,
                    NOT_SPECIFIED,
                    -1,
                    -1,
                    StudentQuestionnaire.WorkLocation.NoPreference,
                    StudentQuestionnaire.WorkStyle.NoPreference,
                    QUESTIONNAIRE_NOT_FILLED,
                    NOT_SPECIFIED,
                    NOT_SPECIFIED,
                    NOT_SPECIFIED);
        }

        StudentQuestionnaire questionnaire = student.getQuestionnaire();
        
        // Return raw data
        return new StudentQuestionnaireRecord(
                questionnaire.getDesiredProjectId1(),
                questionnaire.getDesiredProjectId2(),
                questionnaire.getDesiredProjectId3(),
                questionnaire.getDesiredGroupSizeMin(),
                questionnaire.getDesiredGroupSizeMax(),
                questionnaire.getDesiredWorkLocation(),
                questionnaire.getDesiredWorkStyle(),
                getValueOrDefault(questionnaire.getPersonalSkills(), NOT_SPECIFIED),
                getValueOrDefault(questionnaire.getSpecialNeeds(), NOT_SPECIFIED),
                getValueOrDefault(questionnaire.getAcademicInterests(), NOT_SPECIFIED),
                getValueOrDefault(questionnaire.getComments(), NOT_SPECIFIED));
    }

    // Group record from student data
    public StudentGroupRecord buildGroupRecord(Student student) {
        // Check if student has groupId
        String groupId = student.getGroupId();
        if (groupId != null) {
            Group group = db.getGroups().getItem(groupId);
            if (group != null) {
                String projectName = NO_PROJECT_ASSIGNED;
                if (group.getProjectId() != null) {
                    Project project = db.getProjects().getItem(group.getProjectId());
                    if (project != null) {
                        projectName = project.getName();
                    }
                }

                return new StudentGroupRecord(
                        group.getId(),
                        true,
                        projectName,
                        group.getStudentIds().size());
            }
        }

        return new StudentGroupRecord(
                null,
                false,
                NOT_IN_GROUP,
                0);
    }

    // Remove student
    public void removeStudent(StudentSessionData validation) {
        try {
            // Remove student from group
            String groupId = validation.student.getGroupId();
            if (groupId != null) {
                Group group = db.getGroups().getItem(groupId);
                if (group != null) {
                    groupService.leaveGroup(group, validation.student);
                }
            }

            // Remove student from database
            db.getStudents().cascadeRemove(db, validation.student);
        } catch (Exception e) {
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to remove student: " + e.getMessage());
        }
    }

    // Reset student password and send via email
    public String resetStudentPassword(StudentSessionData validation) {
        // Generate new password
        String newPassword = UUID.randomUUID().toString();
        userService.modifyPassword(newPassword, validation.student);

        // Send new password via email
        try {
            String subject = "AAU Grouping System - New Password";
            String body = """
                    Hello,

                    Your password for the AAU Grouping System has been reset for session: %s

                    Your login credentials are:
                    ID: %s
                    Password: %s

                    Please use your ID and password to access the AAU Grouping System.

                    Best regards,
                    AAU Grouping System""".formatted(
                            validation.session.getName(), 
                            validation.student.getId(), 
                            newPassword);

            emailService.builder()
                    .to(validation.student.getEmail())
                    .subject(subject)
                    .text(body)
                    .send();

            return "New password sent successfully to " + validation.student.getEmail();
        } catch (Exception e) {
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to reset password: " + e.getMessage());
        }
    }

    private String getValueOrDefault(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }
}