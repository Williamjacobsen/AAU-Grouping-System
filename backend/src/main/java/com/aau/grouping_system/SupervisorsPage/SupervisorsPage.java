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
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Supervisor.Supervisor;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sessions/{sessionId}/supervisors")
public class SupervisorsPage {
	
	private final Database db;
	private final PasswordEncoder passwordEncoder;
	private final AuthService authService;

	public SupervisorsPage(Database db, PasswordEncoder passwordEncoder, AuthService authService) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
		this.authService = authService;
	}

	private Coordinator RequireCoordinatorExists(HttpServletRequest request) {
		Coordinator coordinator = authService.getCoordinatorByUser(request);
		if (coordinator == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized.");
		}
		return coordinator;
	}

	private Session RequireSessionExists(String sessionId) {
		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Session not found.");
		}
		return session;
	}

	private boolean hasPermission(String sessionId, Coordinator coordinator) {
		Session session = db.getSessions().getItem(sessionId);
		return session != null && session.getCoordinatorId().equals(coordinator.getId());
	}

	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getSupervisors(@PathVariable String sessionId, HttpServletRequest request) {
		Coordinator coordinator = RequireCoordinatorExists(request);
		Session session = RequireSessionExists(sessionId);
		
		if (!hasPermission(sessionId, coordinator)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Access denied.");
		}

		@SuppressWarnings("unchecked")
		CopyOnWriteArrayList<Supervisor> supervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors().getItems(db);
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

	@PostMapping
	public ResponseEntity<String> addSupervisor(@PathVariable String sessionId, @RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
		Coordinator coordinator = RequireCoordinatorExists(httpRequest);
		Session session = RequireSessionExists(sessionId);
		
		if (!hasPermission(sessionId, coordinator)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Access denied.");
		}

		String email = request.get("email");
		if (email == null || email.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Email is required");
		}

		// Check if supervisor with this email already exists in this session
		@SuppressWarnings("unchecked")
		CopyOnWriteArrayList<Supervisor> existingSupervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors().getItems(db);
		boolean supervisorExists = existingSupervisors.stream()
			.anyMatch(supervisor -> supervisor.getEmail().equals(email.trim()));
		
		if (supervisorExists) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Supervisor with this email already exists in this session");
		}

		// Create supervisor with UUID as password
		String password = UUID.randomUUID().toString();
		String passwordHash = passwordEncoder.encode(password);
		
		Supervisor newSupervisor = new Supervisor(
			db,
			session.getSupervisors(),
			email.trim(),
			passwordHash,
			email.trim().split("@")[0], // Use email as default name
			session
		);

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
			
			EmailService.sendEmail(email.trim(), subject, body);
			return ResponseEntity.status(HttpStatus.CREATED).body("Supervisor added successfully and password sent via email");
		} catch (Exception e) {
			// If email fails, still return success since supervisor was created
			return ResponseEntity.status(HttpStatus.CREATED).body("Supervisor added successfully, but email failed to send: " + e.getMessage());
		}
	}

	@DeleteMapping("/{supervisorId}")
	public ResponseEntity<String> removeSupervisor(@PathVariable String sessionId, @PathVariable String supervisorId, HttpServletRequest request) {
		Coordinator coordinator = RequireCoordinatorExists(request);
		Session session = RequireSessionExists(sessionId);
		
		if (!hasPermission(sessionId, coordinator)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Access denied.");
		}

		// Find supervisor in the session
		@SuppressWarnings("unchecked")
		CopyOnWriteArrayList<Supervisor> sessionSupervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors().getItems(db);
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
	public ResponseEntity<String> sendNewPassword(@PathVariable String sessionId, @PathVariable String supervisorId, HttpServletRequest request) {
		Coordinator coordinator = RequireCoordinatorExists(request);
		Session session = RequireSessionExists(sessionId);
		
		if (!hasPermission(sessionId, coordinator)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Access denied.");
		}

		// Find supervisor in the session
		@SuppressWarnings("unchecked")
		CopyOnWriteArrayList<Supervisor> sessionSupervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors().getItems(db);
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
