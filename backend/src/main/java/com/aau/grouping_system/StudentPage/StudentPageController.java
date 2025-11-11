package com.aau.grouping_system.StudentPage;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Student.StudentQuestionnaire;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated
@RequestMapping("/session/{sessionId}/student")
public class StudentPageController {

	private final Database db;
	private final PasswordEncoder passwordEncoder;
	private final RequirementService requirementService;
	private final EmailService emailService;

	public StudentPageController(Database db, PasswordEncoder passwordEncoder, RequirementService requirementService,
			EmailService emailService) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
		this.requirementService = requirementService;
		this.emailService = emailService;
	}

	private static final String NOT_SPECIFIED = "Not specified";
	private static final String NOT_AVAILABLE = "Not available";
	private static final String NO_PROJECT_ASSIGNED = "No project assigned";
	private static final String NOT_IN_GROUP = "Not in a group";
	private static final String QUESTIONNAIRE_NOT_FILLED = "Student has not filled out questionnaire yet.";
	private static final String NO_MIN = "No min";
	private static final String NO_MAX = "No max";
	private static final String NO_PREFERENCE = "NoPreference";

	private void sendPasswordResetEmail(String email, String sessionName, String studentId, String password) throws Exception {
		String subject = "AAU Grouping System - New Password";
		String body = """
			Hello,

			Your password for the AAU Grouping System has been reset for session: %s

			Your login credentials are:
			ID: %s
			Password: %s

			Please use your ID and password to access the AAU Grouping System.

			Best regards,
			AAU Grouping System""".formatted(sessionName, studentId, password);

		emailService.builder()
				.to(email)
				.subject(subject)
				.text(body)
				.send();
	}


	private StudentSessionData validateCoordinatorAndStudent(HttpServletRequest servlet, String sessionId, String studentId) {
		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		if (coordinator == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized");
		}

		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Student not found");
		}

		Session session = db.getSessions().getItem(student.getSessionId());
		if (session == null || !session.getId().equals(sessionId)) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Student does not belong to the specified session");
		}

		Coordinator sessionCoordinator = db.getCoordinators().getItem(session.getCoordinatorId());
		if (sessionCoordinator == null || !sessionCoordinator.equals(coordinator)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Coordinator is not authorized for this session");
		}

		return new StudentSessionData(student, session);
	}

	private StudentSessionData validateStudentAndSession(String sessionId, String studentId) {
		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Student not found");
		}

		Session session = db.getSessions().getItem(student.getSessionId());
		if (session == null || !session.getId().equals(sessionId)) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Student does not belong to the specified session");
		}

		return new StudentSessionData(student, session);
	}

	private String getValueOrDefault(String value, String defaultValue) {
		return (value == null || value.isEmpty()) ? defaultValue : value;
	}

	private Group findGroupByStudentId(String studentId) {
		for (Group group : db.getGroups().getAllItems().values()) {
			if (group.getStudentIds().contains(studentId)) {
				return group;
			}
		}
		return null;
	}

	private boolean removeStudentFromGroup(String studentId) {
		Group group = findGroupByStudentId(studentId);
		if (group != null) {
			group.getStudentIds().remove(studentId);
			return true;
		}
		return false;
	}

	private String formatGroupSizePreference(Integer min, Integer max) {
		if (min == null || max == null || (min == -1 && max == -1)) {
			return NOT_SPECIFIED;
		}

		if (min.equals(max) && min != -1) {
			return String.valueOf(min);
		}

		String minStr = min == -1 ? NO_MIN : String.valueOf(min);
		String maxStr = max == -1 ? NO_MAX : String.valueOf(max);
		return minStr + " - " + maxStr;
	}

	private String formatWorkingEnvironment(StudentQuestionnaire.WorkLocation location, 
			StudentQuestionnaire.WorkStyle style) {
		if (location == null || style == null) {
			return NOT_SPECIFIED;
		}

		String locationStr = location.name();
		String styleStr = style.name();
		
		if (NO_PREFERENCE.equals(locationStr) && NO_PREFERENCE.equals(styleStr)) {
			return NOT_SPECIFIED;
		}

		return locationStr + " / " + styleStr;
	}

	private static class StudentSessionData {
		final Student student;
		final Session session;

		StudentSessionData(Student student, Session session) {
			this.student = student;
			this.session = session;
		}
	}

	@GetMapping("/{studentId}")
	public ResponseEntity<StudentDetailsRecord> getStudentDetails(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {
		
		StudentSessionData validation = validateCoordinatorAndStudent(servlet, sessionId, studentId);

		StudentQuestionnaireRecord questionnaireRecord = buildQuestionnaireRecord(validation.student);
		StudentGroupRecord groupRecord = buildGroupRecord(validation.student);

		StudentDetailsRecord studentData = new StudentDetailsRecord(
				validation.student.getId(),
				validation.student.getName(),
				validation.student.getEmail(),
				questionnaireRecord,
				groupRecord);

		return ResponseEntity.ok(studentData);
	}

	@GetMapping("/{studentId}/public")
	public ResponseEntity<StudentDetailsRecord> getStudentPublicDetails(
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		StudentSessionData validation = validateStudentAndSession(sessionId, studentId);

		StudentQuestionnaireRecord questionnaireRecord = buildQuestionnaireRecord(validation.student);
		StudentGroupRecord groupRecord = buildGroupRecord(validation.student);

		StudentDetailsRecord studentData = new StudentDetailsRecord(
				validation.student.getId(),
				validation.student.getName(),
				null,
				questionnaireRecord,
				groupRecord);

		return ResponseEntity.ok(studentData);
	}

	private StudentQuestionnaireRecord buildQuestionnaireRecord(Student student) {
		if (student == null || student.getQuestionnaire() == null) {
			return new StudentQuestionnaireRecord(
					NOT_SPECIFIED,
					NOT_SPECIFIED,
					NOT_SPECIFIED,
					NOT_SPECIFIED,
					NOT_SPECIFIED,
					NOT_SPECIFIED,
					NOT_SPECIFIED,
					QUESTIONNAIRE_NOT_FILLED,
					null,
					null);
		}

		StudentQuestionnaire questionnaire = student.getQuestionnaire();

		String project1Name = questionnaire.getDesiredProjectId1();
		String project2Name = questionnaire.getDesiredProjectId2();
		String project3Name = questionnaire.getDesiredProjectId3();

		String groupSizePreference = formatGroupSizePreference(
				questionnaire.getDesiredGroupSizeMin(), 
				questionnaire.getDesiredGroupSizeMax());
		String workingEnvironment = formatWorkingEnvironment(
				questionnaire.getDesiredWorkLocation(), 
				questionnaire.getDesiredWorkStyle());

		return new StudentQuestionnaireRecord(
				project1Name,
				project2Name,
				project3Name,
				NOT_AVAILABLE,
				groupSizePreference,
				workingEnvironment,
				getValueOrDefault(questionnaire.getSpecialNeeds(), NOT_SPECIFIED),
				getValueOrDefault(questionnaire.getComments(), NOT_SPECIFIED),
				questionnaire.getPersonalSkills() == null || questionnaire.getPersonalSkills().isEmpty() ? null
						: java.util.Arrays.asList(questionnaire.getPersonalSkills().split(",")),
				questionnaire.getAcademicInterests() == null || questionnaire.getAcademicInterests().isEmpty() ? null
						: java.util.Arrays.asList(questionnaire.getAcademicInterests().split(",")));
	}

	private StudentGroupRecord buildGroupRecord(Student student) {
		Group group = findGroupByStudentId(student.getId());
		
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
					group.getStudentIds().size(),
					group.getMaxStudents());
		}

		return new StudentGroupRecord(
				null,
				false,
				NOT_IN_GROUP,
				0,
				0);
	}

	@DeleteMapping("/{studentId}")
	public ResponseEntity<String> removeStudent(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		StudentSessionData validation = validateCoordinatorAndStudent(servlet, sessionId, studentId);

		try {
			// Remove student from group
			removeStudentFromGroup(validation.student.getId());

			// Remove student from database
			db.getStudents().cascadeRemove(db, validation.student);

			return ResponseEntity.ok("Student removed successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, 
				"Failed to remove student: " + e.getMessage());
		}
	}

	@PostMapping("/{studentId}/reset-password")
	public ResponseEntity<String> resetStudentPassword(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		StudentSessionData validation = validateCoordinatorAndStudent(servlet, sessionId, studentId);

		// Generate new UUID password
		String newPassword = UUID.randomUUID().toString();
		String passwordHash = passwordEncoder.encode(newPassword);
		validation.student.setPasswordHash(passwordHash);

		// Send new password via email
		try {
			sendPasswordResetEmail(validation.student.getEmail(), validation.session.getName(), 
					validation.student.getId(), newPassword);
			return ResponseEntity.ok("New password sent successfully to " + validation.student.getEmail());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to reset password: " + e.getMessage());
		}
	}
}
