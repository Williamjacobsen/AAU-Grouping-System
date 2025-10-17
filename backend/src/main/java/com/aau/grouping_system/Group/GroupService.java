package com.aau.grouping_system.Group;

import org.springframework.stereotype.Service;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.Student.Student;

@Service
public class GroupService {

	private final Database db;

	public GroupService(Database db) {
		this.db = db;
	}

	private void logGroupActivity(String activity, Student student, String groupId) {
		System.out.println(student.getName() + " " + activity + " group " + groupId + ".");
	}

	public void joinGroup(String groupId, Student student) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Group not found");
		}

		if (group.getStudents().contains(student)) {
			throw new IllegalStateException("Student is already in the group");
		}

		if (group.getStudents().size() >= group.getMaxStudents()) {
			throw new IllegalStateException("Group is full");
		}

		group.getStudents().add(student);
		logGroupActivity("joined", student, groupId);
	}

	public void leaveGroup(String groupId, Student student) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Group not found");
		}

		group.getStudents().remove(student);
		logGroupActivity("left", student, groupId);
	}

	public void requestToJoin(String groupId, Student student) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Group not found");
		}

		if (student == null) {
			throw new IllegalArgumentException("Student cannot be null");
		}

		if (group.getJoinRequests().contains(student)) {
			throw new IllegalStateException("Student already has a pending request");
		}

		if (group.getJoinRequests().size() >= group.getMaxRequests()) {
			throw new IllegalStateException("Join request queue is full");
		}

		group.getJoinRequests().add(student);
		logGroupActivity("requested to join", student, groupId);
	}

	public void acceptJoinRequest(String groupId, Student student) {
		Group group = db.getGroups().getItem(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Group not found");
		}

		if (!group.getJoinRequests().contains(student)) {
			throw new IllegalStateException("No join request found from this student");
		}

		if (group.getStudents().size() >= group.getMaxStudents()) {
			throw new IllegalStateException("Group is full");
		}

		group.getJoinRequests().remove(student);
		group.getStudents().add(student);
		logGroupActivity("was accepted to join", student, groupId);
	}
}
