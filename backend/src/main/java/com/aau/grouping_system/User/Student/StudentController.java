package com.aau.grouping_system.User.Student;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Authentication.AuthService;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.Session.SessionService;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.InputValidation.NoWhitespace;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
@Validated // enables method-level validation
@RequestMapping("/student")
public class StudentController {

	private final Database db;
	private final StudentService studentService;
	private final AuthService authService;
	private final SessionService sessionService;
	private final RequirementService requirementService;

	public StudentController(Database db, StudentService studentService, AuthService authService,
			SessionService sessionService, RequirementService requirementService) {
		this.db = db;
		this.studentService = studentService;
		this.authService = authService;
		this.sessionService = sessionService;
		this.requirementService = requirementService;
	}

	@PostMapping("/saveQuestionnaireAnswers")
	public ResponseEntity<String> saveQuestionnaireAnswers(HttpServletRequest servlet,
			@Valid @RequestBody StudentQuestionnaireRecord record) {

		Student student = authService.getStudentByUser(servlet);
		if (student == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Student not found");
		}

		Session session = db.getSessions().getItem(student.getSessionId());
		if (sessionService.isQuestionnaireDeadlineExceeded(session)) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Questionnaire submission deadline exceeded.");
		}

		studentService.applyQuestionnaireAnswers(student, record.toQuestionnaire());

		return ResponseEntity.ok("Saved questionnaire answers successfully.");
	}

	private record CreateStudentRecord(
			@NoDangerousCharacters @NotBlank String sessionId,
			@NoDangerousCharacters @NotBlank @NoWhitespace @Email String email,
			@NoDangerousCharacters @NotBlank @NoWhitespace String password,
			@NoDangerousCharacters @NotBlank String name) {
	}

	@PostMapping("/create")
	public ResponseEntity<String> createStudent(@Valid @RequestBody CreateStudentRecord record) {

		Session session = db.getSessions().getItem(record.sessionId);
		if (session == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Session not found");
		}

		Student student = studentService.addStudent(session, record.email, record.password, record.name);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body("Student created successfully with ID: " + student.getId());
	}
}
