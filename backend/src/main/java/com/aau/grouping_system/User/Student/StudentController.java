package com.aau.grouping_system.User.Student;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Session.SessionService;
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
			return ResponseEntity.notFound().build();
		}

		Session session = db.getSessions().getItem(student.getSessionId());
		if (sessionService.isQuestionnaireDeadlineExceeded(session)) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Unauthorized: Questionnaire submission deadline exceeded.");
		}

		studentService.applyQuestionnaireAnswers(student, body);

		return ResponseEntity.ok("Saved questionnaire answers successfully.");
	}
}
