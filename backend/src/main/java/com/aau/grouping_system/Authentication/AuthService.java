package com.aau.grouping_system.Authentication;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	public AuthService(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	public boolean isPasswordCorrect(String password, User user) {
		return passwordEncoder.matches(password, user.getPasswordHash());
	}

	public User findByEmailOrId(String emailOrId, User.Role role) {
		switch (role) {
			case User.Role.Coordinator:
				for (Coordinator coordinator : db.getCoordinators().getAllItems().values()) {
					if (coordinator.getEmail().equals(emailOrId)) {
						return coordinator;
					}
				}
				return null;
			case User.Role.Supervisor:
				for (Supervisor supervisor : db.getSupervisors().getAllItems().values()) {
					if (supervisor.getEmail().equals(emailOrId)) {
						return supervisor;
					}
				}
				return null;
			case User.Role.Student:
				for (Student student : db.getStudents().getAllItems().values()) {
					if (student.getEmail().equals(emailOrId)) {
						return student;
					}
				}	
			default:
				return null;
		}
	}

	public void invalidateOldSession(HttpServletRequest request) {
		HttpSession oldSession = request.getSession(false);
		if (oldSession != null)
			oldSession.invalidate();
	}

	public void createNewSession(HttpServletRequest request, User user) {
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(86400); // 1 day
		// Save the key "user" in the HttpSession object.
		session.setAttribute("user", user);
	}

	public Boolean hasAuthorizedRole(User user, User.Role[] authorizedRoles) {

		if (user == null || authorizedRoles.length == 0) {
			return false;
		}

		Boolean isAuthorizedRole = false;

		for (User.Role role : authorizedRoles) {
			if (user.getRole().equals(role)) {
				isAuthorizedRole = true;
				break;
			}
		}

		return isAuthorizedRole;
	}

	public User getUser(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (User) session.getAttribute("user");
	}

	public Coordinator getCoordinatorByUser(HttpServletRequest request) {
		User user = getUser(request);
		if (user instanceof Coordinator) {
			return (Coordinator) user;
		} else {
			return null;
		}
	}

	public Student getStudentByUser(HttpServletRequest request) {
		User user = getUser(request);
		if (user instanceof Student) {
			return (Student) user;
		} else {
			return null;
		}
	}

}
