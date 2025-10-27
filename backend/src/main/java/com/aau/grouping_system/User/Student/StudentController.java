package com.aau.grouping_system.User.Student;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Authentication.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/student")
public class StudentController {

	private final Database db;
	private final StudentService studentService;
	private final AuthService authService;

	public StudentController(Database db, StudentService studentService, AuthService authService) {
		this.db = db;
		this.studentService = studentService;
		this.authService = authService;
	}

	@SuppressWarnings("unchecked") // Suppress in-editor warnings about type safety violations because it isn't
																	// true here because Java's invariance of generics.
	@GetMapping("/getSessionStudents/{sessionId}")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getSessionStudents(@PathVariable String sessionId,
			HttpServletRequest request) {

		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		CopyOnWriteArrayList<Student> students = (CopyOnWriteArrayList<Student>) session.students.getItems(db);

		return ResponseEntity.ok(students);
	}

	@PostMapping("/saveQuestionnaireAnswers")
	public ResponseEntity<String> saveQuestionnaireAnswers(HttpServletRequest request,
			@RequestBody Map<String, String> body) {

		Student student = authService.getStudentByUser(request);
		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		String name = body.get("name");

		studentService.saveQuestionnaireAnswers(student, name);

		return ResponseEntity.ok("Saved questionnaire answers successfully.");
	}
}
