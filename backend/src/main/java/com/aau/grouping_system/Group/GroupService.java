package com.aau.grouping_system.Group;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;
import com.aau.grouping_system.User.Student.Student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class GroupService {

	private final Database db;

	public GroupService(Database db) {
		this.db = db;
	}

	private void logGroupActivity(@NotBlank String activity, @NotNull Student student,
			@NoDangerousCharacters @NotBlank String groupId) {
		System.out.println(student.getName() + " " + activity + " group " + groupId + ".");
	}

	public void joinGroup(@NoDangerousCharacters @NotBlank String groupId, @NotNull Student student) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Group not found");
		}

		if (group.getStudentIds().contains(student.getId())) {
			throw new IllegalStateException("Student is already in the group");
		}

		if (group.getStudentIds().size() >= group.getMaxStudents()) {
			throw new IllegalStateException("Group is full");
		}

		group.getStudentIds().add(student.getId());
		student.setGroupId(group.getId());
		logGroupActivity("joined", student, groupId);
	}

	public void leaveGroup(@NoDangerousCharacters @NotBlank String groupId, @NotNull Student student) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Group not found");
		}

		group.getStudentIds().remove(student.getId());
		student.setGroupId(null);
		logGroupActivity("left", student, groupId);
	}

	public void requestToJoin(@NoDangerousCharacters @NotBlank String groupId, @NotNull Student student) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Group not found");
		}

		if (student == null) {
			throw new IllegalArgumentException("Student cannot be null");
		}

		if (group.getJoinRequestStudentIds().contains(student.getId())) {
			throw new IllegalStateException("Student already has a pending request");
		}

		if (group.getJoinRequestStudentIds().size() >= group.getMaxRequests()) {
			throw new IllegalStateException("Join request queue is full");
		}

		group.getJoinRequestStudentIds().add(student.getId());
		logGroupActivity("requested to join", student, groupId);
	}

	public void acceptJoinRequest(@NoDangerousCharacters @NotBlank String groupId, @NotNull Student student) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Group not found");
		}

		if (!group.getJoinRequestStudentIds().contains(student.getId())) {
			throw new IllegalStateException("No join request found from this student");
		}

		if (group.getStudentIds().size() >= group.getMaxStudents()) {
			throw new IllegalStateException("Group is full");
		}

		group.getJoinRequestStudentIds().remove(student.getId());
		group.getStudentIds().add(student.getId());
		student.setGroupId(group.getId());
		logGroupActivity("was accepted to join", student, groupId);
	}
}
