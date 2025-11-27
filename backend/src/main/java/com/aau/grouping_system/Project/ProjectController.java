package com.aau.grouping_system.Project;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@RestController // handles CRUD ops
@Validated // enables method-level validation
@RequestMapping("/project") // mapping, all URLs that has /project are routed to this controller
public class ProjectController {

	private final Database db; // storage in db (final never changes once set)
	private final RequestRequirementService requestRequirementService;

	// constructor
	// dependency injection for db and RequestRequirementService
	public ProjectController(
			Database db,
			RequestRequirementService requestRequirementService) {
		this.db = db;
		this.requestRequirementService = requestRequirementService;
	}

	private void requireAllowStudentProjectProposals(Session session) {
		if (!session.getAllowStudentProjectProposals()) { // checks if students can propose projects
			throw new RequestException(HttpStatus.UNAUTHORIZED,
					"Your coordinator does not allow student project proposals in this session");
		}
	}

	private void requireUserIsCreatorOfTheProject(User user, Project project) {
		if (!project.getCreatorUserId().equals(user.getId())) { // verification that the user created the project that
																														// they're trying to modify
			throw new RequestException(HttpStatus.UNAUTHORIZED,
					"User is neither the coordinator or the creator of the project");
		}
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	@GetMapping({ "/sessions/{sessionId}/getProjects", "/getSessionProjects/{sessionId}" }) // retrieves all projects from
																																													// sessionid
	public ResponseEntity<CopyOnWriteArrayList<Project>> getSessionsProjects(@PathVariable String sessionId) {
		Session session = db.getSessions().getItem(sessionId); // ask the database for session with certain id

		// Check if session exists if not throw error
		if (session == null) {
			return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(null);
		}

		// Get that session’s projects and type cast to CopyOnWriteArrayList
		CopyOnWriteArrayList<Project> projects = (CopyOnWriteArrayList<Project>) session.getProjects().getItems(db);

		// Return them with 200 ok
		return ResponseEntity.ok(projects);
	}

	@DeleteMapping("/delete/{projectId}/{sessionId}")
	public ResponseEntity<String> deleteProject(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String projectId,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Project project = requestRequirementService.requireProjectExists(projectId);
		Session session = requestRequirementService.requireSessionExists(sessionId);
		User user = requestRequirementService.requireUserExists(servlet);

		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);
		if (user.getRole() != User.Role.Coordinator) { // only coordinators can delete any project
			requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);
			requireUserIsCreatorOfTheProject(user, project); // supervisor/student can only delete within deadline
		}

		// Delete the project from the database
		db.getProjects().cascadeRemoveItem(db, project);

		// Return success message with 200 ok
		return ResponseEntity.ok("Project with id " + projectId + " has been deleted successfully.");
	}

	@PostMapping("/create/{sessionId}/{projectName}/{description}")
	public ResponseEntity<Map<String, Object>> createProject(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String projectName,
			@NoDangerousCharacters @NotBlank @PathVariable String description) {

		User user = requestRequirementService.requireUserExists(servlet);
		Session session = requestRequirementService.requireSessionExists(sessionId);

		if (user.getRole() != User.Role.Coordinator) {
			requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);
		}
		if (user.getRole() == User.Role.Student) {
			requireAllowStudentProjectProposals(session);
		}

		Project newProject = db.getProjects().addItem(
				session.getProjects(),
				new Project(projectName, description, user));

		// Create a response map
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Project '" + projectName + "' has been created successfully.");
		response.put("id", newProject.getId());

		// Return success message with 200 ok
		return ResponseEntity.ok(response);
	}

}