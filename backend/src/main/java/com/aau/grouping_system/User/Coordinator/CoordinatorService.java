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

	// methods

	public void addCoordinator(String email, String password, String name) {
		String passwordHash = passwordEncoder.encode(password);
		Coordinator newCoordinator = new Coordinator(null, email, passwordHash, name);
		db.getCoordinators().put(newCoordinator);
	}

	public void modifyEmail(String newEmail, Integer coordinatorID) {
		db.getCoordinators().getItem(coordinatorID).setEmail(newEmail);
	}

	public void modifyPassword(String newPassword, Integer coordinatorID) {
		String passwordHash = passwordEncoder.encode(newPassword);
		db.getCoordinators().getItem(coordinatorID).setPasswordHash(passwordHash);
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
