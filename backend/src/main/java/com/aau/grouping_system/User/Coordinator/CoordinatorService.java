package com.aau.grouping_system.User.Coordinator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;

@Service
public class CoordinatorService {

	private final Database db;
	private final PasswordEncoder passwordEncoder;

	public CoordinatorService(Database db, PasswordEncoder passwordEncoder) {
		this.db = db;
		this.passwordEncoder = passwordEncoder;
	}

	public Coordinator addCoordinator(String email, String password, String name) {
		String passwordHash = passwordEncoder.encode(password);
		return new Coordinator(db, null, email, passwordHash, name);
	}
}
