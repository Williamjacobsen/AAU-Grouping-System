package com.aau.grouping_system.User;

import java.util.Collection;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;

@Service
public class UserService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	public UserService(
			Database db,
			PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	public User getUser(User.Role role, String id) {
		switch (role) {
			case User.Role.Coordinator:
				return db.getCoordinators().getItem(id);
			case User.Role.Supervisor:
				return db.getSupervisors().getItem(id);
			case User.Role.Student:
				return db.getStudents().getItem(id);
			default:
				throw new IllegalArgumentException("Passed in a User.Role that is not a valid value.");
		}
	}

	/// "User" is allowed to be null (in case we're dealing with a coordinator sign
	/// up attempt).
	@SuppressWarnings("unused")
	public boolean isEmailDuplicate(String email, User user) {

		Collection<? extends User> existingUsers;
		switch (user) {
			// If "user" is null, then we're dealing with a sign up attempt by an
			// up-and-coming coordinator
			case null:
				existingUsers = (Collection<? extends User>) db.getCoordinators().getAllItems().values();
				break;
			case Coordinator coordinator:
				existingUsers = (Collection<? extends User>) db.getCoordinators().getAllItems().values();
				break;
			case Supervisor supervisor:
				Session supervisorSession = db.getSessions().getItem(supervisor.getSessionId());
				existingUsers = (Collection<? extends User>) db.getSupervisors()
						.getItems(supervisorSession.getSupervisors().getIds());
				break;
			case Student student:
				Session studentSession = db.getSessions().getItem(student.getSessionId());
				existingUsers = (Collection<? extends User>) db.getStudents()
						.getItems(studentSession.getStudents().getIds());
				break;
			default:
				throw new IllegalArgumentException("Passed in a User.Role that is not a valid value.");
		}

		for (User existingUser : existingUsers) {
			if (existingUser.getEmail().equals(email)) {
				return true;
			}
		}
		return false;
	}

	/// Except for coordinators, users in the same session with the same role cannot
	/// have duplicate names.
	@SuppressWarnings("unused")
	public boolean isNameDuplicate(String name, User user) {

		Collection<? extends User> existingUsers;
		switch (user) {
			case Coordinator coordinator:
				return false;
			case Supervisor supervisor:
				Session supervisorSession = db.getSessions().getItem(supervisor.getSessionId());
				existingUsers = (Collection<? extends User>) db.getSupervisors()
						.getItems(supervisorSession.getSupervisors().getIds());
				break;
			case Student student:
				Session studentSession = db.getSessions().getItem(student.getSessionId());
				existingUsers = (Collection<? extends User>) db.getStudents()
						.getItems(studentSession.getStudents().getIds());
				break;
			default:
				throw new IllegalArgumentException("Passed in a User.Role that is not a valid value.");
		}

		for (User existingUser : existingUsers) {
			if (existingUser.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void modifyEmail(String newEmail, User user) {
		user.setEmail(newEmail);
	}

	public void modifyPassword(String newPassword, User user) {
		String passwordHash = passwordEncoder.encode(newPassword);
		user.setPasswordHash(passwordHash);
	}

	public void modifyName(String newName, User user) {
		user.setName(newName);
	}

}
