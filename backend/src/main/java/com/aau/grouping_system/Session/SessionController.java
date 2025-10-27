package com.aau.grouping_system.Session;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sessions")
public class SessionController {

	private final Database db;
	private final SessionService sessionService;
	private final AuthService authService;

	public SessionController(Database db, SessionService sessionService, AuthService authService) {
		this.db = db;
		this.sessionService = sessionService;
		this.authService = authService;
	}

	@PostMapping
	public ResponseEntity<Session> createSession(@RequestBody Map<String, String> request,
			HttpServletRequest httpRequest) {
		Coordinator coordinator = authService.getCoordinatorByUser(httpRequest);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String sessionName = request.get("name");
		if (sessionName == null || sessionName.trim().isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		try {
			Session newSession = sessionService.createSession(sessionName.trim(), coordinator);
			return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping
	public ResponseEntity<CopyOnWriteArrayList<Session>> getAllSessions(HttpServletRequest request) {
		Coordinator coordinator = authService.getCoordinatorByUser(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		CopyOnWriteArrayList<Session> sessions = sessionService.getSessionsByCoordinator(coordinator);
		return ResponseEntity.ok(sessions);
	}

	@GetMapping("/{sessionId}")
	public ResponseEntity<Session> getSession(@PathVariable String sessionId, HttpServletRequest request) {

		Session session = sessionService.getSession(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		User user = authService.getUser(request);
		if (user == null || !sessionService.isUserAuthorizedSession(sessionId, user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.ok(session);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getSupervisors")
	public ResponseEntity<CopyOnWriteArrayList<Supervisor>> getSupervisors(@PathVariable String sessionId,
			HttpServletRequest request) {

		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		User user = authService.getUser(request);
		if (user == null || !sessionService.isUserAuthorizedSession(sessionId, user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		CopyOnWriteArrayList<Supervisor> supervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);

		return ResponseEntity.ok(supervisors);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getStudents")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getSessionStudents(@PathVariable String sessionId,
			HttpServletRequest request) {

		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		User user = authService.getUser(request);
		if (user == null || !sessionService.isUserAuthorizedSession(sessionId, user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		CopyOnWriteArrayList<Student> students = (CopyOnWriteArrayList<Student>) session.getStudents().getItems(db);

		return ResponseEntity.ok(students);
	}

	@DeleteMapping("/{sessionId}")
	public ResponseEntity<String> deleteSession(@PathVariable String sessionId, HttpServletRequest request) {
		Coordinator coordinator = authService.getCoordinatorByUser(request);
		if (coordinator == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		boolean deleted = sessionService.deleteSession(sessionId, coordinator);
		if (deleted) {
			return ResponseEntity.ok("Session deleted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied or session not found");
		}
	}

	@PostMapping("/{sessionId}/saveSetup")
	public ResponseEntity<String> saveSetup(HttpServletRequest httpRequest, @PathVariable String sessionId,
			@RequestBody Map<String, String> request) {

		// 1) Få sessionen via "sessionId" og tjek, at den eksisterer.
		// Hint: Tjek linje 86-89 i denne fil.

		// 2) Få useren via "httpRequest" og tjek, at han eksisterer og har adgang til
		// sessionen.
		// Hint: Tjek linje 91-94 i denne fil.

		// 3) Ekstraher data fra "request" og gem dem i nogle variable.
		// Hint:
		// String name = request.get("name");
		// String studentEmails = request.get("studentEmails");
		// osv.

		// 4) Kald en funktion kaldet "applySetup", som jeg har lavet til dig i
		// "SessionService.java"-filen (du skal dog selv fylde den ud, den er tom lige
		// nu).
		// Funktionen's parametre skal være den data, som du fik ekstraheret i trin 3).

		return ResponseEntity.ok("Session setup saved successfully!");
	}

}
