package com.aau.grouping_system.Session;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
@Validated // enables method-level validation
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

	public Coordinator RequireCoordinatorExists(HttpServletRequest servlet) {
		Coordinator coordinator = authService.getCoordinatorByUser(servlet);
		if (coordinator == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized.");
		}
		return coordinator;
	}

	public Session RequireSessionExists(String sessionId) {
		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Session not found.");
		}
		return session;
	}

	public User RequireUserExists(HttpServletRequest servlet) {
		User user = authService.getUser(servlet);
		if (user == null) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User not authorized");
		}
		return user;
	}

	private void RequireUserIsAuthorizedSession(String sessionId, User user) {
		if (!sessionService.isUserAuthorizedSession(sessionId, user)) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "User is not authorized session.");
		}
	}

	private record CreateSessionRecord(
			@NoDangerousCharacters @NotBlank String name) {
	}

	@PostMapping
	public ResponseEntity<Session> createSession(HttpServletRequest servlet,
			@Valid @RequestBody CreateSessionRecord record) {

		Coordinator coordinator = RequireCoordinatorExists(servlet);

		try {
			Session newSession = sessionService.createSession(record.name.trim(), coordinator);
			return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
		} catch (Exception e) {
			throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<CopyOnWriteArrayList<Session>> getAllSessions(HttpServletRequest servlet) {
		Coordinator coordinator = RequireCoordinatorExists(servlet);

		CopyOnWriteArrayList<Session> sessions = sessionService.getSessionsByCoordinator(coordinator);
		return ResponseEntity.ok(sessions);
	}

	@GetMapping("/{sessionId}")
	public ResponseEntity<Session> getSession(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = RequireSessionExists(sessionId);
		User user = RequireUserExists(servlet);
		RequireUserIsAuthorizedSession(sessionId, user);

		return ResponseEntity.ok(session);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getSupervisors")
	public ResponseEntity<CopyOnWriteArrayList<Supervisor>> getSupervisors(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = RequireSessionExists(sessionId);
		User user = RequireUserExists(servlet);
		RequireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Supervisor> supervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);

		return ResponseEntity.ok(supervisors);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getStudents")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getStudents(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = RequireSessionExists(sessionId);
		User user = RequireUserExists(servlet);
		RequireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Student> students = (CopyOnWriteArrayList<Student>) session.getStudents().getItems(db);

		return ResponseEntity.ok(students);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here despite Java's invariance of generics.
	@GetMapping("/{sessionId}/getProjects")
	public ResponseEntity<CopyOnWriteArrayList<Project>> getProjects(
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = db.getSessions().getItem(sessionId); // ask the database for session with certain id
		// Check if session exists if not throw error
		if (session == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Session not found");
		}

		// Get that session’s projects and type cast
		CopyOnWriteArrayList<Project> projects = (CopyOnWriteArrayList<Project>) session.getProjects().getItems(db);

		// Return them with 200 ok
		return ResponseEntity.ok(projects);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getGroups")
	public ResponseEntity<CopyOnWriteArrayList<Group>> getGroups(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = RequireSessionExists(sessionId);
		User user = RequireUserExists(servlet);
		RequireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Group> groups = (CopyOnWriteArrayList<Group>) session.getGroups().getItems(db);

		return ResponseEntity.ok(groups);
	}

	@DeleteMapping("/{sessionId}")
	public ResponseEntity<String> deleteSession(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Coordinator coordinator = RequireCoordinatorExists(servlet);

		boolean deleted = sessionService.deleteSession(sessionId, coordinator);
		if (deleted) {
			return ResponseEntity.ok("Session deleted successfully");
		} else {
			throw new RequestException(HttpStatus.FORBIDDEN, "Access denied or session not found");
		}
	}

	// TODO: Needs content inside its parameter.
	private record SaveSetupRecord() {
	}

	@PostMapping("/{sessionId}/saveSetup")
	public ResponseEntity<String> saveSetup(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody SaveSetupRecord record) {

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
