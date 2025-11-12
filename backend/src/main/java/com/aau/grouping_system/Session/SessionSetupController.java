package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@Validated // enables method-level validation
@RequestMapping("/sessionSetup")
public class SessionSetupController {

	private final Database db;
	private final RequestRequirementService requestRequirementService;
	private final SessionSetupService sessionSetupService;
	private final UserService userService;
	private final EmailService emailService;

	public SessionSetupController(
			Database db,
			RequestRequirementService requestRequirementService,
			SessionSetupService sessionSetupService,
			UserService userService,
			EmailService emailService) {
		this.db = db;
		this.requestRequirementService = requestRequirementService;
		this.sessionSetupService = sessionSetupService;
		this.userService = userService;
		this.emailService = emailService;
	}

	@PostMapping("/{sessionId}/saveSetup")
	public ResponseEntity<String> saveSetup(
			HttpServletRequest httpRequest,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody SessionSetupRecord record) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(httpRequest);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		sessionSetupService.updateSessionSetup(session, record);

		return ResponseEntity.ok("Session setup saved successfully!");
	}

	record SendLoginCodeRecord(
			Boolean sendOnlyNew) {
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	@PostMapping("/{sessionId}/sendLoginCodeTo/students")
	public ResponseEntity<String> sendLoginCodeToStudents(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody SendLoginCodeRecord record) {

		Session session = requestRequirementService.requireSessionExists(sessionId);

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		CopyOnWriteArrayList<User> students = (CopyOnWriteArrayList<User>) session.getStudents().getItems(db);
		if (record.sendOnlyNew) {
			students.removeIf(student -> student.getLoginCode() != null);
		}

		userService.applyAndEmailLoginCodes(session, students);

		return ResponseEntity.ok("Emails have been sent to students.");
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	@PostMapping("/{sessionId}/sendLoginCodeTo/supervisors")
	public ResponseEntity<String> sendLoginCodeToSupervisors(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody SendLoginCodeRecord record) {

		Session session = requestRequirementService.requireSessionExists(sessionId);

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		CopyOnWriteArrayList<User> supervisors = (CopyOnWriteArrayList<User>) session.getSupervisors().getItems(db);
		if (record.sendOnlyNew) {
			supervisors.removeIf(supervisor -> supervisor.getLoginCode() != null);
		}

		userService.applyAndEmailLoginCodes(session, supervisors);

		return ResponseEntity.ok("Emails have been sent to supervisors.");
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/{sessionId}/notify")
	public ResponseEntity<String> notifyParticipants(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		// BUG: "Dear Not specified"  

		Session session = requestRequirementService.requireSessionExists(sessionId);
		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		CopyOnWriteArrayList<User> students = (CopyOnWriteArrayList<User>) session.getStudents().getItems(db);
		CopyOnWriteArrayList<User> supervisors = (CopyOnWriteArrayList<User>) session.getSupervisors().getItems(db);

		String subject = String.format("Groups ready for session: %s", session.getName());
		String bodyTemplate = "Dear %s,\n\nAll groups for the session \"%s\" have now been created. "
				+ "Please log in to the grouping system to view your group and next steps.\n\n"
				+ "Best regards,\n%s";

		try {
			for (User student : students) {
				if (student.getEmail() == null || student.getEmail().isBlank())
					continue;

				String studentName = (student.getName() == null || student.getName().isBlank())
						? "Student"
						: student.getName();

				String body = String.format(bodyTemplate, studentName, session.getName(),
						coordinator.getName() != null ? coordinator.getName() : "Course Coordinator");

				emailService.builder()
						.to(student.getEmail())
						.subject(subject)
						.text(body)
						.send();
			}

			for (User supervisor : supervisors) {
				if (supervisor.getEmail() == null || supervisor.getEmail().isBlank())
					continue;

				String supervisorName = (supervisor.getName() == null || supervisor.getName().isBlank())
						? "Supervisor"
						: supervisor.getName();

				String body = String.format(bodyTemplate, supervisorName, session.getName(),
						coordinator.getName() != null ? coordinator.getName() : "Course Coordinator");

				emailService.builder()
						.to(supervisor.getEmail())
						.subject(subject)
						.text(body)
						.send();
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to send notifications: " + e.getMessage());
		}

		return ResponseEntity.ok("Notifications sent successfully to participants.");
	}

}
