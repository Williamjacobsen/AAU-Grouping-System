package com.aau.grouping_system.Group;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
@RequestMapping("/groups")
public class GroupController {

	private final Database db;
	private final GroupService groupService;
	private final RequirementService requirementService;

	public GroupController(Database db, GroupService groupService, RequirementService requirementService) {
		this.db = db;
		this.groupService = groupService;
		this.requirementService = requirementService;
	}

	@PostMapping("/{groupId}/accept-request/{studentId}")
	public ResponseEntity<String> acceptJoinRequest(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		User user = requirementService.requireUserExists(servlet);
		
		Group group = requirementService.requireGroupExists(groupId);
		
		if (user.getRole() != User.Role.Supervisor || !user.getId().equals(group.getSupervisorId())) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Only the group's supervisor can accept join requests");
		}

		Student student = requirementService.requireStudentExists(studentId);

		try {
			groupService.acceptJoinRequest(groupId, student);
			return ResponseEntity.ok("Join request accepted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to accept request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}/requests")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getJoinRequests(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId) {

		User user = requirementService.requireUserExists(servlet);
		
		Group group = requirementService.requireGroupExists(groupId);
		
		if (user.getRole() != User.Role.Supervisor || !user.getId().equals(group.getSupervisorId())) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Only the group's supervisor can view join requests");
		}

		CopyOnWriteArrayList<Student> joinRequestStudents = db.getStudents().getItems(group.getJoinRequestStudentIds());

		return ResponseEntity.ok(joinRequestStudents);
	}

	@PostMapping("/{groupId}/request-join/{studentId}")
	public ResponseEntity<String> requestToJoin(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Student authenticatedStudent = requirementService.requireUserStudentExists(servlet);
		
		if (!authenticatedStudent.getId().equals(studentId)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Students can only request to join groups for themselves");
		}

		requirementService.requireGroupExists(groupId);

		try {
			groupService.requestToJoin(groupId, authenticatedStudent);
			return ResponseEntity.ok("Join request submitted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to submit request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<Group> getGroup(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId) {
		
		User user = requirementService.requireUserExists(servlet);
		
		Group group = requirementService.requireGroupExists(groupId);
		
		Supervisor supervisor = db.getSupervisors().getItem(group.getSupervisorId());
		if (supervisor == null) {
			throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, "Group supervisor not found");
		}
		
		String sessionId = supervisor.getSessionId();
		requirementService.requireUserIsAuthorizedSession(sessionId, user);
		
		return ResponseEntity.ok(group);
	}

	@GetMapping
	public ResponseEntity<Map<String, Group>> getAllGroups(HttpServletRequest servlet) {
		
		requirementService.requireUserCoordinatorExists(servlet);
		
		return ResponseEntity.ok(db.getGroups().getAllItems());
	}

	@PostMapping("/{groupId}/join/{studentId}")
	public ResponseEntity<String> joinGroup(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Student authenticatedStudent = requirementService.requireUserStudentExists(servlet);
		
		if (!authenticatedStudent.getId().equals(studentId)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Students can only join groups for themselves");
		}

		requirementService.requireGroupExists(groupId);

		try {
			groupService.joinGroup(groupId, authenticatedStudent);
			return ResponseEntity.ok("Successfully joined the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to join group: " + e.getMessage());
		}
	}

	@PostMapping("/{groupId}/leave/{studentId}")
	public ResponseEntity<String> leaveGroup(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Student authenticatedStudent = requirementService.requireUserStudentExists(servlet);
		
		if (!authenticatedStudent.getId().equals(studentId)) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Students can only leave groups for themselves");
		}

		requirementService.requireGroupExists(groupId);

		try {
			groupService.leaveGroup(groupId, authenticatedStudent);
			return ResponseEntity.ok("Successfully left the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to leave group: " + e.getMessage());
		}
	}
}
