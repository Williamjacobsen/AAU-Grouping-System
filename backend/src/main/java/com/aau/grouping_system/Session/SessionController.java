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
import com.aau.grouping_system.Utils.RequestRequirementService;

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
	private final RequestRequirementService requestRequirementService;

	public SessionController(Database db, SessionService sessionService, AuthService authService,
			RequestRequirementService requestRequirementService) {
		this.db = db;
		this.sessionService = sessionService;
		this.authService = authService;
		this.requestRequirementService = requestRequirementService;
	}

	private record CreateSessionRecord(
			@NoDangerousCharacters @NotBlank String name) {
	}

	@PostMapping
	public ResponseEntity<Session> createSession(
			HttpServletRequest servlet,
			@Valid @RequestBody CreateSessionRecord record) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);

		try {
			Session newSession = sessionService.createSession(record.name.trim(), coordinator);
			return ResponseEntity.status(HttpStatus.CREATED).body(newSession);
		} catch (Exception e) {
			throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error: " + e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<CopyOnWriteArrayList<Session>> getAllSessions(HttpServletRequest servlet) {
		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);

		CopyOnWriteArrayList<Session> sessions = sessionService.getSessionsByCoordinator(coordinator);
		return ResponseEntity.ok(sessions);
	}

	@GetMapping("/{sessionId}")
	public ResponseEntity<Session> getSession(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		User user = requestRequirementService.requireUserExists(servlet);
		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);

		return ResponseEntity.ok(session);
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	@GetMapping("/{sessionId}/getSupervisors")
	public ResponseEntity<CopyOnWriteArrayList<Supervisor>> getSupervisors(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		User user = requestRequirementService.requireUserExists(servlet);
		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Supervisor> supervisors = (CopyOnWriteArrayList<Supervisor>) session.getSupervisors()
				.getItems(db);

		return ResponseEntity.ok(supervisors);
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	@GetMapping("/{sessionId}/getStudents")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getStudents(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		User user = requestRequirementService.requireUserExists(servlet);
		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Student> students = (CopyOnWriteArrayList<Student>) session.getStudents().getItems(db);

		return ResponseEntity.ok(students);
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
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

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	@GetMapping("/{sessionId}/getGroups")
	public ResponseEntity<CopyOnWriteArrayList<Group>> getGroups(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		User user = requestRequirementService.requireUserExists(servlet);
		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Group> groups = (CopyOnWriteArrayList<Group>) session.getGroups().getItems(db);

		return ResponseEntity.ok(groups);
	}

	@DeleteMapping("/{sessionId}")
	public ResponseEntity<String> deleteSession(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);

		boolean deleted = sessionService.deleteSession(sessionId, coordinator);
		if (deleted) {
			return ResponseEntity.ok("Session deleted successfully");
		} else {
			throw new RequestException(HttpStatus.FORBIDDEN, "Access denied or session not found");
		}
	}

}
