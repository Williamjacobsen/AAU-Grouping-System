package com.aau.grouping_system.SupervisorsPage;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated
@RequestMapping("/sessions/{sessionId}/supervisors")
public class SupervisorsPageController {

	private final RequestRequirementService requestRequirementService;
	private final SupervisorsPageService supervisorsPageService;

	public SupervisorsPageController(
			RequestRequirementService requestRequirementService,
			SupervisorsPageService supervisorsPageService) {
		this.requestRequirementService = requestRequirementService;
		this.supervisorsPageService = supervisorsPageService;
	}

	public Session validateSessionAccess(HttpServletRequest servlet, String sessionId) {
		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		Session session = requestRequirementService.requireSessionExists(sessionId);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);
		return session;
	}

	@GetMapping
	public ResponseEntity<List<Map<String, Object>>> getSupervisors(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {
		Session session = validateSessionAccess(servlet, sessionId);
		List<Map<String, Object>> supervisorList = supervisorsPageService.getFormattedSupervisors(session);
		return ResponseEntity.ok(supervisorList);
	}

	@PostMapping
	public ResponseEntity<String> addSupervisor(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody SupervisorsPageService.AddSupervisorRequest record) {

		Session session = validateSessionAccess(servlet, sessionId);
		String result = supervisorsPageService.addSupervisor(session, record);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	@DeleteMapping("/{supervisorId}")
	public ResponseEntity<String> removeSupervisor(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String supervisorId) {
		validateSessionAccess(servlet, sessionId);
	
		Supervisor supervisor = requestRequirementService.requireSupervisorExists(supervisorId);
		requestRequirementService.requireSupervisorIsAuthorizedSession(sessionId, supervisor);

		supervisorsPageService.removeSupervisor(supervisorId);
		return ResponseEntity.ok("Supervisor removed successfully");
	}

	@PostMapping("/{supervisorId}/send-new-password")
	public ResponseEntity<String> sendNewPassword(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String supervisorId) {

		Session session = validateSessionAccess(servlet, sessionId);
	
		Supervisor supervisor = requestRequirementService.requireSupervisorExists(supervisorId);
		requestRequirementService.requireSupervisorIsAuthorizedSession(sessionId, supervisor);

		String result = supervisorsPageService.sendNewPassword(session, supervisor);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/{supervisorId}/max-groups")
	public ResponseEntity<String> updateSupervisorMaxGroups(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String supervisorId,
			@Valid @RequestBody UpdateMaxGroupsRequest request) {

		validateSessionAccess(servlet, sessionId);
	
		Supervisor supervisor = requestRequirementService.requireSupervisorExists(supervisorId);
		requestRequirementService.requireSupervisorIsAuthorizedSession(sessionId, supervisor);

		supervisor.setMaxGroups(request.maxGroups);
		return ResponseEntity.ok("Supervisor max groups updated successfully");
	}

	public record UpdateMaxGroupsRequest(
			@jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(100) Integer maxGroups) {
	}

	public Supervisor findSupervisorInSession(Session session, String supervisorId) {
		Supervisor supervisor = requestRequirementService.requireSupervisorExists(supervisorId);
		
		// Check if the supervisor belongs to this session
		if (!supervisor.getSessionId().equals(session.getId())) {
			return null;
		}
		
		return supervisor;
	}
}
