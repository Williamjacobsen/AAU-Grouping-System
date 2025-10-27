package com.aau.grouping_system.User.Student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
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

	@PostMapping("/saveQuestionnaireAnswers")
	public ResponseEntity<String> saveQuestionnaireAnswers(HttpServletRequest request,
			@RequestBody Student.Questionnaire body) {

		Student student = authService.getStudentByUser(request);
		if (student == null) {
			return ResponseEntity.notFound().build();
		}

		studentService.applyQuestionnaireAnswers(student, body);

		return ResponseEntity.ok("Saved questionnaire answers successfully.");
	}
}
