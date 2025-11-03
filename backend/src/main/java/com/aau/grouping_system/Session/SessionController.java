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
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Group.Group;
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

	public Coordinator RequireCoordinatorExists(HttpServletRequest request) {
		Coordinator coordinator = authService.getCoordinatorByUser(request);
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

	public User RequireUserExists(HttpServletRequest request) {
		User user = authService.getUser(request);
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

	@PostMapping
	public ResponseEntity<Session> createSession(@RequestBody Map<String, String> body,
			HttpServletRequest request) {

		String sessionName = body.get("name");
		if (sessionName == null || sessionName.trim().isEmpty()) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Name not found in request.");
		}

		Coordinator coordinator = RequireCoordinatorExists(request);

		try {
			Session newSession = sessionService.createSession(sessionName.trim(), coordinator);
			return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
		} catch (Exception e) {
			throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<CopyOnWriteArrayList<Session>> getAllSessions(HttpServletRequest request) {
		Coordinator coordinator = RequireCoordinatorExists(request);

		CopyOnWriteArrayList<Session> sessions = sessionService.getSessionsByCoordinator(coordinator);
		return ResponseEntity.ok(sessions);
	}

	@GetMapping("/{sessionId}")
	public ResponseEntity<Session> getSession(@PathVariable String sessionId, HttpServletRequest request) {

		Session session = RequireSessionExists(sessionId);
		User user = RequireUserExists(request);
		RequireUserIsAuthorizedSession(sessionId, user);

		return ResponseEntity.ok(session);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getSupervisors")
	public ResponseEntity<CopyOnWriteArrayList<Supervisor>> getSupervisors(@PathVariable String sessionId,
			HttpServletRequest request) {

		Session session = RequireSessionExists(sessionId);
		User user = RequireUserExists(request);
		RequireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Supervisor> supervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);

		return ResponseEntity.ok(supervisors);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getStudents")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getStudents(@PathVariable String sessionId,
			HttpServletRequest request) {

		Session session = RequireSessionExists(sessionId);
		User user = RequireUserExists(request);
		RequireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Student> students = (CopyOnWriteArrayList<Student>) session.getStudents().getItems(db);

		return ResponseEntity.ok(students);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here despite Java's invariance of generics.
	@GetMapping("/{sessionId}/getProjects")
	public ResponseEntity<CopyOnWriteArrayList<Project>> getProjects(@PathVariable String sessionId) {

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
	public ResponseEntity<CopyOnWriteArrayList<Group>> getGroups(@PathVariable String sessionId,
			HttpServletRequest request) {

		Session session = RequireSessionExists(sessionId);
		User user = RequireUserExists(request);
		RequireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Group> groups = (CopyOnWriteArrayList<Group>) session.getGroups().getItems(db);

		return ResponseEntity.ok(groups);
	}

	@DeleteMapping("/{sessionId}")
	public ResponseEntity<String> deleteSession(@PathVariable String sessionId, HttpServletRequest request) {

		Coordinator coordinator = RequireCoordinatorExists(request);

		boolean deleted = sessionService.deleteSession(sessionId, coordinator);
		if (deleted) {
			return ResponseEntity.ok("Session deleted successfully");
		} else {
			throw new RequestException(HttpStatus.FORBIDDEN, "Access denied or session not found");
		}
	}

	@PostMapping("/{sessionId}/saveSetup")
	public ResponseEntity<String> saveSetup(HttpServletRequest httpRequest, @PathVariable String sessionId,
			@RequestBody Map<String, String> request) {
				
			
		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		User user = authService.getUser(request);
		if (user == null || !sessionService.isUserAuthorizedSession(sessionId, user)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
			
		String name = request.get("name");
		String description = request.get("description");
		String studentEmails = request.get("studentEmails");
		String supervisorEmails = request.get("supervisorEmails");
		String coordinatorName = request.get("coordinatorName");
		String questionnaireDeadline = request.get("questionnaireDeadline");
		String initialProjects = request.get("initialProjects");
		String optionalQuestionnaire = request.get("optionalQuestionnaire");
		int groupSize = Integer.parseInt(request.get("groupSize"));

		sessionService.applySetup(session, name, description, studentEmails, supervisorEmails,
		coordinatorName, questionnaireDeadline, initialProjects, optionalQuestionnaire, groupSize);

		return ResponseEntity.ok("Session setup saved successfully!");
	}

}
