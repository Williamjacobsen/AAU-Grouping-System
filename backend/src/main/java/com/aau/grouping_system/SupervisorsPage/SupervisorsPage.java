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

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
@Validated // enables method-level validation
@RequestMapping("/sessions/{sessionId}/supervisors")
public class SupervisorsPage {

	private final Database db;
	private final PasswordEncoder passwordEncoder;
	private final AuthService authService;
	private final RequirementService requirementService;

	public SupervisorsPage(Database db, PasswordEncoder passwordEncoder, AuthService authService,
			RequirementService requirementService) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
		this.authService = authService;
		this.requirementService = requirementService;
	}

	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getSupervisors(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {
		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		Session session = requirementService.requireSessionExists(sessionId);

		requirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		@SuppressWarnings("unchecked")
		CopyOnWriteArrayList<Supervisor> supervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);
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

	private record AddSupervisorRecord(
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String email) {
	}

	@PostMapping
	public ResponseEntity<String> addSupervisor(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody AddSupervisorRecord record) {

		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		Session session = requirementService.requireSessionExists(sessionId);

		requirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		// Check if supervisor with this email already exists in this session
		@SuppressWarnings("unchecked")
		CopyOnWriteArrayList<Supervisor> existingSupervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);
		boolean supervisorExists = existingSupervisors.stream()
				.anyMatch(supervisor -> supervisor.getEmail().equals(record.email.trim()));

		if (supervisorExists) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body("Supervisor with this email already exists in this session");
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
			String subject = "AAU Grouping System - Supervisor Access";
			String body = """
					Hello,

					You have been added as a supervisor for the session: %s

					Your login credentials are:
					ID: %s
					Password: %s

					Please use your ID and password to access the AAU Grouping System.

					Best regards,
					AAU Grouping System""".formatted(session.getName(), newSupervisor.getId(), password);

			EmailService.sendEmail(record.email.trim(), subject, body);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Supervisor added successfully and password sent via email");
		} catch (Exception e) {
			// If email fails, still return success since supervisor was created
			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Supervisor added successfully, but email failed to send: " + e.getMessage());
		}
	}

	@DeleteMapping("/{supervisorId}")
	public ResponseEntity<String> removeSupervisor(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String supervisorId) {
		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		Session session = requirementService.requireSessionExists(sessionId);

		requirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		// Find supervisor in the session
		@SuppressWarnings("unchecked")
		CopyOnWriteArrayList<Supervisor> sessionSupervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);
		Supervisor supervisor = sessionSupervisors.stream()
				.filter(s -> s.getId().equals(supervisorId))
				.findFirst()
				.orElse(null);

		if (supervisor == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Supervisor not found in this session");
		}

		// Remove supervisor from database
		db.getSupervisors().cascadeRemove(db, supervisorId);

		return ResponseEntity.ok("Supervisor removed successfully");
	}

	@PostMapping("/{supervisorId}/send-new-password")
	public ResponseEntity<String> sendNewPassword(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String supervisorId) {

		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		Session session = requirementService.requireSessionExists(sessionId);

		requirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		// Find supervisor in the session
		@SuppressWarnings("unchecked")
		CopyOnWriteArrayList<Supervisor> sessionSupervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);
		Supervisor supervisor = sessionSupervisors.stream()
				.filter(s -> s.getId().equals(supervisorId))
				.findFirst()
				.orElse(null);

		if (supervisor == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Supervisor not found in this session");
		}

		// Generate new password and update supervisor
		String newPassword = UUID.randomUUID().toString();
		String passwordHash = passwordEncoder.encode(newPassword);
		supervisor.setPasswordHash(passwordHash);

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
					AAU Grouping System""".formatted(session.getName(), supervisor.getId(), newPassword);

			EmailService.sendEmail(supervisor.getEmail(), subject, body);
			return ResponseEntity.ok("New password sent successfully to " + supervisor.getEmail());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to reset password: " + e.getMessage());
		}
	}
}
