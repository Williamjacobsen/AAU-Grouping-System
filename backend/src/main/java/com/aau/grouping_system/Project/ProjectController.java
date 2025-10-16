package com.aau.grouping_system.Project;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/project")
public class ProjectController {

	private final Database db;

	// constructors

	public ProjectController(Database db) {
		this.db = db;
	}

	// requests

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/getSessionProjects/{sessionId}")
	public ResponseEntity<CopyOnWriteArrayList<Project>> getSessionsProjects(@PathVariable Integer sessionId) {
	 Session session = db.getSessions().getItem(sessionId);

    // Check if session exists
    if (session == null) {
		return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(null);
    }

    // Get that session’s projects
    CopyOnWriteArrayList<Project> projects = (CopyOnWriteArrayList<Project>) session.projects.getItems();

    // Return them
    return ResponseEntity.ok(projects);
	}

}
