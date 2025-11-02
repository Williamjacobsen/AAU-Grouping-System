package com.aau.grouping_system.Group;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Student.Student;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/groups")
public class GroupController {

	private final Database db;
	private final GroupService groupService;

	public GroupController(Database db, GroupService groupService) {
		this.db = db;
		this.groupService = groupService;
	}

	@PostMapping("/{groupId}/accept-request/{studentId}")
	public ResponseEntity<String> acceptJoinRequest(
			@PathVariable String groupId,
			@PathVariable String studentId) {

		Group group = db.getGroups().getItem(groupId);
		Student student = db.getStudents().getItem(studentId);

		if (group == null) {
			return ResponseEntity.notFound().build();
		}

		if (student == null) {
			return ResponseEntity.badRequest().body("Student not found");
		}

		try {
			groupService.acceptJoinRequest(groupId, student);
			return ResponseEntity.ok("Join request accepted successfully");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to accept request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}/requests")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getJoinRequests(@PathVariable String groupId) {
		Group group = db.getGroups().getItem(groupId);

		if (group == null) {
			return ResponseEntity.notFound().build();
		}

		CopyOnWriteArrayList<Student> joinRequestStudents = db.getStudents().getItems(group.getJoinRequestStudentIds());

		return ResponseEntity.ok(joinRequestStudents);
	}

	@PostMapping("/{groupId}/request-join/{studentId}")
	public ResponseEntity<String> requestToJoin(
			@PathVariable String groupId,
			@PathVariable String studentId) {

		Group group = db.getGroups().getItem(groupId);
		Student student = db.getStudents().getItem(studentId);

		if (group == null) {
			return ResponseEntity.notFound().build();
		}

		if (student == null) {
			return ResponseEntity.badRequest().body("Student not found");
		}

		try {
			groupService.requestToJoin(groupId, student);
			return ResponseEntity.ok("Join request submitted successfully");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to submit request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<Group> getGroup(@PathVariable String groupId) {
		Group group = db.getGroups().getItem(groupId);

		if (group == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(group);
	}

	@GetMapping
	/* 
	public ResponseEntity<Map<String, Group>> getAllGroups() {
		return ResponseEntity.ok(db.getGroups().getAllItems());
	 }
*/

// ---TEST------TEST------TEST------TEST---
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
			@PathVariable String groupId,
			@PathVariable String studentId) {

		Group group = db.getGroups().getItem(groupId);
		Student student = db.getStudents().getItem(studentId);

		if (group == null) {
			return ResponseEntity.notFound().build();
		}

		if (student == null) {
			return ResponseEntity.badRequest().body("Student not found");
		}

		try {
			groupService.joinGroup(groupId, student);
			return ResponseEntity.ok("Successfully joined the group");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to join group: " + e.getMessage());
		}
	}

	@PostMapping("/{groupId}/leave/{studentId}")
	public ResponseEntity<String> leaveGroup(
			@PathVariable String groupId,
			@PathVariable String studentId) {

		Group group = db.getGroups().getItem(groupId);
		Student student = db.getStudents().getItem(studentId);

		if (group == null) {
			return ResponseEntity.notFound().build();
		}

		if (student == null) {
			return ResponseEntity.badRequest().body("Student not found");
		}

		try {
			groupService.leaveGroup(groupId, student);
			return ResponseEntity.ok("Successfully left the group");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to leave group: " + e.getMessage());
		}
	}
}
