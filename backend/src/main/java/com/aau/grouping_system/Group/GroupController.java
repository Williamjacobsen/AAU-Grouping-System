package com.aau.grouping_system.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Utils.RequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated // enables method-level validation
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

	private Coordinator validateCoordinatorAccess(HttpServletRequest servlet) {
		return requirementService.requireUserCoordinatorExists(servlet);
	}

	private Student validateStudentAccess(HttpServletRequest servlet, String studentId) {
		Student authenticatedStudent = requirementService.requireUserStudentExists(servlet);
		Student targetStudent = requirementService.requireStudentExists(studentId);

		if (!authenticatedStudent.getId().equals(targetStudent.getId())) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Students can only perform operations on themselves");
		}

		return targetStudent;
	}

	private User validateUserAccess(HttpServletRequest servlet, String groupId) {
		User user = requirementService.requireUserExists(servlet);
		requirementService.requireGroupExists(groupId);
		return user;
	}

	@PostMapping("/{groupId}/accept-request/{studentId}")
	public ResponseEntity<String> acceptJoinRequest(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		validateCoordinatorAccess(servlet);

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
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId) {

		validateCoordinatorAccess(servlet);

		Group group = requirementService.requireGroupExists(groupId);

		CopyOnWriteArrayList<Student> joinRequestStudents = db.getStudents().getItems(group.getJoinRequestStudentIds());

		return ResponseEntity.ok(joinRequestStudents);
	}

	@PostMapping("/{groupId}/request-join/{studentId}")
	public ResponseEntity<String> requestToJoin(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Student student = validateStudentAccess(servlet, studentId);

		Group group = requirementService.requireGroupExists(groupId);

		try {
			groupService.requestToJoin(groupId, student);
			return ResponseEntity.ok("Join request submitted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to submit request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<Group> getGroup(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId) {

		validateUserAccess(servlet, groupId);

		Group group = requirementService.requireGroupExists(groupId);
		return ResponseEntity.ok(group);
	}

	/*
	 * public ResponseEntity<Map<String, Group>> getAllGroups() {
	 * return ResponseEntity.ok(db.getGroups().getAllItems());
	 * }
	 */

	// ---TEST------TEST------TEST------TEST---
	@GetMapping
	public ResponseEntity<Object> getAllGroups() {

		Map<String, Object> mockGroups = Map.of(
				"1", Map.of(
						"id", "1",
						"name", "Group 1",
						"project", "AI Chatbot",
						"members", List.of(
								Map.of("name", "Student 1", "priority1", "AI Chatbot", "priority2", "Web App", "priority3",
										"Data Science"))),
				"2", Map.of(
						"id", "2",
						"name", "Group 2",
						"project", "Web App",
						"members", List.of(
								Map.of("name", "Student 2", "priority1", "Web App", "priority2", "AI Chatbot", "priority3",
										"Game Design"),
								Map.of("name", "Student 3", "priority1", "Game Design", "priority2", "Web App", "priority3",
										"AI Chatbot"))),
				"3", Map.of(
						"id", "3",
						"name", "Group 3",
						"project", "Health Tracker",
						"members", List.of(
								Map.of("name", "Student 4", "priority1", "Health Tracker", "priority2", "AI Chatbot", "priority3",
										"IoT System"),
								Map.of("name", "Student 5", "priority1", "IoT System", "priority2", "Health Tracker", "priority3",
										"Game Design"),
								Map.of("name", "Student 6", "priority1", "Game Design", "priority2", "Health Tracker", "priority3",
										"AI Chatbot"))),
				"4", Map.of(
						"id", "4",
						"name", "Group 4",
						"project", "Smart Home",
						"members", List.of(
								Map.of("name", "Student 7", "priority1", "Smart Home", "priority2", "Web App", "priority3",
										"Health Tracker"),
								Map.of("name", "Student 8", "priority1", "Web App", "priority2", "Smart Home", "priority3",
										"AI Chatbot"),
								Map.of("name", "Student 9", "priority1", "Health Tracker", "priority2", "AI Chatbot", "priority3",
										"Smart Home"),
								Map.of("name", "Student 10", "priority1", "AI Chatbot", "priority2", "Web App", "priority3",
										"Smart Home"))),
				"5", Map.of(
						"id", "5",
						"name", "Group 5",
						"project", "Finance Dashboard",
						"members", List.of(
								Map.of("name", "Student 11", "priority1", "Finance Dashboard", "priority2", "AI Chatbot", "priority3",
										"Web App"),
								Map.of("name", "Student 12", "priority1", "AI Chatbot", "priority2", "Finance Dashboard", "priority3",
										"Health Tracker"),
								Map.of("name", "Student 13", "priority1", "Web App", "priority2", "Health Tracker", "priority3",
										"Finance Dashboard"),
								Map.of("name", "Student 14", "priority1", "Health Tracker", "priority2", "Web App", "priority3",
										"Finance Dashboard"),
								Map.of("name", "Student 15", "priority1", "Game Design", "priority2", "AI Chatbot", "priority3",
										"Finance Dashboard"))),
				"6", Map.of(
						"id", "6",
						"name", "Group 6",
						"project", "E-Commerce Site",
						"members", List.of(
								Map.of("name", "Student 16", "priority1", "E-Commerce Site", "priority2", "Finance Dashboard",
										"priority3", "Web App"),
								Map.of("name", "Student 17", "priority1", "Web App", "priority2", "E-Commerce Site", "priority3",
										"Smart Home"),
								Map.of("name", "Student 18", "priority1", "Finance Dashboard", "priority2", "Smart Home", "priority3",
										"E-Commerce Site"),
								Map.of("name", "Student 19", "priority1", "Health Tracker", "priority2", "Web App", "priority3",
										"E-Commerce Site"),
								Map.of("name", "Student 20", "priority1", "AI Chatbot", "priority2", "Smart Home", "priority3",
										"E-Commerce Site"),
								Map.of("name", "Student 21", "priority1", "Game Design", "priority2", "AI Chatbot", "priority3",
										"E-Commerce Site"))),
				"7", Map.of(
						"id", "7",
						"name", "Group 7",
						"project", "Game Design",
						"members", List.of(
								Map.of("name", "Student 22", "priority1", "Game Design", "priority2", "AI Chatbot", "priority3",
										"Web App"),
								Map.of("name", "Student 23", "priority1", "AI Chatbot", "priority2", "Game Design", "priority3",
										"Finance Dashboard"),
								Map.of("name", "Student 24", "priority1", "Finance Dashboard", "priority2", "AI Chatbot", "priority3",
										"Health Tracker"),
								Map.of("name", "Student 25", "priority1", "Web App", "priority2", "Game Design", "priority3",
										"Smart Home"),
								Map.of("name", "Student 26", "priority1", "Smart Home", "priority2", "Finance Dashboard", "priority3",
										"Web App"),
								Map.of("name", "Student 27", "priority1", "IoT System", "priority2", "Health Tracker", "priority3",
										"Smart Home"),
								Map.of("name", "Student 28", "priority1", "Health Tracker", "priority2", "Game Design", "priority3",
										"Finance Dashboard"))),
				"8", Map.of(
						"id", "8",
						"name", "Group 8",
						"project", "IoT System",
						"members", List.of(
								Map.of("name", "Student 29", "priority1", "IoT System", "priority2", "Smart Home", "priority3",
										"Web App"),
								Map.of("name", "Student 30", "priority1", "Smart Home", "priority2", "IoT System", "priority3",
										"Health Tracker"),
								Map.of("name", "Student 31", "priority1", "Health Tracker", "priority2", "Smart Home", "priority3",
										"IoT System"),
								Map.of("name", "Student 32", "priority1", "AI Chatbot", "priority2", "Finance Dashboard", "priority3",
										"IoT System"),
								Map.of("name", "Student 33", "priority1", "Finance Dashboard", "priority2", "IoT System", "priority3",
										"AI Chatbot"),
								Map.of("name", "Student 34", "priority1", "Game Design", "priority2", "AI Chatbot", "priority3",
										"IoT System"),
								Map.of("name", "Student 35", "priority1", "Web App", "priority2", "Finance Dashboard", "priority3",
										"IoT System"))));

		return ResponseEntity.ok(mockGroups);
	}

	// ---TEST------TEST------TEST------TEST---

	@PostMapping("/{groupId}/join/{studentId}")
	public ResponseEntity<String> joinGroup(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Student student = validateStudentAccess(servlet, studentId);

		Group group = requirementService.requireGroupExists(groupId);

		try {
			groupService.joinGroup(groupId, student);
			return ResponseEntity.ok("Successfully joined the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to join group: " + e.getMessage());
		}
	}

	@PostMapping("/{groupId}/leave/{studentId}")
	public ResponseEntity<String> leaveGroup(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Student student = validateStudentAccess(servlet, studentId);

		Group group = requirementService.requireGroupExists(groupId);

		try {
			groupService.leaveGroup(groupId, student);
			return ResponseEntity.ok("Successfully left the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to leave group: " + e.getMessage());
		}
	}

	@PostMapping("/{fromGroupId}/move-student/{toGroupId}/{studentId}")
	public ResponseEntity<String> moveStudentBetweenGroups(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String fromGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String toGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		validateCoordinatorAccess(servlet);

		try {
			Student student = requirementService.requireStudentExists(studentId);
			Group fromGroup = requirementService.requireGroupExists(fromGroupId);
			Group toGroup = requirementService.requireGroupExists(toGroupId);

			// Remove student from old group
			groupService.leaveGroup(fromGroupId, student);
			groupService.joinGroup(toGroupId, student);

			return ResponseEntity.ok("Student moved successfully.");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to move student: " + e.getMessage());
		}
	}

	@PostMapping("/{fromGroupId}/move-members/{toGroupId}")
	public ResponseEntity<String> moveAllMembersBetweenGroups(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String fromGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String toGroupId) {

		validateCoordinatorAccess(servlet);

		try {
			Group fromGroup = requirementService.requireGroupExists(fromGroupId);
			Group toGroup = requirementService.requireGroupExists(toGroupId);

			// Check group size limit
			if (toGroup.getStudentIds().size() + fromGroup.getStudentIds().size() > toGroup.getMaxStudents()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Target group is full");
			}

			// a copy of the student list, to avoid errors when modifying the original list
			// inside the loop
			for (String studentId : new ArrayList<>(fromGroup.getStudentIds())) {
				Student student = requirementService.requireStudentExists(studentId);
				groupService.leaveGroup(fromGroupId, student);
				groupService.joinGroup(toGroupId, student);
			}

			return ResponseEntity.ok("Members moved successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to move members: " + e.getMessage());
		}
	}

}
