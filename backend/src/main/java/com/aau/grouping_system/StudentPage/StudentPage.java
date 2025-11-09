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
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Student.StudentQuestionnaire;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated // enables method-level validation
@RequestMapping("/session/{sessionId}/student")
public class StudentPage {

	private final Database db;
	private final PasswordEncoder passwordEncoder;
	private final RequirementService requirementService;
	private final EmailService emailService;

	public StudentPage(Database db, PasswordEncoder passwordEncoder, RequirementService requirementService, EmailService emailService) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
		this.requirementService = requirementService;
		this.emailService = emailService;
	}

	@GetMapping("/{studentId}")
	public ResponseEntity<StudentDetailsDTO> getStudentDetails(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {
		try {
			Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
			if (coordinator == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			Student student = db.getStudents().getItem(studentId);
			if (student == null) {
				return ResponseEntity.notFound().build();
			}

			Session session = db.getSessions().getItem(student.getSessionId());
			if (session == null || !session.getId().equals(sessionId)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}

			Coordinator sessionCoordinator = db.getCoordinators().getItem(session.getCoordinatorId());
			if (sessionCoordinator == null || !sessionCoordinator.equals(coordinator)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			StudentQuestionnaireDTO questionnaireDTO = buildQuestionnaireDTO(student, true);
			StudentGroupDTO groupDTO = buildGroupDTO(student);

			StudentDetailsDTO studentData = new StudentDetailsDTO(
					student.getId(),
					student.getName(),
					student.getEmail(),
					questionnaireDTO,
					groupDTO);

			return ResponseEntity.ok(studentData);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}

	@GetMapping("/{studentId}/public")
	public ResponseEntity<StudentDetailsDTO> getStudentPublicDetails(
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		try {
			Student student = db.getStudents().getItem(studentId);
			if (student == null) {
				return ResponseEntity.notFound().build();
			}

			Session session = db.getSessions().getItem(student.getSessionId());
			if (session == null || !session.getId().equals(sessionId)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}

			StudentQuestionnaireDTO questionnaireDTO = buildQuestionnaireDTO(student, false);
			StudentGroupDTO groupDTO = buildGroupDTO(student);

			StudentDetailsDTO studentData = new StudentDetailsDTO(
					student.getId(),
					student.getName(),
					null,
					questionnaireDTO,
					groupDTO);

			return ResponseEntity.ok(studentData);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}

	@SuppressWarnings("unused")
	private StudentQuestionnaireDTO buildQuestionnaireDTO(Student student, boolean includePrivate) {
		if (student == null || student.getQuestionnaire() == null) {
			return new StudentQuestionnaireDTO(
					"Not specified",
					"Not specified",
					"Not specified",
					"Not specified",
					"Not specified",
					"Not specified",
					"Not specified",
					"Student has not filled out questionnaire yet.",
					null,
					null);
		}

		StudentQuestionnaire questionnaire = student.getQuestionnaire();

		// Get project names from IDs
		String project1Name = questionnaire.getDesiredProjectId1();
		String project2Name = questionnaire.getDesiredProjectId2();

		// Make group size preference
		String groupSizePreference = "Not specified";
		if (questionnaire.getDesiredGroupSizeMin() != null && questionnaire.getDesiredGroupSizeMax() != null &&
				(questionnaire.getDesiredGroupSizeMin() != -1 || questionnaire.getDesiredGroupSizeMax() != -1)) {
			if (questionnaire.getDesiredGroupSizeMin().equals(questionnaire.getDesiredGroupSizeMax())
					&& questionnaire.getDesiredGroupSizeMin() != -1) {
				groupSizePreference = String.valueOf(questionnaire.getDesiredGroupSizeMin());
			} else {
				String min = questionnaire.getDesiredGroupSizeMin() == -1 ? "No min"
						: String.valueOf(questionnaire.getDesiredGroupSizeMin());
				String max = questionnaire.getDesiredGroupSizeMax() == -1 ? "No max"
						: String.valueOf(questionnaire.getDesiredGroupSizeMax());
				groupSizePreference = min + " - " + max;
			}
		}

		// Create working environment string combining location and style
		String workingEnvironment = "Not specified";
		if (questionnaire.getDesiredWorkLocation() != null && questionnaire.getDesiredWorkStyle() != null) {
			String location = questionnaire.getDesiredWorkLocation().name();
			String style = questionnaire.getDesiredWorkStyle().name();
			if (!"NoPreference".equals(location) || !"NoPreference".equals(style)) {
				workingEnvironment = location + " / " + style;
			}
		}

		return new StudentQuestionnaireDTO(
				project1Name,
				project2Name,
				"Not available",
				"Not available",
				groupSizePreference,
				workingEnvironment,
				questionnaire.getSpecialNeeds() == null || questionnaire.getSpecialNeeds().isEmpty() ? "Not specified"
						: questionnaire.getSpecialNeeds(),
				questionnaire.getComments() == null || questionnaire.getComments().isEmpty() ? "Not specified"
						: questionnaire.getComments(),
				questionnaire.getPersonalSkills() == null || questionnaire.getPersonalSkills().isEmpty() ? null
						: java.util.Arrays.asList(questionnaire.getPersonalSkills().split(",")),
				questionnaire.getAcademicInterests() == null || questionnaire.getAcademicInterests().isEmpty() ? null
						: java.util.Arrays.asList(questionnaire.getAcademicInterests().split(",")));
	}

	private StudentGroupDTO buildGroupDTO(Student student) {
		for (Group group : db.getGroups().getAllItems().values()) {
			if (group.getStudentIds().contains(student.getId())) {
				String projectName = "No project assigned";
				if (group.getProjectId() != null) {
					Project project = db.getProjects().getItem(group.getProjectId());
					if (project != null) {
						projectName = project.getName();
					}
				}

				return new StudentGroupDTO(
						group.getId(),
						true,
						projectName,
						group.getStudentIds().size(),
						group.getMaxStudents());
			}
		}

		return new StudentGroupDTO(
				null,
				false,
				"Not in a group",
				0,
				0);
	}

	private String getProjectNameById(String projectId) {
		Project project = db.getProjects().getItem(projectId);
		if (project != null) {
			return project.getName();
		}
		return "Project not found (ID: " + projectId + ")";
	}

	@DeleteMapping("/{studentId}")
	public ResponseEntity<String> removeStudent(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		// Validate student belongs to session
		Session session = db.getSessions().getItem(student.getSessionId());
		if (session == null || !session.getId().equals(sessionId)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		// Validate coordinator session
		Coordinator sessionCoordinator = db.getCoordinators().getItem(session.getCoordinatorId());
		if (sessionCoordinator == null || !sessionCoordinator.equals(coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		try {
			// Remove student from group
			for (Group group : db.getGroups().getAllItems().values()) {
				if (group.getStudentIds().contains(student.getId())) {
					group.getStudentIds().remove(student.getId());
					break;
				}
			}

			// Remove student from database
			db.getStudents().cascadeRemove(db, student);

			return ResponseEntity.ok("Student removed successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to remove student: " + e.getMessage());
		}
	}

	@PostMapping("/{studentId}/reset-password")
	public ResponseEntity<String> resetStudentPassword(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		// Validate student belongs to session
		Session session = db.getSessions().getItem(student.getSessionId());
		if (session == null || !session.getId().equals(sessionId)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		// Validate coordinator session
		Coordinator sessionCoordinator = db.getCoordinators().getItem(session.getCoordinatorId());
		if (sessionCoordinator == null || !sessionCoordinator.equals(coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		try {
			// Generate new UUID password
			String newPassword = UUID.randomUUID().toString();
			String passwordHash = passwordEncoder.encode(newPassword);
			student.setPasswordHash(passwordHash);

			// Send password via email
			String subject = "AAU Grouping System - New Password";
			String body = """
					Hello,

					Your password for the AAU Grouping System has been reset for session: %s

					Your login credentials are:
					ID: %s
					Password: %s

					Please use your ID and password to access the AAU Grouping System.

					Best regards,
					AAU Grouping System""".formatted(session.getName(), student.getId(), newPassword);
			emailService.builder()
					.to(student.getEmail())
					.subject(subject)
					.text(body)
					.send();
			return ResponseEntity.ok("New password sent successfully to " + student.getEmail());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to reset password: " + e.getMessage());
		}
	}
}
