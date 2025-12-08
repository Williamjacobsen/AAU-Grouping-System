package com.aau.grouping_system.User.SessionMember.Student;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.SessionMemberService;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
@Validated // enables method-level validation
@RequestMapping("/api/student")
public class StudentController {

	private final StudentService studentService;
	private final RequestRequirementService requestRequirementService;
	private final SessionMemberService sessionMemberService;

	public StudentController(
			StudentService studentService,
			RequestRequirementService requestRequirementService,
			SessionMemberService sessionMemberService) {
		this.studentService = studentService;
		this.requestRequirementService = requestRequirementService;
		this.sessionMemberService = sessionMemberService;
	}

	@GetMapping("/{studentId}")
	public ResponseEntity<Student> getStudent(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		User user = requestRequirementService.requireUserExists(servlet);
		Student student = requestRequirementService.requireStudentExists(studentId);

		requestRequirementService.requireUserIsAuthorizedSession(student.getSessionId(), user);

		return ResponseEntity.ok(student);
	}

	@DeleteMapping("/{studentId}")
	public ResponseEntity<String> removeStudent(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Coordinator coordinatorUser = requestRequirementService.requireUserCoordinatorExists(servlet);
		Student student = requestRequirementService.requireStudentExists(studentId);

		requestRequirementService.requireCoordinatorIsAuthorizedSession(student.getSessionId(), coordinatorUser);

		studentService.removeStudent(student);

		return ResponseEntity.ok("Successfully removed student");
	}

	@PostMapping("/{studentId}/reset-password")
	public ResponseEntity<String> resetStudentPassword(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Coordinator userCoordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		Student student = requestRequirementService.requireStudentExists(studentId);
		Session session = requestRequirementService.requireSessionExists(student.getSessionId());

		requestRequirementService.requireCoordinatorIsAuthorizedSession(student.getSessionId(), userCoordinator);

		sessionMemberService.applyAndEmailNewPassword(session, student);

		return ResponseEntity.ok("Successfully sent a new password to the student");
	}

	@PostMapping("/saveQuestionnaireAnswers")
	public ResponseEntity<String> saveQuestionnaireAnswers(
			HttpServletRequest servlet,
			@Valid @RequestBody StudentQuestionnaireRecord record) {

		Student userStudent = requestRequirementService.requireUserStudentExists(servlet);
		Session session = requestRequirementService.requireSessionExists(userStudent.getSessionId());

		requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);

		studentService.applyQuestionnaireAnswers(userStudent, record.toQuestionnaire());

		return ResponseEntity.ok("Saved questionnaire answers successfully.");
	}
}
