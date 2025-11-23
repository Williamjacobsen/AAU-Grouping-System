package com.aau.grouping_system.EmailSystem;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

@RestController
@Validated
public class NotifyParticipants {

	private final Database db;
	private final RequestRequirementService requestRequirementService;
	private final EmailService emailService;

	public NotifyParticipants(
			RequestRequirementService requestRequirementService,
			EmailService emailService,
			Database db) {
		this.requestRequirementService = requestRequirementService;
		this.emailService = emailService;
		this.db = db;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/{sessionId}/notify")
	public ResponseEntity<String> notifyParticipants(
			HttpServletRequest servlet,
			@NoDangerousCharacters @NotBlank @PathVariable String sessionId) {

		Session session = requestRequirementService.requireSessionExists(sessionId);
		Coordinator coordinator = requestRequirementService.requireUserCoordinatorExists(servlet);
		requestRequirementService.requireCoordinatorIsAuthorizedSession(sessionId, coordinator);

		CopyOnWriteArrayList<User> students = (CopyOnWriteArrayList<User>) session.getStudents().getItems(db);
		CopyOnWriteArrayList<User> supervisors = (CopyOnWriteArrayList<User>) session.getSupervisors().getItems(db);
		CopyOnWriteArrayList<Group> groups = (CopyOnWriteArrayList<Group>) session.getGroups().getItems(db);

		String subject = String.format("Groups ready for session: %s", session.getName());

		try {
			// Notify students
			for (User student : students) {
				if (student.getEmail() == null || student.getEmail().isBlank())
					continue;

				String studentName = (student.getName() == null || student.getName().isBlank())
						? "Student"
						: student.getName();

				String groupInfo = getStudentGroupInfo(student, groups);
				String body = buildStudentEmailBody(studentName, session.getName(), groupInfo,
						coordinator.getName() != null ? coordinator.getName() : "Course Coordinator");

				emailService.builder()
						.to(student.getEmail())
						.subject(subject)
						.text(body)
						.send();
			}

			// Notify supervisors
			for (User supervisor : supervisors) {
				if (supervisor.getEmail() == null || supervisor.getEmail().isBlank())
					continue;

				String supervisorName = (supervisor.getName() == null || supervisor.getName().isBlank())
						? "Supervisor"
						: supervisor.getName();

				String supervisedGroupsInfo = getSupervisorGroupsInfo(supervisor, groups);
				String body = buildSupervisorEmailBody(supervisorName, session.getName(), supervisedGroupsInfo,
						coordinator.getName() != null ? coordinator.getName() : "Course Coordinator");

				emailService.builder()
						.to(supervisor.getEmail())
						.subject(subject)
						.text(body)
						.send();
			}

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to send notifications: " + e.getMessage());
		}

		return ResponseEntity.ok("Notifications sent successfully to participants.");
	}

	private String getStudentGroupInfo(User student, CopyOnWriteArrayList<Group> groups) {
		for (Group group : groups) {
			if (group.getStudentIds().contains(student.getId())) {

				String members = "";
				boolean firstMember = true;
				for (String studentId : group.getStudentIds()) {
					User member = db.getStudents().getItem(studentId);
					String memberName = "Unnamed Student";
					if (member != null && member.getName() != null) {
						memberName = member.getName();
					}
					if (firstMember) {
						members = memberName;
						firstMember = false;
					} else {
						members = members + "\n  - " + memberName;
					}
				}

				String groupName = "Unnamed Group";
				if (group.getName() != null) {
					groupName = group.getName();
				}

				return "Group: " + groupName + "\nMembers:\n  - " + members;
			}
		}
		return "You have not been assigned to a group yet.";
	}

	private String getSupervisorGroupsInfo(User supervisor, CopyOnWriteArrayList<Group> groups) {
		String supervisedGroups = "";
		boolean hasGroups = false;

		for (Group group : groups) {
			if (supervisor.getId().equals(group.getSupervisorId())) {
				hasGroups = true;

				String members = "";
				boolean firstMember = true;
				for (String studentId : group.getStudentIds()) {
					User member = db.getStudents().getItem(studentId);
					String memberName = "Unnamed Student";
					if (member != null && member.getName() != null) {
						memberName = member.getName();
					}
					if (firstMember) {
						members = memberName;
						firstMember = false;
					} else {
						members = members + "\n    - " + memberName;
					}
				}

				String groupName = "Unnamed Group";
				if (group.getName() != null) {
					groupName = group.getName();
				}

				String groupInfo = "Group: " + groupName + "\n  Members:\n    - " + members + "\n\n";
				supervisedGroups = supervisedGroups + groupInfo;
			}
		}

		if (!hasGroups) {
			return "You are not currently supervising any groups.";
		}

		return supervisedGroups;
	}

	private String buildStudentEmailBody(String userName, String sessionName, String groupInfo, String coordinatorName) {
		return String.format(
				"Dear %s,\n\n" +
						"All groups for the session \"%s\" have now been created.\n\n" +
						"Your group assignment:\n%s\n\n" +
						"Best regards,\n%s",
				userName, sessionName, groupInfo, coordinatorName);
	}

	private String buildSupervisorEmailBody(String userName, String sessionName, String groupsInfo,
			String coordinatorName) {
		return String.format(
				"Dear %s,\n\n" +
						"All groups for the session \"%s\" have now been created.\n\n" +
						"Your supervised groups:\n%s\n" +
						"Best regards,\n%s",
				userName, sessionName, groupsInfo, coordinatorName);
	}
}