package com.aau.grouping_system.EmailSystem;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
public class NotifyParticipants {

	private final Database db;
	private final RequestRequirementService requestRequirementService;
	private final EmailService emailService;

	public NotifyParticipants(
			RequestRequirementService requestRequirementService,
			EmailService emailService,
			Database db) {
		this.requestRequirementService = requestRequirementService;
		this.emailService = emailService;
		this.db = db;
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
