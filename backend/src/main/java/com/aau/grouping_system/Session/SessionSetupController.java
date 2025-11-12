package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@Validated // enables method-level validation
@RequestMapping("/sessionSetup")
public class SessionSetupController {

	private final Database db;
	private final RequestRequirementService requestRequirementService;
	private final SessionSetupService sessionSetupService;
	private final UserService userService;

	public SessionSetupController(
			Database db,
			RequestRequirementService requestRequirementService,
			SessionSetupService sessionSetupService,
			UserService userService) {
		this.db = db;
		this.requestRequirementService = requestRequirementService;
		this.sessionSetupService = sessionSetupService;
		this.userService = userService;
	}

	@PostMapping("/{sessionId}/saveSetup")
	public ResponseEntity<String> saveSetup(
			HttpServletRequest httpRequest,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody SessionSetupRecord record) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(httpRequest);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		sessionSetupService.updateSessionSetup(session, record);

		return ResponseEntity.ok("Session setup saved successfully!");
	}

	record SendLoginCodeRecord(
			Boolean sendOnlyNew) {
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	@PostMapping("/{sessionId}/sendLoginCodeTo/students")
	public ResponseEntity<String> sendLoginCodeToStudents(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody SendLoginCodeRecord record) {

		Session session = requestRequirementService.requireSessionExists(sessionId);

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		CopyOnWriteArrayList<User> students = (CopyOnWriteArrayList<User>) session.getStudents().getItems(db);
		if (record.sendOnlyNew) {
			students.removeIf(student -> student.getLoginCode() != null);
		}

		userService.applyAndEmailLoginCodes(session, students);

		return ResponseEntity.ok("Emails have been sent to students.");
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	@PostMapping("/{sessionId}/sendLoginCodeTo/supervisors")
	public ResponseEntity<String> sendLoginCodeToSupervisors(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody SendLoginCodeRecord record) {

		Session session = requestRequirementService.requireSessionExists(sessionId);

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		CopyOnWriteArrayList<User> supervisors = (CopyOnWriteArrayList<User>) session.getSupervisors().getItems(db);
		if (record.sendOnlyNew) {
			supervisors.removeIf(supervisor -> supervisor.getLoginCode() != null);
		}

		userService.applyAndEmailLoginCodes(session, supervisors);

		return ResponseEntity.ok("Emails have been sent to supervisors.");
	}

}
