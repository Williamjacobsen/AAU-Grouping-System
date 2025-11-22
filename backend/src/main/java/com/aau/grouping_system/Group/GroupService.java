package com.aau.grouping_system.Group;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.SessionMember.Student.Student;

@Service
public class GroupService {

	private final Database db;

	public GroupService(Database db) {
		this.db = db;
	}

	public void createGroup(Session session, String name, Student foundingMember) {
		Group newGroup = db.getGroups().addItem(
				db,
				session.getGroups(),
				new Group(session, name));
		joinGroup(newGroup, foundingMember);
	}

	public Group createGroupAndReturnObject(Session session, String name, Student foundingMember) {
		Group newGroup = db.getGroups().addItem(
				db,
				session.getGroups(),
				new Group(session, name));
		joinGroup(newGroup, foundingMember);
		return newGroup;
	}

	public void joinGroup(Group group, Student student) {

		requireStudentNotAlreadyInTheGroup(group, student);
		requireGroupNotFull(group);

		// Leave previous group
		String previousGroupId = student.getGroupId();
		if (previousGroupId != null) {
			Group previousGroup = db.getGroups().getItem(previousGroupId);
			leaveGroup(previousGroup, student);
		}

		group.getStudentIds().add(student.getId());
		student.setGroupId(group.getId());
		cancelJoinRequest(student);

		logGroupActivity("joined", student, group.getId());
	}

	public void leaveGroup(Group group, Student student) {

		group.getStudentIds().remove(student.getId());
		student.setGroupId(null);

		// Removing the last member deletes the group
		if (group.getStudentIds().size() <= 0) {
			db.getGroups().cascadeRemoveItem(db, group);
		}

		logGroupActivity("left", student, group.getId());
	}

	public void leaveGroupWithoutDeleting(Group group, Student student) {
		// Removes student, but doesnt delete the group (for merging)
		group.getStudentIds().remove(student.getId());
		student.setGroupId(null);

		logGroupActivity("left", student, group.getId());
	}

	public void requestToJoin(Group group, Student student) {

		requireStudentNotAlreadySentJoinRequest(group, student);
		requireStudentNotAlreadyInTheGroup(group, student);

		cancelJoinRequest(student);

		group.getJoinRequestStudentIds().add(student.getId());
		student.setActiveJoinRequestGroupId(group.getId());

		logGroupActivity("requested to join", student, group.getId());
	}

	public void acceptJoinRequest(Group group, Student requestingStudent) {

		requireJoinRequestExists(group, requestingStudent);
		requireGroupNotFull(group);

		group.getJoinRequestStudentIds().remove(requestingStudent.getId());
		joinGroup(group, requestingStudent);

		logGroupActivity("was accepted to join", requestingStudent, group.getId());
	}

	public void cancelJoinRequest(Student student) {
		if (student.getActiveJoinRequestGroupId() != null) {
			Group previousJoinRequestGroup = db.getGroups().getItem(student.getActiveJoinRequestGroupId());
			previousJoinRequestGroup.getJoinRequestStudentIds().remove(student.getId());
		}
		student.setActiveJoinRequestGroupId(null);
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	public void requireGroupNameNotDuplicate(Session session, String name) {

		CopyOnWriteArrayList<Group> sessionGroups = (CopyOnWriteArrayList<Group>) session.getGroups().getItems(db);

		boolean alreadyExists = sessionGroups.stream()
				.anyMatch(group -> group.getName().equals(name));

		if (alreadyExists) {
			throw new RequestException(HttpStatus.CONFLICT, "Group name is already used by another group in the session.");
		}
	}

	public void requireUserOwnsGroupOrIsCoordinator(Group group, User user) {

		// The coordinator is always an owner.
		if (user.getRole() == User.Role.Coordinator) {
			return;
		}

		// Only the student who has been a member for the longest is the group owner.
		if (group.getStudentIds().getFirst() != user.getId()) {
			throw new RequestException(HttpStatus.UNAUTHORIZED, "Unauthorized: User does not own the group.");
		}
	}

	public void requireUserCanAssignFoundingMember(User user, Student student) {

		// The coordinator can always assign students a new group.
		if (user.getRole() == User.Role.Coordinator) {
			return;
		}

		// Students are only allowed to create new groups for themselves.
		if (user.getId() != student.getId()) {
			throw new RequestException(HttpStatus.UNAUTHORIZED,
					"Unauthorized: User is not a coordinator or themselves the found member.");
		}
	}

	// Private methods

	private void logGroupActivity(String activity, Student student, String groupId) {
		System.out.println(student.getName() + " " + activity + " group " + groupId + ".");
	}

	private void requireGroupNotFull(Group group) {
		Session session = db.getSessions().getItem(group.getSessionId());
		if (group.getStudentIds().size() >= session.getMaxGroupSize()) {
			throw new IllegalStateException("Group is full");
		}
	}

	private void requireStudentNotAlreadyInTheGroup(Group group, Student student) {
		if (group.getStudentIds().contains(student.getId())) {
			throw new IllegalStateException("Student is already in the group");
		}
	}

	private void requireStudentNotAlreadySentJoinRequest(Group group, Student student) {
		if (group.getJoinRequestStudentIds().contains(student.getId())) {
			throw new IllegalStateException("Student already has a pending request");
		}
	}

	private void requireJoinRequestExists(Group group, Student student) {
		if (!group.getJoinRequestStudentIds().contains(student.getId())) {
			throw new IllegalStateException("No join request found from this student");
		}
	}

}
