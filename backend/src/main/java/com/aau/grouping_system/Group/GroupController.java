package com.aau.grouping_system.Group;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

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
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
import com.aau.grouping_system.Utils.RequestRequirementService;
import com.aau.grouping_system.SupervisorsPage.SupervisorsPageController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@Validated // enables method-level validation
@RequestMapping("/groups")
public class GroupController {

	private final Database db;
	private final GroupService groupService;
	private final RequestRequirementService requestRequirementService;

	public GroupController(Database db, GroupService groupService,
			RequestRequirementService requestRequirementService) {
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
		groupService.requireUserOwnsGroupOrIsCoordinator(group, user);

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

	@GetMapping("/{sessionId}/getGroups")
	public ResponseEntity<CopyOnWriteArrayList<Group>> getGroups(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		User user = requestRequirementService.requireUserExists(servlet);
		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);

		CopyOnWriteArrayList<Group> sessionGroups = db.getGroups().getItems(session.getGroups().getIds());

		return ResponseEntity.ok(sessionGroups);
	}

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

	@PostMapping("/{fromGroupId}/move-student/{toGroupId}/{studentId}/{sessionId}")
	public ResponseEntity<String> moveStudentBetweenGroups(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String fromGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String toGroupId,
			@NoDangerousCharacters @NotBlank @PathVariable String studentId,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		try {
			Student student = requestRequirementService.requireStudentExists(studentId);
			Group toGroup = requestRequirementService.requireGroupExists(toGroupId);

			requestRequirementService.requireSessionExists(sessionId);
			groupService.joinGroup(toGroup, student);

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

		requestRequirementService.requireUserCoordinatorExists(servlet);

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

				// Use safe leave (doesnt delete the group, if its empty)
				groupService.leaveGroupWithoutDeleting(fromGroup, student);
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

		try {
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

		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to create group: " + e.getMessage());
		}
	}

	private record ModifyGroupPreferencesRecord(
			@NoDangerousCharacters @NotBlank String name,
			@NotNull @Min(-1) Integer desiredGroupSizeMin,
			@NotNull @Min(-1) Integer desiredGroupSizeMax,
			@NoDangerousCharacters @NotNull String desiredProjectId1,
			@NoDangerousCharacters @NotNull String desiredProjectId2,
			@NoDangerousCharacters @NotNull String desiredProjectId3) {

		void applyGroupPreferences(Group group) {
			group.setName(name);
			group.setDesiredGroupSizeMin(desiredGroupSizeMin);
			group.setDesiredGroupSizeMax(desiredGroupSizeMax);
			group.setDesiredProjectId1(desiredProjectId1);
			group.setDesiredProjectId2(desiredProjectId2);
			group.setDesiredProjectId3(desiredProjectId3);
		}
	}

	@PostMapping("/{sessionId}/modifyGroupPreferences/{groupId}")
	public ResponseEntity<String> modifyGroupPreferences(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@Valid @RequestBody ModifyGroupPreferencesRecord record) {

		User user = requestRequirementService.requireUserExists(servlet);
		Session session = requestRequirementService.requireSessionExists(sessionId);
		Group group = requestRequirementService.requireGroupExists(groupId);

		requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);
		if (!group.getName().equals(record.name)) {
			groupService.requireGroupNameNotDuplicate(session, record.name);
		}
		groupService.requireUserOwnsGroupOrIsCoordinator(group, user);
		if (user.getRole() == User.Role.Student) {
			requestRequirementService.requireQuestionnaireDeadlineNotExceeded(session);
		}

		record.applyGroupPreferences(group);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Group preferences succesfully modified");
	}

	@PostMapping("/{sessionId}/modifyGroupSupervisor/{groupId}/{supervisorId}")
	public ResponseEntity<String> modifyGroupSupervisor(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupId,
			@NoDangerousCharacters @NotBlank @PathVariable String supervisorId) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		Group group = requestRequirementService.requireGroupExists(groupId);
		Supervisor supervisor = requestRequirementService.requireSupervisorExists(supervisorId);

		requestRequirementService.requireSessionExists(sessionId);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		group.setSupervisorId(supervisor != null ? supervisor.getId() : null);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("Group supervisor succesfully modified");
	}

	@PostMapping("/{sessionId}/modifyGroupAssignedProject/{groupId}/{projectId}")
	public ResponseEntity<String> modifyGroupAssignedProject(
			HttpServletRequest servlet,
			@PathVariable String sessionId,
			@PathVariable String groupId,
			@PathVariable String projectId) {

		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		Group group = requestRequirementService.requireGroupExists(groupId);
		group.setAssignedProjectId(projectId);

		return ResponseEntity.ok("Group assigned project successfully updated");
	}

	@PostMapping("/{sessionId}/createGroupWithStudent/{foundingStudentId}/{secondStudentId}/{groupName}")
	public ResponseEntity<String> createGroupWithStudent(HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId,
			@NoDangerousCharacters @NotBlank @PathVariable String foundingStudentId,
			@NoDangerousCharacters @NotBlank @PathVariable String secondStudentId,
			@NoDangerousCharacters @NotBlank @PathVariable String groupName) {

		try {
			User user = requestRequirementService.requireUserExists(servlet);
			Session session = requestRequirementService.requireSessionExists(sessionId);
			Student foundingMember = requestRequirementService.requireStudentExists(foundingStudentId);
			Student secondMember = requestRequirementService.requireStudentExists(secondStudentId);

			requestRequirementService.requireUserIsAuthorizedSession(sessionId, user);
			groupService.requireUserCanAssignFoundingMember(user, foundingMember);
			groupService.requireGroupNameNotDuplicate(session, groupName);

			Group newGroup = groupService.createGroupAndReturnObject(session, groupName, foundingMember);

			if (!foundingStudentId.equals(secondStudentId)) {
				groupService.joinGroup(newGroup, secondMember);
			}

			return ResponseEntity.status(HttpStatus.CREATED)
					.body("Group created with two students. Group ID: " + newGroup.getId());
		} catch (Exception e) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Failed to create group: " + e.getMessage());
		}
	}

}
