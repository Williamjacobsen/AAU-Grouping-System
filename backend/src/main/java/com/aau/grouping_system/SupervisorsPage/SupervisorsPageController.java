package com.aau.grouping_system.SupervisorsPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
@RequestMapping("/sessions/{sessionId}/supervisors")
public class SupervisorsPageController {

	private final Database db;
	private final PasswordEncoder passwordEncoder;
	private final RequirementService requirementService;
	private final EmailService emailService;
	private final UserService userService;

	public SupervisorsPageController(
			Database db,
			PasswordEncoder passwordEncoder,
			RequirementService requirementService,
			EmailService emailService,
			UserService userService) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
		this.requirementService = requirementService;
		this.emailService = emailService;
		this.userService = userService;
	}

	private Session validateSessionAccess(HttpServletRequest servlet, String sessionId) {
		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		Session session = requirementService.requireSessionExists(sessionId);
		requirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);
		return session;
	}

	@SuppressWarnings("unchecked")
	private CopyOnWriteArrayList<Supervisor> getSessionSupervisors(Session session) {
		return (CopyOnWriteArrayList<Supervisor>) session.getSupervisors().getItems(db);
	}

	private Supervisor findSupervisorInSession(Session session, String supervisorId) {
		CopyOnWriteArrayList<Supervisor> sessionSupervisors = getSessionSupervisors(session);
		return sessionSupervisors.stream()
				.filter(s -> s.getId().equals(supervisorId))
				.findFirst()
				.orElse(null);
	}

	private void sendCredentialsEmail(String email, String sessionName, String supervisorId, String password,
			boolean isNewPassword) throws Exception {
		String subject = isNewPassword ? "AAU Grouping System - New Password" : "AAU Grouping System - Supervisor Access";
		String actionText = isNewPassword ? "Your password for the AAU Grouping System has been reset for"
				: "You have been added as a supervisor for the";

		String body = """
				Hello,

				%s session: %s

				Your login credentials are:
				ID: %s
				Password: %s

				Please use your ID and password to access the AAU Grouping System.

				Best regards,
				AAU Grouping System""".formatted(actionText, sessionName, supervisorId, password);

		emailService.builder()
				.to(email)
				.subject(subject)
				.text(body)
				.send();
	}

	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getSupervisors(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {
		Session session = validateSessionAccess(servlet, sessionId);
		CopyOnWriteArrayList<Supervisor> supervisors = getSessionSupervisors(session);

		List<Map<String, Object>> supervisorList = supervisors.stream()
				.map(supervisor -> {
					Map<String, Object> supervisorMap = new HashMap<>();
					supervisorMap.put("id", supervisor.getId());
					supervisorMap.put("email", supervisor.getEmail());
					supervisorMap.put("name", supervisor.getName());
					return supervisorMap;
				})
				.collect(Collectors.toList());

		return ResponseEntity.ok(supervisorList);
	}

	public record AddSupervisorRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String email) {
	}

	@PostMapping
	public ResponseEntity<String> addSupervisor(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody AddSupervisorRecord record) {

		Session session = validateSessionAccess(servlet, sessionId);
		CopyOnWriteArrayList<Supervisor> existingSupervisors = getSessionSupervisors(session);

		// Check if supervisor with this email already exists in this session
		boolean supervisorExists = existingSupervisors.stream()
				.anyMatch(supervisor -> supervisor.getEmail().equals(record.email.trim()));

		if (supervisorExists) {
			throw new RequestException(HttpStatus.CONFLICT,
					"Supervisor with this email already exists in this session");
		}

		// Create supervisor with UUID as password
		String password = UUID.randomUUID().toString();
		String passwordHash = passwordEncoder.encode(password);

		Supervisor newSupervisor = new Supervisor(
				db,
				session.getSupervisors(),
				record.email.trim(),
				passwordHash,
				record.email.trim().split("@")[0], // Use email as default name
				session);

		// Send password via email
		try {
			sendCredentialsEmail(record.email.trim(), session.getName(), newSupervisor.getId(), password, false);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Supervisor added successfully and password sent via email");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Supervisor added successfully, but email failed to send: " + e.getMessage());
		}
	}

	@DeleteMapping("/{supervisorId}")
	public ResponseEntity<String> removeSupervisor(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String supervisorId) {
		Session session = validateSessionAccess(servlet, sessionId);
		Supervisor supervisor = findSupervisorInSession(session, supervisorId);

		if (supervisor == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Supervisor not found in this session");
		}

		// Remove supervisor from database
		db.getSupervisors().cascadeRemove(db, supervisorId);

		return ResponseEntity.ok("Supervisor removed successfully");
	}

	@PostMapping("/{supervisorId}/send-new-password")
	public ResponseEntity<String> sendNewPassword(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String supervisorId) {

		Session session = validateSessionAccess(servlet, sessionId);
		Supervisor supervisor = findSupervisorInSession(session, supervisorId);

		if (supervisor == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Supervisor not found in this session");
		}

		// Generate new UUID password
		String newPassword = UUID.randomUUID().toString();
		userService.modifyPassword(newPassword, supervisor);

		// Send new password via email
		try {
			sendCredentialsEmail(supervisor.getEmail(), session.getName(), supervisor.getId(), newPassword, true);
			return ResponseEntity.ok("New password sent successfully to " + supervisor.getEmail());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to reset password: " + e.getMessage());
		}
	}
}
