package com.aau.grouping_system.User.Coordinator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;

@Service
public class CoordinatorService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	// constructors

	public CoordinatorService(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	// public methods

	public Coordinator addCoordinator(String email, String password, String name) {
		String passwordHash = passwordEncoder.encode(password);
		return new Coordinator(db.getCoordinators(), null, db, email, passwordHash, name);
	}

	public void modifyEmail(String newEmail, String coordinatorId) {
		db.getCoordinators().getItem(coordinatorId).setEmail(newEmail);
	}

	public void modifyPassword(String newPassword, String coordinatorId) {
		String passwordHash = passwordEncoder.encode(newPassword);
		db.getCoordinators().getItem(coordinatorId).setPasswordHash(passwordHash);
	}

	public boolean isEmailDuplicate(String email) {
		for (Coordinator existingCoordinator : db.getCoordinators().getAllItems().values()) {
			if (existingCoordinator.getEmail().equals(email)) {
				return true;
			}
		}
		return false;
	}
}
