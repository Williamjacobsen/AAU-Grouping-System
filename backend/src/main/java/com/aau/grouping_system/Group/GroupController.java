package com.aau.grouping_system.Group;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Utils.RequestRequirementService;
import com.aau.grouping_system.SupervisorsPage.SupervisorsPageController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated // enables method-level validation
@RequestMapping("/groups")
public class GroupController {

	private final Database db;
	private final GroupService groupService;
	private final RequestRequirementService requestRequirementService;
	private final SupervisorsPageController supervisorsPageController;

	public GroupController(Database db, GroupService groupService, RequestRequirementService requestRequirementService,
			SupervisorsPageController supervisorsPageController) {
		this.db = db;
		this.groupService = groupService;
		this.requestRequirementService = requestRequirementService;
		this.supervisorsPageController = supervisorsPageController;
	}

	private Coordinator validateCoordinatorAccess(HttpServletRequest servlet) {
		return requestRequirementService.requireUserCoordinatorExists(servlet);
	}

	private Student validateStudentAccess(HttpServletRequest servlet, String studentId) {
		Student authenticatedStudent = requestRequirementService.requireUserStudentExists(servlet);
		Student targetStudent = requestRequirementService.requireStudentExists(studentId);

		if (!authenticatedStudent.getId().equals(targetStudent.getId())) {
			throw new RequestException(HttpStatus.FORBIDDEN, "Students can only perform operations on themselves");
		}

		return targetStudent;
	}

	private User validateUserAccess(HttpServletRequest servlet, String groupId) {
		User user = requestRequirementService.requireUserExists(servlet);
		requestRequirementService.requireGroupExists(groupId);
		return user;
	}

	@PostMapping("/{groupId}/accept-request/{studentId}")
	public ResponseEntity<String> acceptJoinRequest(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		validateCoordinatorAccess(servlet);

		Group group = requestRequirementService.requireGroupExists(groupId);
		Student student = requestRequirementService.requireStudentExists(studentId);

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

		Group group = requestRequirementService.requireGroupExists(groupId);

		CopyOnWriteArrayList<Student> joinRequestStudents = db.getStudents().getItems(group.getJoinRequestStudentIds());

		return ResponseEntity.ok(joinRequestStudents);
	}

	@PostMapping("/{groupId}/request-join/{studentId}")
	public ResponseEntity<String> requestToJoin(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Student student = validateStudentAccess(servlet, studentId);

		Group group = requestRequirementService.requireGroupExists(groupId);

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

		Group group = requestRequirementService.requireGroupExists(groupId);
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
		Map<String, Group> allGroups = db.getGroups().getAllItems();
		Map<String, Object> response = new LinkedHashMap<>();

		for (Map.Entry<String, Group> entry : allGroups.entrySet()) {
			Group group = entry.getValue();

			// Get project name safely
			String projectName = "No project";
			if (group.getProjectId() != null && !group.getProjectId().isEmpty()) {
				Project project = db.getProjects().getItem(group.getProjectId());
				if (project != null) {
					projectName = project.getName();
				}
			}

			// Build members list
			List<Map<String, Object>> membersList = new ArrayList<>();
			for (String studentId : group.getStudentIds()) {
				Student student = db.getStudents().getItem(studentId);
				if (student == null)
					continue;

				Map<String, Object> studentInfo = new LinkedHashMap<>();
				studentInfo.put("id", student.getId());
				studentInfo.put("name", student.getName());

				if (student.getQuestionnaire() != null) {
					studentInfo.put("priority1", student.getQuestionnaire().getDesiredProjectId1());
					studentInfo.put("priority2", student.getQuestionnaire().getDesiredProjectId2());
					studentInfo.put("priority3", student.getQuestionnaire().getDesiredProjectId3());
				}

				membersList.add(studentInfo);
			}

			// Build group data
			Map<String, Object> groupData = new LinkedHashMap<>();
			groupData.put("id", group.getId());
			groupData.put("name", "Group " + group.getId()); // if there’s no explicit name property
			groupData.put("project", projectName);
			groupData.put("maxStudents", group.getMaxStudents());
			groupData.put("members", membersList);
			groupData.put("supervisor", group.getSupervisorId());

			response.put(entry.getKey(), groupData);
		}

		return ResponseEntity.ok(response);
	}

