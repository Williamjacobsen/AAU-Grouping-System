package com.aau.grouping_system.Group;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.User.Student.Student;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/groups")
public class GroupController {

	private final Database db;
	private final GroupService groupService;

	public GroupController(Database db, GroupService groupService) {
		this.db = db;
		this.groupService = groupService;
	}

	private Group RequireGroupExists(String groupId) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new RequestException(HttpStatus.NOT_FOUND, "Group not found");
		}
		return group;
	}

	private Student RequireStudentExists(String studentId) {
		Student student = db.getStudents().getItem(studentId);
		if (student == null) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Student not found");
		}
		return student;
	}

	@PostMapping("/{groupId}/accept-request/{studentId}")
	public ResponseEntity<String> acceptJoinRequest(
			@PathVariable String groupId,
			@PathVariable String studentId) {

		Group group = RequireGroupExists(groupId);
		Student student = RequireStudentExists(studentId);

		try {
			groupService.acceptJoinRequest(groupId, student);
			return ResponseEntity.ok("Join request accepted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to accept request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}/requests")
	public ResponseEntity<CopyOnWriteArrayList<Student>> getJoinRequests(@PathVariable String groupId) {

		Group group = RequireGroupExists(groupId);

		CopyOnWriteArrayList<Student> joinRequestStudents = db.getStudents().getItems(group.getJoinRequestStudentIds());

		return ResponseEntity.ok(joinRequestStudents);
	}

	@PostMapping("/{groupId}/request-join/{studentId}")
	public ResponseEntity<String> requestToJoin(
			@PathVariable String groupId,
			@PathVariable String studentId) {

		Group group = RequireGroupExists(groupId);
		Student student = RequireStudentExists(studentId);

		try {
			groupService.requestToJoin(groupId, student);
			return ResponseEntity.ok("Join request submitted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to submit request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<Group> getGroup(@PathVariable String groupId) {
		Group group = RequireGroupExists(groupId);
		return ResponseEntity.ok(group);
	}

	@GetMapping
	public ResponseEntity<Map<String, Group>> getAllGroups() {
		return ResponseEntity.ok(db.getGroups().getAllItems());
	}

	@PostMapping("/{groupId}/join/{studentId}")
	public ResponseEntity<String> joinGroup(
			@PathVariable String groupId,
			@PathVariable String studentId) {

		Group group = RequireGroupExists(groupId);
		Student student = RequireStudentExists(studentId);

		try {
			groupService.joinGroup(groupId, student);
			return ResponseEntity.ok("Successfully joined the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to join group: " + e.getMessage());
		}
	}

	@PostMapping("/{groupId}/leave/{studentId}")
	public ResponseEntity<String> leaveGroup(
			@PathVariable String groupId,
			@PathVariable String studentId) {

		Group group = RequireGroupExists(groupId);
		Student student = RequireStudentExists(studentId);

		try {
			groupService.leaveGroup(groupId, student);
			return ResponseEntity.ok("Successfully left the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to leave group: " + e.getMessage());
		}
	}
}
