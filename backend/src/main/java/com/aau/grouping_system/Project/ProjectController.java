package com.aau.grouping_system.Project;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;

import jakarta.servlet.http.HttpServletRequest;
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
																	// true here despite Java's invariance of generics.
	@GetMapping("/getSessionProjects/{sessionId}")
	public ResponseEntity<CopyOnWriteArrayList<Project>> getSessionsProjects(@PathVariable Integer sessionId,
			HttpServletRequest request) {

		// todo: Brug db-variablen til at få den Session, som har samme ID som
		// "sessionId"-variablen. Husk også at tjekke, at den Session du requrest'er
		// faktisk eksisterer (hvis ikke skal du sende en fejlmeddelse tilbage).

		// todo: Brug den Session, som du har fået, til at få dens liste af Projects.
		CopyOnWriteArrayList<Project> projects = null; // Indsæt din egen kode i stedet for "null"

		// todo: Returner en ResponseEntity som indeholder din liste af Projects.
		return ResponseEntity.ok(projects);
	}

}
