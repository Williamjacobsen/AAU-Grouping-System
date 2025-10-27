package com.SupervisorsPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Supervisor.Supervisor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/sessions/{sessionId}/supervisors")
public class SupervisorsPage {
	
	private final Database db;
	private final PasswordEncoder passwordEncoder;

	public SupervisorsPage(Database db, PasswordEncoder passwordEncoder) {
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

	private boolean hasPermission(Integer sessionId, Coordinator coordinator) {
		Session session = db.getSessions().getItem(sessionId);
		return session != null && session.getCoordinator().equals(coordinator);
	}

	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getSupervisors(@PathVariable Integer sessionId, HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (!hasPermission(sessionId, coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		List<Map<String, Object>> supervisorList = session.getSupervisors().getReferenceList().stream()
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
	public ResponseEntity<String> addSupervisor(@PathVariable Integer sessionId, @RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
		Coordinator coordinator = getCurrentCoordinator(httpRequest);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (!hasPermission(sessionId, coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		String email = request.get("email");
		if (email == null || email.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Email is required");
		}

		// Check if supervisor with this email already exists in this session
		boolean supervisorExists = session.getSupervisors().getReferenceList().stream()
			.anyMatch(supervisor -> supervisor.getEmail().equals(email.trim()));
		
		if (supervisorExists) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Supervisor with this email already exists in this session");
		}

		// Create supervisor with default password
		String defaultPassword = "supervisor123"; // TODO: Generate random password and send via email
		String passwordHash = passwordEncoder.encode(defaultPassword);
		
		new Supervisor(
			db.getSupervisors(),
			session.getSupervisors(),
			email.trim(),
			passwordHash,
			email.trim().split("@")[0], // Use email as default name
			session
		);

		return ResponseEntity.status(HttpStatus.CREATED).body("Supervisor added successfully");
	}

	@DeleteMapping("/{supervisorId}")
	public ResponseEntity<String> removeSupervisor(@PathVariable Integer sessionId, @PathVariable Integer supervisorId, HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (!hasPermission(sessionId, coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		// Find supervisor in the session
		Supervisor supervisor = session.getSupervisors().getReferenceList().stream()
			.filter(s -> s.getId() == supervisorId)
			.findFirst()
			.orElse(null);
		
		if (supervisor == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Supervisor not found in this session");
		}

		// Remove supervisor from database
		db.getSupervisors().remove(supervisorId);

		return ResponseEntity.ok("Supervisor removed successfully");
	}
}
