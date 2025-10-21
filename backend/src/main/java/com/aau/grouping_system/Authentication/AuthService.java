package com.aau.grouping_system.Authentication;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Coordinator.Coordinator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	// constructer

	public AuthService(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	// methodes

	public boolean isPasswordCorrect(String password, User user) {
		return passwordEncoder.matches(password, user.getPasswordHash());
	}

	public User findByEmailOrId(String emailOrId, User.Role role) {
		switch (role) {
			case User.Role.COORDINATOR:
				for (Coordinator coordinator : db.getCoordinators().getAllItems().values()) {
					if (coordinator.getEmail().equals(emailOrId)) {
						return coordinator;
					}
				}
				return null;
			case User.Role.SUPERVISOR:
				return db.getSupervisors().getItem(emailOrId);
			case User.Role.STUDENT:
				return db.getStudents().getItem(emailOrId);
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

}
