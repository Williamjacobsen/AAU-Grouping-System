package com.aau.grouping_system.Group;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Utils.RequirementService;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.validation.constraints.*;

@RestController
@Validated // enables method-level validation
@RequestMapping("/groups")
public class GroupController {

	// TODO: This lacks user authentication.

	private final Database db;
	private final GroupService groupService;
	private final RequirementService requirementService;

	public GroupController(Database db, GroupService groupService, RequirementService requirementService) {
		this.db = db;
		this.groupService = groupService;
		this.requirementService = requirementService;
	}

	@PostMapping("/{groupId}/accept-request/{studentId}")
	public ResponseEntity<String> acceptJoinRequest(
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Group group = requirementService.requireGroupExists(groupId);
		Student student = requirementService.requireStudentExists(studentId);

		try {
			groupService.acceptJoinRequest(groupId, student);
			return ResponseEntity.ok("Join request accepted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to accept request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}/requests")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getJoinRequests(
			@NoDangerousCharacters @NotBlank @PathVariable String groupId) {

		Group group = requirementService.requireGroupExists(groupId);

		CopyOnWriteArrayList<Student> joinRequestStudents = db.getStudents().getItems(group.getJoinRequestStudentIds());

		return ResponseEntity.ok(joinRequestStudents);
	}

	@PostMapping("/{groupId}/request-join/{studentId}")
	public ResponseEntity<String> requestToJoin(
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Group group = requirementService.requireGroupExists(groupId);
		Student student = requirementService.requireStudentExists(studentId);

		try {
			groupService.requestToJoin(groupId, student);
			return ResponseEntity.ok("Join request submitted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to submit request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<Group> getGroup(@NoDangerousCharacters @NotBlank @PathVariable String groupId) {
		Group group = requirementService.requireGroupExists(groupId);
		return ResponseEntity.ok(group);
	}

	@GetMapping
	public ResponseEntity<Map<String, Group>> getAllGroups() {
		return ResponseEntity.ok(db.getGroups().getAllItems());
	}

	@PostMapping("/{groupId}/join/{studentId}")
	public ResponseEntity<String> joinGroup(
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Group group = requirementService.requireGroupExists(groupId);
		Student student = requirementService.requireStudentExists(studentId);

		try {
			groupService.joinGroup(groupId, student);
			return ResponseEntity.ok("Successfully joined the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to join group: " + e.getMessage());
		}
	}

	@PostMapping("/{groupId}/leave/{studentId}")
	public ResponseEntity<String> leaveGroup(
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Group group = requirementService.requireGroupExists(groupId);
		Student student = requirementService.requireStudentExists(studentId);

		try {
			groupService.leaveGroup(groupId, student);
			return ResponseEntity.ok("Successfully left the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to leave group: " + e.getMessage());
		}
	}
}
