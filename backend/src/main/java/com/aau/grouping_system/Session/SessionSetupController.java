package com.aau.grouping_system.Session;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.SessionMember;
import com.aau.grouping_system.User.SessionMember.SessionMemberService;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@Validated // enables method-level validation
@RequestMapping("/api/sessionSetup")
public class SessionSetupController {

	private final Database db;
	private final RequestRequirementService requestRequirementService;
	private final SessionSetupService sessionSetupService;
	private final SessionMemberService sessionMemberService;

	public SessionSetupController(
			Database db,
			RequestRequirementService requestRequirementService,
			SessionSetupService sessionSetupService,
			SessionMemberService sessionMemberService) {
		this.db = db;
		this.requestRequirementService = requestRequirementService;
		this.sessionSetupService = sessionSetupService;
		this.sessionMemberService = sessionMemberService;
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

	// Record for sending new emails
	// sendOnlyNew: if true, only send codes to users without existing codes
	record emailNewPasswordsRecord(
			Boolean sendOnlyNew) {
	}

	private void emailNewPasswordsToSessionMembers(
			HttpServletRequest servlet,
			Session session,
			emailNewPasswordsRecord record,
			CopyOnWriteArrayList<? extends SessionMember> sessionMembers) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(session.getId(), coordinator);

		// If sendOnlyNew=true, do NOT re-send login codes to users who already have
		// one.
		if (record.sendOnlyNew) {
			sessionMembers.removeIf(sessionMember -> sessionMember.getHasBeenSentPassword());
		}

		sessionMemberService.applyAndEmailNewPasswords(session, sessionMembers);
	}

	// unchecked cast + CopyOnWriteArrayList:
	// getItems returns a raw CopyOnWriteArrayList from the database module.
	// We know the content is always User, so the cast is safe.
	@PostMapping("/{sessionId}/emailNewPasswordTo/students")
	public ResponseEntity<String> emailNewPasswordsToStudents(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody emailNewPasswordsRecord record) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		CopyOnWriteArrayList<Student> students = db.getStudents().getItems(session.getStudents().getIds());

		emailNewPasswordsToSessionMembers(servlet, session, record, students);

		return ResponseEntity.ok("Emails have been sent to students.");
	}

	@PostMapping("/{sessionId}/emailNewPasswordTo/supervisors")
	public ResponseEntity<String> emailNewPasswordsToSupervisors(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody emailNewPasswordsRecord record) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		CopyOnWriteArrayList<Supervisor> supervisors = db.getSupervisors().getItems(session.getSupervisors().getIds());

		emailNewPasswordsToSessionMembers(servlet, session, record, supervisors);

		return ResponseEntity.ok("Emails have been sent to supervisors.");
	}
}
