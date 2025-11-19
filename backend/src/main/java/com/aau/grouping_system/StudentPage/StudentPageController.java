package com.aau.grouping_system.StudentPage;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated
@RequestMapping("/session/{sessionId}/student")
public class StudentPageController {

	private final RequestRequirementService requestRequirementService;
	private final StudentPageService studentPageService;

	public StudentPageController(
			RequestRequirementService requestRequirementService,
			StudentPageService studentPageService) {
		this.requestRequirementService = requestRequirementService;
		this.studentPageService = studentPageService;
	}

	private StudentPageService.StudentSessionData validateCoordinatorAndStudent(HttpServletRequest servlet, String sessionId,
			String studentId) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		StudentPageService.StudentSessionData validation = studentPageService.validateStudentAndSession(sessionId, studentId);
		
		// Validate coordinator is authorized for the session
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		return validation;
	}

	@GetMapping("/{studentId}")
	public ResponseEntity<StudentDetailsRecord> getStudentDetails(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		StudentPageService.StudentSessionData validation = validateCoordinatorAndStudent(servlet, sessionId, studentId);
		StudentDetailsRecord studentData = studentPageService.getStudentDetails(validation, true);
		return ResponseEntity.ok(studentData);
	}

	@GetMapping("/{studentId}/public")
	public ResponseEntity<StudentDetailsRecord> getStudentPublicDetails(
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		StudentPageService.StudentSessionData validation = studentPageService.validateStudentAndSession(sessionId, studentId);
		StudentDetailsRecord studentData = studentPageService.getStudentDetails(validation, false);
		return ResponseEntity.ok(studentData);
	}
	
	@DeleteMapping("/{studentId}")
	public ResponseEntity<String> removeStudent(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		StudentPageService.StudentSessionData validation = validateCoordinatorAndStudent(servlet, sessionId, studentId);
		studentPageService.removeStudent(validation);
		return ResponseEntity.ok("Student removed successfully");
	}

	@PostMapping("/{studentId}/reset-password")
	public ResponseEntity<String> resetStudentPassword(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		StudentPageService.StudentSessionData validation = validateCoordinatorAndStudent(servlet, sessionId, studentId);
		String result = studentPageService.resetStudentPassword(validation);
		return ResponseEntity.ok(result);
	}
}