	// ---TEST------TEST------TEST------TEST---

	@PostMapping("/{groupId}/join/{studentId}")
	public ResponseEntity<String> joinGroup(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Student student = validateStudentAccess(servlet, studentId);

		Group group = requestRequirementService.requireGroupExists(groupId);

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

		Group group = requestRequirementService.requireGroupExists(groupId);

		try {
			groupService.leaveGroup(groupId, student);
			return ResponseEntity.ok("Successfully left the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to leave group: " + e.getMessage());
		}
	}

	@PostMapping("/{fromGroupId}/move-student/{toGroupId}/{studentId}/{sessionId}")
	public ResponseEntity<String> moveStudentBetweenGroups(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String fromGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String toGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		validateCoordinatorAccess(servlet);

		try {
			Student student = requestRequirementService.requireStudentExists(studentId);
			Group fromGroup = db.getGroups().getItem(fromGroupId);
			Group toGroup = requestRequirementService.requireGroupExists(toGroupId);
			
			Session session = requestRequirementService.requireSessionExists(sessionId);
			int maxGroupSize = session.getMaxGroupSize();

			if (toGroup.getStudentIds().size() >= maxGroupSize) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body("Target group is full");
				}
			
			if (fromGroup != null) {

				// Remove student from old group and add student to new group
				groupService.leaveGroup(fromGroupId, student);
				groupService.joinGroup(toGroupId, student);

				return ResponseEntity.ok("Student moved successfully.");
			}
			groupService.joinGroup(toGroupId, student);
			
			return ResponseEntity.ok("Student moved successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to move student: " + e.getMessage());
		}
	}

	@PostMapping("/{fromGroupId}/move-members/{toGroupId}/{sessionId}")
	public ResponseEntity<String> moveAllMembersBetweenGroups(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String fromGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String toGroupId,
			@PathVariable String sessionId) {

		validateCoordinatorAccess(servlet);

		try {
			Group fromGroup = requestRequirementService.requireGroupExists(fromGroupId);
			Group toGroup = requestRequirementService.requireGroupExists(toGroupId);

			Session session = requestRequirementService.requireSessionExists(sessionId);
			int maxGroupSize = session.getMaxGroupSize();

			// Check group size limit
			if (toGroup.getStudentIds().size() + fromGroup.getStudentIds().size() > maxGroupSize) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Target group is full");
			}

			// a copy of the student list, to avoid errors when modifying the original list
			// inside the loop
			for (String studentId : new ArrayList<>(fromGroup.getStudentIds())) {
				Student student = requestRequirementService.requireStudentExists(studentId);
				groupService.leaveGroup(fromGroupId, student);
				groupService.joinGroup(toGroupId, student);
			}

			return ResponseEntity.ok("Members moved successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to move members: " + e.getMessage());
		}
	}

	@PostMapping("/{sessionId}/{groupId}/assign-supervisor/{supervisorId}")
	public ResponseEntity<String> assignSupervisorToGroup(
			HttpServletRequest servlet,
			@PathVariable String groupId,
			@PathVariable String supervisorId,
			@PathVariable String sessionId) {

		validateCoordinatorAccess(servlet);

		try {
			// Find the group
			Group group = requestRequirementService.requireGroupExists(groupId);

			// Find supervisor in the same session
			Session session = supervisorsPageController.validateSessionAccess(servlet, sessionId);
			Supervisor supervisor = supervisorsPageController.findSupervisorInSession(session, supervisorId);

			if (supervisor == null) {
				throw new RequestException(HttpStatus.NOT_FOUND, "Supervisor not found in this session");
			}

			// Assign supervisor
			group.setSupervisorId(supervisorId);

			return ResponseEntity.ok("Supervisor assigned successfully!");
		} catch (RequestException e) {
			return ResponseEntity.status(e.getStatus()).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to assign supervisor: " + e.getMessage());
		}
	}

}
