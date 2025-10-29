package com.aau.grouping_system.Project;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/project") // mapping, all URLs that has /project are handled here
public class ProjectController {

	private final Database db; // storage in db (final never changes once set)

	// constructor
	// dependency injection
	public ProjectController(Database db) {
		this.db = db;
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here despite Java's invariance of generics.
	@GetMapping("/getSessionProjects/{sessionId}")
	public ResponseEntity<CopyOnWriteArrayList<Project>> getSessionsProjects(@PathVariable String sessionId) {

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

}
