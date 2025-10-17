package com.aau.grouping_system.Group;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Student.Student;

import java.util.List;
import java.util.Map;

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
	public ResponseEntity<List<Student>> getJoinRequests(@PathVariable String groupId) {
		Group group = db.getGroups().getItem(groupId);

		if (group == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(group.getJoinRequests());
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
	public ResponseEntity<Map<String, Group>> getAllGroups() {
		return ResponseEntity.ok(db.getGroups().getAllItems());
	}

	@PostMapping
	public ResponseEntity<Group> createGroup(@RequestBody Group group) {
		try {
			db.getGroups().put(group);
			return ResponseEntity.ok(group);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

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
