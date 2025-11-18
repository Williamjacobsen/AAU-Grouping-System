package com.aau.grouping_system.Group;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated // enables method-level validation
@RequestMapping("/groups")
public class GroupController {

	private final Database db;
	private final GroupService groupService;
	private final RequestRequirementService requestRequirementService;

	public GroupController(Database db, GroupService groupService, RequestRequirementService requestRequirementService) {
		this.db = db;
		this.groupService = groupService;
		this.requestRequirementService = requestRequirementService;
	}

	@PostMapping("/{sessionId}/{groupId}/accept-request/{studentId}")
	public ResponseEntity<String> acceptJoinRequest(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		User user = requestRequirementService.requireUserExists(servlet);
		Group group = requestRequirementService.requireGroupExists(groupId);
		Student requestingStudent = requestRequirementService.requireStudentExists(studentId);
		Session session = requestRequirementService.requireSessionExists(sessionId);

		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);
		requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);
		groupService.requireUserOwnsGroup(group, user);

		try {
			groupService.acceptJoinRequest(group, requestingStudent);
			return ResponseEntity.ok("Join request accepted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to accept request: " + e.getMessage());
		}
	}

	@PostMapping("/{sessionId}/{groupId}/request-to-join")
	public ResponseEntity<String> requestToJoin(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId) {

		Student requestingStudentUser = requestRequirementService.requireUserStudentExists(servlet);
		Group group = requestRequirementService.requireGroupExists(groupId);
		Session session = requestRequirementService.requireSessionExists(sessionId);

		requestRequirementService.requireUserIsAuthorizedSession(sessionId, requestingStudentUser);
		requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);

		try {
			groupService.requestToJoin(group, requestingStudentUser);
			return ResponseEntity.ok("Join request submitted successfully");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to submit request: " + e.getMessage());
		}
	}

	@GetMapping("/{groupId}")
	public ResponseEntity<Group> getGroup(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId) {

		User user = requestRequirementService.requireUserExists(servlet);

		requestRequirementService.requireGroupExists(groupId);

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
			groupData.put("members", membersList);

			response.put(entry.getKey(), groupData);
		}

		return ResponseEntity.ok(response);
	}

	// ---TEST------TEST------TEST------TEST---

	@PostMapping("/{sessionId}/{groupId}/leave/{studentId}")
	public ResponseEntity<String> leaveGroup(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		User user = requestRequirementService.requireUserExists(servlet);

		// Coordinators are allowed to make students leave, but students are only
		// allowed to make themselves leave
		if (user.getRole() == User.Role.Coordinator) {
			requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, (Coordinator) user);
		} else {
			requestRequirementService.requireStudentExists(user.getId());
			if (!user.getId().equals(studentId)) {
				throw new RequestException(HttpStatus.UNAUTHORIZED, "Student users cannot make other students leave a group.");
			}
			Session session = requestRequirementService.requireSessionExists(sessionId);
			requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);
		}

		Group group = requestRequirementService.requireGroupExists(groupId);
		Student student = requestRequirementService.requireStudentExists(studentId);

		try {
			groupService.leaveGroup(group, student);
			return ResponseEntity.ok("Successfully left the group");
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to leave group: " + e.getMessage());
		}
	}

	@PostMapping("/{sessionId}/{fromGroupId}/move-student/{toGroupId}/{studentId}")
	public ResponseEntity<String> moveStudentBetweenGroups(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String fromGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String toGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		try {
			Student student = requestRequirementService.requireStudentExists(studentId);
			Group fromGroup = requestRequirementService.requireGroupExists(fromGroupId);
			Group toGroup = requestRequirementService.requireGroupExists(toGroupId);

			if (toGroup.getStudentIds().size() >= 7) {// Default is max = 7, needs to change so that it gets the number from
																								// the max students session setup page
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("Target group is full");
			}

			// Remove student from old group
			groupService.leaveGroup(fromGroup, student);
			groupService.joinGroup(toGroup, student);

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

		requestRequirementService.requireUserCoordinatorExists(servlet);

		try {
			Group fromGroup = requestRequirementService.requireGroupExists(fromGroupId);
			Group toGroup = requestRequirementService.requireGroupExists(toGroupId);

			// Check group size limit
			if (toGroup.getStudentIds().size() + fromGroup.getStudentIds().size() > 7) {// Default is max = 7, needs to change
																																									// so that it gets the number from the
																																									// max students session setup page
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Target group is full");
			}

			// a copy of the student list, to avoid errors when modifying the original list
			// inside the loop
			for (String studentId : new ArrayList<>(fromGroup.getStudentIds())) {
				Student student = requestRequirementService.requireStudentExists(studentId);
				groupService.leaveGroup(fromGroup, student);
				groupService.joinGroup(toGroup, student);
			}

			return ResponseEntity.ok("Members moved successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Failed to move members: " + e.getMessage());
		}
	}

	@PostMapping("/cancelJoinRequest")
	public ResponseEntity<String> cancelJoinRequest(HttpServletRequest servlet) {

		Student student = requestRequirementService.requireUserStudentExists(servlet);

		groupService.cancelJoinRequest(student);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Group join request succesfully canceled");
	}

	private record CreateGroupRecord(
			@NoDangerousCharacters @NotBlank String name,
			@NoDangerousCharacters @NotBlank String studentId) {
	}

	@PostMapping("/{sessionId}/createGroup")
	public ResponseEntity<String> createGroup(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@Valid @RequestBody CreateGroupRecord record) {

		User user = requestRequirementService.requireUserExists(servlet);
		Session session = requestRequirementService.requireSessionExists(sessionId);
		Student foundingMember = requestRequirementService.requireStudentExists(record.studentId);

		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);
		groupService.requireUserCanAssignFoundingMember(user, foundingMember);
		groupService.requireGroupNameNotDuplicate(session, record.name);

		groupService.createGroup(session, record.name, foundingMember);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body("Group succesfully created");
	}

	private record ModifyGroupNameRecord(
			@NoDangerousCharacters @NotBlank String newName) {
	}

	@PostMapping("/{sessionId}/modifyGroupName/{groupId}")
	public ResponseEntity<String> modifyGroupName(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@Valid @RequestBody ModifyGroupNameRecord record) {

		User user = requestRequirementService.requireUserExists(servlet);
		Session session = requestRequirementService.requireSessionExists(sessionId);
		Group group = requestRequirementService.requireGroupExists(groupId);

		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);
		groupService.requireGroupNameNotDuplicate(session, record.newName);
		groupService.requireUserOwnsGroup(group, user);
		if (user.getRole() == User.Role.Student) {
			requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);
		}

		groupService.modifyGroupName(group, record.newName);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Group name succesfully modified");
	}

	private record ModifyGroupProjectRecord(
			@NoDangerousCharacters String newProjectId) {
	}

	@PostMapping("/{sessionId}/modifyGroupProject/{groupId}")
	public ResponseEntity<String> modifyGroupProject(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@Valid @RequestBody ModifyGroupProjectRecord record) {

		User user = requestRequirementService.requireUserExists(servlet);
		Session session = requestRequirementService.requireSessionExists(sessionId);
		Group group = requestRequirementService.requireGroupExists(groupId);

		Project project;
		if (record.newProjectId == null) {
			project = null;
		} else {
			project = requestRequirementService.requireProjectExists(record.newProjectId);
		}

		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);
		groupService.requireUserOwnsGroup(group, user);
		if (user.getRole() == User.Role.Student) {
			requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);
		}

		groupService.modifyGroupProject(group, project);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Group project succesfully modified");
	}

	private record ModifyGroupSupervisorRecord(
			@NoDangerousCharacters @NotBlank String newSupervisorId) {
	}

	@PostMapping("/{sessionId}/modifyGroupSupervisor/{groupId}")
	public ResponseEntity<String> modifyGroupSupervisor(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@Valid @RequestBody ModifyGroupSupervisorRecord record) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		Session session = requestRequirementService.requireSessionExists(sessionId);
		Group group = requestRequirementService.requireGroupExists(groupId);
		Supervisor supervisor = requestRequirementService.requireSupervisorExists(record.newSupervisorId);

		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		groupService.modifyGroupSupervisor(group, supervisor);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Group supervisor succesfully modified");
	}

}
