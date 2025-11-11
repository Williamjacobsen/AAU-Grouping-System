package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@Validated // enables method-level validation
@RequestMapping("/api/sessions/{sessionId}")
public class SessionSetupController {

	private final Database db;
	private final RequirementService requirementService;
	private final UserService userService;

	public SessionSetupController(
			Database db,
			RequirementService requirementService,
			UserService userService) {
		this.db = db;
		this.requirementService = requirementService;
		this.userService = userService;
	}

	record SendLoginCodeRecord(
			Boolean sendOnlyNew) {
	}

	// Suppress in-editor warnings about type safety violations because it isn't
	// true here because Java's invariance of generics.
	@SuppressWarnings("unchecked")
	@PostMapping("/sendLoginCodeToStudents")
	public ResponseEntity<String> sendLoginCodeToStudents(
			HttpServletRequest servlet,
			@PathVariable String sessionId,
			@RequestBody SendLoginCodeRecord record) {

		Session session = requirementService.requireSessionExists(sessionId);

		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		requirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		CopyOnWriteArrayList<User> students = (CopyOnWriteArrayList<User>) session.getStudents().getItems(db);
		if (record.sendOnlyNew) {
			students.removeIf(student -> student.getLoginCode() != null);
		}

		userService.applyAndEmailLoginCodes(session, students);

		return ResponseEntity.ok("Emails have been sent to students.");
	}

	// Suppress in-editor warnings about type safety violations because it isn't
	// true here because Java's invariance of generics.
	@SuppressWarnings("unchecked")
	@PostMapping("/sendLoginCodeToSupervisors")
	public ResponseEntity<String> sendLoginCodeToSupervisors(
			HttpServletRequest servlet,
			@PathVariable String sessionId,
			@RequestBody SendLoginCodeRecord record) {

		Session session = requirementService.requireSessionExists(sessionId);

		Coordinator coordinator = requirementService.requireUserCoordinatorExists(servlet);
		requirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		CopyOnWriteArrayList<User> supervisors = (CopyOnWriteArrayList<User>) session.getSupervisors().getItems(db);
		if (record.sendOnlyNew) {
			supervisors.removeIf(supervisor -> supervisor.getLoginCode() != null);
		}

		userService.applyAndEmailLoginCodes(session, supervisors);

		return ResponseEntity.ok("Emails have been sent to supervisors.");
	}

}
