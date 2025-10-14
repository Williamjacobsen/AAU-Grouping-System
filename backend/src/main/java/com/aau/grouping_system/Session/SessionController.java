package com.aau.grouping_system.Session;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aau.grouping_system.User.Coordinator.Coordinator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/sessions")
public class SessionController {

	private final SessionService sessionPageService;

	public SessionController(SessionService sessionPageService) {
		this.sessionPageService = sessionPageService;
	}

	private Coordinator getCurrentCoordinator(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (Coordinator) session.getAttribute("user");
	}

	@PostMapping
	public ResponseEntity<Session> createSession(@RequestBody Map<String, String> request,
			HttpServletRequest httpRequest) {
		Coordinator coordinator = getCurrentCoordinator(httpRequest);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String sessionName = request.get("name");
		if (sessionName == null || sessionName.trim().isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		try {
			Session newSession = sessionPageService.createSession(sessionName.trim(), coordinator);
			return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping
	public ResponseEntity<CopyOnWriteArrayList<Session>> getAllSessions(HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		CopyOnWriteArrayList<Session> sessions = sessionPageService.getSessionsByCoordinator(coordinator);
		return ResponseEntity.ok(sessions);
	}

	@GetMapping("/{sessionId}")
	public ResponseEntity<Session> getSession(@PathVariable Integer sessionId, HttpServletRequest request) {

		Coordinator coordinator = getCurrentCoordinator(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Session session = sessionPageService.getSession(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		if (!sessionPageService.hasPermission(sessionId, coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return ResponseEntity.ok(session);
	}

	@DeleteMapping("/{sessionId}")
	public ResponseEntity<String> deleteSession(@PathVariable Integer sessionId, HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		boolean deleted = sessionPageService.deleteSession(sessionId, coordinator);
		if (deleted) {
			return ResponseEntity.ok("Session deleted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied or session not found");
		}
	}

	@PostMapping("/{sessionId}/open")
	public ResponseEntity<String> openSession(@PathVariable Integer sessionId, HttpServletRequest request) {
		Coordinator coordinator = getCurrentCoordinator(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (!sessionPageService.hasPermission(sessionId, coordinator)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Session session = sessionPageService.getSession(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok("/status/" + sessionId);
	}
}
