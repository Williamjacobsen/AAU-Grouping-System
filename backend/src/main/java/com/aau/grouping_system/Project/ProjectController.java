package com.aau.grouping_system.Project;

import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;

import java.util.concurrent.CopyOnWriteArrayList;

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
@GetMapping("/getSessionProjects/{sessionId}")
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

	@DeleteMapping("/delete/{projectId}")
	public ResponseEntity<String> deleteProject(@PathVariable String projectId) {
		Project project = db.getProjects().getItem(projectId); // ask the database for project with certain id

		// Check if project exists if not throw error
		if (project == null) {
			return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
					.body("Project with id " + projectId + " does not exist.");
		}

		// Delete the project from the database
		db.getProjects().cascadeRemove(db, project);

		// Return success message with 200 ok
		return ResponseEntity.ok("Project with id " + projectId + " has been deleted successfully.");
	}
	@PostMapping("/create/{sessionId}/{projectName}")
	public ResponseEntity<String> createProject(@PathVariable String sessionId, @PathVariable String projectName) {
		Session session = db.getSessions().getItem(sessionId); // ask the database for session with certain id

		// Check if session exists if not throw error
		if (session == null) {
			return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
					.body("Session with id " + sessionId + " does not exist.");
		}

		// Create new project
		Project newProject = new Project(db, session.getProjects(), "Test", "Description 1");
		newProject.setName(projectName);

		// Return success message with 200 ok
		return ResponseEntity.ok("Project '" + projectName + "' has been created successfully with id "
				+ newProject.getId() + " and linked to session " + sessionId + ".");
	}
	@PutMapping("/update/{projectId}/{newName}/{newDescription}")
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
		return ResponseEntity.ok("Project with id " + projectId + " has been updated successfully.");
	}
}
