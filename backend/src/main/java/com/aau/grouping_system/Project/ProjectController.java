package com.aau.grouping_system.Project;

import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@Validated // enables method-level validation
@RequestMapping("/project") // mapping, all URLs that has /project are handled here
public class ProjectController {

	private final Database db; // storage in db (final never changes once set)

	// constructor
	// dependency injection
	public ProjectController(Database db) {
		this.db = db;
	}

	@GetMapping({ "/sessions/{sessionId}/getProjects", "/getSessionProjects/{sessionId}" })
	public ResponseEntity<CopyOnWriteArrayList<Project>> getSessionsProjects(@PathVariable String sessionId) {
		Session session = db.getSessions().getItem(sessionId); // ask the database for session with certain id

		// Check if session exists if not throw error
		if (session == null) {
			return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(null);
		}

		// Get that session’s projects and type cast
		CopyOnWriteArrayList<Project> projects = (CopyOnWriteArrayList<Project>) session.getProjects().getItems(db);

		// Return them with 200 ok
		return ResponseEntity.ok(projects);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProject(@PathVariable String id) {
		Project project = db.getProjects().getItem(id); // ask the database for project with certain id

		// Check if project exists if not throw error
		if (project == null) {
			return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
					.body("Project with id " + id + " does not exist.");
		}

		// Delete the project from the database
		db.getProjects().cascadeRemove(db, project);

		// Return success message with 200 ok
		return ResponseEntity.ok("Project with id " + id + " has been deleted successfully.");
	}

	@PostMapping("/create/{sessionId}/{projectName}/{description}")
	public ResponseEntity<Map<String, Object>> createProject(@PathVariable String sessionId,
			@PathVariable String projectName, @PathVariable String description) {
		Session session = db.getSessions().getItem(sessionId); // ask the database for session with certain id

		// Check if session exists if not throw error
		if (session == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("error", "Session with id " + sessionId + " does not exist."));
		}
		Project newProject = new Project(db, session.getProjects(), projectName, description);

		// Create a response map
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Project '" + projectName + "' has been created successfully.");
		response.put("id", newProject.getId());

		// Return success message with 200 ok
		return ResponseEntity.ok(response);
	}

 	
	}
/*@PutMapping("/update/{projectId}/{newName}/{newDescription}")
	public ResponseEntity<String> updateProject(@PathVariable String projectId, @PathVariable String newName,
			@PathVariable String newDescription) {
		Project project = db.getProjects().getItem(projectId); // ask the database for project with certain id

		// Check if project exists if not throw error
		if (project == null) {
			return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
					.body("Project with id " + projectId + " does not exist.");
		}

		// Update project details
		project.setName(newName);
		project.setDescription(newDescription);

		// Return success message with 200 ok
		return ResponseEntity.ok("Project with id " + projectId + " has been updated successfully."); */

