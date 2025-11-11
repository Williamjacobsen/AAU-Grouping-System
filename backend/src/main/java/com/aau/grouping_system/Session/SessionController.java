package com.aau.grouping_system.Session;

import java.util.Map;
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
import com.aau.grouping_system.Utils.RequirementService;

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
	private final RequirementService requirementService;

	public SessionController(Database db, SessionService sessionService, AuthService authService,
			RequirementService requirementService) {
		this.db = db;
		this.sessionService = sessionService;
		this.authService = authService;
		this.requirementService = requirementService;
	}

	private record CreateSessionRecord(
			@NoDangerousCharacters @NotBlank String name) {
	}

	@PostMapping
	public ResponseEntity<Session> createSession(HttpServletRequest servlet,
			@Valid @RequestBody CreateSessionRecord record) {

		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);

		try {
			Session newSession = sessionService.createSession(record.name.trim(), coordinator);
			return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
		} catch (Exception e) {
			throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<CopyOnWriteArrayList<Session>> getAllSessions(HttpServletRequest servlet) {
		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);

		CopyOnWriteArrayList<Session> sessions = sessionService.getSessionsByCoordinator(coordinator);
		return ResponseEntity.ok(sessions);
	}

	@GetMapping("/{sessionId}")
	public ResponseEntity<Session> getSession(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requirementService.requireSessionExists(sessionId);
		User user = requirementService.requireUserExists(servlet);
		requirementService.requireUserIsAuthorizedSession(sessionId, user);

		return ResponseEntity.ok(session);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getSupervisors")
	public ResponseEntity<CopyOnWriteArrayList<Supervisor>> getSupervisors(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requirementService.requireSessionExists(sessionId);
		User user = requirementService.requireUserExists(servlet);
		requirementService.requireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Supervisor> supervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);

		return ResponseEntity.ok(supervisors);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}/getStudents")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getStudents(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requirementService.requireSessionExists(sessionId);
		User user = requirementService.requireUserExists(servlet);
		requirementService.requireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Student> students = (CopyOnWriteArrayList<Student>) session.getStudents().getItems(db);

		return ResponseEntity.ok(students);
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here despite Java's invariance of generics.
	@GetMapping("/{sessionId}/getProjects")
	public ResponseEntity<CopyOnWriteArrayList<Project>> getProjects(
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = db.getSessions().getItem(sessionId);
		System.out.println(session); // ask the database for session with certain id
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

		Session session = requirementService.requireSessionExists(sessionId);
		User user = requirementService.requireUserExists(servlet);
		requirementService.requireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Group> groups = (CopyOnWriteArrayList<Group>) session.getGroups().getItems(db);

		return ResponseEntity.ok(groups);
	}

	@DeleteMapping("/{sessionId}")
	public ResponseEntity<String> deleteSession(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);

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
	public ResponseEntity<String> saveSetup(HttpServletRequest httpRequest, @PathVariable String sessionId,
			@RequestBody Map<String, String> request) {
				
			
		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		User user = authService.getUser(httpRequest);
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
