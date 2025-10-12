package com.aau.grouping_system.Authentication;

import com.aau.grouping_system.Database.Database;
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


	public boolean isPasswordCorrect(String password, Coordinator user) {
		if (passwordEncoder.matches(password, user.getPasswordHash())) {
			return true;
		}
		return false;
	}

	public Coordinator findByEmail(String email) {
		for (Coordinator existingCoordinator : db.getCoordinators().getAllEntries().values()) {
			if (existingCoordinator.getEmail().equals(email)) {
				return existingCoordinator;
			}
		}
		return null;
	}

	public void invalidateOldSession(HttpServletRequest request) {
		HttpSession oldSession = request.getSession(false);
		if (oldSession != null)
			oldSession.invalidate();
	}

	public void createNewSession(HttpServletRequest request, Coordinator user) {
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(86400); // 1 dag
		// Gemmer nøglen "user" i session objektet.
		session.setAttribute("user", user);
	}

}
