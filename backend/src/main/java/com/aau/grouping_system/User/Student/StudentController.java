package com.aau.grouping_system.User.Student;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Session.SessionService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/student")
public class StudentController {

	private final Database db;
	private final StudentService studentService;
	private final AuthService authService;
	private final SessionService sessionService;

	public StudentController(Database db, StudentService studentService, AuthService authService,
			SessionService sessionService) {
		this.db = db;
		this.studentService = studentService;
		this.authService = authService;
		this.sessionService = sessionService;
	}

	@PostMapping("/saveQuestionnaireAnswers")
	public ResponseEntity<String> saveQuestionnaireAnswers(HttpServletRequest request,
			@RequestBody Student.Questionnaire body) {

		Student student = authService.getStudentByUser(request);
		if (student == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Student not found");
		}

		Session session = db.getSessions().getItem(student.getSessionId());
		if (sessionService.isQuestionnaireDeadlineExceeded(session)) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Questionnaire submission deadline exceeded.");
		}

		studentService.applyQuestionnaireAnswers(student, body);

		return ResponseEntity.ok("Saved questionnaire answers successfully.");
	}

	@PostMapping("/create")
	public ResponseEntity<String> createStudent(@RequestBody Map<String, String> body) {
		String sessionId = body.get("sessionId");
		String email = body.get("email");
		String password = body.get("password");
		String name = body.get("name");

		if (sessionId == null || email == null || password == null || name == null) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Missing required fields: sessionId, email, password, name");
		}

		Session session = db.getSessions().getItem(sessionId);
		if (session == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Session not found");
		}

		Student student = studentService.addStudent(session, email, password, name);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Student created successfully with ID: " + student.getId());
	}
}
