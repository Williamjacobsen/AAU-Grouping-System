package com.aau.grouping_system.Session;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/sessions")
public class SessionController {

	private final Database db;
	private final SessionService service;

	public SessionController(Database db, SessionService service) {
		this.db = db;
		this.service = service;
	}

	public User getUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (User) session.getAttribute("user");
	}

	private Coordinator getCoordinatorByUser(HttpServletRequest request) {
		User user = getUser(request);
		if (user instanceof Coordinator) {
			return (Coordinator) user;
		} else {
			return null;
		}
	}

	@PostMapping
	public ResponseEntity<Session> createSession(@RequestBody Map<String, String> request,
			HttpServletRequest httpRequest) {
		Coordinator coordinator = getCoordinatorByUser(httpRequest);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String sessionName = request.get("name");
		if (sessionName == null || sessionName.trim().isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		try {
			Session newSession = service.createSession(sessionName.trim(), coordinator);
			return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping
	public ResponseEntity<CopyOnWriteArrayList<Session>> getAllSessions(HttpServletRequest request) {
		Coordinator coordinator = getCoordinatorByUser(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		CopyOnWriteArrayList<Session> sessions = service.getSessionsByCoordinator(coordinator);
		return ResponseEntity.ok(sessions);
	}

	@GetMapping("/{sessionId}")
	public ResponseEntity<Session> getSession(@PathVariable String sessionId, HttpServletRequest request) {

		Session session = service.getSession(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		User user = getUser(request);
		if (user == null || !service.isUserAuthorized(sessionId, user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.ok(session);
	}

	@DeleteMapping("/{sessionId}")
	public ResponseEntity<String> deleteSession(@PathVariable String sessionId, HttpServletRequest request) {
		Coordinator coordinator = getCoordinatorByUser(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		boolean deleted = service.deleteSession(sessionId, coordinator);
		if (deleted) {
			return ResponseEntity.ok("Session deleted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied or session not found");
		}
	}

}
