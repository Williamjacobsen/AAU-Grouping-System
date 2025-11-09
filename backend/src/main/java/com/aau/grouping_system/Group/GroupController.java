package com.aau.grouping_system.Group;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Utils.RequirementService;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@CrossOrigin(origins = "http://localhost:3000")
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

		Group group = requirementService.RequireGroupExists(groupId);
		Student student = requirementService.RequireStudentExists(studentId);

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

		Group group = requirementService.RequireGroupExists(groupId);

		CopyOnWriteArrayList<Student> joinRequestStudents = db.getStudents().getItems(group.getJoinRequestStudentIds());

		return ResponseEntity.ok(joinRequestStudents);
	}

	@PostMapping("/{groupId}/request-join/{studentId}")
	public ResponseEntity<String> requestToJoin(
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Group group = requirementService.RequireGroupExists(groupId);
		Student student = requirementService.RequireStudentExists(studentId);

		try {
			groupService.requestToJoin(groupId, student);
			return ResponseEntity.ok("Join request submitted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to submit request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<Group> getGroup(@NoDangerousCharacters @NotBlank @PathVariable String groupId) {
		Group group = requirementService.RequireGroupExists(groupId);
		return ResponseEntity.ok(group);
	}

	/* 
	public ResponseEntity<Map<String, Group>> getAllGroups() {
		return ResponseEntity.ok(db.getGroups().getAllItems());
		}
		*/
		
		// ---TEST------TEST------TEST------TEST---
@GetMapping
public ResponseEntity<Object> getAllGroups() {

    Map<String, Object> mockGroups = Map.of(
        "1", Map.of(
            "id", "1",
            "name", "Group 1",
            "members", List.of("Student 1", "Student 2", "Student 3", "Student 4", "Student 5", "Student 6", "Student 7")
        ),
        "2", Map.of(
            "id", "2",
            "name", "Group 2",
            "members", List.of("Student 8", "Student 9", "Student 10", "Student 11", "Student 12", "Student 13")
        ),
        "3", Map.of(
            "id", "3",
            "name", "Group 3",
            "members", List.of("Student 14", "Student 15", "Student 16", "Student 17", "Student 18")
        ),
        "4", Map.of(
            "id", "4",
            "name", "Group 4",
            "members", List.of("Student 19", "Student 20", "Student 21")
        ),
        "5", Map.of(
            "id", "5",
            "name", "Group 5",
            "members", List.of("Student 22", "Student 23")
        )
    );
    return ResponseEntity.ok(mockGroups);
}
// ---TEST------TEST------TEST------TEST---

	@PostMapping("/{groupId}/join/{studentId}")
	public ResponseEntity<String> joinGroup(
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Group group = requirementService.RequireGroupExists(groupId);
		Student student = requirementService.RequireStudentExists(studentId);

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

		Group group = requirementService.RequireGroupExists(groupId);
		Student student = requirementService.RequireStudentExists(studentId);

		try {
			groupService.leaveGroup(groupId, student);
			return ResponseEntity.ok("Successfully left the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to leave group: " + e.getMessage());
		}
	}
}
