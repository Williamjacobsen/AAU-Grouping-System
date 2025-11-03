package com.aau.grouping_system.StudentPage;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/session/{sessionId}/student")
public class StudentPage {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	public StudentPage(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	private Coordinator getCurrentCoordinator(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (Coordinator) session.getAttribute("user");
	}

	@GetMapping("/{studentId}")
	public ResponseEntity<StudentDetailsDTO> getStudentDetails(
			@PathVariable String sessionId,
			@PathVariable String studentId, 
			HttpServletRequest request) {
		try {
			Coordinator coordinator = getCurrentCoordinator(request);
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
				groupDTO
			);

			return ResponseEntity.ok(studentData);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}
	}

	@GetMapping("/{studentId}/public")
	public ResponseEntity<StudentDetailsDTO> getStudentPublicDetails(
			@PathVariable String sessionId,
			@PathVariable String studentId) {
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
				groupDTO
			);

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
				null
			);
		}

		Student.Questionnaire q = student.getQuestionnaire();
		
		// Get project names from IDs
		String project1Name = "Not specified";
		String project2Name = "Not specified";
		if (q.desiredProjectIds != null && !q.desiredProjectIds.isEmpty()) {
			project1Name = getProjectNameById(q.desiredProjectIds.get(0));
			if (q.desiredProjectIds.size() > 1) {
				project2Name = getProjectNameById(q.desiredProjectIds.get(1));
			}
		}
		
		// Make group size preference
		String groupSizePreference = "Not specified";
		if (q.desiredGroupSizeMin != null && q.desiredGroupSizeMax != null &&
			(q.desiredGroupSizeMin != -1 || q.desiredGroupSizeMax != -1)) {
			if (q.desiredGroupSizeMin.equals(q.desiredGroupSizeMax) && q.desiredGroupSizeMin != -1) {
				groupSizePreference = String.valueOf(q.desiredGroupSizeMin);
			} else {
				String min = q.desiredGroupSizeMin == -1 ? "No min" : String.valueOf(q.desiredGroupSizeMin);
				String max = q.desiredGroupSizeMax == -1 ? "No max" : String.valueOf(q.desiredGroupSizeMax);
				groupSizePreference = min + " - " + max;
			}
		}
		
		// Create working environment string combining location and style
		String workingEnvironment = "Not specified";
		if (q.desiredWorkLocation != null && q.desiredWorkStyle != null) {
			String location = q.desiredWorkLocation.name();
			String style = q.desiredWorkStyle.name();
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
			q.specialNeeds == null || q.specialNeeds.isEmpty() ? "Not specified" : q.specialNeeds,
			q.comments == null || q.comments.isEmpty() ? "Not specified" : q.comments,
			q.personalSkills == null || q.personalSkills.isEmpty() ? null : java.util.Arrays.asList(q.personalSkills.split(",")),
			q.academicInterests == null || q.academicInterests.isEmpty() ? null : java.util.Arrays.asList(q.academicInterests.split(","))
		);
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
					group.getMaxStudents()
				);
			}
		}
		
		return new StudentGroupDTO(
			null,
			false,
			"Not in a group",
			0,
			0
		);
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
			@PathVariable String sessionId,
			@PathVariable String studentId,
			HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
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
			@PathVariable String sessionId,
			@PathVariable String studentId,
			HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
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
			EmailService.sendEmail(student.getEmail(), subject, body);
			return ResponseEntity.ok("New password sent successfully to " + student.getEmail());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to reset password: " + e.getMessage());
		}
	}
}
