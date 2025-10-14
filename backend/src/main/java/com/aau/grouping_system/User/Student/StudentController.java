package com.aau.grouping_system.User.Student;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/student")
public class StudentController {

	private final Database db;

	public StudentController(Database db) {
		this.db = db;
	}

	// requests

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/{sessionId}")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getSession(@PathVariable Integer sessionId,
			HttpServletRequest request) {

		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		CopyOnWriteArrayList<Student> students = (CopyOnWriteArrayList<Student>) session.students.getItems();

		return ResponseEntity.ok(students);
	}

}
